package net.rbcraft.hideandseek.game.disguise;

import net.rbcraft.hideandseek.game.Game;
import net.rbcraft.hideandseek.util.PositionUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SolidRunnable implements Runnable {

    private final DisguiseManager manager;

    private final Location location;

    private final Player player;

    private SolidRunnable(DisguiseManager manager, Location location, Player player) {
        this.manager = manager;
        this.location = location.clone();
        this.player = player;
        manager.getGame().getManager().coreCounter.start(60, this);
    }

    public static void start(DisguiseManager manager, Location location, Player player) {
        if (manager == null || location == null || player == null) return;
        if (!DisguiseUtils.canSolid(location) || !manager.isAllowDisguise(player) || !manager.isDisguised(player)) return;

        new SolidRunnable(manager, location, player);
    }

    @Override
    public void run() {
        Game game = manager.getGame();
        if (!game.isGaming() || !game.isInGame(player) || !manager.isDisguised(player) ||
                !manager.isAllowDisguise(player) || manager.isSolid(player)) return;
        if (PositionUtils.sameBlockPosition(location, player.getLocation())) manager.solid(player, true);
    }

}
