package net.rbcraft.hideandseek.command.commands;

import net.rbcraft.hideandseek.HideAndSeek;
import net.rbcraft.hideandseek.command.Command;
import net.rbcraft.hideandseek.game.Game;
import net.rbcraft.hideandseek.game.GameManager;
import net.rbcraft.hideandseek.io.I18n;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Stop extends Command {

    public Stop() {
        super("stop", true);
    }

    @Override
    protected void dealCommand(CommandSender sender, String[] args) {
        GameManager games = HideAndSeek.getInstance().games;
        boolean inGame = sender instanceof Player && games.isInGame((Player) sender);
        if (args.length < 2 && !inGame) {
            I18n.sendMessage(sender, I18n.formats("unknown_param"));
            return;
        }

        Game game;
        String name;
        if (args.length > 1) {
            name = args[1];
            if (!games.contains(name)) {
                I18n.sendMessage(sender, I18n.formats("unknown_game"));
                return;
            }
            game = games.getGame(name);
        } else {
            game = games.getGame((Player) sender);
            name = game.getData().name;
        }

        game.stop(I18n.formats("game_stop_force"));
        I18n.sendMessage(sender, I18n.formats("stop_complete").replace("%game%", name));
    }

}
