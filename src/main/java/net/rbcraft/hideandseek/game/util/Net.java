package net.rbcraft.hideandseek.game.util;

import net.rbcraft.hideandseek.game.Game;
import net.rbcraft.hideandseek.io.I18n;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.HashSet;

public class Net {

    private static final double g = 0.03D;

    private static final double f = 0.02D;

    private static final double speed = 0.3D;

    private final float direction;

    private final double k;

    private final Location start;

    private final Player seeker;

    private final Game game;

    private double speed_vertical, speed_horizontal;

    private double distance, high;

    private final double radius;

    public Net(Player player, Game game, double radius) {
        Location eye = player.getEyeLocation();
        double pitch = Math.toRadians(eye.getPitch());

        this.seeker = player;
        this.speed_vertical = Math.sin(-pitch) * speed;
        this.speed_horizontal = Math.cos(-pitch) * speed;
        this.direction = translate(-player.getLocation().getYaw());
        this.k = direction % 90 == 0 ? 0 : Math.tan(Math.toRadians(this.direction));
        this.distance = 0;
        this.high = 0;
        this.start = eye.clone();
        this.game = game;
        this.radius = radius;
    }

    public Location getEndPoint() {
        double a = f * speed_horizontal;
        distance += speed_horizontal - 0.5D * a;
        speed_horizontal -= a;

        high += speed_vertical - 0.5D * g;
        speed_vertical -= g;

        double x, z;

        if (k != 0) {
            //三维坐标系中x,z对应到平面坐标系x,y
            z = Math.sqrt(distance * distance / (1 + k * k));
            if (direction > 90 && direction < 270) z = -z;
            x = k * z;
        } else {
            if (direction % 180 == 0) {
                //z轴方向
                x = 0;
                z = distance;
                if (direction == 180) z = -z;
            } else {
                //x轴方向
                z = 0;
                x = distance;
                if (direction == 270) x = -x;
            }
        }

        Location location = start.clone().add(x, high, z);
        playParticle(location);

        boolean end = location.getWorld().getBlockAt(location).getType().isSolid();
        if (!end && !game.getData().isInArea(location)) end = true;

        return end ? location : null;
    }

    public void releaseNet(Location endPoint) {
        if (endPoint == null) return;
        endPoint.getWorld().spawnParticle(Particle.FLAME, endPoint, 240, 0.2D, 0.2D, 0.2D, 0.2D);

        //搜索游侠
        HashSet<Player> sought = new HashSet<>();
        for (Player player : game.getHiders()) {
            if (player.getLocation().distance(endPoint) < radius) sought.add(player);
        }
        if (!sought.isEmpty()) {
            for (Player player : sought) {
                game.getDisguiseManager().forbidDisguise(player, 100);
            }
            sought.clear();
            I18n.sendMessage(seeker, I18n.formats("skill_sensed_cast_net"));
        }
    }

    private static float translate(float angle) {
        angle %= 360;
        if (angle < 0) angle += 360;
        return angle;
    }

    private static void playParticle(Location location) {
        if (location == null) return;
        location.getWorld().spawnParticle(Particle.FLAME, location, 2, 0, 0, 0, 0);
    }

}
