package net.rbcraft.hideandseek.command.commands;

import net.rbcraft.hideandseek.HideAndSeek;
import net.rbcraft.hideandseek.command.Command;
import net.rbcraft.hideandseek.game.Game;
import net.rbcraft.hideandseek.game.GameManager;
import net.rbcraft.hideandseek.io.I18n;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Leave extends Command {

    public Leave() {
        super("leave", false);
    }

    @Override
    protected void dealCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            I18n.sendMessage(sender, I18n.formats("only_player"));
            return;
        }

        GameManager games = HideAndSeek.getInstance().games;
        Player player = (Player) sender;
        Game game = games.getGame(player);

        if (game == null) {
            I18n.sendMessage(player, I18n.formats("leave_out"));
            return;
        }
        game.leave(player);
        I18n.sendMessage(player, I18n.formats("leave_complete"));
    }

}
