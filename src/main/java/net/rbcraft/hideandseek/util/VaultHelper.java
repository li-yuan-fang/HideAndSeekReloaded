package net.rbcraft.hideandseek.util;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHelper {

    public static Economy econ;

    public static boolean init() {
        if (econ != null) return true;
        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer()
                .getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            econ = economyProvider.getProvider();
        }

        return (econ != null);
    }

    public static boolean add(Player player, double money) {
        if (econ == null || player == null) return false;
        return (econ.depositPlayer(player, money).type == EconomyResponse.ResponseType.SUCCESS);
    }

    public static boolean take(Player player, double money) {
        if (econ == null || player == null) return false;
        return (econ.withdrawPlayer(player, money).type == EconomyResponse.ResponseType.SUCCESS);
    }

    public static boolean has(Player player, double money) {
        if (econ == null || player == null) return false;
        return econ.has(player, money);
    }

    public static double getBalance(Player player) {
        if (econ == null || player == null) return 0.0D;
        return econ.getBalance(player);
    }

}
