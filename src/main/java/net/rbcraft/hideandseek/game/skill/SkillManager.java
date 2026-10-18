package net.rbcraft.hideandseek.game.skill;

import net.rbcraft.hideandseek.io.I18n;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.LinkedHashMap;

public class SkillManager {

    private final LinkedHashMap<Player, PlayerSkill> skills = new LinkedHashMap<>();

    private final File skillFolder;

    public SkillManager(Plugin plugin) {
        skillFolder = new File(plugin.getDataFolder(), "skills");
    }

    public void init() {
        if (!skillFolder.exists() && !skillFolder.mkdirs()) I18n.log("&c技能储存文件夹创建失败，技能将不会被储存");
    }

    public void onJoin(Player player) {
        PlayerSkill skill = new PlayerSkill(player);

        File skillFile = new File(skillFolder, player.getUniqueId().toString() + ".yml");
        if (skillFile.exists()) {
            try {
                Reader reader = new InputStreamReader(Files.newInputStream(skillFile.toPath()), StandardCharsets.UTF_8);
                YamlConfiguration skillCache = YamlConfiguration.loadConfiguration(reader);
                skill.hiderBase = skillCache.getInt("hider_base", 0);
                skill.seekerBase = skillCache.getInt("seeker_base", 0);
                skill.seekerAirDrop = skillCache.getInt("seeker_airdrop", 0);
            } catch (IOException e) {
                I18n.log("&c读取玩家 " + player.getName() + " 的技能文件失败");
                e.printStackTrace();
            }
        }
        skills.put(player, skill);
    }

    public void onLeave(Player player) {
        quit(player, true);
    }

    private void quit(Player player, boolean remove) {
        if (!skills.containsKey(player) || !skillFolder.exists()) return;

        PlayerSkill skill = skills.get(player);
        YamlConfiguration skillCache = new YamlConfiguration();
        skillCache.set("hider_base", skill.hiderBase);
        skillCache.set("seeker_base", skill.seekerBase);
        skillCache.set("seeker_airdrop", skill.seekerAirDrop);

        String s = skillCache.saveToString();
        File skillFile = new File(skillFolder, player.getUniqueId() + ".yml");
        try {
            Files.write(skillFile.toPath(), s.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        } catch (IOException e) {
            I18n.log("&c保存玩家 " + player.getName() + " 的技能文件失败");
            e.printStackTrace();
        }

        if (remove) skills.remove(player);
    }

    public void saveSkills() {
        for (Player player : skills.keySet()) {
            quit(player, false);
        }
    }

    public void saveSkillsAndClose() {
        saveSkills();
        skills.clear();
    }

    public PlayerSkill getSkill(Player player) {
        return skills.getOrDefault(player, null);
    }

}
