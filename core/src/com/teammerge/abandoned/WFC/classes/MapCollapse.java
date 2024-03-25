package com.teammerge.abandoned.WFC.classes;

import com.teammerge.abandoned.WFC.records.Index;

import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class MapCollapse extends BacktrackingWaveFunctionCollapse {
    static abstract class Areas {
        public static final String RESCUE_AREA = "Ra";
        public static final String FOREST = "Fo";

        public static final String VILLAGE = "Vi";

        public static final String PARK = "Pa";

        public static final String COMMERCIAL_BLDG = "CB";

        public static final String MALL = "Ma";

        public static final String FARM = "Fa";

        public static final String HOSPITAL = "Ho";
    }

    /// Rescue Area
    ///  Can be next to: Forests.

    /// Forests
    ///   Can be next to: Forests, Village, Parks, Farms

    /// Village
    ///   Can be next to: Village, Commercial Bldg, Park, Forests

    /// Parks
    ///    Can be next to: Village, Forest

    /// Commercial Bldg:
    ///    Can be next to: Village, Malls, Parks, Hospitals

    /// Malls:
    ///    Can be next to: Commercial Bldg

    /// Farms:
    ///    Can be next to: Village, Forests

    /// Hospitals:
    ///    Can be next to: Commercial Bldg, Hospitals.

    public String render(int[][] wave) {
        StringBuilder buffer = new StringBuilder();
        for (int[] row : wave) {
            for (int active : row) {
                if (Superpositions.isInvalid(active)) {
                    buffer.append("!");
                } else if (Superpositions.isCollapsed(active)) {
                    buffer.append(tiles[Superpositions.getSingle(active)]);
                } else {
                    buffer.append("?");
                }
                buffer.append(" ");
            }
            buffer.append('\n');
        }

        return buffer.toString();
    }

    private static final String[] tiles = {
            "Ra", "Fo", "Vi", "Pa", "CB", "Ma", "Fa" , "Ho"
    };

    @Override
    protected int[] getWeights() {
        return new int[]{1, 2, 2, 2, 2, 2, 2, 2
        };
    }

    public int[][] generateWave(Board board) {
        int height = board.size();
        int width = board.get(0).size();

        int[][] wave = new int[height][width];
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int superposition = 0;
                for (int i = 0; i < tiles.length; ++i) {
                    superposition = Superpositions.union(superposition, Superpositions.identity(i));
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
        remove.put(index, Superpositions.difference(wave[index.y()][index.x()], Superpositions.identity(value)));

        Queue<Index> queue = new LinkedBlockingQueue<>();
        queue.add(index);

        while (!queue.isEmpty()) {
            Index latest = queue.remove();
            int y = latest.y();
            int x = latest.x();

            switch (tiles[value]) {
                case "Ra":
                    if (y - 1 >= 0) {
                        Index neighbor = new Index(y - 1, x);
                        remove.put(
                                neighbor,
                                Superpositions.union(
                                        remove.getOrDefault(neighbor, Superpositions.empty()),
                                        Superpositions.difference(
                                                remove.getOrDefault(neighbor, Superpositions.empty()),
                                                Superpositions.identity(0)
                                        )
                                )
                        );
                    }
                    if (y + 1 < height) {
                        Index neighbor = new Index(y + 1, x);
                        remove.put(
                                neighbor,
                                Superpositions.union(
                                        remove.getOrDefault(neighbor, Superpositions.empty()),
                                        Superpositions.difference(
                                                remove.getOrDefault(neighbor, Superpositions.empty()),
                                                Superpositions.identity(0)
                                        )
                                )
                        );
                    }
                    if (x - 1 >= 0) {
                        Index neighbor = new Index(y , x - 1);
                        remove.put(
                                neighbor,
                                Superpositions.union(
                                        remove.getOrDefault(neighbor, Superpositions.empty()),
                                        Superpositions.difference(
                                                remove.getOrDefault(neighbor, Superpositions.empty()),
                                                Superpositions.identity(0)
                                        )
                                )
                        );
                    }
                    if (x + 1 < width) {
                        Index neighbor = new Index(y, x + 1);
                        remove.put(
                                neighbor,
                                Superpositions.union(
                                        remove.getOrDefault(neighbor, Superpositions.empty()),
                                        Superpositions.difference(
                                                remove.getOrDefault(neighbor, Superpositions.empty()),
                                                Superpositions.identity(0)
                                        )
                                )
                        );
                    }
            }
        }

        return remove;
    }
}
