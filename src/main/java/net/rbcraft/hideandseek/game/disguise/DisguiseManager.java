package net.rbcraft.hideandseek.game.disguise;

import me.libraryaddict.disguise.DisguiseAPI;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.rbcraft.hideandseek.game.Game;
import net.rbcraft.hideandseek.game.GameData;
import net.rbcraft.hideandseek.io.I18n;
import net.rbcraft.hideandseek.io.IOManager;
import net.rbcraft.hideandseek.util.PositionUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.LinkedList;

public class DisguiseManager {

    private final ItemStack air = new ItemStack(Material.AIR);

    private final Game game;

    private final GameData data;

    private final LinkedHashMap<Player, DisguiseState> states = new LinkedHashMap<>();

    public DisguiseManager(Game game) {
        this.game = game;
        this.data = game.getData();
    }

    //玩家管理函数区
    public void updateState(LinkedList<Player> players) {
        states.clear();
        for (Player player : players) {
            states.put(player, new DisguiseState(player));
        }
    }

    //玩家退出游戏
    public void quit(Player player) {
        quit(player, true);
    }

    private void quit(Player player, boolean remove) {
        if (!states.containsKey(player)) return;

        DisguiseState state = states.get(player);
        if (state.isDisguised()) {
            if (state.isSolid()) unSolid(player, false, true);
            DisguiseAPI.undisguiseToAll(player);
        }

        if (state.isInBeacon()) setInBeacon(player, false);
        if (state.isVanish()) setVanish(player, false);

        if (remove) states.remove(player);
    }

    //清除全部玩家的伪装
    public void clear() {
        for (Player player : states.keySet()) {
            quit(player, false);
        }
        states.clear();
    }

    //伪装函数区
    public void unDisguise(Player player) {
        DisguiseState state = states.get(player);
        if (state.isDisguised()) {
            if (state.isSolid()) unSolid(player, true, false);
            DisguiseUtils.disguise(player, null);
            player.getInventory().setItem(8, air);
            player.setWalkSpeed(0.2F);
        }
    }

    public boolean disguise(Player player) {
        DisguiseState state = states.get(player);
        if (!state.isAllowDisguise()) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                    new TextComponent(I18n.formats("game_no_disguise")));
            return false;
        }

        int id;
        if (state.isDisguised()) {
            if (state.isSolid()) unSolid(player, true, false);
            id = state.getId() + 1;
            if (id >= data.items.length) id = 0;
        } else {
            id = 0;
        }

        state.setId(id);
        ItemStack item = data.items[id];
        DisguiseUtils.disguise(player, item);

        player.getInventory().setItem(8, item.clone());

        if (!shouldVanish(player))
            SolidRunnable.start(this, player.getLocation(), player);
        player.setWalkSpeed((float) IOManager.getConfigDouble("speed_disguised") * 0.2F);
        return true;
    }

    public boolean isDisguised(Player player) {
        if (!states.containsKey(player)) return false;
        return states.get(player).isDisguised();
    }

    //固定方块函数区
    public void solid(Player player, boolean inform) {
        if (!states.containsKey(player) || shouldVanish(player)) return;
        DisguiseState state = states.get(player);
        DisguiseUtils.disguise(player, null);
        DisguiseUtils.hide(player);

        ItemStack item = data.items[state.getId()];
        DisguiseUtils.updateFakeBlock(player, player.getLocation(), item.getType());
        state.solid(player.getLocation());
        updateVanishState(player);

        if (inform) I18n.sendMessage(player, I18n.formats("game_solid"));
    }

    public void unSolid(Player player, boolean inform, boolean force) {
        if (!states.containsKey(player)) return;
        DisguiseState state = states.get(player);
        DisguiseUtils.updateFakeBlock(player, state.getSolidPos(), Material.AIR);

        ItemStack item = data.items[state.getId()];
        DisguiseUtils.disguise(player, item);
        state.unSolid();
        if (force || !shouldVanish(player)) DisguiseUtils.show(player);
        updateVanishState(player);

        if (inform) I18n.sendMessage(player, I18n.formats("game_unsolid"));
    }

    public boolean isSolid(Player player) {
        if (!states.containsKey(player)) return false;
        return states.get(player).isSolid();
    }

    //限制伪装函数区
    public void forbidDisguise(Player player, int time) {
        if (!states.containsKey(player)) return;
        DisguiseState state = states.get(player);
        unDisguise(player);
        state.setAllowDisguise(false);
        updateVanishState(player);

        game.getManager().coreCounter.start(time, () -> allowDisguise(player));
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                new TextComponent(I18n.formats("game_forbid_disguise")));
    }

    //允许伪装
    private void allowDisguise(Player player) {
        if (!states.containsKey(player) || isAllowDisguise(player)) return;
        states.get(player).setAllowDisguise(true);
        updateVanishState(player);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                new TextComponent(I18n.formats("game_allow_disguise")));
    }

    public boolean isAllowDisguise(Player player) {
        if (!states.containsKey(player)) return false;
        return states.get(player).isAllowDisguise();
    }

    //信标函数区
    public void joinBeacon(Player player) {
        setInBeacon(player, true);
    }

    public void leaveBeacon(Player player) {
        setInBeacon(player, false);

        if (!states.containsKey(player)) return;
        DisguiseState state = states.get(player);
        if (state.isDisguised() && state.isAllowDisguise() && !state.isVanish())
            SolidRunnable.start(this, player.getLocation(), player);
    }

    private void setInBeacon(Player player, boolean flag) {
        if (!states.containsKey(player)) return;
        states.get(player).setInBeacon(flag);
        updateVanishState(player);
    }

    public boolean isInBeacon(Player player) {
        if (!states.containsKey(player)) return false;
        return states.get(player).isInBeacon();
    }

    //透明戏法函数区
    public void vanish(Player player, int time) {
        setVanish(player, true);
        getGame().getManager().coreCounter.start(time, () -> {
            if (isVanish(player)) setVanish(player, false);
        });
    }

    private void setVanish(Player player, boolean vanish) {
        if (!states.containsKey(player)) return;
        states.get(player).setVanish(vanish);
        updateVanishState(player);
    }

    public boolean isVanish(Player player) {
        if (!states.containsKey(player)) return false;
        return states.get(player).isVanish();
    }

    //隐身状态区
    private void updateVanishState(Player player) {
        if (!states.containsKey(player)) return;
        DisguiseState state = states.get(player);
        if ((state.isAllowDisguise() && (state.isInBeacon() || state.isSolid())) || state.isVanish()) {
            DisguiseUtils.hide(player);
        } else {
            DisguiseUtils.show(player);
        }
    }

    //是否应该隐身
    private boolean shouldVanish(Player player) {
        if (!states.containsKey(player)) return false;
        DisguiseState state = states.get(player);
        return state.isVanish() || state.isInBeacon();
    }

    //外部事件区
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (event.isCancelled()) return;

        if (game.isInGame(player) && game.getSeekers().contains(player)) {
            //踩踏检测
            for (Player hider : states.keySet()) {
                DisguiseState state = states.get(hider);
                if (state.isSolid() &&
                        PositionUtils.distanceFlatBlock(player.getLocation(), state.getSolidPos()) == 0) {
                    double offsetY = player.getLocation().getY() - state.getSolidPos().getBlockY();
                    if (offsetY >= 0 && offsetY < 2) {
                        //解除固定状态
                        hider.playSound(hider.getLocation(), Sound.ENTITY_BAT_HURT, 1.0F, 1.0F);
                        unSolid(hider, true, false);
                        break;
                    }
                }
            }
        }

        //固定检测
        if (!states.containsKey(player) || !states.get(player).isDisguised() ||
                PositionUtils.samePosition(event.getTo(), event.getFrom())) return;

        DisguiseState state = states.get(player);
        if (state.isSolid()) {
            if (!state.isSolid(event.getTo())) unSolid(player, true, false);
        } else {
            if (state.isAllowDisguise() && !shouldVanish(player))
                SolidRunnable.start(this, event.getTo(), player);
        }
    }

    //外部函数区
    public Game getGame() {
        return game;
    }

}
