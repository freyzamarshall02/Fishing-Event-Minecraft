package com.iwak.fishing;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class FishingEventPlugin extends JavaPlugin {

    private FishingManager manager;

    @Override
    public void onEnable() {
        this.manager = new FishingManager(this);
        // Events
        Bukkit.getPluginManager().registerEvents(new FishListener(manager), this);

        // Commands
        getCommand("fishingstart").setExecutor(new StartCommand(manager));
        getCommand("fishingstop").setExecutor(new StopCommand(manager));

        // Placeholders
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new FishingPlaceholders(manager).register();
            getLogger().info("PlaceholderAPI found. Fishing placeholders registered.");
        } else {
            getLogger().warning("PlaceholderAPI not found. Placeholders will be unavailable.");
        }
    }

    @Override
    public void onDisable() {
        if (manager != null) manager.forceStop(false);
    }

    public FishingManager getManager() { return manager; }
}
