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
        return "fishing_event";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Iwak";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        // Keep this expansion registered even after /papi reload
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        // Examples: %fishing_event_1_name%, %fishing_event_3_score%
        String[] parts = params.split("_");

        if (parts.length == 3) {
            try {
                int rank = Integer.parseInt(parts[1]);
                String type = parts[2].toLowerCase();

                if (rank >= 1 && rank <= 5) {
                    if (type.equals("name")) {
                        return manager.getTopName(rank);
                    } else if (type.equals("score")) {
                        return manager.getTopScore(rank);
                    }
                }
            } catch (NumberFormatException ignored) {}
        }
        return "§7---"; // fallback if format is wrong
    }
}
