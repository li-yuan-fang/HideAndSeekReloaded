package net.rbcraft.hideandseek.game.skill.skills;

import net.rbcraft.hideandseek.game.Game;
import net.rbcraft.hideandseek.game.beacon.Beacon;
import net.rbcraft.hideandseek.game.beacon.BeaconManager;
import net.rbcraft.hideandseek.game.skill.Skill;
import net.rbcraft.hideandseek.game.skill.SkillRole;
import net.rbcraft.hideandseek.game.skill.SkillType;
import net.rbcraft.hideandseek.io.I18n;
import net.rbcraft.hideandseek.util.PositionUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.LinkedList;

public class SkillCatch extends Skill {

    private static final double radius = 3.0D;

    private static final PotionEffect blind = new PotionEffect(PotionEffectType.BLINDNESS, 100, 3);

    public SkillCatch() {
        super("catch", SkillType.E, SkillRole.Seeker, 5, 3);
    }

    @Override
    protected boolean function(PlayerInteractEvent event, Game game) {
        Player player = event.getPlayer();
        Location location = player.getLocation();
        drawCirculate(location);

        //检测是否捕获到信标
        BeaconManager beacons = game.getBeaconManager();
        Beacon beacon = beacons.getBeaconInverse(location, radius);
        if (beacon != null) {
            beacons.remove(beacon);
            I18n.sendMessage(player, I18n.formats("skill_failed_catch"));
            player.addPotionEffect(blind);
            player.playSound(player.getLocation(), Sound.ENTITY_BAT_HURT, 1.0F, 1.0F);
            return true;
        }

        //检测游侠
        String msg = I18n.formats("skill_success_catch");
        LinkedList<Player> caught = new LinkedList<>();
        for (Player hider : game.getHiders()) {
            if (effected(location, hider.getLocation())) caught.add(hider);
        }
        if (!caught.isEmpty()) {
            for (Player hider : caught) {
                //检测信标保护
                if (beacons.isProtected(hider)) continue;

                //捕获
                I18n.sendMessage(player, msg.replace("%hider%", hider.getName()));
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
                game.seek(hider);
            }
            caught.clear();
        }

        return true;
    }

    //检测目标是否在范围内
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
