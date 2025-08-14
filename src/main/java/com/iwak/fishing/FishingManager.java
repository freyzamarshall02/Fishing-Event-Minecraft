package com.iwak.fishing;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
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
    // Material -> points mapping
    private final Map<Material, Integer> pointsMap = new HashMap<>();

    public FishingManager(FishingEventPlugin plugin) {
        this.plugin = plugin;
        setupDefaultPoints();
    }

    private void setupDefaultPoints() {
        // Example points — customize for your server
        pointsMap.put(Material.COD, 1);
        pointsMap.put(Material.SALMON, 2);
        pointsMap.put(Material.TROPICAL_FISH, 3);
        pointsMap.put(Material.PUFFERFISH, 5);
    }

    public int pointsFor(Material type) {
        return pointsMap.getOrDefault(type, 0);
    }

    public boolean isRunning() {
        return eventRunning;
    }

    public void startEvent(int durationSeconds) {
        if (eventRunning) return;

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

    public void addCatch(Player player, String fishName, int score) {
        if (!eventRunning) return;

        PlayerStats ps = stats.computeIfAbsent(player.getUniqueId(),
                uuid -> new PlayerStats(player.getName()));
        ps.addFish(score);

        Bukkit.broadcastMessage("§e" + player.getName() + " caught " + fishName +
                " §7(Total Score: §a" + ps.getScore() + "§7)");

        updateHologram();
    }

    public void addPlayerToBossBar(Player player) {
        if (bossBar != null) {
            bossBar.addPlayer(player);
        }
    }

    private void updateHologram() {
        // If using DecentHolograms or another plugin, update here via placeholders
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
                Bukkit.broadcastMessage("§6#" + (i + 1) + " §e" + ps.getName() +
                        " §7- Fish: §a" + ps.getFishCount() +
                        " §7Score: §a" + ps.getScore());
            }
        }
    }

    public List<PlayerStats> getTopPlayers(int limit) {
        return stats.values().stream()
                .sorted(Comparator.comparingInt(PlayerStats::getScore).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    // Placeholder-friendly methods
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
