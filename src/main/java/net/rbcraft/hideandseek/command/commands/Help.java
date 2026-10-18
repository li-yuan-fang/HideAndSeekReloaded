package net.rbcraft.hideandseek.command.commands;

import net.rbcraft.hideandseek.command.Command;
import net.rbcraft.hideandseek.command.Commands;
import net.rbcraft.hideandseek.io.I18n;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Help extends Command {

    public Help() {
        super("help", false);
    }

    @Override
    public void dealCommand(CommandSender sender, String[] args) {
        I18n l = I18n.getInstance();
        I18n.sendMessage(sender, l.format("help_title"));
        for (Commands command : Commands.values()) {
            String name = command.getName().toLowerCase();
            String s = "&9/has " + name + " " + I18n.formats(name + "_desc");
            if (command.requireOP()) s += I18n.formats("only_op");
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
        }
    }

}
