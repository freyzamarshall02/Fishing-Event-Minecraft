package com.iwak.fishing;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class FishingResetCommand implements CommandExecutor {

    private final FishingManager manager;

    public FishingResetCommand(FishingManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        manager.resetLeaderboard();
        sender.sendMessage(Messages.get("leaderboard-reset-confirm"));
        return true;
    }
}
