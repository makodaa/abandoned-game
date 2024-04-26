package com.teammerge.abandoned.entities;

import java.util.Random;

public class FishBasketTrap {
    boolean isBuilt;
    int baitRemaining;

    public FishBasketTrap() {
        isBuilt = false;
        baitRemaining = 0;
    }

    public void build(Player player){
//        Removes 5 stick and 2 rope from inventory
        for (int i=0;i<5;i++) {player.getInventory().remove("stick");}
        for (int i=0;i<2;i++) {player.getInventory().remove("rope");}
        setBuilt(true);
    }

//    Gives 1-3 Raw Fish, Removes 1 Bait;
    public void collect(Player player) {
        Random random = new Random();
        for (int i = 0; i < random.nextInt(1,4); i++) {
            player.getInventory().add("raw_fish");
        }
        setBaitRemaining(getBaitRemaining() - 1);
    }

//    Adds bait to trap
    public void addBait(Player player){
        player.getInventory().remove("bait");
        setBaitRemaining(getBaitRemaining() + 1);
    }

    public boolean isBuilt() {
        return isBuilt;
    }

    public void setBuilt(boolean built) {
        isBuilt = built;
    }

    public int getBaitRemaining() {
        return baitRemaining;
    }

    public void setBaitRemaining(int baitRemaining) {
        this.baitRemaining = baitRemaining;
    }
}
