package net.rbcraft.hideandseek.io;

import net.rbcraft.hideandseek.HideAndSeek;
import net.rbcraft.hideandseek.game.GameData;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;

public class IOManager {

    private final Plugin plugin;

    public I18n language;

    public YamlConfiguration config;

    public final LinkedList<GameData> gameData = new LinkedList<>();

    public IOManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public boolean init() {
        return reloadAll();
    }

    public boolean reloadAll() {
        File pluginFolder = plugin.getDataFolder();
        if (!pluginFolder.exists()) {
            if (!pluginFolder.mkdir()) return false;
        }
        if (!reloadConfig()) return false;
        reloadMessages();
        reloadGames();
        return true;
    }

    public boolean reloadGames() {
        File dataFolder = new File(plugin.getDataFolder(), "games");
        if (dataFolder.exists()) {
            File[] files = dataFolder.listFiles();
            if (files == null || files.length == 0) return true;

            gameData.clear();
            for (File saved : files) {
                if (!saved.getName().endsWith(".yml")) continue;
                try {
                    Reader reader = new InputStreamReader(Files.newInputStream(saved.toPath()), StandardCharsets.UTF_8);
                    gameData.add(GameData.load(YamlConfiguration.loadConfiguration(reader)));
                } catch (IOException e) {
                    I18n.log("&c加载游戏 " + saved.getName() + " 失败");
                    e.printStackTrace();
                }
            }
        } else {
            return dataFolder.mkdir();
        }
        return true;
    }

    public boolean reloadConfig() {
        File config = new File(plugin.getDataFolder(), "config.yml");
        if (!config.exists()) {
            InputStream in = plugin.getResource("config.yml");
            if (in != null) {
                try {
                    Files.copy(in, config.toPath());
                } catch (IOException e) {
                    I18n.log("&c加载配置文件失败");
                    e.printStackTrace();
                    return false;
                }
            } else {
                this.config = new DefaultConfig();
            }
        }

        try {
            Reader reader = new InputStreamReader(Files.newInputStream(config.toPath()), StandardCharsets.UTF_8);
            this.config = YamlConfiguration.loadConfiguration(reader);
        } catch (IOException e) {
            I18n.log("&c加载配置文件失败");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean reloadMessages() {
        File lang = new File(plugin.getDataFolder(), "messages.yml");
        if (!lang.exists()) {
            InputStream in = plugin.getResource("messages.yml");
            if (in != null) {
                try {
                    Files.copy(in, lang.toPath());
                } catch (IOException e) {
                    I18n.log("&c创建语言文件失败");
                    e.printStackTrace();
                    return false;
                }
            }
        }

        try {
            this.language = new I18n(lang);
        } catch (Exception e) {
            I18n.log("&c加载语言文件失败");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void saveGames() {
        if (gameData.isEmpty()) return;

        File dataFolder = new File(plugin.getDataFolder(), "games");
        if (!dataFolder.exists() && !dataFolder.mkdir()) {
            I18n.log("&c创建游戏保存目录失败，游戏将不会被保存");
            return;
        }
        for (GameData data : gameData) {
            byte[] encoded = data.save().getBytes(StandardCharsets.UTF_8);
            File save = new File(dataFolder, data.name + ".yml");
            try {
                Files.write(save.toPath(), encoded, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            } catch (IOException e) {
                I18n.log("&c保存游戏 &e" + data.name + " &c时出错");
                e.printStackTrace();
            }
        }
    }

    public static int getConfigInt(String name) {
        return HideAndSeek.getInstance().io.config.getInt(name);
    }

    public static double getConfigDouble(String name) {
        return HideAndSeek.getInstance().io.config.getDouble(name);
    }

}
