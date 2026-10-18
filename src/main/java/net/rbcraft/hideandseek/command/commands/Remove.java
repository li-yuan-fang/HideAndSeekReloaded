package net.rbcraft.hideandseek.command.commands;

import net.rbcraft.hideandseek.HideAndSeek;
import net.rbcraft.hideandseek.command.Command;
import net.rbcraft.hideandseek.game.Game;
import net.rbcraft.hideandseek.game.GameData;
import net.rbcraft.hideandseek.game.GameManager;
import net.rbcraft.hideandseek.io.I18n;
import org.bukkit.command.CommandSender;

public class Remove extends Command {

    public Remove() {
        super("remove", true);
    }

    @Override
    protected void dealCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            I18n.sendMessage(sender, I18n.formats("unknown_param"));
            return;
        }

        String name = args[1];
        GameManager games = HideAndSeek.getInstance().games;
        if (!games.contains(name)) {
            I18n.sendMessage(sender, I18n.formats("unknown_game"));
            return;
        }

        Game game = games.getGame(name);
        GameData data = game.getData();
        HideAndSeek.getInstance().io.gameData.remove(data);
        games.remove(name);

        I18n.sendMessage(sender, I18n.formats("remove_complete").replace("%game%", name));
    }

}
