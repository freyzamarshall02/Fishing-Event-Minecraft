package com.iwak.fishing;

import org.bukkit.plugin.java.JavaPlugin;

public class FishingEventPlugin extends JavaPlugin {

    private FishingManager fishingManager;

    @Override
    public void onEnable() {
        // Load config
        saveDefaultConfig();
        Messages.load(this);

        // Initialize manager
        fishingManager = new FishingManager(this);

        // Register events
        getServer().getPluginManager().registerEvents(new FishListener(fishingManager), this);

        // Register commands
        getCommand("fishingstart").setExecutor(new FishingStartCommand(fishingManager));
        getCommand("fishingstop").setExecutor(new FishingStopCommand(fishingManager));
        getCommand("fishingreset").setExecutor(new FishingResetCommand(fishingManager));

        // Register placeholders (if PAPI is installed)
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new FishingPlaceholders(fishingManager).register();
            getLogger().info("FishingEvent placeholders registered with PlaceholderAPI.");
        }

        getLogger().info("FishingEvent enabled!");
    }

    @Override
    public void onDisable() {
        if (fishingManager != null && fishingManager.isEventRunning()) {
            fishingManager.stopEvent();
        }
        getLogger().info("FishingEvent disabled!");
    }

    public FishingManager getFishingManager() {
        return fishingManager;
    }
}
