package net.rbcraft.hideandseek.command;

import net.rbcraft.hideandseek.command.commands.*;
import net.rbcraft.hideandseek.io.I18n;
import org.bukkit.command.CommandSender;

public enum Commands {
    INFO(new Info()),
    HELP(new Help()),
    JOIN(new Join()),
    LEAVE(new Leave()),
    LIST(new List()),
    START(new Start()),
    STOP(new Stop()),
    CREATE(new Create()),
    EDIT(new Edit()),
    SETWARP(new SetWarp()),
    REMOVE(new Remove()),
    SAVE(new Save()),
    RELOAD(new Reload());

    Commands(Command command) {
        this.command = command;
    }

    private final Command command;

    public boolean equalsName(String name) {
        return command.equalsName(name);
    }

    public String getName() {
        return command.getName();
    }

    public boolean requireOP() {
        return command.requireOP;
    }

    public static void dealCommand(String name, CommandSender sender, String[] args) {
        for (Commands command : Commands.values()) {
            if (command.equalsName(name)) {
                command.command.deal(sender, args);
                return;
            }
        }
        I18n.sendMessage(sender, I18n.formats("unknown_command"));
    }

}
