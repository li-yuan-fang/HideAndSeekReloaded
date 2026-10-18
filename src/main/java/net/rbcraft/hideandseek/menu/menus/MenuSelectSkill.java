package net.rbcraft.hideandseek.menu.menus;

import net.rbcraft.hideandseek.HideAndSeek;
import net.rbcraft.hideandseek.game.skill.PlayerSkill;
import net.rbcraft.hideandseek.game.skill.SkillRole;
import net.rbcraft.hideandseek.game.skill.SkillType;
import net.rbcraft.hideandseek.game.skill.Skills;
import net.rbcraft.hideandseek.io.I18n;
import net.rbcraft.hideandseek.menu.Menu;
import net.rbcraft.hideandseek.reflect.NBTHelper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MenuSelectSkill extends Menu {
    private static final int hiderBase = 2;

    private static final int seekerBase = 6;

    private static final int seekerAirdrop = 8;

    public MenuSelectSkill() {
        super("select_skill", 4);
    }

    @Override
    protected void addItems(Inventory inv, Player player, Object... param) {
        //标题行
        inv.setItem(4, createSkull(player));
        inv.setItem(0, getItem(Material.BEACON, I18n.formats("menu_hider_select_skill")));
        inv.setItem(8, getItem(Material.IRON_SWORD, I18n.formats("menu_seeker_select_skill")));

        //技能选择
        PlayerSkill skill = getPlayerSkill(player);

        //游侠技能
        addSkills(inv, 0, 0, SkillType.Q, SkillRole.Hider);
        addSkills(inv, 1, 0, SkillType.E, SkillRole.Hider);
        addSkills(inv, hiderBase, skill.hiderBase, SkillType.N, SkillRole.Hider);

        //猎手技能
        addSkills(inv, seekerBase, skill.seekerBase, SkillType.N, SkillRole.Seeker);
        addSkills(inv, 7, 0, SkillType.E, SkillRole.Seeker);
        addSkills(inv, seekerAirdrop, skill.seekerAirDrop, SkillType.Q, SkillRole.Seeker);

    }

    @Override
    protected void onClose(InventoryCloseEvent event) {
    }

    @Override
    protected void onClick(InventoryClickEvent event) {
        if (event.getRawSlot() < 9) return;

        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) return;

        //获取玩家对象
        Player player = getPlayer(event.getInventory());
        if (player == null) return;

        int id = event.getRawSlot() % 9;
        int value = event.getRawSlot() / 9 - 1;
        if (value < 0) return;

        //处理修改
        switch (id) {
            case hiderBase:
                getPlayerSkill(player).hiderBase = value;
                break;
            case seekerBase:
                getPlayerSkill(player).seekerBase = value;
                break;
            case seekerAirdrop:
                getPlayerSkill(player).seekerAirDrop = value;
                break;
            default:
                return;
        }
        player.closeInventory();
        open(player);
    }

    private PlayerSkill getPlayerSkill(Player player) {
        return HideAndSeek.getInstance().games.skillManager.getSkill(player);
    }

    private static ItemStack createSkull(Player player) {

        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);

        ItemMeta meta = skull.getItemMeta();
        meta.setDisplayName(I18n.formats("menu_skull_select_skill")
                .replace("%player%", player.getName()));
        skull.setItemMeta(meta);

        return NBTHelper.setParam(skull, player.getUniqueId().toString());
    }

    private static Player getPlayer(Inventory inv) {
        if (inv == null) return null;

        ItemStack skull = inv.getItem(4);
        if (skull == null || skull.getType() != Material.PLAYER_HEAD) return null;

        String uuid = NBTHelper.getParam(skull);
        if (uuid == null || uuid.length() == 0) return null;
        return getPlayer(uuid);
    }

    private static Player getPlayer(String uuid) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getUniqueId().toString().equalsIgnoreCase(uuid)) return player;
        }
        return null;
    }

    private static void addSkills(Inventory inv, int id, int value, SkillType type, SkillRole role) {
        if (inv == null) return;
        ItemStack[] items = Skills.getSkillItems(type, role);
        if (items.length > 0) {
            for (int i = 0; i < items.length; i++) {
                if (value == i) {
                    ItemMeta meta = items[i].getItemMeta();
                    meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 10, true);
                    items[i].setItemMeta(meta);
                }
                inv.setItem((i + 1) * 9 + id, items[i]);
            }
        }
    }

    private static ItemStack getItem(Material material, String name) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

}
