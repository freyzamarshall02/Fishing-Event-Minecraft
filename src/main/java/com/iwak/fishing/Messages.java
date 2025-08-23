package com.iwak.fishing;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Messages {
    private static FileConfiguration config;

    public static void load(JavaPlugin plugin) {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
    }

    public static String get(String path) {
        return ChatColor.translateAlternateColorCodes('&', config.getString(path, path));
    }
}
