package net.rbcraft.hideandseek.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;

public class CommandsProcessor implements CommandExecutor {

    public final Plugin plugin;

    public CommandsProcessor(Plugin plugin) {
        this.plugin = plugin;
    }

    public void register() {
        PluginCommand command = Bukkit.getPluginCommand("hideandseek");
        if (command != null) command.setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String name = args.length > 0 ? args[0] : "info";
        Commands.dealCommand(name, sender, args);
        return true;
    }
}
