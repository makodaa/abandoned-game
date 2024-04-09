package com.teammerge.abandoned.utilities.wfc.classes;

import com.teammerge.abandoned.utilities.wfc.records.Event;
import com.teammerge.abandoned.utilities.wfc.records.Index;

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
                .reduce(Math::max)
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
        propagationMap.put(index, Superpositions.difference(wave[index.y()][index.x()], Superpositions.singletonFrom(value)));

        for (Map.Entry<Index, Integer> entry : propagationMap.entrySet()) {
            Index key = entry.getKey();
            int y = key.y();
            int x = key.x();

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
        Optional<Index> tryingIndex = Optional.empty();

        while (true) {
            boolean backtrack = true;

            do {
                if (indices.isEmpty()) {
                    return;
                }

                Index index = tryingIndex.orElseGet(() -> Utils.chooseRandomFromWave(wave, indices));
                int superposition = wave[index.y()][index.x()];

                int viable = tried == nullTried
                        ? superposition
                        : Superpositions.difference(superposition, tried);

                if (Superpositions.isInvalid(viable)) {
                    break; /// This is a '!'
                }

                int value = Utils.chooseRandomFromSuperposition(viable, getWeights());
                HashMap<Index, Integer> propagationMap = computePropagation(wave, index, value);
                propagationMap.put(index, Superpositions.difference(wave[index.y()][index.x()], Superpositions.singletonFrom(value)));

                // Compute the changes
                HashSet<Index> removals = computeReducedIndices(wave, propagationMap, indices);

                for (Map.Entry<Index, Integer> entry : propagationMap.entrySet()) {
                    Index key = entry.getKey();
                    int y = key.y();
                    int x = key.x();

                    wave[y][x] = Superpositions.difference(wave[y][x], entry.getValue());
                }

                indices.removeAll(removals);
                events.add(new Event(
                        index,
                        removals,
                        propagationMap,
                        tried == nullTried
                                ? Superpositions.singletonFrom(value)
                                : Superpositions.union(tried, Superpositions.singletonFrom(value))
                ));

                tried = nullTried;
                tryingIndex = Optional.empty();

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

                tryingIndex = Optional.of(index);
                tried = previousTried;

                indices.addAll(removals);

                for (Map.Entry<Index, Integer> entry : propagationMap.entrySet()) {
                    Index key = entry.getKey();
                    int y = key.y();
                    int x = key.x();

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
