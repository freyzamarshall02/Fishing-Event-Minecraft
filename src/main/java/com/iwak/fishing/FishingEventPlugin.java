package com.iwak.fishing;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class FishingEventPlugin extends JavaPlugin {

    private FishingManager manager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("messages.yml", false); // ensure messages.yml exists
        Messages.load(this);

        this.manager = new FishingManager(this);

        // Register commands
        getCommand("fishingstart").setExecutor(new FishingStartCommand(manager));
        getCommand("fishingstop").setExecutor(new FishingStopCommand(manager));
        getCommand("fishingreset").setExecutor(new FishingResetCommand(manager));

        // Register listener
        Bukkit.getPluginManager().registerEvents(new FishListener(manager), this);

        getLogger().info(Messages.get("plugin-enabled"));
    }

    @Override
    public void onDisable() {
        getLogger().info(Messages.get("plugin-disabled"));
    }

    public FishingManager getManager() {
        return manager;
    }
}
