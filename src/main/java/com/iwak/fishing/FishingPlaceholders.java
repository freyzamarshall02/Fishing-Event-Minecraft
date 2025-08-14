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
        return "fishing_event"; // this is the prefix in %fishing_event_1_name%
    }

    @Override
    public @NotNull String getAuthor() {
        return "Iwak"; // change to your name
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true; // Keep registered after reloads
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        // Example: params = "1_name" or "3_score"
        String[] split = params.split("_");
        if (split.length != 2) return null;

        try {
            int rank = Integer.parseInt(split[0]);
            String type = split[1];

            if (type.equalsIgnoreCase("name")) {
                return manager.getTopName(rank);
            } else if (type.equalsIgnoreCase("score")) {
                return manager.getTopScore(rank);
            }
        } catch (NumberFormatException ignored) {}

        return null;
    }
}
