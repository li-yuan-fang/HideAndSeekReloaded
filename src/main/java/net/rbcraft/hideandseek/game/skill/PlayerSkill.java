package net.rbcraft.hideandseek.game.skill;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerSkill {

    private final Player player;

    public int hiderBase = 0, seekerBase = 0, seekerAirDrop = 0;

    public PlayerSkill(Player player) {
        this.player = player;
    }

    public void giveHiderSkill() {
        player.getInventory().clear();
        player.getInventory().addItem(
                Skills.getSavedSkill(SkillType.N, SkillRole.Hider, hiderBase).getItemStack(),
                Skills.getSavedSkill(SkillType.E, SkillRole.Hider, 0).getItemStack(),
                Skills.getLeaveButton()
        );
    }

    public void giveSeekerSkill() {
        player.getInventory().clear();
        player.getInventory().addItem(
                Skills.getSavedSkill(SkillType.N, SkillRole.Seeker, seekerBase).getItemStack(),
                Skills.getSavedSkill(SkillType.E, SkillRole.Seeker, 0).getItemStack(),
                Skills.getLeaveButton()
        );
    }

    public void giveSpectatorSkill() {
        player.getInventory().clear();
        player.getInventory().addItem(
                Skills.getSavedSkill(SkillType.N, SkillRole.Spectator, 0).getItemStack(),
                Skills.getLeaveButton()
        );
    }

    public void giveAirDrop(boolean hider) {
        ItemStack airdrop;
        if (hider) {
            airdrop = Skills.getSavedSkill(SkillType.Q, SkillRole.Hider, 0).getItemStack();
        } else {
            airdrop = Skills.getSavedSkill(SkillType.Q, SkillRole.Seeker, seekerAirDrop).getItemStack();
        }
        player.getInventory().addItem(airdrop);
    }

    public Player getPlayer() {
        return player;
    }

}
