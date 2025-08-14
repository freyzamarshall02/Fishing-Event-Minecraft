package com.iwak.fishing;

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
        if (manager.isRunning()) {
            sender.sendMessage("§cA fishing event is already running!");
            return true;
        }

        int duration = 100; // default 100 seconds
        if (args.length > 0) {
            try {
                duration = Integer.parseInt(args[0]);
                if (duration <= 0) {
                    sender.sendMessage("§cDuration must be positive. Using default 100 seconds.");
                    duration = 100;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid number, using default 100 seconds.");
            }
        }

        manager.startEvent(duration);
        sender.sendMessage("§aFishing event started for " + duration + " seconds!");
        return true;
    }
}
