package net.rbcraft.hideandseek.game.util;

import net.rbcraft.hideandseek.game.Game;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

public abstract class Lock {

    protected final Player locked;

    protected final Location location;

    protected final Game game;

    protected final int lockTime;

    private int time;

    protected Lock(Player player, Game game, int lockTime) {
        this.locked = player;
        this.location = player.getLocation().clone();
        this.time = 0;
        this.game = game;
        this.lockTime = lockTime;
    }

    public boolean next() {
        time++;
        boolean complete = time >= lockTime || isStop();
        if (!complete) {
            locked.teleport(location);
            if (time % 20 == 0) drawCage(locked);
        }

        return complete;
    }

    private static void dealLock(Game game, Lock lock) {
        if (game == null || lock == null || !game.isGaming() || lock.next()) return;
        start(game, lock);
    }

    public static void start(Game game, Lock lock) {
        game.getManager().coreCounter.start(1, () -> dealLock(game, lock));
    }

    protected abstract boolean isStop();

    private static void drawCage(Player player) {
        Location location = player.getLocation();
        double flat = location.getBlockY();
        drawSquare(location, flat, 1.0D);
        drawSquare(location, flat + 2.0D, 1.0D);

        World world = location.getWorld();
        if (world == null) return;
        double x = location.getX();
        double z = location.getZ();
        for (double y = flat + 0.25D; y < flat + 2.0D; y += 0.25D) {
            world.spawnParticle(Particle.FLAME, x - 0.5D, y, z - 0.5D, 1,
                    0, 0, 0, 0);
            world.spawnParticle(Particle.FLAME, x + 0.5D, y, z - 0.5D, 1,
                    0, 0, 0, 0);
            world.spawnParticle(Particle.FLAME, x + 0.5D, y, z + 0.5D, 1,
                    0, 0, 0, 0);
            world.spawnParticle(Particle.FLAME, x - 0.5D, y, z + 0.5D, 1,
                    0, 0, 0, 0);
        }
    }

    private static void drawSquare(Location center, double y, double size) {
        if (center == null) return;

        World world = center.getWorld();
        if (world == null) return;

        double offset = size / 2.0D;
        for (double x = center.getX() - offset; x <= center.getX() + offset; x += 0.25D) {
            world.spawnParticle(Particle.FLAME, x, y, center.getZ() + offset, 1,
                    0, 0, 0, 0);
            world.spawnParticle(Particle.FLAME, x, y, center.getZ() - offset, 1,
                    0, 0, 0, 0);
        }
        for (double z = center.getZ() - offset; z <= center.getZ() + offset; z += 0.25D) {
            world.spawnParticle(Particle.FLAME, center.getX() + offset, y, z, 1,
                    0, 0, 0, 0);
            world.spawnParticle(Particle.FLAME, center.getX() - offset, y, z, 1,
                    0, 0, 0, 0);
        }
    }

}
