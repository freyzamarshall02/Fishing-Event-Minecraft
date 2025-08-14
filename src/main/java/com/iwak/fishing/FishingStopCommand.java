package com.iwak.fishing;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class FishingStopCommand implements CommandExecutor {
    private final FishingManager manager;
    public StopCommand(FishingManager manager) { this.manager = manager; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("fishingevent.admin")) {
            sender.sendMessage(ChatColor.RED + "No permission.");
            return true;
        }
        boolean announce = true; // announce winners on manual stop too
        manager.forceStop(announce);
        sender.sendMessage(ChatColor.GREEN + "Fishing event stopped.");
        return true;
    }
}
