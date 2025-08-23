package com.iwak.fishing;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class FishingStopCommand implements CommandExecutor {

    private final FishingManager manager;

    public FishingStopCommand(FishingManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("fishingevent.stop")) {
            sender.sendMessage("§cYou do not have permission to stop the fishing event.");
            return true;
        }

        if (!manager.isEventRunning()) {
            sender.sendMessage("§eNo fishing event is currently running.");
            return true;
        }

        manager.stopEvent();
        sender.sendMessage("§aFishing event stopped and winners announced!");

        return true;
    }
}
