package net.rbcraft.hideandseek.command;

import net.rbcraft.hideandseek.io.I18n;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class Command {

    protected final String name;

    protected final boolean requireOP;

    protected Command(String name, boolean requireOP) {
        this.name = name.toLowerCase();
        this.requireOP = requireOP;
    }

    public String getName() {
        return name;
    }

    public static boolean equalsName(String name, Command command) {
        if (command == null || name == null || command.getName() == null) return false;
        return command.getName().equalsIgnoreCase(name);
    }

    public boolean equalsName(String name) {
        return equalsName(name, this);
    }

    protected abstract void dealCommand(CommandSender sender, String[] args);

    public void deal(CommandSender sender, String[] args) {
        if (requireOP && !(!(sender instanceof Player) || sender.isOp())) {
            I18n.sendMessage(sender, I18n.formats("no_permission"));
            return;
        }
        dealCommand(sender, args);
    }

}
