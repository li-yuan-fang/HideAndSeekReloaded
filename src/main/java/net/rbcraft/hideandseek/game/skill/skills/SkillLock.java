package net.rbcraft.hideandseek.game.skill.skills;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.rbcraft.hideandseek.game.Game;
import net.rbcraft.hideandseek.game.skill.Skill;
import net.rbcraft.hideandseek.game.skill.SkillRole;
import net.rbcraft.hideandseek.game.skill.SkillType;
import net.rbcraft.hideandseek.game.util.Lock;
import net.rbcraft.hideandseek.io.I18n;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.LinkedList;
import java.util.Random;

public class SkillLock extends Skill {

    private static final int lockTime = 800;

    private final Random rand = new Random();

    public SkillLock() {
        super("lock", SkillType.Q, SkillRole.Seeker, 0, 1);
    }

    @Override
    protected boolean function(PlayerInteractEvent event, Game game) {
        LinkedList<Player> hiders = game.getHiders();
        Player lucky = hiders.get(rand.nextInt(hiders.size()));
        game.getDisguiseManager().unDisguise(lucky);
        Lock.start(game, new LockForSkill(lucky, game));

        String broadcast = I18n.formats("skill_broadcast_lock").replace("%hider%", lucky.getName());
        game.broadcast(broadcast);
        lucky.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                new TextComponent(I18n.formats("skill_message_lock")));
        return true;
    }

    private static class LockForSkill extends Lock {

        public LockForSkill(Player player, Game game) {
            super(player, game, SkillLock.lockTime);
        }

        @Override
        protected boolean isStop() {
            return !game.isGaming() || !game.getHiders().contains(locked);
        }
    }

}
