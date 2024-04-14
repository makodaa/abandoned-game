package com.teammerge.abandoned.utilities.wfc.records;

public record Index(int y, int x) {
    public int squareDistance(Index other) {
        int dy = this.y - other.y;
        int dx = this.x - other.x;

        return dy * dy + dx * dx;
    }
}
