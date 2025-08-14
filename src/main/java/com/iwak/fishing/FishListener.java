package com.iwak.fishing;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

public class FishListener implements Listener {

    private final FishingManager manager;

    public FishListener(FishingManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (!manager.isRunning()) return;
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        if (!(event.getCaught() instanceof Item item)) return;

        ItemStack stack = item.getItemStack();
        Material type = stack.getType();

        manager.pointsFor(type).ifPresent(points -> {
            Player p = event.getPlayer();
            // Count each fish item in the stack (usually 1 for fishing)
            int totalPoints = points * Math.max(1, stack.getAmount());
            for (int i = 0; i < Math.max(1, stack.getAmount()); i++) {
                manager.addCatch(p, points, type);
            }
        });
    }
}
