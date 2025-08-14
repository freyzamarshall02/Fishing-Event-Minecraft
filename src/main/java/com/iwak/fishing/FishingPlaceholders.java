package com.iwak.fishing;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FishingPlaceholders extends PlaceholderExpansion {

    private final FishingManager manager;

    public FishingPlaceholders(FishingManager manager) {
        this.manager = manager;
    }

    @Override public @NotNull String getIdentifier() { return "fishing_event"; }
    @Override public @NotNull String getAuthor() { return "iwak"; }
    @Override public @NotNull String getVersion() { return "1.0.0"; }
    @Override public boolean persist() { return true; }

    // New-style hook (PAPI 2.11+)
    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        return resolve(params);
    }

    // Old-style hook (some plugins still use this)
    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        return resolve(params);
    }

    private String resolve(String params) {
        // Supports:
        // 1_name .. 5_name
        // 1_score .. 5_score
        String p = params.toLowerCase();
        switch (p) {
            case "1_name":  return manager.getTopName(1);
            case "2_name":  return manager.getTopName(2);
            case "3_name":  return manager.getTopName(3);
            case "4_name":  return manager.getTopName(4);
            case "5_name":  return manager.getTopName(5);
            case "1_score": return manager.getTopScore(1);
            case "2_score": return manager.getTopScore(2);
            case "3_score": return manager.getTopScore(3);
            case "4_score": return manager.getTopScore(4);
            case "5_score": return manager.getTopScore(5);
            default: return "";
        }
    }
}
