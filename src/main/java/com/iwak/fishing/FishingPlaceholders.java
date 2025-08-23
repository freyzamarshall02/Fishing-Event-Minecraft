package com.iwak.fishing;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
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
        return "iwak";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.2";
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) return "";

        // %fishingevent_points%
        if (params.equalsIgnoreCase("points")) {
            return String.valueOf(manager.getPoints(player.getUniqueId()));
        }

        // %fishingevent_top_1_name%
        if (params.startsWith("top_") && params.endsWith("_name")) {
            try {
                int pos = Integer.parseInt(params.split("_")[1]) - 1;
                var top = manager.getTopPlayers(pos + 1);
                if (pos < top.size()) {
                    return top.get(pos).getName();
                }
            } catch (NumberFormatException ignored) {}
            return "";
        }

        // %fishingevent_top_1_points%
        if (params.startsWith("top_") && params.endsWith("_points")) {
            try {
                int pos = Integer.parseInt(params.split("_")[1]) - 1;
                var top = manager.getTopPlayers(pos + 1);
                if (pos < top.size()) {
                    return String.valueOf(top.get(pos).getPoints());
                }
            } catch (NumberFormatException ignored) {}
            return "";
        }

        return null; // not recognized
    }
}
