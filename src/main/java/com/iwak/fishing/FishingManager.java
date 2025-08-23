package com.iwak.fishing;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FishingManager {
    private final FishingEventPlugin plugin;
    private final Map<UUID, PlayerStats> playerStats;
    private boolean eventRunning;
    private int taskId = -1;

    public FishingManager(FishingEventPlugin plugin) {
        this.plugin = plugin;
        this.playerStats = new ConcurrentHashMap<>();
        this.eventRunning = false;
    }

    /**
     * Start a fishing event for a given duration in minutes
     */
    public void startEvent(int durationMinutes) {
        if (eventRunning) {
            Bukkit.broadcastMessage(Messages.PREFIX + " §cA fishing event is already running!");
            return;
        }

        eventRunning = true;
        Bukkit.broadcastMessage(Messages.PREFIX + " §aThe fishing event has started! It will last " + durationMinutes + " minutes.");

        // Schedule event stop after duration
        taskId = new BukkitRunnable() {
            @Override
            public void run() {
                stopEvent();
            }
        }.runTaskLater(plugin, durationMinutes * 60L * 20L).getTaskId();
    }

    /**
     * Stop the fishing event and announce the winner
     */
    public void stopEvent() {
        if (!eventRunning) {
            Bukkit.broadcastMessage(Messages.PREFIX + " §cNo fishing event is currently running.");
            return;
        }

        eventRunning = false;

        Bukkit.broadcastMessage(Messages.PREFIX + " §eThe fishing event has ended!");
        announceWinner();

        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
    }

    /**
     * Reset leaderboard stats
     */
    public void resetLeaderboard() {
        playerStats.clear();
        Bukkit.broadcastMessage(Messages.PREFIX + " §bThe fishing leaderboard has been reset!");
    }

    /**
     * Add a catch for a player
     */
    public void recordCatch(Player player) {
        playerStats.computeIfAbsent(player.getUniqueId(), id -> new PlayerStats(player.getName()))
                .incrementFishCaught();
    }

    /**
     * Get top players sorted by catches
     */
    public List<PlayerStats> getTopPlayers(int limit) {
        List<PlayerStats> sorted = new ArrayList<>(playerStats.values());
        sorted.sort(Comparator.comparingInt(PlayerStats::getFishCaught).reversed());
        return sorted.subList(0, Math.min(limit, sorted.size()));
    }

    /**
     * Announce the winner
     */
    private void announceWinner() {
        if (playerStats.isEmpty()) {
            Bukkit.broadcastMessage(Messages.PREFIX + " §cNo one caught any fish during the event!");
            return;
        }

        PlayerStats winner = getTopPlayers(1).get(0);
        Bukkit.broadcastMessage(Messages.PREFIX + " §6The winner is §a" + winner.getName() +
                " §ewith §b" + winner.getFishCaught() + " fish!");
    }

    public boolean isEventRunning() {
        return eventRunning;
    }
}
