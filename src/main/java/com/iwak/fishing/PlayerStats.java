package com.iwak.fishing;

public class PlayerStats {
    private final String name;
    private int fishCount;
    private int score;

    public PlayerStats(String name) {
        this.name = name;
        this.fishCount = 0;
        this.score = 0;
    }

    public void addFish(int scoreToAdd) {
        this.fishCount++;
        this.score += scoreToAdd;
    }

    public String getName() {
        return name;
    }

    public int getFishCount() {
        return fishCount;
    }

    public int getScore() {
        return score;
    }
}
