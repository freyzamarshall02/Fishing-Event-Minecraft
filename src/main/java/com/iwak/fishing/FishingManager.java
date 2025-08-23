package com.iwak.fishing;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class FishingManager {

    private final FishingEventPlugin plugin;
    private boolean eventRunning = false;
    private int taskId = -1;
    private int remainingTime = 0;
    private BossBar bossBar;

    // Player UUID -> PlayerStats
    private final Map<UUID, PlayerStats> stats = new HashMap<>();
    private final Map<Material, Integer> fishScores = new HashMap<>();
    private final int minimumPlayers;
    private final boolean enablePrizes;
    private final Map<Integer, String> prizes = new HashMap<>();

    public FishingManager(FishingEventPlugin plugin) {
        this.plugin = plugin;

        // Load config
        FileConfiguration config = plugin.getConfig();
        this.minimumPlayers = config.getInt("minimum-players", 2);
        this.enablePrizes = config.getBoolean("enable-prizes", true);

        // Load prize commands
        if (config.isConfigurationSection("prizes")) {
            for (String key : config.getConfigurationSection("prizes").getKeys(false)) {
                try {
                    int rank = Integer.parseInt(key);
                    prizes.put(rank, config.getString("prizes." + key));
                } catch (NumberFormatException ignored) {}
            }
        }

        // Load fish scores
        if (config.isConfigurationSection("fish-scores")) {
            for (String key : config.getConfigurationSection("fish-scores").getKeys(false)) {
                try {
                    Material mat = Material.valueOf(key.toUpperCase());
                    int score = config.getInt("fish-scores." + key);
                    fishScores.put(mat, score);
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }

    public boolean isRunning() {
        return eventRunning;
    }

    public void startEvent(int durationSeconds) {
        if (eventRunning) return;

        if (Bukkit.getOnlinePlayers().size() < minimumPlayers) {
            Bukkit.broadcastMessage("§cNot enough players to start the Fishing Event! Minimum: " + minimumPlayers);
            return;
        }

        eventRunning = true;
        remainingTime = durationSeconds;
        stats.clear();

        // Create BossBar
        bossBar = Bukkit.createBossBar("§bFishing Event - Time Left: " + remainingTime + "s",
                BarColor.BLUE, BarStyle.SEGMENTED_10);
        bossBar.setVisible(true);
        Bukkit.getOnlinePlayers().forEach(bossBar::addPlayer);

        // Countdown task
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            remainingTime--;

            double progress = Math.max(0, (double) remainingTime / durationSeconds);
            bossBar.setProgress(progress);
            bossBar.setTitle("§bFishing Event - Time Left: " + remainingTime + "s");

            if (remainingTime <= 0) {
                forceStop(true);
            }
        }, 20L, 20L);

        Bukkit.broadcastMessage("§aFishing Event started for " + durationSeconds + " seconds!");
    }

    public void addCatch(Player player, Material type, int amount) {
        if (!eventRunning) return;

        int score = fishScores.getOrDefault(type, 1) * amount;

        PlayerStats ps = stats.computeIfAbsent(player.getUniqueId(),
                uuid -> new PlayerStats(player.getName()));
        ps.addFish(score);

        Bukkit.broadcastMessage("§e" + player.getName() + " caught " + type.name() +
                " §7(Total Score: §a" + ps.getScore() + "§7)");

        updateHologram();
    }

    private void updateHologram() {
        // Hook PlaceholderAPI or DecentHolograms here if needed
    }

    public void forceStop(boolean announceWinners) {
        if (!eventRunning) return;

        eventRunning = false;

        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }

        if (bossBar != null) {
            bossBar.removeAll();
            bossBar = null;
        }

        if (announceWinners) {
            Bukkit.broadcastMessage("§bFishing Event Ended!");
            List<PlayerStats> top = getTopPlayers(3);

            for (int i = 0; i < top.size(); i++) {
                PlayerStats ps = top.get(i);
                int rank = i + 1;
                Bukkit.broadcastMessage("§6#" + rank + " §e" + ps.getName() +
                        " §7- Fish: §a" + ps.getFishCount() +
                        " §7Score: §a" + ps.getScore());

                // Give prize if enabled
                if (enablePrizes && prizes.containsKey(rank)) {
                    String cmd = prizes.get(rank).replace("%player%", ps.getName());
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                    Bukkit.broadcastMessage("§d#" + rank + " " + ps.getName() + " won prize: §f" + cmd);
                }
            }
        }
    }

    public List<PlayerStats> getTopPlayers(int limit) {
        return stats.values().stream()
                .sorted(Comparator.comparingInt(PlayerStats::getScore).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    // Placeholder support
    public String getTopName(int rank) {
        List<PlayerStats> top = getTopPlayers(rank);
        if (top.size() >= rank) {
            return top.get(rank - 1).getName();
        }
        return "§7---";
    }

    public String getTopScore(int rank) {
        List<PlayerStats> top = getTopPlayers(rank);
        if (top.size() >= rank) {
            return String.valueOf(top.get(rank - 1).getScore());
        }
        return "§7---";
    }

    public String getTopFishCount(int rank) {
        List<PlayerStats> top = getTopPlayers(rank);
        if (top.size() >= rank) {
            return String.valueOf(top.get(rank - 1).getFishCount());
        }
        return "§7---";
    }
}
