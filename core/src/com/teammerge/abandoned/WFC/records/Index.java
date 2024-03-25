package com.teammerge.abandoned.WFC.records;

public class Index {
    private final int _y;
    private final int _x;

    public Index(int y, int x) {
        this._y = y;
        this._x = x;
    }

    public int y() {
        return _y;
    }

    public int x() {
        return _x;
    }
}
