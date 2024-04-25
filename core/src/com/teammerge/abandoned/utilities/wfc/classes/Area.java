package com.teammerge.abandoned.utilities.wfc.classes;

import com.teammerge.abandoned.utilities.wfc.enums.AreaType;

import java.util.ArrayList;
import java.util.List;

public class Area {
    private AreaType type;
    private int distance;
    private String name;
    private double rescueProbability;
    private final String[] items;

    public Area(AreaType type, double rescueProbability) {
        this.type = type;
        this.rescueProbability = rescueProbability;

        this.distance = Utils.random.nextInt(31, 73);

        String[] prefixes = type.getPrefixes();
        String[] suffixes = type.getSuffixes();

        this.name = prefixes[Utils.random.nextInt(0, prefixes.length)] + " " + suffixes[Utils.random.nextInt(0, suffixes.length)];
        this.items = loadItems();
    }

    private String[] loadItems() {
        ArrayList<String> items = new ArrayList<>();
        String[] lootTable = type.getLootTable();
        for (String item : lootTable) {
            int countOfThisItem = Utils.random.nextInt(0, 3);
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

    public List<String> extract() {
        int count = Utils.random.nextInt(0, items.length);
        /// random sampling.
        ArrayList<String> chosen = new ArrayList<>();
        for (int i = 0; i < count; ++i) {
            int currentIndex = -1;
            float probability;
            int j = 0;
            while (j < items.length) {
                if (items[j] != null) {
                    probability = 1f / (j + 1);
                    if (Utils.random.nextFloat() > probability) {
                        currentIndex = j;
                    }
                }
                j++;
            }

            /// There hasn't been anything chosen in the iteration,
            ///   so it's likely that there are no available items.
            if (currentIndex == -1) {
                break;
            }

            chosen.add(items[currentIndex]);
            items[currentIndex] = null;
        }

        return chosen;
    }
}
