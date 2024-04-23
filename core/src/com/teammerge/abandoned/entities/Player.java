package com.teammerge.abandoned.entities;

import com.teammerge.abandoned.enums.Direction;
import com.teammerge.abandoned.records.Index;
import com.teammerge.abandoned.records.Item;
import com.teammerge.abandoned.utilities.items.ItemRepository;

import java.util.Arrays;
import java.util.Random;

public class Player {
    private int condition, fullness, hydration, energy, minutes;

    private Index position;

    public long timeSinceLastSecond;
    Random random = new Random();

    public Player(Index starting) {
        timeSinceLastSecond = 0;
        minutes = random.nextInt(0, 8);
        condition = 80 + random.nextInt( 20);
        fullness = 80 + random.nextInt( 20);
        hydration = 80 + random.nextInt( 20);
        energy = 80 + random.nextInt(20);
        position = starting;
    }

    public void move(Direction direction) {
        this.position = position.add(direction.getVector());
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
        setFullness(fullness - random.nextInt(0,2));
        setHydration(hydration - random.nextInt(0,2));
        setEnergy(energy - random.nextInt(0,2));

        if(fullness < 10 || hydration < 10 || energy < 5) setCondition(condition - random.nextInt(0,2));
    }

    public void setCondition(int condition) { this.condition = Math.min(Math.max(condition,0),100); }

    public void setFullness(int fullness) {
        this.fullness = Math.min(Math.max(fullness, 0),100);
    }

    public void setHydration(int hydration) {
        this.hydration = Math.min(Math.max(hydration,0),100);
    }

    public void setEnergy(int energy) {
        this.energy = Math.min(Math.max(energy,0), 100);
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getCondition() {
        return condition;
    }

    public int getFullness() {
        return fullness;
    }

    public int getHydration() {
        return hydration;
    }

    public int getEnergy() {
        return energy;
    }

    public long getTimeSinceLastSecond() {
        return timeSinceLastSecond;
    }

    public Index getPosition() { return this.position; }
}
