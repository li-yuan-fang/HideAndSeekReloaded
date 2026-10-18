package net.rbcraft.hideandseek.game.skill.skills;

import net.rbcraft.hideandseek.game.Game;
import net.rbcraft.hideandseek.game.skill.Skill;
import net.rbcraft.hideandseek.game.skill.SkillRole;
import net.rbcraft.hideandseek.game.skill.SkillType;
import net.rbcraft.hideandseek.game.util.Net;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerInteractEvent;

public class SkillCastNet extends Skill {

    private static final float radius = 5.0f;

    public SkillCastNet() {
        super("cast_net", SkillType.N, SkillRole.Seeker, 10, 2);
    }

    @Override
    protected boolean function(PlayerInteractEvent event, Game game) {
        start(game, new Net(event.getPlayer(), game, radius));
        return true;
    }

    private void dealNet(Game game, Net net) {
        if (game == null || net == null || !game.isGaming()) return;
        Location end = net.getEndPoint();
        if (end != null) {
            net.releaseNet(end);
            return;
        }
        start(game, net);
    }

    private void start(Game game, Net net) {
        game.getManager().coreCounter.start(1, () -> dealNet(game, net));
    }

}
