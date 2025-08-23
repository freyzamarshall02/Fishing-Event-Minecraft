package com.iwak.fishing;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FishingStopCommand implements CommandExecutor {

    private final FishingManager manager;

    public FishingStopCommand(FishingManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.prefix("&cOnly players can run this command."));
            return true;
        }

        if (!sender.hasPermission("fishingevent.stop")) {
            sender.sendMessage(Messages.prefix("&cYou don’t have permission to stop the fishing event."));
            return true;
        }

        if (!manager.isEventRunning()) {
            sender.sendMessage(Messages.prefix("&eNo fishing event is currently running."));
            return true;
        }

        manager.stopEvent();
        sender.sendMessage(Messages.prefix("&aFishing event has been stopped and results announced."));
        return true;
    }
}
