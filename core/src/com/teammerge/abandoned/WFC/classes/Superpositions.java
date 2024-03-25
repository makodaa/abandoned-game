package com.teammerge.abandoned.WFC.classes;

import java.util.ArrayList;
import java.util.Arrays;

public class Superpositions {
    public static int identity(int value) {
        int output = 0;
        output |= 1 << value;

        return output;
    }

    public static int empty() {
        return 0;
    }

    public static int getSingle(int superposition) {
        if (!isSingle(superposition)) {
            throw new Error("The superposition " + superposition + " has multiple values!");
        }

        return (int)Math.floor(Math.log(superposition) / Math.log(2));
    }

    public static int difference(int left, int right) {
        return left & ~right;
    }

    public static int createFrom(int ...rawValues) {
        return Arrays.stream(rawValues)
                .map(Superpositions::identity)
                .reduce(Superpositions::union)
                .orElseGet(() -> 0);
    }

    public static int union(int left, int right) {
        return left | right;
    }

    public static boolean isSingle(int superposition) {
        double logged = Math.log(superposition) / Math.log(2);

        return Math.floor(logged) - logged == 0;
    }

    public static boolean isCollapsed(int superposition) {
        return isSingle(superposition) && superposition > 0;
    }

    /**
     * Returns [true] whenever there are still more than one options for the superposition.
     * @param superposition The superposition to be checked.
     * @return Returns true whenever the superposition is not collapsed yet.
     */
    public static boolean isCollapsible(int superposition) {
        return !isCollapsed(superposition);
    }

    public static boolean isInvalid(int superposition) {
        return superposition == 0;
    }

    public static int getSizeOf(int superposition) {
        int count = 0;
        while (superposition != 0) {
            superposition &= superposition - 1;
            count += 1;
        }

        return count;
    }

    public static ArrayList<Integer> iterableOf(int superposition) {
        ArrayList<Integer> results = new ArrayList<>();
        for (int i = 0; superposition != 0; superposition >>= 1, ++i) {
            if ((superposition & 1) != 0) {
                results.add(i);
            }
        }

        return results;
    }
}
