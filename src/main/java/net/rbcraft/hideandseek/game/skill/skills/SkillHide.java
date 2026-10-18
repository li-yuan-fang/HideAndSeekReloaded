package net.rbcraft.hideandseek.game.skill.skills;

import net.rbcraft.hideandseek.game.Game;
import net.rbcraft.hideandseek.game.skill.Skill;
import net.rbcraft.hideandseek.game.skill.SkillRole;
import net.rbcraft.hideandseek.game.skill.SkillType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class SkillHide extends Skill {

    public SkillHide() {
        super("hide", SkillType.E, SkillRole.Hider, 0.3, 1);
    }

    @Override
    protected boolean function(PlayerInteractEvent event, Game game) {
        Player player = event.getPlayer();
        switch (event.getAction()) {
            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:
                game.getDisguiseManager().unDisguise(player);
                break;
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                if (!game.getDisguiseManager().disguise(player)) return false;
                break;
            case PHYSICAL:
                return false;
        }
        return true;
    }

}
