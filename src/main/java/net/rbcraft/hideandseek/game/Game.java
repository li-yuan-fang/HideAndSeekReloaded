package net.rbcraft.hideandseek.game;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.rbcraft.hideandseek.game.beacon.BeaconManager;
import net.rbcraft.hideandseek.game.counter.Counter;
import net.rbcraft.hideandseek.game.disguise.DisguiseManager;
import net.rbcraft.hideandseek.game.disguise.DisguiseUtils;
import net.rbcraft.hideandseek.game.lobby.Lobby;
import net.rbcraft.hideandseek.game.skill.Skills;
import net.rbcraft.hideandseek.game.stamina.StaminaManager;
import net.rbcraft.hideandseek.game.util.Lock;
import net.rbcraft.hideandseek.io.I18n;
import net.rbcraft.hideandseek.io.IOManager;
import net.rbcraft.hideandseek.reflect.NBTHelper;
import net.rbcraft.hideandseek.util.PositionUtils;
import net.rbcraft.hideandseek.util.VaultHelper;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.LinkedList;
import java.util.Random;

public class Game {

    private static final PotionEffect seeker_patch = new PotionEffect(PotionEffectType.BLINDNESS, 600, 2);

    private final GameManager manager;

    private final Random rand = new Random();

    private final LinkedList<Player> hiders = new LinkedList<>();

    private final LinkedList<Player> spectators = new LinkedList<>();

    private final LinkedList<Player> seekers = new LinkedList<>();

    private final DisguiseManager disguiseManager;

    private final BeaconManager beaconManager;

    private final StaminaManager staminaManager;

    private final GameData data;

    private final Lobby lobby;

    private final Counter preCounter, gameCounter, seekerCounter;

    private boolean gaming = false;

    private boolean isLastTime = false, airdropped = false;

    private Item airdrop;

    public boolean isGaming() {
        return gaming;
    }

    public boolean isInGame(Player player) {
        return lobby.isInGame(player);
    }

    protected Game(GameManager manager, GameData data) {
        this.manager = manager;
        this.data = data;

        this.lobby = new Lobby(this, IOManager.getConfigInt("min_players"),
                IOManager.getConfigInt("waiting_time"));

        this.preCounter = new PreCounter(this, lobby);
        this.gameCounter = new GameCounter(this);
        this.seekerCounter = new SeekerCounter(this);

        this.disguiseManager = new DisguiseManager(this);
        this.beaconManager = new BeaconManager(this);
        this.staminaManager = new StaminaManager(this);
    }

    public static Game loadGame(GameManager manager, GameData data) {
        if (manager == null || data == null) return null;
        return new Game(manager, data);
    }

    public boolean isPreparing() {
        return manager.coreCounter.isCounting(preCounter);
    }

    public void start() throws Exception {
        if (!data.isReady()) throw new Exception(I18n.formats("game_no_ready"));
        if (isGaming()) throw new Exception(I18n.formats("game_gaming"));
        if (lobby.getPlayers().size() < 2) throw new Exception(I18n.formats("game_no_players"));
        if (isPreparing()) throw new Exception(I18n.formats("game_starting"));

        lobby.stopWaiting();
        lobby.clearInventory();
        manager.coreCounter.start(preCounter);
    }

    private void startNow() {
        //初始化
        gaming = true;
        seekers.clear();
        hiders.clear();
        spectators.clear();

        //清空背包
        LinkedList<Player> players = new LinkedList<>(lobby.getPlayers());
        for (Player player : players) {
            player.getInventory().clear();
        }

        //抽取猎手
        int seeker_count = Math.max((int) (IOManager.getConfigDouble("seeker_rate") * players.size()), 1);
        while (seekers.size() < seeker_count) {
            Player chosen = players.get(rand.nextInt(players.size()));
            if (seekers.contains(chosen)) continue;
            seekers.add(chosen);
            start_seeker(chosen);
        }

        //读取游侠
        for (Player player : lobby.getPlayers()) {
            if (seekers.contains(player)) continue;
            hiders.add(player);
            start_hider(player);
        }
        disguiseManager.updateState(getHiders());

        //启动体力管理
        staminaManager.updatePlayers();

        //启动计分板
        manager.scoreboards.start(this);

        //环境变量控制
        isLastTime = false;
        airdropped = false;

        //开始计时
        ((GameCounter) gameCounter).start();
        ((SeekerCounter) seekerCounter).start();
    }

    public void stop(String reason) {
        if (isGaming()) {
            gaming = false;
            ((GameCounter) gameCounter).stop();
            if (airdropped && airdrop != null)  airdrop.remove();

            boolean mistake = reason != null && reason.length() > 0;

            if (!mistake) {
                boolean seeker_win = hiders.size() == 0;

                //发放奖励
                if (VaultHelper.econ != null) {
                    double b = IOManager.getConfigDouble("base_prize");
                    double w = IOManager.getConfigDouble("win_prize");
                    for (Player player : seekers) {
                        double prize = seeker_win ? b + w : b;
                        VaultHelper.add(player, prize);
                        I18n.sendMessage(player, I18n.formats("game_prize")
                                .replace("%prize%", String.valueOf(prize)));
                        playSoundLater(player, seeker_win, 2);
                    }
                    for (Player player : hiders) {
                        double prize = seeker_win ? b : b + w;
                        VaultHelper.add(player, prize);
                        I18n.sendMessage(player, I18n.formats("game_prize")
                                .replace("%prize%", String.valueOf(prize)));
                        playSoundLater(player, !seeker_win, 2);
                    }
                }


                //设置原因
                reason = I18n.formats("game_win").replace("%role%",
                        I18n.formats("game_" + (seeker_win ? "seeker" : "hider")));
            }

            //发送结束标题
            String over_title = I18n.formats("game_over");
            for (Player player : lobby.getPlayers()) {
                player.sendTitle(over_title, reason, 20, 100, 20);
            }

            //广播
            broadcast(over_title + " " + reason);

            //结束计分板
            manager.scoreboards.stop(this);

            //游观者结束隐身状态
            for (Player spectator : spectators) {
                DisguiseUtils.show(spectator);
            }

            //全部玩家退出游戏
            seekers.clear();
            hiders.clear();
            spectators.clear();
            disguiseManager.clear();
            beaconManager.clear();
            staminaManager.clear();
        }

        lobby.leaveAll();
    }

    public void join(Player player) throws Exception {
        if (!data.isReady()) throw new Exception(I18n.formats("game_no_ready"));
        if (isGaming()) throw new Exception(I18n.formats("game_gaming"));
        lobby.join(player);
    }

    public void leave(Player player) {
        if (isPreparing()) {
            I18n.sendMessage(player, I18n.formats("game_forbid_leave"));
            return;
        }
        if (isGaming()) {
            if (hiders.contains(player)) {
                hiders.remove(player);
                disguiseManager.quit(player);
            } else {
                if (spectators.contains(player)) {
                    spectators.remove(player);
                    DisguiseUtils.show(player);
                } else {
                    seekers.remove(player);
                }
            }
            staminaManager.quit(player);
            manager.scoreboards.clearScoreboard(player);
            manager.scoreboards.updateScoreboard(this);
            checkGameState();
        }
        lobby.leave(player);
    }

    public void seek(Player player) {
        hiders.remove(player);
        spectators.add(player);
        disguiseManager.quit(player);
        staminaManager.quit(player);
        player.getInventory().clear();

        manager.scoreboards.updateScoreboard(this);

        boolean last = hiders.size() == 0;

        player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_DEATH, 1.0F, 1.0F);
        player.sendTitle(I18n.formats("game_hider_caught"), last ? "" : I18n.formats("game_hider_caught_sub"),
                15, 60, 15);
        broadcast(I18n.formats("game_hider_caught_broadcast").replace("%hider%", player.getName()));

        if (last) {
            stop(null);
        } else {
            Lock.start(this, new LockMonkey(player, this));
            manager.coreCounter.start(100, () -> {
                if (!isGaming()) return;
                DisguiseUtils.hide(player);
                manager.skillManager.getSkill(player).giveSpectatorSkill();
            });
        }
    }

    private void start_seeker(Player player) {
        player.teleport(data.seeker);
        player.addPotionEffect(seeker_patch);
        player.setWalkSpeed(0.0F);
        player.sendTitle(I18n.formats("game_start"), I18n.formats("game_seeker_wait"), 20, 60, 20);
    }

    private void release_seekers() {
        if (!isGaming()) return;
        for (Player player : seekers) {
            manager.skillManager.getSkill(player).giveSeekerSkill();
            player.setWalkSpeed(0.2F);
            player.getInventory().setChestplate(new ItemStack(Material.ELYTRA));

            player.teleport(data.hider);
            player.sendTitle(I18n.formats("game_seeker_start"), "", 15, 60, 15);
        }
        broadcast(I18n.formats("game_seeker_release"));
    }

    private void start_hider(Player player) {
        manager.skillManager.getSkill(player).giveHiderSkill();
        player.getInventory().setChestplate(new ItemStack(Material.ELYTRA));

        player.teleport(data.hider);
        player.sendTitle(I18n.formats("game_start"), I18n.formats("game_hider_start"), 20, 60, 20);
    }

    private void playSoundLater(Player player, boolean win, int delay) {
        manager.coreCounter.start(delay, () -> {
            player.playSound(player.getLocation(),
                    (win ?  Sound.ENTITY_PLAYER_LEVELUP : Sound.ENTITY_ENDER_DRAGON_GROWL),
                    1.0F, 1.0F);
        });
    }

    //眷顾之能
    private void summon_airdrop() {
        for (Player player : lobby.getPlayers()) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                    new TextComponent(I18n.formats("game_summon_airdrop_pre")));
        }

        int x1 = data.pos1.getBlockX(), z1 = data.pos1.getBlockZ();
        int x2 = data.pos2.getBlockX(), z2 = data.pos2.getBlockZ();
        int x = Math.min(x1, x2) + rand.nextInt(Math.abs(x1 - x2) + 1);
        int z = Math.min(z1, z2) + rand.nextInt(Math.abs(z1 - z2) + 1);

        World world = data.pos1.getWorld();
        int y = world.getHighestBlockAt(x, z).getY();
        Location airPos = new Location(world, x, y, z);
        world.spawnEntity(airPos, EntityType.LIGHTNING);

        getManager().coreCounter.start(60, () -> {
            airdrop = world.dropItem(airPos, NBTHelper.setParam(new ItemStack(Material.DIAMOND), "airdrop"));
            airdrop.setGlowing(true);
            airdropped = true;
            broadcast(I18n.formats("game_summon_airdrop")
                    .replace("%pos%", "(" + x + ", " + z + ")"));
        });
    }

    private void checkGameState() {
        if (seekers.size() == 0 || (hiders.size() + spectators.size()) == 0) {
            stop(ChatColor.translateAlternateColorCodes('&', "&c&l") +
                    I18n.formats("game_no_players"));
        }
    }

    public void broadcast(String message) {
        for (Player player : lobby.getPlayers()) {
            I18n.sendMessage(player, message);
        }
    }

    public void enableLastTime() {
        isLastTime = true;
        broadcast(I18n.formats("game_last_time"));
    }

    public boolean isLastTime() {
        return isLastTime;
    }

    public LinkedList<Player> getHiders() {
        return hiders;
    }

    public LinkedList<Player> getSpectators() {
        return spectators;
    }

    public LinkedList<Player> getSeekers() {
        return seekers;
    }

    public DisguiseManager getDisguiseManager() {
        return disguiseManager;
    }

    public BeaconManager getBeaconManager() {
        return beaconManager;
    }

    public GameManager getManager() {
        return manager;
    }

    public GameData getData() {
        return data;
    }

    public int getTimeLeft() {
        return isGaming() ? ((GameCounter) gameCounter).getTime() : 0;
    }

    public LinkedList<Player> getPlayers() {
        return lobby.getPlayers();
    }

    private void sendTeamChat(Player player, String message) {
        if (!isGaming() || !isInGame(player)) return;

        String msg = I18n.formats("team_chat").replace("%player%", player.getName()) + message;
        if (seekers.contains(player)) {
            for (Player seeker : seekers) {
                seeker.sendMessage(msg);
            }
        } else {
            for (Player hider : hiders) {
                hider.sendMessage(msg);
            }
            for (Player spectator : spectators) {
                spectator.sendMessage(msg);
            }
        }
    }

    private static class PreCounter implements Counter {

        private final Lobby lobby;
        private final Game game;

        public PreCounter(Game game, Lobby lobby) {
            this.game = game;
            this.lobby = lobby;
        }

        @Override
        public void end() {
            game.startNow();
        }

        @Override
        public void countdown(int time) {
            for (Player player : lobby.getPlayers()) {
                player.setLevel(time);
                player.setExp((float) time / 5.0F);
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, time * 0.5F);
                I18n.sendMessage(player, I18n.formats("lobby_ready_nearly").replace("%sec%", String.valueOf(time)));
            }
        }

        @Override
        public int getCounterTime() {
            return 5;
        }

        @Override
        public boolean isStop() {
            return false;
        }

    }

    private static class GameCounter implements Counter {

        private final Game game;

        private boolean stop;

        private int time;

        public GameCounter(Game game) {
            this.game = game;
            this.stop = true;
        }

        public void start() {
            stop = false;
            game.manager.coreCounter.start(this);
        }

        public void stop() {
            stop = true;
        }

        public int getTime() {
            return time;
        }

        @Override
        public void end() {
            if (!game.isGaming()) return;
            //游戏结束
            game.stop(null);
            stop = true;
        }

        @Override
        public void countdown(int time) {
            if (!game.isGaming()) return;
            this.time = time;

            //局部事件
            for (Player player : game.lobby.getPlayers()) {
                if (!game.isInGame(player)) continue;
                player.setLevel(time);
                player.setExp((float) time / (float) game.data.gameTime);
                if (time <= 5)
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, time * 0.5F);
                if (time % 60 == 0 || time == 10) {
                    I18n.sendMessage(player, I18n.formats("game_deadline").replace("%sec%", String.valueOf(time)));
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.3F, 1.0F);
                }
            }

            //全局事件
            if (time == 100) game.summon_airdrop(); //眷顾之能
            if (time == 30) game.enableLastTime();

            //刷新计分板
            game.manager.scoreboards.onCount(game);
        }

        @Override
        public int getCounterTime() {
            return game.data.gameTime;
        }

        @Override
        public boolean isStop() {
            return stop;
        }

    }

    private static class SeekerCounter implements Counter {

        private final Game game;

        private boolean stop = true;

        public SeekerCounter(Game game) {
            this.game = game;
        }

        public void start() {
            stop = false;
            game.getManager().coreCounter.start(this);
        }

        public void stop() {
            stop = true;
        }

        @Override
        public void end() {
            if (!game.isGaming()) return;
            game.release_seekers();
        }

        @Override
        public void countdown(int time) {
            if (!game.isGaming()) stop();
            Location spawn = game.getData().seeker;
            for (Player seeker : game.getSeekers()) {
                if (PositionUtils.distanceFlatBlock(seeker.getLocation(), spawn) > 3) seeker.teleport(spawn);
            }
        }

        @Override
        public int getCounterTime() {
            return 30;
        }

        @Override
        public boolean isStop() {
            return stop;
        }
    }
    private static class LockMonkey extends Lock {

        public LockMonkey(Player player, Game game) {
            super(player, game, 100);
        }

        @Override
        protected boolean isStop() {
            return !game.isGaming();
        }

    }

    public void onPlayerPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        if (!isInGame(player)) return;
        event.setCancelled(true);

        if (!isGaming()) return;
        String param = NBTHelper.getParam(event.getItem().getItemStack());
        if (param == null || param.length() == 0) return;
        if (param.equalsIgnoreCase("airdrop") && !spectators.contains(player)) {
            airdropped = false;
            event.getItem().remove();

            //给予技能
            boolean isHider = hiders.contains(player);
            manager.skillManager.getSkill(player).giveAirDrop(isHider);
            broadcast(I18n.formats("game_get_airdrop")
                    .replace("%role%", I18n.formats("game_" + (isHider ? "hider" : "seeker")))
                    .replace("%player%", player.getName()));
        }
    }

    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (isInGame(player)) {
            event.setCancelled(true);

            Item item = event.getItemDrop();
            if (isGaming() && Skills.isLeaveButton(item.getItemStack())) {
                event.setCancelled(false);
                item.remove();
                leave(player);
            }
        }
    }

    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!isGaming() || !isInGame(player)) return;

        if (!data.isInArea(event.getTo())) {
            event.setCancelled(true);
            return;
        }

        disguiseManager.onPlayerMove(event);
        beaconManager.onPlayerMove(event);
        staminaManager.onPlayerMove(event);
    }

    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!isInGame(player)) return;
        event.setCancelled(true);

        if (isGaming()) {
            Skills.onPlayerUseSkill(event, this);
        } else {
            lobby.onPlayerInteract(event);
        }
    }

    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (!isInGame(player) || event.isCancelled() || player.isOp()) return;

        String label = event.getMessage();
        if (!label.startsWith("/")) return;

        label = label.substring(1);
        if (label.contains(" ")) label = label.split(" ")[0];

        if (!label.equalsIgnoreCase("hideandseek") &&
                !label.equalsIgnoreCase("has")) {
            event.setCancelled(true);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                    new TextComponent(I18n.formats("game_forbid_command")));
        }
    }

    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (isInGame(player)) leave(player);
    }

    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!isGaming() || !isInGame(player)) return;

        String text = event.getMessage();
        if (text.startsWith(".") && text.length() > 1) {
            sendTeamChat(player, text.substring(1));
            event.setCancelled(true);
        }
    }

    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;

        Player player = (Player) event.getPlayer();
        if (!isGaming() || !isInGame(player)) return;
        event.setCancelled(true);
    }

    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player) || event.isCancelled()) return;

        Player player = (Player) event.getEntity();
        if (isInGame(player) && !staminaManager.contains(player)) {
            event.setCancelled(true);
            player.setFoodLevel(20);
        }
    }

    public void onEntityDamage(EntityDamageEvent event) {
        if (event.isCancelled()) return;
        boolean flag = false;
        switch (event.getEntityType()) {
            case PLAYER:
                if (isInGame((Player) event.getEntity())) flag = true;
                break;
            case DROPPED_ITEM:
                if (isGaming() && airdropped && event.getEntity().equals(airdrop)) flag = true;
                break;
        }
        event.setCancelled(flag);
    }

    public void onBlockPlace(BlockPlaceEvent event) {
        if (isInGame(event.getPlayer())) event.setCancelled(true);
    }

    public void onBlockBreak(BlockBreakEvent event) {
        if (isInGame(event.getPlayer())) event.setCancelled(true);
    }

    public void onTick() {
        if (!isGaming()) return;
        staminaManager.onTick();
        beaconManager.onTick();
    }

}
