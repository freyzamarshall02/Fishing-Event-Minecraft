package com.iwak.fishing;

import org.bukkit.Bukkit;
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

    // Config values
    private final int minimumPlayers;
    private final boolean enablePrizes;
    private final Map<Integer, String> prizes;

    public FishingManager(FishingEventPlugin plugin) {
        this.plugin = plugin;

        // Load config values
        this.minimumPlayers = plugin.getConfig().getInt("minimum-players", 2);
        this.enablePrizes = plugin.getConfig().getBoolean("enable-prizes", false);

        // Load prizes
        Map<Integer, String> loadedPrizes = new HashMap<>();
        if (plugin.getConfig().isConfigurationSection("prizes")) {
            for (String key : plugin.getConfig().getConfigurationSection("prizes").getKeys(false)) {
                try {
                    int place = Integer.parseInt(key);
                    loadedPrizes.put(place, plugin.getConfig().getString("prizes." + key));
                } catch (NumberFormatException ignored) {}
            }
        }
        this.prizes = loadedPrizes;
    }

    public boolean isRunning() {
        return eventRunning;
    }

    public void startEvent(int durationSeconds) {
        if (eventRunning) return;

        // Check minimum players
        int online = Bukkit.getOnlinePlayers().size();
        if (online < minimumPlayers) {
            Bukkit.broadcastMessage("§cNot enough players to start the Fishing Event! Need at least " + minimumPlayers + " players.");
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

    public void addCatch(Player player, String fishName, int score) {
        if (!eventRunning) return;

        PlayerStats ps = stats.computeIfAbsent(player.getUniqueId(),
                uuid -> new PlayerStats(player.getName()));
        ps.addFish(score);

        Bukkit.broadcastMessage("§e" + player.getName() + " caught " + fishName +
                " §7(Total Score: §a" + ps.getScore() + "§7)");

        updateHologram();
    }

    private void updateHologram() {
        // If using DecentHolograms or PlaceholderAPI, placeholders will pull from getTopName(), getTopScore(), etc.
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
                int place = i + 1;
                Bukkit.broadcastMessage("§6#" + place + " §e" + ps.getName() +
                        " §7- Fish: §a" + ps.getFishCount() +
                        " §7Score: §a" + ps.getScore());

                // Prize handling
                if (enablePrizes && prizes.containsKey(place)) {
                    String rawCommand = prizes.get(place);
                    String command = rawCommand.replace("%player%", ps.getName());
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);

                    Bukkit.broadcastMessage("§dPrize: §6#" + place + " §e" + ps.getName() +
                            " §7won §a" + rawCommand.replace("%player%", ps.getName()));
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
}
