package com.teammerge.abandoned.utilities.wfc.records;

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

    public int squareDistance(Index other) {
        int dy = other._y - this._y;
        int dx = other._x - this._x;

        return dy * dy + dx * dx;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Index) {
            Index otherIndex = (Index)other;

            return otherIndex._y == this._y && otherIndex._x == this._x;
        }

        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + _y;
        result = prime * result + _x;

        return result;
    }


    @Override
    public String toString() {
        return "Location[x=" +
                _x +
                ", y=" +
                _y +
                "]";
    }
}
