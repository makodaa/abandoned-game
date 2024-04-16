package com.teammerge.abandoned.utilities.wfc.classes;

import java.util.Random;

import com.teammerge.abandoned.records.Index;

/// Represents a moving 2-dimensional index.

public class Actor {
    public static final Random random = new Random();

    private Location location;

    /// Refers to the radius the actor "needs" between other actors.
    private int sensitivity;

    public Actor(Location index) {
        this.location = index;
        this.sensitivity = (int) Math.floor((6 * random.nextDouble()));
    }

    public Location getLocation() {
        return location;
    }

    public int getSensitivity() {
        return sensitivity;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location separate(Actor[] locations) {
        final Location steer = new Location(0, 0);
        int count = 0;

        for (Actor other : locations) {
            if (this == other) {
                continue;
            }

            double distance = this.location.squareDistance(other.location);
            if (distance < sensitivity * sensitivity) {
                /// We should consider this when separating.
                Location difference = this.location.difference(other.location).normalized().divide(distance);
                steer.addToSelf(difference);
                count += 1;
            }
        }

        if (count > 0) {
            steer.divideSelf(count);
        }

        steer.multiplyToSelf(Math.sqrt(sensitivity));

        if (steer.computeMagnitude() > 0) {
            steer.x = (double) Math.round(steer.x * 10_000) / 10_000;
            steer.y = (double) Math.round(steer.y * 10_000) / 10_000;
        }

        return steer;
    }

    @SuppressWarnings("unused")
    public void setSensitivity(int sensitivity) {
        this.sensitivity = sensitivity;
    }

    public String toString() {
        return "Actor[index=" +
                this.location +
                ", sensitivity="
                + this.sensitivity +
                "]";
    }

    public static class Location {
        private double y;
        private double x;

        public Location(double y, double x) {
            this.y = y;
            this.x = x;
        }

        public static Location fromPoint(int y, int x) {
            return new Location(y, x);
        }

        /**
         *
         * @param distance Distance from the origin.
         * @param angle    A float in [0, 2π).
         * @return A [Location] object with normalized (x, y).
         */
        public static Location fromRadial(double distance, double angle) {
            return new Location(
                    (distance * Math.sin(angle)),
                    (distance * Math.cos(angle)));
        }

        /**
         *
         * @param distance Distance from the origin.
         * @param angle    A float in [0, 2π).
         * @return A [Location] object with normalized (x, y).
         */
        public static Location fromRadial(double distance, double angle, Location origin) {
            return new Location(
                    origin.y + (distance * Math.sin(angle)),
                    origin.x + (distance * Math.cos(angle)));
        }

        public double y() {
            return this.y;
        }

        public double x() {
            return this.x;
        }

        public Index asIndex() {
            return new Index(
                    (int) Math.round(this.y),
                    (int) Math.round(this.x));
        }

        public double computeMagnitude() {
            return Math.sqrt(y * y + x * x);
        }

        public double squareDistance(Location other) {
            double dy = other.y - this.y;
            double dx = other.x - this.x;

            return dy * dy + dx * dx;
        }

        public Location normalized() {
            double magnitude = computeMagnitude();

            return new Location(
                    this.y / magnitude,
                    this.x / magnitude);
        }

        public void normalizeSelf() {
            double magnitude = computeMagnitude();

            this.y /= magnitude;
            this.x /= magnitude;
        }

        public Location constrainSelfX(double min, double max) {
            this.x = Math.min(Math.max(this.x, min), max);

            return this;
        }

        public Location constrainSelfY(double min, double max) {
            this.y = Math.min(Math.max(this.y, min), max);

            return this;
        }

        public Location limitSelf(double scalar) {
            double ratio = computeMagnitude() / scalar;
            if (ratio < 1.0) {
                return this;
            }

            this.y /= ratio;
            this.x /= ratio;

            return this;
        }

        public Location add(Location other) {
            return new Location(
                    this.y + other.y,
                    this.x + other.x);
        }

        public Location addToSelf(Location other) {
            this.y += other.y;
            this.x += other.x;

            return this;
        }

        public Location difference(Location other) {
            return new Location(
                    this.y - other.y,
                    this.x - other.x);
        }

        public Location multiply(double scalar) {
            return new Location(
                    this.y * scalar,
                    this.x * scalar);
        }

        public Location multiplyToSelf(double scalar) {
            this.y *= scalar;
            this.x *= scalar;

            return this;
        }

        public Location divide(double divisor) {
            return new Location(
                    this.y / divisor,
                    this.x / divisor);
        }

        public void divideSelf(double divisor) {
            this.y /= divisor;
            this.x /= divisor;
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof Location otherLocation) {
                return otherLocation.y == this.y && otherLocation.x == this.x;
            }

            return false;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + Double.valueOf(y).hashCode();
            result = prime * result + Double.valueOf(x).hashCode();

            return result;
        }

        @Override
        public String toString() {
            return "Location[x=" +
                    x +
                    ", y=" +
                    y +
                    "]";
        }
    }
}
