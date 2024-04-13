package com.teammerge.abandoned.utilities.wfc.classes;

import com.teammerge.abandoned.utilities.wfc.records.Index;

import java.util.*;
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

    public String renderWave(int[][] wave) {
        StringBuilder buffer = new StringBuilder();
        for (int[] row : wave) {
            for (int active : row) {
                if (Superpositions.isInvalid(active)) {
                    buffer.append("! ");
                } else if (Superpositions.isCollapsed(active)) {
                    buffer.append(Areas.aliases[tiles[Superpositions.getSingle(active)][0]]);
                } else {
                    buffer.append("? ");
                }
                buffer.append(" ");
            }
            buffer.append('\n');
        }

        return buffer.toString();
    }

    public String renderMap(int[][] map) {
        StringBuilder buffer = new StringBuilder();
        for (int[] row : map) {
            for (int active : row) {
                buffer.append(Areas.aliases[tiles[active][0]]);
                buffer.append(" ");
            }
            buffer.append('\n');
        }

        return buffer.toString();
    }

    public static final int[][] tiles = {            /// Rescue Area
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
    protected int[] getWeights() { return new int[]{1, 3, 1, 5, 2, 5, 5, 5}; }

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

    /**
     * Returns
     * @param width Represents the horizontal size of the matrix.
     * @param height Represents the vertical size of the matrix.
     * @return a matrix (2d array) of integers which represent values in the [MapCollapse.Areas]
     */
    public int[][] generateMap(int width, int height) {
        Board board = Board.generate(height, width);
        Actor.Location origin = new Actor.Location((double)height / 2, (double)width / 2);

        int[][] wave = generateWave(board);

        Actor[] actors = new Actor[16];

        for (int i = 0; i < actors.length; ++i) {
            actors[i] = new Actor(
                    Actor.Location.fromPoint(
                        Utils.random.nextInt(0, height),
                        Utils.random.nextInt(0, width)
                    )
            );
        }

        //// WARNING: Really abhorrent code incoming.
        // TODO: Change the code use a separation algorithm instead of brute force.

        int count = 0;
        Actor.Location[] previousMovements = new Actor.Location[actors.length];
        boolean change = false;
        do {
            change = false;

            Actor.Location[] movements = new Actor.Location[actors.length];
            for (int i = 0; i < actors.length; ++i) {
                Actor actor = actors[i];
                Actor.Location movement = actor.separate(actors);

                System.out.println(movement);
                movements[i] = movement;
            }

            for (int i = 0; i < actors.length; ++i) {
                actors[i].getLocation()
                        .addToSelf(movements[i])
                        .constrainSelfX(0, width - 1)
                        .constrainSelfY(0, height - 1);
            }

            for (int i = 0; i < movements.length; ++i) {
                change = change || !movements[i].equals(previousMovements[i]);
            }

            previousMovements = movements;
        } while (count++ < 120 && change);

        for (Actor actor : actors) {
            partialCollapse(wave, actor.getLocation().asIndex(), MapCollapse.Areas.RESCUE_AREA);
        }
        fullCollapse(wave);

        System.out.println(renderWave(wave));
        for (int y = 0; y < wave.length; ++y) {
            for (int x = 0; x < wave[y].length; ++x) {
                wave[y][x] = Superpositions.getSingle(wave[y][x]);
            }
        }

        return wave;
    }

    public int[][] generateRescueProbabilityMatrix(int[][] map) {
        int height = map.length;
        int width = map[0].length;

        Index center = new Index(height / 2, width / 2);
        ArrayList<Index> allRescueAreas = new ArrayList<>();
        for (int y = 0; y < map.length; ++y) {
            for (int x = 0; x < map[y].length; ++x) {
                int collapsedValue = map[y][x];
                if (collapsedValue == MapCollapse.Areas.RESCUE_AREA) {
                    allRescueAreas.add(new Index(y, x));
                }
            }
        }

        int[][] rescueProbabilityMatrix = new int[height][width];
        for (Index rescueArea : allRescueAreas) {
            double radiusOfArea = Math.pow(rescueArea.squareDistance(center), 0.25);
            HashSet<Index> visited = new HashSet<>();
            Queue<Index> queue = new LinkedBlockingQueue<>();

            queue.add(rescueArea);

            while (!queue.isEmpty()) {
                Index latest = queue.remove();
                int y = latest.y();
                int x = latest.x();

                int squareDistance = latest.squareDistance(rescueArea);

                /// Remove if either:
                if (!visited.add(latest) || /// Already visited.
                        !(0 <= y && y < height) || /// [y] is beyond the indices
                        !(0 <= x && x < width) ||  /// [x] is beyond the indices
                        (squareDistance >= radiusOfArea * radiusOfArea)) /// the block is beyond the radius.
                    continue;

                final Index[] neighbors = {
                        new Index(y - 1, x),
                        new Index(y, x + 1),
                        new Index(y + 1, x),
                        new Index(y, x - 1),
                };

                rescueProbabilityMatrix[y][x] = Math.max(
                        rescueProbabilityMatrix[y][x],
                        (int)Math.round(radiusOfArea - Math.sqrt(squareDistance))
                );

                queue.addAll(Arrays.asList(neighbors));
            }

        }

        return rescueProbabilityMatrix;
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
