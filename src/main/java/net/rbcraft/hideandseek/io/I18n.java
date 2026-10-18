package net.rbcraft.hideandseek.io;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class I18n {

    private final Configuration config;

    private static I18n instance;
    public static I18n getInstance() {
        return instance;
    }

    public I18n(File language) {
        try (Reader reader = new InputStreamReader(Files.newInputStream(language.toPath()), StandardCharsets.UTF_8)) {
            this.config = YamlConfiguration.loadConfiguration(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        instance = this;
    }

    public String format(String key) {
        String s = this.config.getString(key);
        if (s != null) s = ChatColor.translateAlternateColorCodes('&', s);
        return s != null ? s : "";
    }

    public static String formats(String key) {
        return getInstance().format(key);
    }

    public static void sendMessage(CommandSender sender, String context) {
        if (sender == null) return;
        String prefix = getInstance() == null ? "§6[§9游侠与猎手§6]" : getInstance().format("prefix");
        sender.sendMessage(prefix + " " + ChatColor.translateAlternateColorCodes('&', context));
    }

    public static void log(String context) {
        sendMessage(Bukkit.getConsoleSender(), context);
    }

}
