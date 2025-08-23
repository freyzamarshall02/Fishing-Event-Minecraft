package com.iwak.fishing;

import java.util.UUID;

public class PlayerStats {
    private final UUID uuid;
    private final String name;
    private int fishCaught;

    public PlayerStats(String name) {
        this.uuid = UUID.randomUUID(); // If you want real UUID, pass it instead of random
        this.name = name;
        this.fishCaught = 0;
    }

    public PlayerStats(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.fishCaught = 0;
    }

    public String getName() {
        return name;
    }

    public int getFishCaught() {
        return fishCaught;
    }

    public void incrementFishCaught() {
        this.fishCaught++;
    }

    public UUID getUuid() {
        return uuid;
    }
}
