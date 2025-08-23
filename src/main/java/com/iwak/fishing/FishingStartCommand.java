package com.iwak.fishing;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FishingStartCommand implements CommandExecutor {

    private final FishingManager manager;

    public FishingStartCommand(FishingManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can run this command.");
            return true;
        }

        if (!sender.hasPermission("fishingevent.start")) {
            sender.sendMessage("§cYou do not have permission to start the fishing event.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage("§eUsage: /fishingstart <seconds>");
            return true;
        }

        try {
            int seconds = Integer.parseInt(args[0]);
            if (seconds <= 0) {
                sender.sendMessage("§cThe duration must be greater than 0.");
                return true;
            }

            manager.startEvent(seconds);
            sender.sendMessage("§aFishing event started for " + seconds + " seconds!");

        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid number: " + args[0]);
        }

        return true;
    }
}
