package net.rbcraft.hideandseek.game.stamina;

import net.rbcraft.hideandseek.game.Game;
import net.rbcraft.hideandseek.io.IOManager;
import net.rbcraft.hideandseek.util.PositionUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.LinkedHashMap;

public class StaminaManager {

    private final LinkedHashMap<Player, StaminaState> states = new LinkedHashMap<>();

    private final Game game;

    public StaminaManager(Game game) {
        this.game = game;
    }

    public void updatePlayers() {
        clear();
        for (Player hider : game.getHiders()) {
            join(hider);
        }
        for (Player seeker : game.getSeekers()) {
            join(seeker);
        }
    }

    public void join(Player player) {
        states.put(player, new StaminaState(player, this));
    }

    public void quit(Player player) {
        quit(player, true);
    }

    private void quit(Player player, boolean remove) {
        if (!states.containsKey(player)) return;
        states.get(player).fullFoodLevel();
        if (remove) states.remove(player);
    }

    public boolean contains(Player player) {
        return states.containsKey(player);
    }

    public void clear() {
        if (states.isEmpty()) return;
        for (Player player : states.keySet()) {
            quit(player, false);
        }
        states.clear();
    }

    public Game getGame() {
        return game;
    }

    public void onTick() {
        for (Player player : states.keySet()) {
            StaminaState state = states.get(player);
            if (!player.isSprinting() && !state.isTired()) state.addStamina();
            if (player.isSneaking()) state.dash();
        }
    }

    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!game.isGaming() || !game.isInGame(player) || !states.containsKey(player)) return;

        if (!event.isCancelled() && player.isSprinting()) {
            double distance = PositionUtils.distanceFlat(event.getFrom(), event.getTo());
            if (game.getSeekers().contains(player) && game.isLastTime())
                distance *= IOManager.getConfigDouble("stamina_use_last");
            states.get(player).useStamina((int) distance);
        }
    }

}
