package com.iwak.fishing;

import org.bukkit.Material;
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
            Material caught = event.getCaught().getType();
            manager.addCatch(event.getPlayer(), caught);
            event.getPlayer().sendMessage(Messages.get("fish-caught")
                    .replace("%fish%", caught.name()));
        }
    }
}
