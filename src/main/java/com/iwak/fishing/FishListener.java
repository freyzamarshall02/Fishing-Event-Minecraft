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

        int points = manager.getPointsFor(type);
        if (points > 0) {
            Player p = event.getPlayer();
            int amount = Math.max(1, stack.getAmount());
            manager.addCatch(p, points * amount, type);
        }
    }
}
