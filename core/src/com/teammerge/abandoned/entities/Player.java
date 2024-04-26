package com.teammerge.abandoned.entities;

import com.teammerge.abandoned.enums.Direction;
import com.teammerge.abandoned.records.Index;
import com.teammerge.abandoned.utilities.InsertionSort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Player {
    private int condition, fullness, hydration, energy, minutes;

    private Index position;

    private final ArrayList<String> inventory;

    public long timeSinceLastSecond;
    Random random = new Random();

    public Player(Index starting) {
        inventory = new ArrayList<>();
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
            minutes++;
            decay();
        };
    }

    public void decay(){
        setFullness(fullness - random.nextInt(1,3));
        setHydration(hydration - random.nextInt(1,3));
        setEnergy(energy - random.nextInt(1,3));

        if(fullness < 10 || hydration < 10 || energy < 5) setCondition(condition - random.nextInt(0,3));
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

    public ArrayList<String> getInventory() {
        return this.inventory;
    }

    public Index getPosition() { return this.position; }

    public void addAllItems(String ...item) {
        inventory.addAll(Arrays.asList(item));
        InsertionSort.run(inventory, String::compareTo);
    }

    public void addItem(String item) {
        inventory.add(item);
        InsertionSort.run(inventory, String::compareTo);
    }
}
