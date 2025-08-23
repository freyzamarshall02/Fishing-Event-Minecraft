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
        if (args.length != 1) {
            sender.sendMessage(Messages.get("usage-fishingstart"));
            return true;
        }

        try {
            int duration = Integer.parseInt(args[0]);
            manager.startEvent(duration);
        } catch (NumberFormatException e) {
            sender.sendMessage(Messages.get("invalid-number"));
        }
        return true;
    }
}
