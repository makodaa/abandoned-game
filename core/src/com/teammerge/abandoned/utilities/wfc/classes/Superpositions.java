package com.teammerge.abandoned.utilities.wfc.classes;

import java.util.ArrayList;
import java.util.Arrays;

public class Superpositions {

    // Returns what is analogous to a full set containing all the possible values in the superposition.
    public static int universal(Object[] options) {
        return (1 << (options.length + 1)) - 1;
    }

    public static int universal(Iterable<?> options) {
        int count = 0;
        for (Object option : options) {
            count += 1;
        }

        return count;
    }

    public static int singletonFrom(int value) { return 1 << value; }

    public static int empty() {
        return 0;
    }

    public static boolean contains(final int superposition, int value) {
        return intersection(superposition, Superpositions.singletonFrom(value)) != 0;
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

    public static int createFrom(Enum<?> ...rawValues) {
        return Arrays.stream(rawValues)
                .map(Enum::ordinal)
                .map(Superpositions::singletonFrom)
                .reduce(Superpositions::union)
                .orElse(0);
    }

    public static int createFrom(int ...rawValues) {
        return Arrays.stream(rawValues)
                .map(Superpositions::singletonFrom)
                .reduce(Superpositions::union)
                .orElse(0);
    }

    public static int union(int left, int right) {
        return left | right;
    }

    public static int intersection(int left, int right) { return left & right; }

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

    public static boolean isEmpty(int superposition) { return superposition == 0; }

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
        for (int i = 0; superposition != 0; superposition >>>= 1, ++i) {
            if ((superposition & 1) != 0) {
                results.add(i);
            }
        }

        return results;
    }
}

//public class Superposition extends HashSet<Integer> {
//    public static Superposition identity(int value) {
//        Superposition result = new Superposition();
//        result.add(value);
//
//        return result;
//    }
//
//    public static Superposition empty() {
//        return new Superposition();
//    }
//
//    public int getSingle() {
//        assert this.size() == 1;
//        return this.iterator().next();
//    }
//
//    public Superposition copy() {
//        Superposition newCopy = new Superposition();
//        newCopy.addAll(this);
//
//        return newCopy;
//    }
//
//    public Superposition difference(HashSet<Integer> right) {
//        Superposition copy = this.copy();
//        copy.removeAll(right);
//
//        return copy;
//    }
//
//    public Superposition union(HashSet<Integer> right) {
//        Superposition copy = this.copy();
//        copy.addAll(right);
//
//        return copy;
//    }
//}
