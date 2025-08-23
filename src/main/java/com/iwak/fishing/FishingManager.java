package com.iwak.fishing;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;

import java.util.*;

public class FishingManager {
    private final Map<UUID, PlayerStats> stats = new HashMap<>();
    private final Map<EntityType, Integer> fishPoints = new HashMap<>();
    private boolean eventRunning = false;

    public void startEvent() {
        stats.clear();
        eventRunning = true;
    }

    public void stopEvent() {
        eventRunning = false;
    }

    public boolean isEventRunning() {
        return eventRunning;
    }

    public void addCatch(UUID playerId, EntityType fishType) {
        if (!eventRunning) return;

        PlayerStats playerStats = stats.computeIfAbsent(playerId, PlayerStats::new);
        int points = getPointsFor(fishType);
        playerStats.addPoints(points);
    }

    public int getPointsFor(EntityType type) {
        return fishPoints.getOrDefault(type, 0);
    }

    public void setPoints(EntityType type, int points) {
        fishPoints.put(type, points);
    }

    public List<PlayerStats> getTopPlayers(int limit) {
        return stats.values().stream()
                .sorted(Comparator.comparingInt(PlayerStats::getPoints).reversed())
                .limit(limit)
                .toList();
    }

    public void announceWinners() {
        List<PlayerStats> winners = getTopPlayers(3);

        Bukkit.broadcastMessage("§e--- Fishing Event Winners ---");
        for (int i = 0; i < winners.size(); i++) {
            PlayerStats winner = winners.get(i);
            Bukkit.broadcastMessage("§6#" + (i + 1) + " §f" + winner.getName() + " §7- §a" + winner.getPoints() + " pts");
        }
    }

    public int getPoints(UUID playerId) {
        return stats.getOrDefault(playerId, new PlayerStats(playerId)).getPoints();
    }
}
