package com.iwak.fishing;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class FishingEventPlugin extends JavaPlugin {

    private FishingManager manager;

    @Override
    public void onEnable() {
        // Save default config.yml if it doesn't exist
        saveDefaultConfig();

        // Initialize FishingManager
        this.manager = new FishingManager(this);

        // Register event listeners
        getServer().getPluginManager().registerEvents(new FishListener(manager), this);

        // Register commands
        if (getCommand("fishingevent") != null) {
            getCommand("fishingevent").setExecutor(new FishingCommand(manager));
        } else {
            getLogger().warning("Command 'fishingevent' is not defined in plugin.yml!");
        }

        // Register placeholders if PlaceholderAPI is present
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new FishingPlaceholders(manager).register();
            getLogger().info("FishingEvent placeholders registered!");
        } else {
            getLogger().warning("PlaceholderAPI not found - placeholders will not work!");
        }

        getLogger().info("FishingEvent enabled!");
    }

    @Override
    public void onDisable() {
        if (manager != null && manager.isRunning()) {
            manager.forceStop(false);
        }
        getLogger().info("FishingEvent disabled!");
    }

    public FishingManager getManager() {
        return manager;
    }
}
