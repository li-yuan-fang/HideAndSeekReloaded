package net.rbcraft.hideandseek.game.beacon;

import net.rbcraft.hideandseek.util.PositionUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Beacon {

    private static final double radius = 5.0D;

    private final Block block;

    public Beacon(Location location, Material material) {
        this.block = getBlock(location);
        this.block.setType(material, false);
    }

    public void remove() {
        this.block.setType(Material.AIR);
    }

    public Location getLocation() {
        return block.getLocation();
    }

    public boolean isEffected(Location location) {
        return isEffected(getLocation(), location, radius);
    }

    public boolean isEffectedInverse(Location location, double distance) {
        return isEffected(location, getLocation(), distance);
    }

    private static boolean isEffected(Location central, Location dest, double distance) {
        if (dest == null || !dest.getWorld().equals(central.getWorld())) return false;
        if (PositionUtils.distanceFlat(dest, central) < distance) {
            double offsetY = dest.getY() - central.getBlockY();
            return offsetY >= -1 && offsetY < 3;
        } else {
            return false;
        }
    }

    public void render(Player player) {
        if (player == null || !player.getWorld().equals(getLocation().getWorld())) return;
        if (PositionUtils.distanceFlat(getLocation(), player.getEyeLocation()) < 32) {
            for (int i = 0; i < 360; i++) {
                double r = Math.toRadians(i);
                double x = Math.sin(r) * radius;
                double z = Math.cos(r) * radius;
                player.spawnParticle(Particle.TOTEM, getLocation().getBlockX() + x + 0.5D,
                        getLocation().getBlockY() + 1.0D,
                        getLocation().getBlockZ() + z + 0.5D,
                        2, 0, 0, 0, 0);
            }
        }
    }

    private static Block getBlock(Location location) {
        return location == null ? null : location.getBlock();
    }

}
