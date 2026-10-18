package net.rbcraft.hideandseek.game.skill.skills;

import net.rbcraft.hideandseek.game.Game;
import net.rbcraft.hideandseek.game.skill.Skill;
import net.rbcraft.hideandseek.game.skill.SkillRole;
import net.rbcraft.hideandseek.game.skill.SkillType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SkillRunFast extends Skill {

    private static final PotionEffect star_speed = new PotionEffect(PotionEffectType.SPEED, 600, 1,
            false, false);

    public SkillRunFast() {
        super("run_fast", SkillType.Q, SkillRole.Hider, 0, 1);
    }

    @Override
    protected boolean function(PlayerInteractEvent event, Game game) {
        Player player = event.getPlayer();
        player.addPotionEffect(star_speed, true);
        return true;
    }

}
