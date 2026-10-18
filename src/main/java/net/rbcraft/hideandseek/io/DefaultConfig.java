package net.rbcraft.hideandseek.io;

import org.bukkit.configuration.file.YamlConfiguration;

public class DefaultConfig extends YamlConfiguration {

    public DefaultConfig() {
        //默认设置
        set("min_players", 4);
        set("waiting_time", 50);
        set("seeker_rate", 0.25D);
        set("allow_sign", true);
        set("speed_disguised", 0.8D);
        set("max_stamina", 140);
        set("stamina_speed", 1.0D);
        set("stamina_use_last", 0.8D);
        set("base_prize", 50.0D);
        set("win_prize", 100.0D);
    }

}
