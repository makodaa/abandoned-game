package com.teammerge.abandoned.utilities.wfc.classes;

import com.teammerge.abandoned.utilities.wfc.records.Index;

import java.util.*;
import java.util.stream.Collectors;

public abstract class Utils {
    private Utils() {}

    public static Random random = new Random();

    public static Index chooseRandomFromWave(int[][] wave, HashSet<Index> indices) throws IndexOutOfBoundsException {
        Index lowest = null;
        double minimum = Double.POSITIVE_INFINITY;
        int probability = 1;

        for (Index index : indices) {
            int length = Superpositions.getSizeOf(wave[index.y()][index.x()]);

            if (length < minimum) {
                lowest = index;
                minimum = length;
                probability = 1;
            } else if (length == minimum) {
                lowest = 1f / (++probability) >= Utils.random.nextDouble() ? index : lowest;
            }
        }

        if (lowest == null) {
            throw new IndexOutOfBoundsException();
        }

        return lowest;
    }

    public static String padRight(String original, char padChar, int padCount) {
        int target = padCount - original.length();
        if (target <= 0) {
            return original;
        }

        StringBuilder builder = new StringBuilder();
        builder.append(original);
        for (int i = 0; i < target; ++i) {
            builder.append(padChar);
        }

        return builder.toString();
    }

    public static <E> E chooseRandom(Iterable<E> iterable) throws IndexOutOfBoundsException {
        E chosen = null;
        int probability = 0;
        for (E value : iterable) {
            chosen = 1f / (++probability) >= Utils.random.nextDouble() ? value : chosen;
        }

        if (chosen == null) {
            throw new IndexOutOfBoundsException();
        }

        return chosen;
    }

    public static int chooseRandomFromSuperposition(int superposition, int[] weights) throws IndexOutOfBoundsException {
        if (Superpositions.isInvalid(superposition)) {
            throw new IndexOutOfBoundsException();
        }

        /// Create an accumulative frequency range.

        /// 1. Get the weights that will be used.
        ArrayList<Integer> weightList = new ArrayList<>();
        for (int i = 0; i < weights.length; ++i) {
            if ((superposition & (1 << i)) != 0) {
                weightList.add(weights[i]);
            }
        }

        /// 2. Create a sum.
        int totalWeight = weightList.stream().reduce(Integer::sum).orElse(1);

        /// 3. Create accumulative sublist.
        List<Float> accumulativeSubList = range(0, weightList.size())
                .stream()
                .map(l -> (float)weightList.subList(0, l + 1).stream().reduce(Integer::sum).orElse(1) / totalWeight)
                .collect(Collectors.toList());

        /// 4. Choose a float from 0 to 1.
        float chosenFloat = random.nextFloat();
        int chosenDigit = -1;
        for (int i = 0; i < accumulativeSubList.size(); ++i) {
            if (chosenFloat < accumulativeSubList.get(i)) {
                chosenDigit = i;
                break;
            }
        }

        /// 5. Get the [chosenDigit]nth 1 from the right.

        /// Bit strings / Bit fields, hooray!

        /// [p] Keeps track of which "one-bit" of the bit string we are (or about to) look at.
        int p = 0;

        /// [c] Keeps track of the true index of the digit, or the index of the bit we are looking at.
        int c = 0;

        /// [s] Is a reducible copy of [superposition] for loop bounds.
        int s = superposition;

        /// Keep looping while [s] has any bits.
        while (s != 0) {
            /// If the first bit, i.e 01000[1] is not zero, then we check if [p] agrees with the chosen digit.
            ///                             ^
            if ((s & 1) != 0) {
                /// If the index of the "one-bit" we are looking at [p] matches the [chosenDigit],
                /// We return [c], which is the true index of the digit.
                if (p == chosenDigit) {
                    return c;
                }

                /// Regardless, increment [p] for the next digit.
                ++p;
            }

            /// Reduce the [s] by half,
            s >>>= 1;

            /// And add to [c].
            c += 1;
        }

        return 0;
    }

    public static HashSet<Index>  copyIndices(HashSet<Index> source) {
        return new HashSet<>(source);
    }

    public static ArrayList<Integer> range(int low, int high) {
        int length = high - low;
        ArrayList<Integer> values = new ArrayList<>();
        for (int i = low; i < high; ++i) {
            values.add(i);
        }

        return values;
    }

    public static int[][] copyWave(int[][] wave) {
        int[][] newWave = new int[wave.length][wave[0].length];
        for (int y = 0; y < wave.length; ++y) {
            System.arraycopy(wave[y], 0, newWave[y], 0, wave[y].length);
        }

        return newWave;
    }

}
