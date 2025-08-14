package com.iwak.fishing;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class FishingStartCommand implements CommandExecutor {
    private final FishingManager manager;

    public FishingStartCommand(FishingManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("fishingevent.admin")) {
            sender.sendMessage(ChatColor.RED + "No permission.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(ChatColor.YELLOW + "Usage: /" + label + " <seconds>");
            return true;
        }

        int sec;
        try {
            sec = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Not a number: " + args[0]);
            return true;
        }

        if (sec <= 0) {
            sender.sendMessage(ChatColor.RED + "Seconds must be > 0");
            return true;
        }

        manager.start(sec);
        sender.sendMessage(ChatColor.GREEN + "Fishing event started for " + sec + " seconds.");
        return true;
    }
}
