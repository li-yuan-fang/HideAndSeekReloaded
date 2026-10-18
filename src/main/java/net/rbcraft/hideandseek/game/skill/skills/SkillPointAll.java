package net.rbcraft.hideandseek.game.skill.skills;

import net.rbcraft.hideandseek.game.Game;
import net.rbcraft.hideandseek.game.disguise.DisguiseManager;
import net.rbcraft.hideandseek.game.skill.Skill;
import net.rbcraft.hideandseek.game.skill.SkillRole;
import net.rbcraft.hideandseek.game.skill.SkillType;
import net.rbcraft.hideandseek.io.I18n;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SkillPointAll extends Skill {

    private static final PotionEffect glow = new PotionEffect(PotionEffectType.GLOWING, 400, 2,
            false, false);

    public SkillPointAll() {
        super("point_all", SkillType.Q, SkillRole.Seeker, 0, 1);
    }

    @Override
    protected boolean function(PlayerInteractEvent event, Game game) {
        DisguiseManager disguise = game.getDisguiseManager();
        for (Player hider : game.getHiders()) {
            disguise.forbidDisguise(hider, 400);
            hider.addPotionEffect(glow);
        }
        game.broadcast(I18n.formats("skill_broadcast_point_all"));
        return true;
    }

}
