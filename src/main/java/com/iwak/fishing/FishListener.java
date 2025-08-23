package com.iwak.fishing;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

public class FishListener implements Listener {
    private final FishingManager manager;

    public FishListener(FishingManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            Entity caught = event.getCaught();
            if (caught != null) {
                Player player = event.getPlayer();
                EntityType type = caught.getType();

                manager.addCatch(player.getUniqueId(), type);

                int points = manager.getPointsFor(type);
                if (points > 0) {
                    player.sendMessage("§aYou caught a " + type.name() + "! +" + points + " points");
                }
            }
        }
    }
}
