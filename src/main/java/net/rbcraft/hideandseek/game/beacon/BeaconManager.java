package net.rbcraft.hideandseek.game.beacon;

import net.rbcraft.hideandseek.game.Game;
import net.rbcraft.hideandseek.game.disguise.DisguiseManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class BeaconManager {

    private final LinkedList<Beacon> beacons = new LinkedList<>();

    private final Random rand;

    private final Game game;

    private long tick;

    public BeaconManager(Game game) {
        this.game = game;
        this.rand = new Random();
        this.tick = 0;
    }

    public void onTick() {
        if (tick > 20) {
            drawBeaconParticles(game.getHiders());
            drawBeaconParticles(game.getSpectators());
            tick = 0;
        }
        tick++;
    }

    public void drawBeaconParticles(List<Player> players) {
        if (players == null || players.isEmpty()) return;
        for (Beacon beacon : beacons) {
            for (Player player : players) {
                beacon.render(player);
            }
        }
    }

    public void remove(Beacon beacon) {
        if (beacon == null || !beacons.contains(beacon)) return;
        beacon.remove();
        beacons.remove(beacon);

        //刷新保护状态
        for (Player hider : game.getHiders()) {
            checkBeaconProtect(hider);
        }
    }

    public void clear() {
        for (Beacon beacon : beacons) {
            beacon.remove();
        }
        beacons.clear();
    }

    public Beacon getBeacon(Location location) {
        if (location == null) return null;
        for (Beacon beacon : beacons) {
            if (beacon.isEffected(location)) return beacon;
        }
        return null;
    }

    public Beacon getBeaconInverse(Location location, double distance) {
        if (location == null || distance < 0) return null;
        for (Beacon beacon : beacons) {
            if (beacon.isEffectedInverse(location, distance)) return beacon;
        }
        return null;
    }

    public boolean seekBeacon(Location location, double distance) {
        return getBeaconInverse(location, distance) != null;
    }

    public boolean spawnBeacon(Player player) {
        if (player == null) return false;
        Location location = player.getLocation();
        if (!isOnGround(location)) return false;

        location = findAvailableLocation(player.getLocation());
        if (location == null) return false;

        ItemStack lucky = game.getData().items[rand.nextInt(game.getData().items.length)];
        Beacon beacon = new Beacon(location, lucky.getType());
        beacons.add(beacon);

        for (Player hider : game.getHiders()) {
            checkBeaconProtect(hider);
        }

        return true;
    }

    private static Location findAvailableLocation(Location location) {
        if (location == null) return null;
        if (isAir(location)) return location;

        World world = location.getWorld();
        for (int x = -1; x < 2; x++) {
            for (int z = -1; z < 2; z++) {
                if (x == 0 && z == 0) continue;
                Location loc = new Location(world, location.getBlockX() + x, 0, location.getBlockZ() + z);
                if (isAir(loc)) return loc;
            }
        }
        return null;
    }

    private static boolean isAir(Location location) {
        if (location == null) return false;
        return location.getWorld().getBlockAt(location).getType() == Material.AIR;
    }

    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.isCancelled()) return;
        checkBeaconProtect(event.getPlayer());
    }

    public boolean isProtected(Player player) {
        for (Beacon beacon : beacons) {
            if (beacon.isEffected(player.getLocation())) return true;
        }
        return false;
    }

    private void checkBeaconProtect(Player player) {
        if (!game.getHiders().contains(player)) return;
        Beacon beacon = getBeacon(player.getLocation());
        DisguiseManager disguise = game.getDisguiseManager();
        if (disguise.isInBeacon(player)) {
            if (beacon == null) disguise.leaveBeacon(player);
        } else {
            if (beacon != null) disguise.joinBeacon(player);
        }
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
