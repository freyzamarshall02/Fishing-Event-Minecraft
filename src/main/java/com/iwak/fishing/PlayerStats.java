package com.iwak.fishing;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class PlayerStats {
    private final UUID playerId;
    private int points;

    public PlayerStats(UUID playerId) {
        this.playerId = playerId;
        this.points = 0;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public String getName() {
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerId);
        return player != null && player.getName() != null ? player.getName() : "Unknown";
    }

    public int getPoints() {
        return points;
    }

    public void addPoints(int amount) {
        this.points += amount;
    }
}
