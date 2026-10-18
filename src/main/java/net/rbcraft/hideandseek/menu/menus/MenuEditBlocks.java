package net.rbcraft.hideandseek.menu.menus;

import net.rbcraft.hideandseek.HideAndSeek;
import net.rbcraft.hideandseek.game.GameData;
import net.rbcraft.hideandseek.game.GameManager;
import net.rbcraft.hideandseek.io.I18n;
import net.rbcraft.hideandseek.menu.Menu;
import net.rbcraft.hideandseek.reflect.NBTHelper;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MenuEditBlocks extends Menu {

    public MenuEditBlocks() {
        super("edit_blocks", 4);
    }

    @Override
    protected void addItems(Inventory inv, Player player, Object... param) {
        if (param == null || param.length == 0) return;

        String name = (String) param[0];
        inv.setItem(0, createGameFlag(name));

        GameManager games = HideAndSeek.getInstance().games;
        if (!games.contains(name)) {
            I18n.sendMessage(player, I18n.formats("unknown_game"));
            return;
        }

        GameData data = games.getGame(name).getData();
        if (data.items != null) inv.addItem(data.items);
    }

    @Override
    protected void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory inv = event.getInventory();

        String name = NBTHelper.getParam(inv.getItem(0));
        if (name == null || name.length() == 0) return;

        GameManager games = HideAndSeek.getInstance().games;
        if (!games.contains(name)) {
            I18n.sendMessage(player, I18n.formats("unknown_game"));
            return;
        }

        GameData data = games.getGame(name).getData();
        LinkedList<ItemStack> items = new LinkedList<>();
        for (int i = 1; i < inv.getSize(); i++) {
            ItemStack item = inv.getContents()[i];
            if (item == null || item.getType() == Material.AIR || !item.getType().isBlock()) continue;
            items.add(item);
        }
        data.items = items.toArray(new ItemStack[0]);
        I18n.sendMessage(player, I18n.formats("menu_tip_save_edit_blocks"));
    }

    @Override
    protected void onClick(InventoryClickEvent event) {
        if (event.getRawSlot() != 0) event.setCancelled(false);
    }

    private static ItemStack createGameFlag(String name) {
        ItemStack item = new ItemStack(Material.BEACON, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(I18n.formats("menu_flag_title_edit_blocks").replace("%game%", name));
        meta.setLore(getDesc(I18n.formats("menu_flag_desc_edit_blocks")));
        item.setItemMeta(meta);

        return NBTHelper.setParam(item, name);
    }

    private static List<String> getDesc(String src) {
        ArrayList<String> desc = new ArrayList<>();
        if (src != null && src.length() > 0) {
            if (src.contains("\\n")) {
                desc.addAll(Arrays.asList(src.split("\\\\n")));
            } else {
                desc.add(src);
            }
        }
        return desc;
    }

}
