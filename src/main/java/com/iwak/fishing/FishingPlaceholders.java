package com.iwak.fishing;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FishingPlaceholders extends PlaceholderExpansion {

    private final FishingManager manager;

    public FishingPlaceholders(FishingManager manager) {
        this.manager = manager;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "fishingevent";
    }

    @Override
    public @NotNull String getAuthor() {
        return "YourName";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true; // This ensures it stays loaded
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        // Example: top_name_1
        if (identifier.startsWith("top_name_")) {
            try {
                int rank = Integer.parseInt(identifier.substring("top_name_".length()));
                return manager.getTopName(rank);
            } catch (NumberFormatException ignored) {
            }
        }

        // Example: top_score_1
        if (identifier.startsWith("top_score_")) {
            try {
                int rank = Integer.parseInt(identifier.substring("top_score_".length()));
                return manager.getTopScore(rank);
            } catch (NumberFormatException ignored) {
            }
        }

        // Example: top_fish_1
        if (identifier.startsWith("top_fish_")) {
            try {
                int rank = Integer.parseInt(identifier.substring("top_fish_".length()));
                return manager.getTopFishCount(rank);
            } catch (NumberFormatException ignored) {
            }
        }

        return null; // Placeholder not found
    }
}
