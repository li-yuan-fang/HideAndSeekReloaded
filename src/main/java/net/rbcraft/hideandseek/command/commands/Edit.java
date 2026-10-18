package net.rbcraft.hideandseek.command.commands;

import net.rbcraft.hideandseek.HideAndSeek;
import net.rbcraft.hideandseek.command.Command;
import net.rbcraft.hideandseek.game.Game;
import net.rbcraft.hideandseek.game.GameData;
import net.rbcraft.hideandseek.game.GameManager;
import net.rbcraft.hideandseek.menu.Menus;
import net.rbcraft.hideandseek.io.I18n;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Edit extends Command {

    public Edit() {
        super("edit", true);
    }

    @Override
    protected void dealCommand(CommandSender sender, String[] args) {
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
        switch (args[2]) {
            case "display":
                if (args.length < 4) {
                    I18n.sendMessage(sender, I18n.formats("less_param"));
                    return;
                }
                data.display = args[3];
                break;
            case "max":
                if (args.length < 4) {
                    I18n.sendMessage(sender, I18n.formats("less_param"));
                    return;
                }
                if (!StringUtils.isNumeric(args[3])) {
                    I18n.sendMessage(sender, I18n.formats("force_number"));
                    return;
                }
                data.max = Math.abs(Integer.parseInt(args[3]));
                break;
            case "time":
                if (args.length < 4) {
                    I18n.sendMessage(sender, I18n.formats("less_param"));
                    return;
                }
                if (!StringUtils.isNumeric(args[3])) {
                    I18n.sendMessage(sender, I18n.formats("force_number"));
                    return;
                }
                data.gameTime = Math.abs(Integer.parseInt(args[3]));
                break;
            case "blocks":
                if (!(sender instanceof Player)) {
                    I18n.sendMessage(sender, I18n.formats("only_player"));
                    return;
                }
                Menus.EditBlocks.openMenu((Player) sender, name);
                break;
            default:
                I18n.sendMessage(sender, I18n.formats("unknown_param"));
                return;
        }
        I18n.sendMessage(sender, I18n.formats("edit_success"));
    }
}
