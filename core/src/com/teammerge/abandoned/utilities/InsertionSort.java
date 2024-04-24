package com.teammerge.abandoned.utilities;

import java.util.ArrayList;
import java.util.Comparator;

public abstract class InsertionSort {
    public static <T> void run(ArrayList<T> input, Comparator<T> comparator) {
        for (int i = 1; i < input.size(); ++i) {
            T key = input.get(i);

            int j = i - 1;
            for (; j >= 0 && comparator.compare(input.get(j), key) > 0; --j) {
                input.set(j + 1, input.get(j));
            }
            input.set(j + 1, key);
        }
    }
}
