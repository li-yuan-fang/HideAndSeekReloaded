package net.rbcraft.hideandseek.util;

import org.bukkit.Location;

public class PositionUtils {

    public static boolean sameBlockPosition(Location loc1, Location loc2) {
        if (loc1 == null || loc2 == null) return false;
        return loc1.getBlockX() == loc2.getBlockX() && loc1.getBlockY() == loc2.getBlockY() &&
                loc1.getBlockZ() == loc2.getBlockZ();
    }

    public static boolean samePosition(Location loc1, Location loc2) {
        if (loc1 == null || loc2 == null) return false;
        return loc1.getX() == loc2.getX() && loc1.getY() == loc2.getY() && loc1.getZ() == loc2.getZ();
    }

    public static double distanceFlat(Location loc1, Location loc2) {
        if (loc1 == null || loc2 == null || !loc1.getWorld().equals(loc2.getWorld())) return -1;
        double offsetX = Math.abs(loc1.getX() - loc2.getX());
        double offsetZ = Math.abs(loc1.getZ() - loc2.getZ());
        return Math.sqrt(offsetX * offsetX + offsetZ * offsetZ);
    }

    public static double distanceFlatBlock(Location loc1, Location loc2) {
        if (loc1 == null || loc2 == null || !loc1.getWorld().equals(loc2.getWorld())) return -1;
        double offsetX = Math.abs(loc1.getBlockX() - loc2.getBlockX());
        double offsetZ = Math.abs(loc1.getBlockZ() - loc2.getBlockZ());
        return Math.sqrt(offsetX * offsetX + offsetZ * offsetZ);
    }

}
