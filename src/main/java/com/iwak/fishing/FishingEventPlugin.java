package com.iwak.fishing;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class FishingEventPlugin extends JavaPlugin {

    private FishingManager manager;

    @Override
    public void onEnable() {
        saveDefaultConfig(); // generate config.yml if missing
        this.manager = new FishingManager(this);

        // Register placeholders if PlaceholderAPI is present
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new FishingPlaceholders(manager).register();
            getLogger().info("FishingEvent placeholders registered!");
        } else {
            getLogger().warning("PlaceholderAPI not found - placeholders will not work!");
        }

        getServer().getPluginManager().registerEvents(new FishListener(manager), this);
        getCommand("fishingstart").setExecutor(new FishingStartCommand(manager));
        getCommand("fishingstop").setExecutor(new FishingStopCommand(manager));
        getCommand("fishingreset").setExecutor(new FishingResetCommand(manager));
    }

    @Override
    public void onDisable() {
        if (manager != null) manager.forceStop(false);
    }

    public FishingManager getManager() {
        return manager;
    }
}
