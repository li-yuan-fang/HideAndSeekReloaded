package net.rbcraft.hideandseek.game.lobby;

import net.rbcraft.hideandseek.game.counter.Counter;
import net.rbcraft.hideandseek.game.Game;
import net.rbcraft.hideandseek.menu.Menus;
import net.rbcraft.hideandseek.io.I18n;
import net.rbcraft.hideandseek.reflect.NBTHelper;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.LinkedHashMap;
import java.util.LinkedList;

public class Lobby {

    private final LinkedHashMap<Player, PlayerData> dataCache = new LinkedHashMap<>();

    private final Counter counter;

    private final int least, waiting;

    private final Game game;

    public Location getLobby() {
        return game.getData().lobby;
    }

    public Lobby(Game game, int least, int waitingTime) {
        this.game = game;
        this.least = least;
        this.waiting = waitingTime;
        this.counter = new WaitingCounter(this);
    }

    public boolean isInGame(Player player) {
        return dataCache.containsKey(player);
    }

    public void join(Player player) {
        if (isInGame(player)) return;

        dataCache.put(player, new PlayerData(player));
        updateInventory(player);
        player.setLevel(waiting);
        player.setExp(1.0F);
        player.setGameMode(GameMode.SURVIVAL);
        player.teleport(getLobby());

        game.broadcast(I18n.formats("lobby_join_broadcast").replace("%player%", player.getName())
                .replace("%count%", dataCache.size() + "/" + game.getData().max));

        checkState();
    }

    public void leave(Player player) {
        if (!isInGame(player)) return;

        PlayerData data = dataCache.get(player);
        data.updateData(player);
        dataCache.remove(player);

        game.broadcast(I18n.formats("lobby_leave_broadcast").replace("%player%", player.getName())
                .replace("%count%", dataCache.size() + "/" + game.getData().max));

        checkState();
    }

    //结束游戏时调用
    public void leaveAll() {
        for (Player player : dataCache.keySet()) {
            dataCache.get(player).updateData(player);
        }
        dataCache.clear();
    }

    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (game.isGaming() || !isInGame(player) || event.getAction() == Action.PHYSICAL) return;

        ItemStack clicked = event.getItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        String param = NBTHelper.getParam(clicked);
        if (param == null) return;
        switch (param) {
            case "leave":
                game.leave(player);
                I18n.sendMessage(player, I18n.formats("game_leave"));
                break;
            case "select_skill":
                Menus.SelectSkill.openMenu(player);
                break;
        }
    }

    public LinkedList<Player> getPlayers() {
        return new LinkedList<>(dataCache.keySet());
    }

    public void clearInventory() {
        for (Player player : getPlayers()) {
            player.getInventory().clear();
        }
    }

    private static void updateInventory(Player player) {
        Inventory inv = player.getInventory();
        inv.clear();

        ItemStack select = new ItemStack(Material.BOOK, 1);
        ItemMeta selectMeta = select.getItemMeta();
        selectMeta.setDisplayName(I18n.formats("skill_select"));
        select.setItemMeta(selectMeta);
        select = NBTHelper.setParam(select, "select_skill");

        ItemStack leave = new ItemStack(Material.BARRIER, 1);
        ItemMeta leaveMeta = leave.getItemMeta();
        leaveMeta.setDisplayName(I18n.formats("skill_leave"));
        leave.setItemMeta(leaveMeta);
        leave = NBTHelper.setParam(leave, "leave");

        inv.addItem(select, leave);
    }

    private void checkState() {
        if (game.isGaming()) return;
        if (game.getManager().coreCounter.isCounting(counter)) {
            if (dataCache.size() < least) {
                ((WaitingCounter) counter).stop();
                for (Player player : dataCache.keySet()) {
                    player.setLevel(waiting);
                }
            }
        } else {
            if (dataCache.size() >= least) {
                for (Player player : dataCache.keySet()) {
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP,1.0F, 1.0F);
                    player.sendTitle(I18n.formats("lobby_count"), I18n.formats("lobby_count_sub"),
                            15, 60, 15);
                }
                ((WaitingCounter) counter).start();
            }
        }
    }

    public void stopWaiting() {
        if (counter.isStop()) return;
        ((WaitingCounter) counter).stop();
    }

    private static class WaitingCounter implements Counter {

        private final Lobby lobby;

        private boolean stop;

        public WaitingCounter(Lobby lobby) {
            this.lobby = lobby;
            stop = true;
        }

        public void start() {
            stop = false;
            lobby.game.getManager().coreCounter.start(this);
        }

        public void stop() {
            stop = true;
        }

        @Override
        public void end() {
            if (!lobby.game.isGaming()) {
                try {
                    lobby.game.start();
                } catch (Exception ignored) {
                }
            }
            stop = true;
        }

        @Override
        public void countdown(int time) {
            if (lobby.game.isGaming()) {
                stop();
                return;
            }

            int real = time + 5;
            for (Player player : lobby.dataCache.keySet()) {
                player.setLevel(real);
                player.setExp((float) real / (float) lobby.waiting);
                if (real % 30 == 0 || real == 10) {
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.3F, 1.0F);
                    I18n.sendMessage(player,
                            I18n.formats("lobby_ready").replace("%sec%", String.valueOf(real)));
                }
            }
        }

        @Override
        public int getCounterTime() {
            return lobby.waiting - 5;
        }

        @Override
        public boolean isStop() {
            return stop;
        }
    }

}
