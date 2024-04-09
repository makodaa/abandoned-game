package com.teammerge.abandoned.utilities.wfc.classes;

import com.teammerge.abandoned.utilities.wfc.records.Index;

import java.util.Random;

/// Represents a moving 2-dimensional index.

public class Actor {
    public static final Random random = new Random();

    private Index index;

    /// [0, 2π)
    private double vectorAngle;

    /// Refers to the radius the actor "needs" between other actors.
    private int sensitivity;

    public Actor(Index index, double vectorAngle) {
        this.index = index;

        this.vectorAngle = vectorAngle;

        this.sensitivity =  (int)Math.floor(4 + (6 * random.nextDouble()));
    }

    public Index getIndex() {
        return index;
    }

    public double getVectorAngle() {
        return vectorAngle;
    }

    public int getSensitivity() {
        return sensitivity;
    }

    public void setIndex(Index index) {
        this.index = index;
    }

    @SuppressWarnings("unused")
    public void setVectorAngle(double vectorAngle) {
        this.vectorAngle = vectorAngle;
    }

    @SuppressWarnings("unused")
    public void setSensitivity(int sensitivity) {
        this.sensitivity = sensitivity;
    }

    public String toString() {
        return "Actor[index=" + this.index + ", sensitivity=" + this.sensitivity + ", vectorAngle=2pi * " + (vectorAngle/(2*Math.PI)) + "]";
    }
}
