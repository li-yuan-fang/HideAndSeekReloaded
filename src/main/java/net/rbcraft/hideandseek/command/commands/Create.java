package net.rbcraft.hideandseek.command.commands;

import net.rbcraft.hideandseek.HideAndSeek;
import net.rbcraft.hideandseek.command.Command;
import net.rbcraft.hideandseek.game.GameData;
import net.rbcraft.hideandseek.game.GameManager;
import net.rbcraft.hideandseek.io.I18n;
import net.rbcraft.hideandseek.io.IOManager;
import org.bukkit.command.CommandSender;

public class Create extends Command {

    public Create() {
        super("create", true);
    }

    @Override
    protected void dealCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            I18n.sendMessage(sender, I18n.formats("unknown_param"));
            return;
        }

        IOManager io = HideAndSeek.getInstance().io;
        GameManager games = HideAndSeek.getInstance().games;

        GameData data = GameData.newGame(args[1]);
        io.gameData.add(data);
        games.add(data);
        I18n.sendMessage(sender, I18n.formats("create_complete"));
    }

}
