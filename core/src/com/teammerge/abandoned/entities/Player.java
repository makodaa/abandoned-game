package com.teammerge.abandoned.entities;

import java.util.Random;
public class Player {
    public int condition, fullness, hydration, energy, minutes;
    public long timeSinceLastSecond;
    Random random = new Random();

    public Player() {
        timeSinceLastSecond = 0;
        condition = 80 + random.nextInt( 20);
        fullness = 80 + random.nextInt( 20);
        hydration = 80 + random.nextInt( 20);
        energy = 80 + random.nextInt(20);
        minutes = random.nextInt(0, 8);
    }

    public void tick(double ms) {
        timeSinceLastSecond += (long) ms;
        if (timeSinceLastSecond > 60000) {
            timeSinceLastSecond = 0;
            decay();
            minutes++;
        };
    }

    public void decay(){
        fullness -= random.nextInt(0,2);
        hydration -= random.nextInt(0,2);
        energy -= random.nextInt(0,2);

        if(fullness < 10 || hydration < 10 || energy < 5) condition -= random.nextInt(0,1);
    }

    public void setCondition(int condition) {
        this.condition = Math.min(condition,100);
    }

    public void setFullness(int fullness) {
        this.fullness = Math.min(fullness,100);
    }

    public void setHydration(int hydration) {
        this.hydration = Math.min(hydration,100);
    }

    public void setEnergy(int energy) {
        this.energy = Math.min(energy, 100);
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }


}
