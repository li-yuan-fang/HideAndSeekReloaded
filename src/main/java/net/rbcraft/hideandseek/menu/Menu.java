package net.rbcraft.hideandseek.menu;

import net.rbcraft.hideandseek.HideAndSeek;
import net.rbcraft.hideandseek.io.I18n;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public abstract class Menu implements Listener {

    protected final String name;

    protected final int size;

    protected final MenuInventoryHolder holder;

    protected Menu(String name, int line) {
        this.name = name;
        this.size = line * 9;
        this.holder = new MenuInventoryHolder(this);
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return I18n.formats("menu_title_" + getName());
    }

    public int getSize() {
        return this.size;
    }

    public void open(Player player, Object... param) {
        Inventory inv = holder.getInventory();
        addItems(inv, player, param);
        player.openInventory(inv);
    }

    public void register() {
        Bukkit.getPluginManager().registerEvents(this, HideAndSeek.getInstance());
    }

    protected abstract void addItems(Inventory inv, Player player, Object... param);

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inv = event.getInventory();
        if (inv == null || inv.getSize() != size) return;

        if (inv.getHolder() instanceof MenuInventoryHolder && ((MenuInventoryHolder) inv.getHolder()).caught(this)) {
            onClose(event);
        }
    }

    protected abstract void onClose(InventoryCloseEvent event);

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        if (inv == null || inv.getSize() != size) return;


        if (inv.getHolder() instanceof MenuInventoryHolder && ((MenuInventoryHolder) inv.getHolder()).caught(this)) {
            event.setCancelled(true);
            onClick(event);
        }
    }

    protected abstract void onClick(InventoryClickEvent event);

}
