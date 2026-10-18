package net.rbcraft.hideandseek.command.commands;

import net.rbcraft.hideandseek.HideAndSeek;
import net.rbcraft.hideandseek.command.Command;
import net.rbcraft.hideandseek.io.I18n;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;

public class Info extends Command {

    public Info() {
        super("info", false);
    }

    @Override
    public void dealCommand(CommandSender sender, String[] args) {
        I18n l = I18n.getInstance();
        PluginDescriptionFile desc = HideAndSeek.getInstance().getDescription();
        I18n.sendMessage(sender,
                l.format("info_title").replace("%version%", desc.getVersion()));
        sender.sendMessage(l.format("info_author").replace("%authors%", desc.getAuthors().toString()));
    }

}
