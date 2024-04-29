package com.teammerge.abandoned.entities;

import com.teammerge.abandoned.enums.Direction;
import com.teammerge.abandoned.records.Index;
import com.teammerge.abandoned.utilities.InsertionSort;
import com.teammerge.abandoned.utilities.wfc.classes.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

public class Player implements Serializable {
    private int condition, fullness, hydration, energy, minutes;

    private Index position;

    private final ArrayList<String> inventory;


    public long timeSinceLastSecond;

    public float inventoryCapacity;

    private HashSet<Index> areasVisited;
    public Player(Index starting) {
        inventory = new ArrayList<>();
        timeSinceLastSecond = 0;
        minutes = Utils.random.nextInt(6, 10);
        condition = 80 + Utils.random.nextInt( 20);
        fullness = 80 + Utils.random.nextInt( 20);
        hydration = 80 + Utils.random.nextInt( 20);
        energy = 80 + Utils.random.nextInt(20);
        position = starting;
        areasVisited = new HashSet<>();
        areasVisited.add(starting);
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
        setFullness(fullness - 2);
        setHydration(hydration - 2);
        setEnergy(energy - 2);

        if(fullness < 10 || hydration < 10 || energy < 5) setCondition(condition - Utils.random.nextInt(1,4));
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

    public float getInventoryCapacity() {
        return inventoryCapacity;
    }

    public void setInventoryCapacity(float inventoryCapacity) {
        this.inventoryCapacity = inventoryCapacity;
    }

    public HashSet<Index> getAreasVisited() {
        return areasVisited;
    }

}
