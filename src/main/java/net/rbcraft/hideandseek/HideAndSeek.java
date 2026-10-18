package net.rbcraft.hideandseek;

import net.rbcraft.hideandseek.command.CommandsProcessor;
import net.rbcraft.hideandseek.game.GameManager;
import net.rbcraft.hideandseek.menu.Menus;
import net.rbcraft.hideandseek.io.I18n;
import net.rbcraft.hideandseek.io.IOManager;
import net.rbcraft.hideandseek.util.VaultHelper;
import org.bukkit.plugin.java.JavaPlugin;

public final class HideAndSeek extends JavaPlugin {

    private static HideAndSeek instance;
    public static HideAndSeek getInstance() {
        return instance;
    }

    public final CommandsProcessor command = new CommandsProcessor(this);

    public final IOManager io = new IOManager(this);

    public final GameManager games = new GameManager(this);

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        //初始化数据
        if (!io.init()) {
            I18n.log("&c数据加载失败，插件将不会运行");
            return;
        }

        //连接经济插件
        if (!VaultHelper.init()) {
            I18n.log("&c经济插件 Vault 未找到，奖励将不会正常发放");
        }

        //加载游戏
        games.init();
        games.reloadGames(io.gameData);

        //注册指令
        command.register();

        //注册菜单
        Menus.registerAll();

        I18n.log("加载成功");
    }

    @Override
    public void onDisable() {
        games.stopAll(I18n.formats("game_stop_force"));
        games.saveSkills();
        io.saveGames();
    }

}
