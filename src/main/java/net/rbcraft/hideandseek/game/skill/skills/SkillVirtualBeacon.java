package net.rbcraft.hideandseek.game.skill.skills;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.rbcraft.hideandseek.game.Game;
import net.rbcraft.hideandseek.game.skill.Skill;
import net.rbcraft.hideandseek.game.skill.SkillRole;
import net.rbcraft.hideandseek.game.skill.SkillType;
import net.rbcraft.hideandseek.io.I18n;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class SkillVirtualBeacon extends Skill {

    public SkillVirtualBeacon() {
        super("virtual_beacon", SkillType.N, SkillRole.Spectator, 20, 2);
    }

    @Override
    protected boolean function(PlayerInteractEvent event, Game game) {
        Player player = event.getPlayer();
        if (!game.getBeaconManager().spawnBeacon(player)) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                    new TextComponent(I18n.formats("game_failed_beacon")));
            return false;
        }
        return true;
    }

}
