package com.iwak.fishing;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class FishingManager {
    private final FishingEventPlugin plugin;
    private final Map<UUID, PlayerStats> leaderboard = new HashMap<>();
    private final Map<Material, Integer> fishPoints = new HashMap<>();
    private boolean running = false;
    private int minimumPlayers;
    private boolean prizeEnabled;
    private List<String> prizeCommands;

    public FishingManager(FishingEventPlugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();

        minimumPlayers = config.getInt("minimum-players", 2);
        prizeEnabled = config.getBoolean("prizes.enabled", false);
        prizeCommands = config.getStringList("prizes.commands");

        fishPoints.clear();
        if (config.isConfigurationSection("fish-points")) {
            for (String key : config.getConfigurationSection("fish-points").getKeys(false)) {
                try {
                    Material material = Material.valueOf(key.toUpperCase());
                    int points = config.getInt("fish-points." + key, 1);
                    fishPoints.put(material, points);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid material in config: " + key);
                }
            }
        }
    }

    public void startEvent(int seconds) {
        if (running) {
            Bukkit.broadcastMessage(ChatColor.RED + "A fishing event is already running!");
            return;
        }

        if (Bukkit.getOnlinePlayers().size() < minimumPlayers) {
            Bukkit.broadcastMessage(ChatColor.YELLOW + "Not enough players to start the fishing event! Need at least " + minimumPlayers + ".");
            return;
        }

        running = true;
        leaderboard.clear();
        Bukkit.broadcastMessage(ChatColor.GREEN + "Fishing event has started for " + seconds + " seconds!");

        Bukkit.getScheduler().runTaskLater(plugin, this::stopEvent, seconds * 20L);
    }

    public void stopEvent() {
        if (!running) return;
        running = false;

        Bukkit.broadcastMessage(ChatColor.RED + "The fishing event has ended!");

        List<PlayerStats> winners = leaderboard.values().stream()
                .sorted((a, b) -> Integer.compare(b.getPoints(), a.getPoints()))
                .limit(3)
                .collect(Collectors.toList());

        for (int i = 0; i < winners.size(); i++) {
            PlayerStats stats = winners.get(i);
            int place = i + 1;
            Bukkit.broadcastMessage(ChatColor.GOLD + "#" + place + " " + stats.getPlayerName()
                    + " - " + stats.getPoints() + " points (" + stats.getFishCaught() + " fish)");

            if (prizeEnabled && place <= prizeCommands.size()) {
                String command = prizeCommands.get(i)
                        .replace("<player>", stats.getPlayerName())
                        .replace("<points>", String.valueOf(stats.getPoints()))
                        .replace("<fish>", String.valueOf(stats.getFishCaught()));

                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                Bukkit.broadcastMessage(ChatColor.AQUA + "#"+ place + " " + stats.getPlayerName() + " won prize: " + command);
            }
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void addCatch(Player player, Material type) {
        if (!running) return;

        int points = fishPoints.getOrDefault(type, 1);
        leaderboard.putIfAbsent(player.getUniqueId(), new PlayerStats(player.getName()));
        leaderboard.get(player.getUniqueId()).addCatch(points);
    }

    public void resetLeaderboard() {
        leaderboard.clear();
    }

    public String getTopName(int place) {
        return leaderboard.values().stream()
                .sorted((a, b) -> Integer.compare(b.getPoints(), a.getPoints()))
                .skip(place - 1)
                .map(PlayerStats::getPlayerName)
                .findFirst()
                .orElse("-");
    }

    public int getTopScore(int place) {
        return leaderboard.values().stream()
                .sorted((a, b) -> Integer.compare(b.getPoints(), a.getPoints()))
                .skip(place - 1)
                .map(PlayerStats::getPoints)
                .findFirst()
                .orElse(0);
    }

    public int getTopFishCount(int place) {
        return leaderboard.values().stream()
                .sorted((a, b) -> Integer.compare(b.getPoints(), a.getPoints()))
                .skip(place - 1)
                .map(PlayerStats::getFishCaught)
                .findFirst()
                .orElse(0);
    }
}
