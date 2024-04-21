package com.teammerge.abandoned.entities;

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

    public void build(Player player){
        // Remove 3 Firewood from Player
        isBuilt = true;
    }
    public void leave(){
        isBuilt = false;
    }

    // Takes Player as parameter and modifies inventory, if fire starter 70% to light, if matches 95% to light
    public void light(Player Player){
        // Removes a tinder and a match from player
        secondsRemaining = 120;
    }

    // Takes Player as parameter, modifies inventory, if tinder +10s, if Firewood +2h , if Hardwood +6h
    public void addWood(Player player){
        switch(10){
            case 1:
                secondsRemaining += 10;
                break;
            case 2:
                secondsRemaining += 30;
                break;
            case 3:
                secondsRemaining += 120;
                break;
            case 4:
                secondsRemaining += 360;
                break;
        }
    }
}
