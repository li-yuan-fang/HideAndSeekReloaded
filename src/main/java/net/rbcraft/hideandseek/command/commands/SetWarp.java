package net.rbcraft.hideandseek.command.commands;

import net.rbcraft.hideandseek.HideAndSeek;
import net.rbcraft.hideandseek.command.Command;
import net.rbcraft.hideandseek.game.Game;
import net.rbcraft.hideandseek.game.GameData;
import net.rbcraft.hideandseek.game.GameManager;
import net.rbcraft.hideandseek.io.I18n;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetWarp extends Command {

    public SetWarp() {
        super("setwarp", true);
    }

    @Override
    protected void dealCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            I18n.sendMessage(sender, I18n.formats("only_player"));
            return;
        }
        if (args.length < 3) {
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
        Player player = (Player) sender;
        switch (args[2]) {
            case "lobby":
                data.lobby = GameData.getNearLocation(player.getLocation(), false);
                break;
            case "seeker":
                data.seeker = GameData.getNearLocation(player.getLocation(), false);
                break;
            case "hider":
                data.hider = GameData.getNearLocation(player.getLocation(), false);
                break;
            case "pos1":
                data.pos1 = GameData.getNearLocation(player.getLocation(), true);
                break;
            case "pos2":
                data.pos2 = GameData.getNearLocation(player.getLocation(), true);
                break;
            default:
                I18n.sendMessage(sender, I18n.formats("unknown_param"));
                return;
        }
        I18n.sendMessage(sender, I18n.formats("swtwarp_success").replace("%warp%", args[2]));
    }

}
