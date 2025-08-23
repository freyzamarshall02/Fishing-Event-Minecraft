package com.iwak.fishing;

import org.bukkit.plugin.java.JavaPlugin;

public class FishingEventPlugin extends JavaPlugin {

    private FishingManager fishingManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // Initialize manager with plugin reference
        fishingManager = new FishingManager(this);

        // Register listeners
        getServer().getPluginManager().registerEvents(new FishListener(fishingManager), this);

        // Register commands
        getCommand("fishingstart").setExecutor(new FishingStartCommand(fishingManager));
        getCommand("fishingstop").setExecutor(new FishingStopCommand(fishingManager));
        getCommand("fishingreset").setExecutor(new FishingResetCommand(fishingManager));

        // Register placeholders (if PlaceholderAPI is installed)
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new FishingPlaceholders(fishingManager).register();
            getLogger().info("PlaceholderAPI detected: Placeholders enabled.");
        }

        getLogger().info("FishingEvent plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("FishingEvent plugin disabled!");
    }

    public FishingManager getFishingManager() {
        return fishingManager;
    }
}
