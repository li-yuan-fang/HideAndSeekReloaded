package net.rbcraft.hideandseek.menu;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class MenuInventoryHolder implements InventoryHolder {

    private final Menu menu;

    public MenuInventoryHolder(Menu menu) {
        this.menu = menu;
    }

    public boolean caught(Menu menu) {
        return menu.getName().equalsIgnoreCase(this.menu.getName());
    }

    @Override
    public Inventory getInventory() {
        return Bukkit.createInventory(this, menu.getSize(), menu.getTitle());
    }

}
