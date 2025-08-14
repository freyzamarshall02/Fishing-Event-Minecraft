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
        // Ensure the joining player sees boss bar if event is running
        if (manager.isRunning()) {
            manager.addPlayerToBossBar(e.getPlayer());
        }
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (!manager.isRunning()) return;
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        if (!(event.getCaught() instanceof Item item)) return;

        ItemStack stack = item.getItemStack();
        Material type = stack.getType();

        int pointsPer = manager.pointsFor(type);
        if (pointsPer <= 0) return; // skip items that aren't counted

        Player p = event.getPlayer();
        int amount = Math.max(1, stack.getAmount());
        int totalPoints = pointsPer * amount;

        manager.addCatch(p, type.name(), totalPoints);
    }
}
