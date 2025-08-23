package com.iwak.fishing;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class FishingManager {
    private final FishingEventPlugin plugin;
    private final Map<UUID, PlayerStats> stats = new HashMap<>();
    private boolean eventRunning = false;
    private int taskId = -1;

    public FishingManager(FishingEventPlugin plugin) {
        this.plugin = plugin;
    }

    public void startEvent(int durationSeconds) {
        if (eventRunning) return;

        if (Bukkit.getOnlinePlayers().size() < plugin.getConfig().getInt("min-players", 2)) {
            Bukkit.broadcastMessage(Messages.get("not-enough-players"));
            return;
        }

        eventRunning = true;
        stats.clear();
        Bukkit.broadcastMessage(Messages.get("event-started"));

        taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::stopEvent, durationSeconds * 20L);
    }

    public void stopEvent() {
        if (!eventRunning) return;

        eventRunning = false;
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }

        if (stats.isEmpty()) {
            Bukkit.broadcastMessage(Messages.get("no-participants"));
            return;
        }

        List<PlayerStats> leaderboard = stats.values().stream()
                .sorted(Comparator.comparingInt(PlayerStats::getPoints).reversed())
                .collect(Collectors.toList());

        Bukkit.broadcastMessage(Messages.get("event-ended"));
        announceWinners(leaderboard);
    }

    private void announceWinners(List<PlayerStats> leaderboard) {
        FileConfiguration config = plugin.getConfig();
        boolean prizeEnabled = config.getBoolean("prizes.enabled", false);

        for (int i = 0; i < Math.min(3, leaderboard.size()); i++) {
            PlayerStats winner = leaderboard.get(i);
            Player player = Bukkit.getPlayer(winner.getName());
            if (player == null) continue;

            String message = Messages.get("winner-announcement")
                    .replace("%position%", String.valueOf(i + 1))
                    .replace("%player%", winner.getName())
                    .replace("%points%", String.valueOf(winner.getPoints()));
            Bukkit.broadcastMessage(message);

            if (prizeEnabled) {
                String prizeCommand = config.getString("prizes." + (i + 1), "");
                if (!prizeCommand.isEmpty()) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), prizeCommand.replace("<player>", winner.getName()));

                    String prizeMessage = Messages.get("prize-given")
                            .replace("%position%", String.valueOf(i + 1))
                            .replace("%player%", winner.getName())
                            .replace("%prize%", prizeCommand);
                    Bukkit.broadcastMessage(prizeMessage);
                }
            }
        }
    }

    public void resetLeaderboard() {
        stats.clear();
        Bukkit.broadcastMessage(Messages.get("leaderboard-reset"));
    }

    public void addCatch(Player player, Material material) {
        if (!eventRunning) return;

        stats.putIfAbsent(player.getUniqueId(), new PlayerStats(player.getName()));
        int points = plugin.getConfig().getInt("fish-scores." + material.name(), 1);
        stats.get(player.getUniqueId()).addCatch(points);
    }

    public String getTopName(int pos) {
        return stats.values().stream()
                .sorted(Comparator.comparingInt(PlayerStats::getPoints).reversed())
                .skip(pos - 1).map(PlayerStats::getName).findFirst().orElse("-");
    }

    public int getTopScore(int pos) {
        return stats.values().stream()
                .sorted(Comparator.comparingInt(PlayerStats::getPoints).reversed())
                .skip(pos - 1).map(PlayerStats::getPoints).findFirst().orElse(0);
    }

    public int getTopFishCount(int pos) {
        return stats.values().stream()
                .sorted(Comparator.comparingInt(PlayerStats::getPoints).reversed())
                .skip(pos - 1).map(PlayerStats::getFishCaught).findFirst().orElse(0);
    }
}
