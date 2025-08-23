package com.iwak.fishing;

public class PlayerStats {
    private final String playerName;
    private int points;
    private int fishCaught;

    public PlayerStats(String playerName) {
        this.playerName = playerName;
        this.points = 0;
        this.fishCaught = 0;
    }

    public void addCatch(int points) {
        this.points += points;
        this.fishCaught++;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getPoints() {
        return points;
    }

    public int getFishCaught() {
        return fishCaught;
    }
}
