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
        // If you don’t have manager.onJoin in FishingManager, you can remove this method entirely
        // or keep it empty.
        // For now, I’ll keep it just in case you add logic later.
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (!manager.isRunning()) return;
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        if (!(event.getCaught() instanceof Item item)) return;

        ItemStack stack = item.getItemStack();
        Material type = stack.getType();

        int pointsPer = manager.pointsFor(type);
        if (pointsPer <= 0) return; // skip items worth 0 points

        Player player = event.getPlayer();
        int amount = Math.max(1, stack.getAmount());
        String fishName = type.name().replace("_", " ").toLowerCase();

        manager.addCatch(player, fishName, pointsPer * amount);
    }
}
