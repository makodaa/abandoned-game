package com.teammerge.abandoned.utilities.wfc.classes;

import com.teammerge.abandoned.utilities.wfc.enums.AreaType;

public class Area {
    private AreaType type;
    private int distance;
    private String name;
    private double rescueProbability;

    public Area(AreaType type, double rescueProbability) {
        this.type = type;
        this.rescueProbability = rescueProbability;

        this.distance = Utils.random.nextInt(31, 73);

        String[] suffixes = type.getSuffixes();

        // TODO: Change this to something more suitable.
        this.name = suffixes[Utils.random.nextInt(0, suffixes.length)];
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
}
