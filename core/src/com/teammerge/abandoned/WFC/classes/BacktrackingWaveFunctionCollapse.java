package com.teammerge.abandoned.WFC.classes;

import com.teammerge.abandoned.WFC.records.Event;
import com.teammerge.abandoned.WFC.records.Index;

import java.util.*;
import java.util.stream.Collectors;

public abstract class BacktrackingWaveFunctionCollapse {

    static private final int nullTried = -1;
    static private final Index nullIndex = new Index(-1, -1);

    public String renderSet(int[][] wave) {
        ArrayList<Integer> profile = new ArrayList<>();
        for (int x = 0; x < wave[0].length; ++x) {
            int x_ = x;
            Arrays.stream(wave)
                .map(row -> (int)Math.floor(Math.log(row[x_]) / Math.log(2)))
                .reduce((a, b) -> a > b ? a : b)
                .ifPresent(profile::add);
        }

        return Arrays.stream(wave)
                .map(row -> {
                    String[] strings = new String[row.length];
                    for (int x = 0; x < strings.length; ++x) {
                        strings[x] = Utils.padRight(Superpositions.iterableOf(row[x]).toString(), ' ', profile.get(x));
                    }

                    return String.join(" ", strings);
                })
                .collect(Collectors.joining("\n"));
    }

    public HashSet<Index> computeReducedIndices(int[][] wave, HashMap<Index, Integer> reduction, HashSet<Index> indices) {
        HashSet<Index> result = new HashSet<>();

        for (Map.Entry<Index, Integer> entry : reduction.entrySet()) {
            Index index = entry.getKey();
            int value = entry.getValue();

            if (indices.contains(index)) {
                int superposition = Superpositions.difference(wave[index.y()][index.x()], value);

                if (Superpositions.isSingle(superposition)) {
                    result.add(index);
                }
            }
        }

        return result;
    }

    public void partialCollapse(int[][] wave, Index index, int value) {
        HashMap<Index, Integer> propagationMap = computePropagation(wave, index, value);
        propagationMap.put(index, Superpositions.difference(wave[index.y()][index.x()], Superpositions.identity(value)));

        for (Map.Entry<Index, Integer> entry : propagationMap.entrySet()) {
            int y = entry.getKey().y();
            int x = entry.getKey().x();

            wave[y][x] = Superpositions.difference(wave[y][x], entry.getValue());
        }
    }

    public void fullCollapse(int[][] wave) {
        ArrayList<Event> events = new ArrayList<>();

        HashSet<Index> indices = new HashSet<>();
        for (int y = 0; y < wave.length; ++y) {
            for (int x = 0; x < wave[y].length; ++x) {
                if (Superpositions.isCollapsible(wave[y][x])) {
                    indices.add(new Index(y, x));
                }
            }
        }


        int tried = nullTried;
        Index tryingIndex = nullIndex;

        while (true) {
            boolean backtrack = true;

            do {
                if (indices.isEmpty()) {
                    return;
                }

                Index index = !tryingIndex.equals(nullIndex)
                        ? tryingIndex
                        : Utils.chooseRandomFromWave(wave, indices);
                int superposition = wave[index.y()][index.x()];

                int viable = tried == nullTried
                        ? superposition
                        : Superpositions.difference(superposition, tried);

                if (Superpositions.isInvalid(viable)) {
                    break; /// This is a '!'
                }

                int value = Utils.chooseRandomFromSuperposition(viable, getWeights());
                HashMap<Index, Integer> propagationMap = computePropagation(wave, index, value);
                propagationMap.put(index, Superpositions.difference(wave[index.y()][index.x()], Superpositions.identity(value)));

                // Compute the changes
                HashSet<Index> removals = computeReducedIndices(wave, propagationMap, indices);

                for (Map.Entry<Index, Integer> entry : propagationMap.entrySet()) {
                    int y = entry.getKey().y();
                    int x = entry.getKey().x();

                    wave[y][x] = Superpositions.difference(wave[y][x], entry.getValue());
                }

                indices.removeAll(removals);
                events.add(events.size() - 1, new Event(
                        index,
                        removals,
                        propagationMap,
                        tried == nullTried
                                ? Superpositions.identity(value)
                                : Superpositions.union(tried, Superpositions.identity(value))
                ));

                tried = nullTried;
                tryingIndex = nullIndex;

                backtrack = false;
            } while (false);

            if (backtrack) {
                if (events.isEmpty()) {
                    return;
                }

                Event lastEvent = events.remove(events.size() - 1);
                Index index = lastEvent.index();
                Set<Index> removals = lastEvent.removals();
                Map<Index, Integer> propagationMap = lastEvent.propagationMap();
                int previousTried = lastEvent.tried();

                tryingIndex = index;
                tried = previousTried;

                indices.addAll(removals);

                for (Map.Entry<Index, Integer> entry : propagationMap.entrySet()) {
                    int y = entry.getKey().y();
                    int x = entry.getKey().x();

                    wave[y][x] = Superpositions.union(wave[y][x], entry.getValue());
                }
            }
        }
    }

    protected abstract int[] getWeights();

    /**
     * The method which determines which indices alongside the values in their superpositions that will be removed.
     * @param wave Represents a copy of the wave that can be used in computing.
     * @param index Represents the index that has been collapsed.
     * @param value Represents the final value collapsed at the index.
     * @return A map containing the indices alongside the values that have been removed. This SHOULD include
     */
    public abstract HashMap<Index, Integer> computePropagation(int[][] wave, Index index, int value);
}
