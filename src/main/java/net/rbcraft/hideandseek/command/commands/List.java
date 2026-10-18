package net.rbcraft.hideandseek.command.commands;

import net.rbcraft.hideandseek.HideAndSeek;
import net.rbcraft.hideandseek.command.Command;
import net.rbcraft.hideandseek.game.Game;
import net.rbcraft.hideandseek.io.I18n;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.LinkedHashMap;

public class List extends Command {

    private static final int lines = 10;

    public List() {
        super("list", false);
    }

    @Override
    protected void dealCommand(CommandSender sender, String[] args) {
        LinkedHashMap<String, Game> games = HideAndSeek.getInstance().games.getGames();
        if (games == null || games.size() == 0) {
            I18n.sendMessage(sender, I18n.formats("list_none"));
            return;
        }

        int index = 0;
        if (args.length > 1) {
            String num = args[1];
            if (num != null && num.length() > 0) {
                if (StringUtils.isNumeric(num)) index = Math.max(0, Integer.parseInt(num) - 1);
            }
        }

        int pages = games.size() / lines + 1;
        index = Math.min(pages - 1, index);

        I18n.sendMessage(sender, I18n.formats("list_show")
                                .replace("%count%", String.valueOf(games.size()))
                                .replace("%cur%", String.valueOf(index + 1))
                                .replace("%max%", String.valueOf(pages))
                            );
        int i = 0;
        for (String name : games.keySet()) {
            if (i >= index * lines) {
                Game game = games.get(name);
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&e" + name + "&b[" + game.getPlayers().size() + "/" +
                                    game.getData().max + "]      &a" + game.getData().display + "  " +
                                    I18n.formats("list_" + (game.isGaming()? "gaming" : "waiting"))));
            }
            i++;
            if (i >= (index + 1) * lines) break;
        }

        if (pages > 1) sender.sendMessage(I18n.formats("list_tips"));
    }

}
