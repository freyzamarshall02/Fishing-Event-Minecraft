package com.iwak.fishing;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;

public class Messages {
    private static FileConfiguration messagesConfig;
    private static File messagesFile;

    public static void init(JavaPlugin plugin) {
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");

        // Copy default if missing
        if (!messagesFile.exists()) {
            try (InputStream in = plugin.getResource("messages.yml")) {
                if (in != null) {
                    plugin.getDataFolder().mkdirs();
                    Files.copy(in, messagesFile.toPath());
                }
            } catch (Exception e) {
                plugin.getLogger().severe("Could not create default messages.yml!");
                e.printStackTrace();
            }
        }

        // Load config
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);

        // Load defaults from JAR (so missing keys auto-fill)
        try (InputStream defStream = plugin.getResource("messages.yml")) {
            if (defStream != null) {
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defStream));
                messagesConfig.setDefaults(defConfig);
            }
        } catch (Exception ignored) {}
    }

    public static String get(String path) {
        if (messagesConfig == null) return ChatColor.RED + "Messages not loaded!";
        String msg = messagesConfig.getString(path, "&cMissing message: " + path);
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}
