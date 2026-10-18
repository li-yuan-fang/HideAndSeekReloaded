package net.rbcraft.hideandseek.command.commands;

import net.rbcraft.hideandseek.HideAndSeek;
import net.rbcraft.hideandseek.command.Command;
import net.rbcraft.hideandseek.game.GameManager;
import net.rbcraft.hideandseek.io.I18n;
import net.rbcraft.hideandseek.io.IOManager;
import org.bukkit.command.CommandSender;

public class Reload extends Command {

    public Reload() {
        super("reload", true);
    }

    @Override
    protected void dealCommand(CommandSender sender, String[] args) {
        String param = args.length < 2 ? "all" : args[1];
        IOManager io = HideAndSeek.getInstance().io;
        GameManager games = HideAndSeek.getInstance().games;

        boolean flag;
        switch (param) {
            case "all":
                flag = io.reloadAll();
                if (flag) games.reloadGames(io.gameData);
                break;
            case "config":
                flag = io.reloadConfig();
                break;
            case "lang":
                flag = io.reloadMessages();
                break;
            case "games":
                flag = io.reloadGames();
                if (flag) games.reloadGames(io.gameData);
                break;
            default:
                I18n.sendMessage(sender, I18n.formats("unknown_param"));
                return;
        }
        games.scoreboards.updateAll();
        I18n.sendMessage(sender, I18n.formats("reload_" + (flag ? "success" : "failed"))
                .replace("%part%", I18n.formats("reload_" + param)));
    }

}
