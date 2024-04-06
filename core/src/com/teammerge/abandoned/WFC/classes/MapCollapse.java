package com.teammerge.abandoned.WFC.classes;

import com.teammerge.abandoned.WFC.records.Index;

import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class MapCollapse extends BacktrackingWaveFunctionCollapse {
    static abstract class Areas {
        private static final String[] aliases = {
                "Ra", "Fo", "Vi", "Pa", "CB", "Ma", "Fa", "Ho",
        };

        public static final int RESCUE_AREA = 0;

        public static final int FOREST = 1;

        public static final int VILLAGE = 2;

        public static final int PARK = 3;

        public static final int COMMERCIAL_BLDG = 4;

        public static final int MALL = 5;

        public static final int FARM = 6;

        public static final int HOSPITAL = 7;

        public static final int UNIVERSAL = Superpositions.universal(tiles);
    }

    public String render(int[][] wave) {
        StringBuilder buffer = new StringBuilder();
        for (int[] row : wave) {
            for (int active : row) {
                if (Superpositions.isInvalid(active)) {
                    buffer.append("!");
                } else if (Superpositions.isCollapsed(active)) {
                    buffer.append(Areas.aliases[tiles[Superpositions.getSingle(active)][0]]);
                } else {
                    buffer.append("?");
                }
                buffer.append(" ");
            }
            buffer.append('\n');
        }

        return buffer.toString();
    }

    public static final int[][] tiles = {
            /// Rescue Area
            ///  Can be next to: Forests.
            {Areas.RESCUE_AREA, Superpositions.createFrom(Areas.FOREST)},

            /// Forests
            ///   Can be next to: Forests, Village, Parks, Farms, [Rescue Area]
            {Areas.FOREST, Superpositions.createFrom(Areas.FOREST, Areas.RESCUE_AREA, Areas.VILLAGE, Areas.PARK, Areas.FARM)},

            /// Villages
            ///    Can be next to: Village, Commercial Bldg, Park, Forests, [Farm]
            {Areas.VILLAGE, Superpositions.createFrom(Areas.VILLAGE, Areas.COMMERCIAL_BLDG, Areas.PARK, Areas.FOREST, Areas.FARM)},

            /// Parks
            ///    Can be next to: Village, Forest
            {Areas.PARK, Superpositions.createFrom(Areas.VILLAGE, Areas.FOREST, Areas.COMMERCIAL_BLDG)},

            /// Commercial Bldg:
            ///    Can be next to: Village, Malls, Parks, Hospitals
            {Areas.COMMERCIAL_BLDG, Superpositions.createFrom(Areas.VILLAGE, Areas.MALL, Areas.PARK, Areas.HOSPITAL)},

            /// Malls:
            ///    Can be next to: Commercial Bldg
            {Areas.MALL, Superpositions.createFrom(Areas.COMMERCIAL_BLDG)},


            /// Farms:
            ///    Can be next to: Village, Forests
            {Areas.FARM, Superpositions.createFrom(Areas.VILLAGE, Areas.FOREST)},

            /// Hospitals:
            ///    Can be next to: Commercial Bldg.
            {Areas.HOSPITAL, Superpositions.createFrom(Areas.COMMERCIAL_BLDG)}
    };

    @Override
    protected int[] getWeights() { return new int[]{1, 5, 1, 5, 5, 5, 5, 5}; }

    public int[][] generateWave(Board board) {
        int height = board.size();
        int width = board.get(0).size();

        int[][] wave = new int[height][width];
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int superposition = 0;
                for (int i = 0; i < tiles.length; ++i) {
                    superposition = Superpositions.union(superposition, Superpositions.singletonFrom(i));
                }
                wave[y][x] = superposition;
            }
        }

        return wave;
    }

    @Override
    public HashMap<Index, Integer> computePropagation(int[][] wave, Index index, int value) {
        int height = wave.length;
        int width = wave[0].length;

        HashMap<Index, Integer> remove = new HashMap<>();
        remove.put(index, Superpositions.difference(wave[index.y()][index.x()], Superpositions.singletonFrom(value)));

        Queue<Index> queue = new LinkedBlockingQueue<>();
        queue.add(index);

        while (!queue.isEmpty()) {
            Index latest = queue.remove();
            int y = latest.y();
            int x = latest.x();

            final int possibilitiesAtTile = Superpositions.difference(
                    wave[y][x],
                    remove.getOrDefault(latest, Superpositions.empty())
            );
            final int allowed = Superpositions.iterableOf(possibilitiesAtTile)
                    .stream()
                    .map((possibleTile) ->  tiles[possibleTile][1])
                    .reduce(Superpositions.empty(), Superpositions::union);
            final int notAllowed = Superpositions.difference(Areas.UNIVERSAL, allowed);

            Index[] neighbors = {
                    new Index(y - 1, x),
                    new Index(y, x + 1),
                    new Index(y + 1, x),
                    new Index(y, x - 1),
            };

            for (Index neighborIndex : neighbors) {
                int ny = neighborIndex.y();
                int nx = neighborIndex.x();

                /// If it is out of bounds, skip the iteration.
                if (!(0 <= ny && ny < height) || !(0 <= nx && nx < width)) continue;
                final int currentRemovals = remove.getOrDefault(neighborIndex, Superpositions.empty());
                final int neighborPossibilities = Superpositions.difference(wave[ny][nx], currentRemovals);
                final int newRemovals = Superpositions.intersection(neighborPossibilities, notAllowed);

                if (Superpositions.isEmpty(newRemovals)) continue;

                final int neighborRemovals = Superpositions.union(currentRemovals, newRemovals);
                remove.put(neighborIndex, neighborRemovals);
                queue.add(neighborIndex);
            }
        }

        return remove;
    }
}
