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
        manager.stopEvent();
        sender.sendMessage(Messages.get("event-force-stopped"));
        return true;
    }
}
