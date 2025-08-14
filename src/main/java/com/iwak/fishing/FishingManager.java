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

    public FishingManager(FishingEventPlugin plugin) {
        this.plugin = plugin;
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

        Bukkit.broadcastMessage("§e" + player.getName() + " caught " + formatFishName(fishName) +
                " §7(Total Score: §a" + ps.getScore() + "§7)");

        updateHologram();
    }

    private void updateHologram() {
        // Placeholder-driven hologram updates
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

    // Placeholder support with empty ranks
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

    /**
     * Returns the point value for each caught fish type.
     */
    public int pointsFor(Material mat) {
        switch (mat) {
            case COD:
            case RAW_FISH:
                return 1;
            case SALMON:
                return 2;
            case TROPICAL_FISH:
                return 3;
            case PUFFERFISH:
                return 4;
            default:
                return 0; // not counted
        }
    }

    /**
     * Nicely formats fish names instead of showing raw enum names.
     */
    private String formatFishName(String raw) {
        String lower = raw.toLowerCase().replace("_", " ");
        String[] words = lower.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                sb.append(Character.toUpperCase(word.charAt(0)))
                  .append(word.substring(1))
                  .append(" ");
            }
        }
        return sb.toString().trim();
    }
}
