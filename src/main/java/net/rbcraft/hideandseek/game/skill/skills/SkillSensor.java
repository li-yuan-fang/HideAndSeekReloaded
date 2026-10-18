package net.rbcraft.hideandseek.game.skill.skills;

import net.rbcraft.hideandseek.game.Game;
import net.rbcraft.hideandseek.game.skill.Skill;
import net.rbcraft.hideandseek.game.skill.SkillRole;
import net.rbcraft.hideandseek.game.skill.SkillType;
import net.rbcraft.hideandseek.io.I18n;
import net.rbcraft.hideandseek.util.PositionUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class SkillSensor extends Skill {

    private static final double radius = 5.0D;

    public SkillSensor() {
        super("sensor", SkillType.N, SkillRole.Seeker, 4, 3);
    }

    @Override
    protected boolean function(PlayerInteractEvent event, Game game) {
        Player player = event.getPlayer();
        drawCirculate(player.getLocation());

        //查找信标
        boolean flag = game.getBeaconManager().seekBeacon(player.getLocation(), radius);

        //查找游侠
        if (!flag) {
            World world = player.getWorld();
            double y = player.getLocation().getBlockY() + 1.0D;
            for (Player hider : game.getHiders()) {
                if (effected(player.getLocation(), hider.getLocation())) {
                    Location location = hider.getLocation();
                    spawnFlame(world, location.getX(), y, location.getZ());
                    flag = true;
                }
            }
        }

        if (flag) I18n.sendMessage(player, I18n.formats("skill_sensed_sensor"));
        return true;
    }

    private static boolean effected(Location central, Location dest) {
        if (central == null || dest == null || central.getWorld() != dest.getWorld() ||
                PositionUtils.distanceFlat(central, dest) > radius) return false;
        double offsetY = dest.getY() - central.getY();
        return offsetY >= -1 && offsetY <= radius;
    }

    private static void drawCirculate(Location location) {
        if (location == null) return;
        World world = location.getWorld();
        double y = location.getBlockY() + 1.0D;
        for (int i = 0; i < 360; i++) {
            double r = Math.toRadians(i);
            double x = Math.sin(r) * radius;
            double z = Math.cos(r) * radius;
            spawnFlame(world, location.getX() + x, y, location.getZ() + z);
        }
    }

    private static void spawnFlame(World world, double x, double y, double z) {
        world.spawnParticle(Particle.FLAME, x, y, z, 2, 0, 0, 0, 0);
    }

}
