package com.teammerge.abandoned.entities;

import java.util.Random;

public class Player {
    public int condition, fullness, hydration, energy;
    public long time;
    Random random = new Random();

    public Player() {
        time = 0;
        condition = 80 + random.nextInt(0, 20);
        fullness = 80 + random.nextInt(0, 20);
        hydration = 80 + random.nextInt(0, 20);
        energy = 80 + random.nextInt(0, 20);
    }

    public void tick(long ms) {
        time += ms;
    }

    public void decay(){
        fullness -= random.nextInt(0,2);
        hydration -= random.nextInt(0,2);
        energy -= random.nextInt(0,2);

        if(fullness < 10 || hydration < 10 || energy < 5) condition -= random.nextInt(0,1);
    }

}
