package net.rbcraft.hideandseek.game.disguise;

import net.rbcraft.hideandseek.util.PositionUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class DisguiseState {

    private final Player player;

    private int id = -1;

    private boolean solid, allowDisguise, vanish, inBeacon;

    private Location solidPos;

    public DisguiseState(Player player) {
        this.player = player;
        this.allowDisguise = true;
        this.vanish = false;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public boolean isSolid() {
        return solid;
    }

    public boolean isSolid(Location location) {
        return solid && PositionUtils.sameBlockPosition(location, solidPos);
    }

    public Location getSolidPos() {
        return solidPos;
    }

    public void solid(Location location) {
        solid = true;
        solidPos = location;
    }

    public void unSolid() {
        solid = false;
        solidPos = null;
    }

    public boolean isDisguised() {
        return id >= 0;
    }

    public Player getPlayer() {
        return player;
    }

    public void setAllowDisguise(boolean flag) {
        allowDisguise = flag;
    }

    public boolean isAllowDisguise() {
        return allowDisguise;
    }

    public void setVanish(boolean flag) {
        vanish = flag;
    }

    public boolean isVanish() {
        return vanish;
    }

    public void setInBeacon(boolean flag) {
        inBeacon = flag;
    }

    public boolean isInBeacon() {
        return inBeacon;
    }

}
