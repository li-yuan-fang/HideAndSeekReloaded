package net.rbcraft.hideandseek.menu;

import net.rbcraft.hideandseek.menu.menus.MenuEditBlocks;
import net.rbcraft.hideandseek.menu.menus.MenuSelectSkill;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public enum Menus implements Listener {
    SelectSkill(new MenuSelectSkill()),
    EditBlocks(new MenuEditBlocks());

    private final Menu menu;

    Menus(Menu menu) {
        this.menu = menu;
    }

    public Menu getMenu() {
        return menu;
    }

    public void openMenu(Player player, Object... param) {
        getMenu().open(player, param);
    }

    public void register() {
        getMenu().register();
    }

    public static void registerAll() {
        for (Menus m : Menus.values()) {
            m.register();
        }
    }

}
