package net.rbcraft.hideandseek.game.skill.skills;

import net.rbcraft.hideandseek.game.Game;
import net.rbcraft.hideandseek.game.skill.Skill;
import net.rbcraft.hideandseek.game.skill.SkillRole;
import net.rbcraft.hideandseek.game.skill.SkillType;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class SkillDirection extends Skill {

    private static final double radius = 1.5D;

    public SkillDirection() {
        super("direction", SkillType.N, SkillRole.Seeker, 40, 1);
    }

    @Override
    protected boolean function(PlayerInteractEvent event, Game game) {
        Player player = event.getPlayer();
        for (Player hider : game.getHiders()) {
            showTrack(player, hider);
        }
        return true;
    }

    private static void showTrack(Player center, Player dest) {
        Location c = center.getLocation();
        Location d = dest.getLocation();

        double dX = d.getBlockX() - c.getBlockX();
        double dZ = d.getBlockZ() - c.getBlockZ();

        if (dX == 0 && dZ == 0) return;
        if (dX == 0 || dZ == 0) {
            drawStraight(center, dX != 0, Math.max(dX, dZ) > 0);
            return;
        }

        drawOblique(center, dX, dZ);
    }

    private static void drawStraight(Player player, boolean x, boolean positive) {
        Location loc = player.getLocation();
        for (double i = 0; i < radius; i += 0.1D) {
            player.spawnParticle(Particle.FLAME,
                    new Location(player.getWorld(),
                            loc.getBlockX() + (x ? (positive ? i : -i) : 0),
                            loc.getBlockY() + 1.0D,
                            loc.getBlockZ() + (x ? 0 : (positive ? i : -i))),
                    2, 0 , 0, 0, 0);
        }
    }

    private static void drawOblique(Player player, double x, double z) {
        double k = Math.abs(z / x);
        Location loc = player.getLocation();
        for (double i = 0; i < radius; i += 0.1D) {
            player.spawnParticle(Particle.FLAME,
                    new Location(player.getWorld(),
                            loc.getBlockX() + (x > 0 ? i : -i),
                            loc.getBlockY() + 1.0D,
                            loc.getBlockZ() + (z > 0 ? i : -i) * k),
                    2, 0 , 0, 0, 0);
        }
    }

}
