package net.rbcraft.hideandseek.game.stamina;

import net.rbcraft.hideandseek.io.IOManager;
import org.bukkit.entity.Player;

public class StaminaState {

    private final Player player;

    private final StaminaManager manager;

    private int stamina;

    private boolean dashing;

    private long tired;

    public StaminaState(Player player, StaminaManager manager) {
        this.player = player;
        this.manager = manager;
        this.stamina = getMaxStamina();
        this.tired = 0;
        updateFoodLevel();
    }

    public void addStamina() {
        int max = getMaxStamina();
        if (stamina < max) {
            stamina += Math.min(max - stamina, getStaminaSpeed());
            updateFoodLevel();
        }
    }

    public boolean isTired() {
        return (System.currentTimeMillis() - tired) < 2000;
    }

    public void useStamina(int used) {
        stamina -= Math.max(used, 1);
        if (stamina < 0) stamina = 0;
        updateFoodLevel();

        tired = System.currentTimeMillis();
    }

    public void dash() {
        if (dashing || stamina < 40) return;
        player.setSneaking(false);

        useStamina(IOManager.getConfigInt("dash_stamina"));
        dashing = true;
        Dash.dash(player, manager);
        manager.getGame().getManager().coreCounter.start(16, () -> dashing = false);
    }

    public void updateFoodLevel() {
        updateFoodLevel(player, stamina);
    }

    public void fullFoodLevel() {
        updateFoodLevel(player, getMaxStamina());
    }

    public static void updateFoodLevel(Player player, int stamina) {
        player.setFoodLevel((int) ((double) stamina / (double) getMaxStamina() * 14.0D) + 6);
    }

    public static int getMaxStamina() {
        return IOManager.getConfigInt("max_stamina");
    }

    public static double getStaminaSpeed() {
        return IOManager.getConfigDouble("stamina_speed");
    }

}
