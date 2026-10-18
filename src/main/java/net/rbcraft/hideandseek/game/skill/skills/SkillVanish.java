package net.rbcraft.hideandseek.game.skill.skills;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.rbcraft.hideandseek.game.Game;
import net.rbcraft.hideandseek.game.disguise.DisguiseManager;
import net.rbcraft.hideandseek.game.skill.Skill;
import net.rbcraft.hideandseek.game.skill.SkillRole;
import net.rbcraft.hideandseek.game.skill.SkillType;
import net.rbcraft.hideandseek.io.I18n;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class SkillVanish extends Skill {

    public SkillVanish() {
        super("vanish", SkillType.N, SkillRole.Hider, 60, 1);
    }

    @Override
    protected boolean function(PlayerInteractEvent event, Game game) {
        Player player = event.getPlayer();
        DisguiseManager disguise = game.getDisguiseManager();
        if (disguise.isVanish(player)) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                    new TextComponent(I18n.formats("game_no_repeat")));
            return false;
        }
        disguise.vanish(player, 100);
        return true;
    }

}
