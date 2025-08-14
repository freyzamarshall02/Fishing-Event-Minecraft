package com.iwak.fishing;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class FishListener implements Listener {

    private final FishingManager manager;

    public FishListener(FishingManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        manager.onJoin(e.getPlayer());
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (!manager.isRunning()) return;
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        if (!(event.getCaught() instanceof Item item)) return;

        ItemStack stack = item.getItemStack();
        Material type = stack.getType();

        manager.pointsFor(type).ifPresent(pointsPer -> {
            Player p = event.getPlayer();
            int amount = Math.max(1, stack.getAmount());
            // Count fish by amount, add points once, broadcast once
            manager.addCatch(p, pointsPer * amount, type); // see note below
        });
    }
}
