package net.rbcraft.hideandseek.game.disguise;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.DisguiseConfig;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MiscDisguise;
import net.rbcraft.hideandseek.HideAndSeek;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DisguiseUtils {

    public static void updateFakeBlock(Player player, Location location, Material material) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (player.equals(p)) continue;
            p.sendBlockChange(location, material.createBlockData());
        }
    }

    public static void show(Player player) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (player.equals(p)) continue;
            p.showPlayer(HideAndSeek.getInstance(), player);
        }
    }

    public static void hide(Player player) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (player.equals(p)) continue;
            p.hidePlayer(HideAndSeek.getInstance(), player);
        }
    }

    public static void disguise(Player player, ItemStack block) {
        if (block == null) {
            DisguiseAPI.undisguiseToAll(player);
            return;
        }
        MiscDisguise disguise = new MiscDisguise(DisguiseType.FALLING_BLOCK, block);
        disguise.setNotifyBar(DisguiseConfig.NotifyBar.NONE);
        DisguiseAPI.disguiseToAll(player, disguise);
    }

    public static boolean canSolid(Location location) {
        if (location == null) return false;
        Block block = location.getBlock();
        return block == null || block.getType() == Material.AIR;
    }

}
