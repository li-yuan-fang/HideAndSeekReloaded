package net.rbcraft.hideandseek.command.commands;

import net.rbcraft.hideandseek.HideAndSeek;
import net.rbcraft.hideandseek.command.Command;
import net.rbcraft.hideandseek.io.I18n;
import org.bukkit.command.CommandSender;

public class Save extends Command {

    public Save() {
        super("save", true);
    }

    @Override
    protected void dealCommand(CommandSender sender, String[] args) {
        HideAndSeek.getInstance().io.saveGames();
        I18n.sendMessage(sender, I18n.formats("save_complete"));
    }
}
