package net.rbcraft.hideandseek.game;

import net.rbcraft.hideandseek.game.counter.TimeCounter;
import net.rbcraft.hideandseek.game.skill.SkillManager;
import net.rbcraft.hideandseek.io.I18n;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import net.rbcraft.hideandseek.game.util.ScoreboardManager;

import java.util.LinkedHashMap;
import java.util.LinkedList;

public class GameManager implements Listener {

    public final SkillManager skillManager;

    public final ScoreboardManager scoreboards = new ScoreboardManager();

    public final TimeCounter coreCounter = new TimeCounter();

    private final Plugin plugin;

    private final Ticker tick = new Ticker(this);

    private final LinkedHashMap<String, Game> games = new LinkedHashMap<>();

    public GameManager(Plugin plugin) {
        this.plugin = plugin;
        skillManager = new SkillManager(this.plugin);
    }

    public void init() {
        skillManager.init();
        scoreboards.init();
        Bukkit.getPluginManager().registerEvents(this, plugin);
        tick.runTaskTimer(plugin, 0, 1);
    }

    public void saveSkills() {
        skillManager.saveSkillsAndClose();
    }

    public LinkedHashMap<String, Game> getGames() {
        return games;
    }

    public void reloadGames(LinkedList<GameData> data) {
        if (data == null) return;

        if (games.size() > 0) stopAll(I18n.formats("game_stop_force"));
        games.clear();

        for (GameData d : data) {
            add(d);
        }
    }

    public void add(GameData data) {
        if (data == null || contains(data.name)) return;

        games.put(data.name, Game.loadGame(this, data));
    }

    public boolean contains(String name) {
        return games.containsKey(name);
    }

    public void remove(String name) {
        if (name == null || name.length() == 0 || !games.containsKey(name)) return;

        Game game = games.get(name);
        if (game.isGaming()) game.stop(I18n.formats("game_stop_force"));

        games.remove(name);
    }

    public Game getGame(String name) {
        return games.get(name);
    }

    public Game getGame(Player player) {
        for (Game game : games.values()) {
            if (game.isInGame(player)) return game;
        }
        return null;
    }

    public boolean isInGame(Player player) {
        return getGame(player) != null;
    }

    public void stopAll(String reason) {
        for (Game game : games.values()) {
            game.stop(reason);
        }
    }

    @EventHandler
    public void onPlayerPickup(EntityPickupItemEvent event) {
        for (Game game : games.values()) {
            game.onPlayerPickup(event);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        for (Game game : games.values()) {
            game.onPlayerDropItem(event);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        for (Game game : games.values()) {
            game.onPlayerMove(event);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        for (Game game : games.values()) {
            game.onPlayerInteract(event);
        }
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        for (Game game : games.values()) {
            game.onPlayerCommand(event);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        skillManager.onJoin(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        skillManager.onLeave(event.getPlayer());
        for (Game game : games.values()) {
            game.onPlayerQuit(event);
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        for (Game game : games.values()) {
            game.onPlayerChat(event);
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        for (Game game : games.values()) {
            game.onInventoryOpen(event);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        for (Game game : games.values()) {
            game.onFoodLevelChange(event);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        for (Game game : games.values()) {
            game.onEntityDamage(event);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        for (Game game : games.values()) {
            game.onBlockPlace(event);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        for (Game game : games.values()) {
            game.onBlockBreak(event);
        }
    }

    public void onTick() {
        coreCounter.onTick();
        for (Game game : games.values()) {
            game.onTick();
        }
    }

    private static class Ticker extends BukkitRunnable {

        private final GameManager manager;

        public Ticker(GameManager manager) {
            this.manager = manager;
        }

        @Override
        public void run() {
            manager.onTick();
        }

    }

}
