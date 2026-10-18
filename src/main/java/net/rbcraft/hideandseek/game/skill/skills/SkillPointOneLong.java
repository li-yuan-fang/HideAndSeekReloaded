package net.rbcraft.hideandseek.game.skill.skills;

import net.rbcraft.hideandseek.game.Game;
import net.rbcraft.hideandseek.game.skill.Skill;
import net.rbcraft.hideandseek.game.skill.SkillRole;
import net.rbcraft.hideandseek.game.skill.SkillType;
import net.rbcraft.hideandseek.io.I18n;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class SkillPointOneLong extends Skill {

    private static final PotionEffect glow = new PotionEffect(PotionEffectType.GLOWING, 1200, 2,
            false, false);

    private final Random rand = new Random();

    public SkillPointOneLong() {
        super("point_one_long", SkillType.Q, SkillRole.Seeker, 0, 1);
    }

    @Override
    protected boolean function(PlayerInteractEvent event, Game game) {
        Player lucky = game.getHiders().get(rand.nextInt(game.getHiders().size()));
        lucky.addPotionEffect(glow);
        game.getDisguiseManager().forbidDisguise(lucky, 1200);

        game.broadcast(I18n.formats("skill_broadcast_point_one_long")
                .replace("%hider%", lucky.getName()));
        return true;
    }

}
