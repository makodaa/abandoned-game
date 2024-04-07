package com.teammerge.abandoned.entities;

import java.util.Random;

public class Player {
    public int condition, saturation, hydration, energy;
    Random random = new Random();

    public Player() {
        condition = (60 + random.nextInt(40));
        saturation = (50 + random.nextInt(40));
        hydration = (50 + random.nextInt(40));
        energy = (70 + random.nextInt(40));
    }
}
