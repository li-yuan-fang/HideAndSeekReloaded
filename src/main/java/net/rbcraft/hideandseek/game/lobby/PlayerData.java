package net.rbcraft.hideandseek.game.lobby;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class PlayerData {

    private final ItemStack[] inventory;

    private final ItemStack[] armors;

    private final GameMode mode;

    private final boolean allowFlight;

    private final Location location;

    private final float walkSpeed, exp;

    private final int level;

    public PlayerData(Player player) {
        this.inventory = player.getInventory().getContents().clone();
        this.armors = player.getInventory().getArmorContents();
        this.mode = player.getGameMode();
        this.allowFlight = player.getAllowFlight();
        this.location = player.getLocation();
        this.exp = player.getExp();
        this.level = player.getLevel();
        this.walkSpeed = player.getWalkSpeed();
    }

    public void updateData(Player player) {
        updateData(player, this);
    }

    public static void updateData(Player player, PlayerData data) {
        player.setGameMode(data.mode);
        player.setAllowFlight(data.allowFlight);
        player.getInventory().setContents(data.inventory);
        player.getInventory().setArmorContents(data.armors);
        player.setWalkSpeed(data.walkSpeed);
        player.setLevel(data.level);
        player.setExp(data.exp);
        player.removePotionEffect(PotionEffectType.BLINDNESS);
        player.removePotionEffect(PotionEffectType.SPEED);
        player.removePotionEffect(PotionEffectType.GLOWING);
        player.teleport(data.location);
    }

}
