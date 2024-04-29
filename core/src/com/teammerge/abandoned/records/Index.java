package com.teammerge.abandoned.records;

import java.io.Serializable;

public record Index(int y, int x) implements Serializable {
    public Index add(Index other) {
        return new Index(this.y + other.y, this.x + other.x);
    }

    public int squareDistance(Index other) {
        int dy = this.y - other.y;
        int dx = this.x - other.x;

        return dy * dy + dx * dx;
    }
}
