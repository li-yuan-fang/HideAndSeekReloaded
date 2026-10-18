package net.rbcraft.hideandseek.game;

import net.rbcraft.hideandseek.io.IOManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GameData {

    public String name, display;

    public Location lobby, seeker, hider, pos1, pos2;

    public int max, gameTime;

    public ItemStack[] items;

    protected GameData(String name, String display, int max, int gameTime, Location lobby, Location seeker, Location hider,
                       Location pos1, Location pos2, ItemStack[] items) {
        this.name = name;
        this.display = display;
        this.max = max;
        this.gameTime = gameTime;
        this.lobby = lobby;
        this.seeker = seeker;
        this.hider = hider;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.items = items;
    }

    public static GameData newGame(String name) {
        return new GameData(name, name, IOManager.getConfigInt("min_players"), 420, null, null,
                null, null, null, null);
    }

    public static GameData load(YamlConfiguration saved) {
        return new GameData(saved.getString("name"), saved.getString("display"), saved.getInt("max"),
                saved.getInt("game_time"), getLocation(saved, "lobby"), getLocation(saved, "seeker_wait"),
                getLocation(saved, "spawn"), getLocation(saved, "area.pos1"), getLocation(saved, "area.pos2"),
                getItemStacks(saved, "blocks"));
    }

    public String save() {
        return save(this);
    }

    public static String save(GameData data) {
        if (data == null) return null;

        YamlConfiguration saved = new YamlConfiguration();
        saved.set("name", data.name);
        saved.set("display", data.display);
        saved.set("max", data.max);
        saved.set("game_time", data.gameTime);

        saveLocation(saved, "lobby", data.lobby);
        saveLocation(saved, "seeker_wait", data.seeker);
        saveLocation(saved, "spawn", data.hider);
        saveLocation(saved, "area.pos1", data.pos1);
        saveLocation(saved, "area.pos2", data.pos2);

        saveItemStacks(saved, "blocks", data.items);

        return saved.saveToString();
    }

    private static Location getLocation(YamlConfiguration yml, String name) {
        if (yml == null || name == null || name.length() == 0) return null;

        String w = yml.getString(name + ".world");
        if (w == null || w.length() == 0) return null;

        World world = Bukkit.getWorld(w);
        if (world == null) return null;
        return new Location(world, yml.getDouble(name + ".x", 0), yml.getDouble(name + ".y", 0),
                yml.getDouble(name + ".z", 0));
    }

    public boolean isInArea(Location location) {
        if (location == null || pos1 == null || pos2 == null) return false;
        return location.getX() <= Math.max(pos1.getX(), pos2.getX()) && location.getX() >= Math.min(pos1.getX(), pos2.getX()) &&
                location.getY() <= Math.max(pos1.getY(), pos2.getY()) && location.getY() >= Math.min(pos1.getY(), pos2.getY()) &&
                location.getZ() <= Math.max(pos1.getZ(), pos2.getZ()) && location.getZ() >= Math.min(pos1.getZ(), pos2.getZ());
    }

    public boolean isReady() {
        return !isEmpty(name) && lobby != null && seeker != null && hider != null && pos1 != null && pos2 != null &&
                pos1.getWorld().equals(pos2.getWorld()) && gameTime >= 120 && !isEmpty(items) &&
                max >= IOManager.getConfigInt("min_players");
    }

    private static boolean isEmpty(String s) {
        return s == null && s.length() == 0;
    }

    private static boolean isEmpty(ItemStack[] items) {
        return items == null || items.length == 0;
    }

    private static void saveLocation(YamlConfiguration save, String name, Location location) {
        if (save == null || name == null || location == null || name.length() == 0) return;

        save.set(name + ".world", location.getWorld().getName());
        save.set(name + ".x", location.getX());
        save.set(name + ".y", location.getY());
        save.set(name + ".z", location.getZ());
    }

    private static void saveItemStacks(YamlConfiguration save, String name, ItemStack[] items) {
        if (save == null || name == null || items == null || name.length() == 0 || items.length == 0) return;

        ArrayList<String> r = new ArrayList<>();
        for (ItemStack item : items) {
            String s = saveItemStack(item);
            if (s != null && s.length() > 0) r.add(s);
        }
        if (r.size() > 0) save.set(name, r);
    }

    private static String saveItemStack(ItemStack item) {
        return item == null ? null : item.getType().name();
    }

    private static ItemStack[] getItemStacks(YamlConfiguration yml, String name) {
        if (yml == null || name == null || name.length() == 0) return null;

        List<String> items = yml.getStringList(name);
        if (items.size() == 0) return null;

        ItemStack[] stacks = new ItemStack[items.size()];
        for (int i = 0; i < stacks.length; i++) {
            stacks[i] = getItemStack(items.get(i));
        }
        return stacks;
    }

    private static ItemStack getItemStack(String name) {
        if (name == null || name.length() == 0) return null;

        Material material = Material.getMaterial(name);
        if (material == null) return null;

        return new ItemStack(material, 1);
    }

    public static Location getNearLocation(Location location, boolean isBlock) {
        if (location == null) return null;
        double x = isBlock ? location.getBlockX() : location.getX();
        double y = isBlock ? location.getBlockY() : location.getY();
        double z = isBlock ? location.getBlockZ() : location.getZ();
        return new Location(location.getWorld(), x, y, z);
    }

}
