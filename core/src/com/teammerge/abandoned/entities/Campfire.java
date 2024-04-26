package com.teammerge.abandoned.entities;

import java.util.Random;

public class Campfire {

    // Define an instance of player to access inventory
    private float secondsRemaining;
    private boolean isBuilt;

    public Campfire() {
        isBuilt = false;
        secondsRemaining = 0;
    }

    public float getSecondsRemaining() {
        return secondsRemaining;
    }

    public void setSecondsRemaining(float secondsRemaining) {
        this.secondsRemaining = secondsRemaining;
    }

    public boolean isBuilt() {
        return isBuilt;
    }

    public void setBuilt(boolean built) {
        isBuilt = built;
    }

    public void build(Player player) {
        // Remove 3 Firewood from Player
        for (int i = 0; i < 3; i++) player.getInventory().remove("firewood");
        setBuilt(true);
    }

    // Takes Player as parameter and modifies inventory, if fire starter 70% to light, if matches 95% to light
    public boolean lightByMatches(Player player) {
        // Removes a tinder and a match from player
        Random random = new Random();
        if (0.10 < random.nextDouble()) {
            player.getInventory().remove("matches");
            player.getInventory().remove("tinder");
            setSecondsRemaining(120);
        }
        return 0 < getSecondsRemaining();
    }

    public boolean lightByFireStarter(Player player) {
        // Removes a tinder and a match from player
        Random random = new Random();
        if (0.20 < random.nextDouble()) {
            player.getInventory().remove("tinder");
            setSecondsRemaining(120);
        }

        return 0 < getSecondsRemaining();
    }


    // Takes Player as parameter, modifies inventory, if tinder +30s, if Firewood +2h , if Hardwood +6h
    public void addTinder(Player player) {
        player.getInventory().remove("tinder");
        setSecondsRemaining(getSecondsRemaining() + 30);
    }

    public void addFirewood(Player player) {
        player.getInventory().remove("firewood");
        setSecondsRemaining(getSecondsRemaining() + 120);
    }

    public void addHardwood(Player player) {
        player.getInventory().remove("hardwood");
        setSecondsRemaining(getSecondsRemaining() + 360);
    }

    public Campfire getCampfire() {
        return this;
    }
}