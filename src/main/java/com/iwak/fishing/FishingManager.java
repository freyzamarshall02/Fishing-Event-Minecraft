package com.iwak.fishing;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class FishingManager {

    private final FishingEventPlugin plugin;
    private final Map<UUID, PlayerStats> stats = new HashMap<>();
    private final Map<Material, Integer> fishScores = new HashMap<>();

    private boolean running = false;
    private int taskId = -1;
    private int remainingSeconds = 0;

    private int minimumPlayers;
    private boolean prizesEnabled;
    private Map<Integer, String> prizeCommands = new HashMap<>();

    public FishingManager(FishingEventPlugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();

        FileConfiguration config = plugin.getConfig();

        // Load fish scores
        fishScores.clear();
        if (config.isConfigurationSection("fish-scores")) {
            for (String key : config.getConfigurationSection("fish-scores").getKeys(false)) {
                Material mat = Material.matchMaterial(key.toUpperCase());
                if (mat != null) {
                    fishScores.put(mat, config.getInt("fish-scores." + key, 1));
                } else {
                    plugin.getLogger().warning("Unknown material in config: " + key);
                }
            }
        }

        // Load minimum players
        minimumPlayers = config.getInt("minimum-players", 2);

        // Load prizes
        prizesEnabled = config.getBoolean("prizes.enabled", false);
        prizeCommands.clear();
        if (config.isConfigurationSection("prizes")) {
            for (String key : config.getConfigurationSection("prizes").getKeys(false)) {
                if (key.equalsIgnoreCase("enabled")) continue;
                try {
                    int place = Integer.parseInt(key);
                    prizeCommands.put(place, config.getString("prizes." + key));
                } catch (NumberFormatException ignored) {
                    plugin.getLogger().warning("Invalid prize key: " + key);
                }
            }
        }
    }

    public void start(int seconds) {
        if (running) {
            Bukkit.broadcastMessage("§cFishing event is already running!");
            return;
        }

        if (Bukkit.getOnlinePlayers().size() < minimumPlayers) {
            Bukkit.broadcastMessage("§cNot enough players to start! Minimum required: " + minimumPlayers);
            return;
        }

        running = true;
        remainingSeconds = seconds;
        Bukkit.broadcastMessage("§aFishing event has started! Duration: " + seconds + " seconds.");

        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            remainingSeconds--;

            if (remainingSeconds == 60 || remainingSeconds == 30 || remainingSeconds == 10 || remainingSeconds <= 5 && remainingSeconds > 0) {
                Bukkit.broadcastMessage("§eFishing event ends in " + remainingSeconds + " seconds!");
            }

            if (remainingSeconds <= 0) {
                stop(true);
            }

        }, 20, 20);
    }

    public void stop(boolean announceWinners) {
        if (!running) return;

        running = false;

        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }

        if (announceWinners) {
            announceWinners();
        }
    }

    public void forceStop(boolean announceWinners) {
        if (running) {
            stop(announceWinners);
        }
    }

    public void reset() {
        stats.clear();
        Bukkit.broadcastMessage("§eFishing leaderboard has been reset!");
    }

    public void onJoin(Player player) {
        stats.putIfAbsent(player.getUniqueId(), new PlayerStats());
    }

    public void addCatch(Player player, int points, Material type) {
        stats.putIfAbsent(player.getUniqueId(), new PlayerStats());
        stats.get(player.getUniqueId()).addCatch(points);
        Bukkit.broadcastMessage("§b" + player.getName() + " caught " + type.name() + " +" + points + " points!");
    }

    public boolean isRunning() {
        return running;
    }

    public int getPointsFor(Material material) {
        return fishScores.getOrDefault(material, 0);
    }

    private void announceWinners() {
        List<Map.Entry<UUID, PlayerStats>> sorted = stats.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue().getPoints(), a.getValue().getPoints()))
                .collect(Collectors.toList());

        if (sorted.isEmpty()) {
            Bukkit.broadcastMessage("§cNo one caught any fish!");
            return;
        }

        Bukkit.broadcastMessage("§6--- Fishing Event Results ---");

        int place = 1;
        for (Map.Entry<UUID, PlayerStats> entry : sorted) {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player != null) {
                Bukkit.broadcastMessage("§e#" + place + " §b" + player.getName() + " §7- §a" + entry.getValue().getPoints() + " pts");

                // Give prize
                if (prizesEnabled && prizeCommands.containsKey(place)) {
                    String command = prizeCommands.get(place).replace("<player>", player.getName());
                    ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
                    Bukkit.dispatchCommand(console, command);

                    Bukkit.broadcastMessage("§d#" + place + " §b" + player.getName() + " §dwon prize: §f" + command);
                }
                place++;
            }
            if (place > 3) break; // Only top 3
        }
    }
}
