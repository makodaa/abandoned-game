package com.teammerge.abandoned.enums;

import com.teammerge.abandoned.records.Index;

public enum Direction {
    UP("North", new Index(-1, 0)),
    DOWN("South", new Index(+1, 0)),
    LEFT("West", new Index(0, -1)),
    RIGHT("East", new Index(0, +1)),
    ;

    private final Index vector;
    private final String cardinalName;

    Direction(String cardinalName, Index vector) {
        this.cardinalName = cardinalName;
        this.vector = vector;
    }

    public Index getVector() {
        return this.vector;
    }

    public String getCardinalName() {
        return this.cardinalName;
    }
}
