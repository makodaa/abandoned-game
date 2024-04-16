package com.teammerge.abandoned.enums;

import com.teammerge.abandoned.records.Index;

public enum Direction {
    UP(new Index(-1, 0)),
    DOWN(new Index(+1, 0)),
    LEFT(new Index(0, -1)),
    RIGHT(new Index(0, +1)),
    ;

    public final Index vector;

    Direction(Index vector) {
        this.vector = vector;
    }
}
