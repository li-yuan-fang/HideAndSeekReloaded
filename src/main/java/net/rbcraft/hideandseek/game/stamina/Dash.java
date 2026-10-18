package net.rbcraft.hideandseek.game.stamina;

import net.rbcraft.hideandseek.reflect.DashHelper;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Dash {

    private static final int dash_time = 4;

    private static final double dash_distance = 4.0D;

    public static void dash(Player player, StaminaManager manager) {
        if (player == null || manager == null || !manager.contains(player) || !isOnGround(player.getLocation())) return;

        double yaw = Math.toRadians(player.getLocation().getYaw());
        double x = -Math.sin(yaw) * dash_distance / dash_time;
        double z = Math.cos(yaw) * dash_distance / dash_time;

        DashHelper.dash(player, x, 0, z);
        manager.getGame().getManager().coreCounter.start(dash_time,
                () -> DashHelper.dash(player, (short) 0, (short) 0, (short) 0));
    }

    private static boolean isOnGround(Location location) {
        if (location == null) return false;
        if (location.getY() - location.getBlockY() < 0.01D) {
            //检测下方方块
            Block step = location.getWorld().getBlockAt(location.getBlockX(), location.getBlockY() - 1, location.getBlockZ());
            return isSolid(step);
        } else {
            //检测当前位置方块
            return isSolid(location.getBlock());
        }
    }

    private static boolean isSolid(Block block) {
        return block != null && block.getType().isSolid();
    }

}
