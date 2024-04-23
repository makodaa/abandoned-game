package com.teammerge.abandoned.utilities.wfc.classes;

import com.teammerge.abandoned.utilities.wfc.enums.AreaType;

import java.util.ArrayList;

public class Area {
    private AreaType type;
    private int distance;
    private String name;
    private double rescueProbability;
    private String[] items;

    public Area(AreaType type, double rescueProbability) {
        this.type = type;
        this.rescueProbability = rescueProbability;

        this.distance = Utils.random.nextInt(31, 73);

        String[] prefixes = type.getPrefixes();
        String[] suffixes = type.getSuffixes();

        this.name = prefixes[Utils.random.nextInt(0, prefixes.length)] + " " + suffixes[Utils.random.nextInt(0, suffixes.length)];

        ArrayList<String> items = new ArrayList<>();
        String[] lootTable = type.getLootTable();
        for (String item : lootTable) {
            int countOfThisItem = Utils.random.nextInt(0, 2);
            for (int j = 0; j < countOfThisItem; ++j) {
                items.add(item);
            }
        }

        this.items = loadItems();
    }

    private String[] loadItems() {
        ArrayList<String> items = new ArrayList<>();
        String[] lootTable = type.getLootTable();
        for (String item : lootTable) {
            int countOfThisItem = Utils.random.nextInt(0, 2);
            for (int j = 0; j < countOfThisItem; ++j) {
                items.add(item);
            }
        }

        return items.toArray(new String[0]);
    }

    public AreaType getType() {
        return this.type;
    }

    public AreaType setType(AreaType value) {
        return this.type = value;
    }

    public int getDistance() {
        return this.distance;
    }

    public int setDistance(int value) {
        return this.distance = value;
    }

    public String getName() {
        return this.name;
    }

    public String setName(String value) {
        return this.name = value;
    }

    public double getRescueProbability() {
        return this.rescueProbability;
    }

    public double setRescueProbability(double value) {
        return this.rescueProbability = value;
    }

    public String[] getItems() {
        return this.items;
    }
}
