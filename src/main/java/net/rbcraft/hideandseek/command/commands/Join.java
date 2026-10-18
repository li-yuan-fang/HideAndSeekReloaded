package net.rbcraft.hideandseek.command.commands;

import net.rbcraft.hideandseek.HideAndSeek;
import net.rbcraft.hideandseek.command.Command;
import net.rbcraft.hideandseek.game.Game;
import net.rbcraft.hideandseek.game.GameManager;
import net.rbcraft.hideandseek.io.I18n;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Join extends Command {

    public Join() {
        super("join", false);
    }

    @Override
    protected void dealCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            I18n.sendMessage(sender, I18n.formats("only_player"));
            return;
        }
        if (args.length < 2) {
            I18n.sendMessage(sender, I18n.formats("unknown_param"));
            return;
        }

        GameManager games = HideAndSeek.getInstance().games;
        Player player = (Player) sender;
        if (games.isInGame(player)) {
            I18n.sendMessage(sender, I18n.formats("join_already"));
            return;
        }

        String name = args[1];
        if (!games.contains(name)) {
            I18n.sendMessage(sender, I18n.formats("unknown_game"));
            return;
        }

        Game game = games.getGame(name);
        try {
            game.join(player);
            I18n.sendMessage(player, I18n.formats("join_success")
                    .replace("%game%", ChatColor.translateAlternateColorCodes('&', game.getData().display)));
        } catch (Exception e) {
            I18n.sendMessage(player, I18n.formats("join_failed").replace("%reason%", e.getMessage()));
        }
    }

}
