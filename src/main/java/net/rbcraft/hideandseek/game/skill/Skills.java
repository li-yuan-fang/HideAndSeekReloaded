package net.rbcraft.hideandseek.game.skill;

import net.rbcraft.hideandseek.game.Game;
import net.rbcraft.hideandseek.game.skill.skills.*;
import net.rbcraft.hideandseek.io.I18n;
import net.rbcraft.hideandseek.reflect.NBTHelper;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.LinkedList;

public enum Skills {
    Direction(new SkillDirection()), //神秘预感
    CastNet(new SkillCastNet()), //猎手投网
    Sensor(new SkillSensor()), //感应光环

    Catch(new SkillCatch()), //捕获

    Lock(new SkillLock()), //禁锢诅咒
    PointAll(new SkillPointAll()), //洞察全局
    PointOneLong(new SkillPointOneLong()), //狩猎直觉

    Beacon(new SkillBeacon()), //匿踪信标
    Vanish(new SkillVanish()), //透明戏法

    Hide(new SkillHide()), //伪装

    RunFast(new SkillRunFast()), //星步急行

    VirtualBeacon(new SkillVirtualBeacon()); //幻形信标

    private final Skill skill;

    Skills(Skill skill) {
        this.skill = skill;
    }

    public Skill getSkill() {
        return skill;
    }

    public static Skill getSkill(String name) {
        for (Skills skills : Skills.values()) {
            Skill skill = skills.getSkill();
            if (skill.getName().equalsIgnoreCase(name)) return skill;
        }
        return null;
    }

    public static Skill getSavedSkill(SkillType type, SkillRole role, int id) {
        int counter = 0;
        for (Skills skills : Skills.values()) {
            Skill skill = skills.getSkill();
            if (skill.getType() == type && skill.getRole() == role) {
                if (counter == id) return skill;
                counter++;
            }
        }
        return getSavedSkill(type, role, 0);
    }

    public static ItemStack[] getSkillItems(SkillType type, SkillRole role) {
        LinkedList<ItemStack> items = new LinkedList<>();
        for (Skills skills : Skills.values()) {
            Skill skill = skills.getSkill();
            if (skill.getType() == type && skill.getRole() == role) items.add(skill.getItemStack());
        }
        return items.toArray(new ItemStack[0]);
    }

    public static ItemStack getLeaveButton() {
        ItemStack item = new ItemStack(Material.BARRIER);

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(I18n.formats("skill_leave"));
        ArrayList<String> lore = new ArrayList<>();
        lore.add(I18n.formats("skill_leave_desc"));
        meta.setLore(lore);
        item.setItemMeta(meta);

        return NBTHelper.setParam(item, "leave");
    }

    public static boolean isLeaveButton(ItemStack item) {
        if (item == null || item.getType() != Material.BARRIER) return false;

        String param = NBTHelper.getParam(item);
        return param != null && param.equalsIgnoreCase("leave");
    }

    public static void onPlayerUseSkill(PlayerInteractEvent event, Game game) {
        Player player = event.getPlayer();
        if (!game.isInGame(player) || event.getAction() == Action.PHYSICAL) return;

        ItemStack item = event.getItem();
        if (item == null || item.getType() == Material.AIR) return;

        String param = NBTHelper.getParam(item);
        if (param == null || param.length() < 7) return;
        if (param.startsWith("skill_")) {
            Skill skill = getSkill(param.substring(6));
            if (skill != null) skill.onPlayerUse(event, game);
        }
    }

}
