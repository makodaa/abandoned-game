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
        Index center = new Index(height / 2, width / 2);

        if ((center.x() * 2 + 1) != width || (center.y() * 2 + 1) != height) {
            throw new Error("Incorrect parameters. Please select a grid size with an odd number.");
        }

        int[][] wave = generateWave(board);

        Actor[] actors = new Actor[16];

        //// WARNING: Really abhorrent code incoming.
        // TODO: Change the code use a separation algorithm instead of brute force.
        boolean redo = false;
        do /* unsafe */ {
            redo = false;
            int retries = 0;

            for (int i = 0; i < actors.length; ++i) {
                actors[i] = new Actor(center, (2 * Math.PI) * ((double) i / actors.length));
            }

            /// Fixed point iteration
            boolean run = false;
            do {
                run = false;
                boolean[] shouldMove = new boolean[actors.length];

                if (retries > 8) {
                    redo = true;
                    System.out.println("Redoing because code bad");
                    break;
                }

                for (int i = 0; i < actors.length; ++i) {
                    for (int j = i + 1; j < actors.length; ++j) {
                        Actor left = actors[i];
                        Actor right = actors[j];
                        if (left == right) { continue; }

                        double squareDistance = left.getIndex().squareDistance(right.getIndex());

                        shouldMove[i] = shouldMove[i] ^ squareDistance < left.getSensitivity() * left.getSensitivity();
                        shouldMove[j] = shouldMove[j] ^ squareDistance < right.getSensitivity() * right.getSensitivity();
                    }
                }

                ArrayList<Integer> toBeReplaced = new ArrayList<>();
                for (int i = 0; i < actors.length; ++i) {
                    Actor actor = actors[i];
                    boolean should = shouldMove[i];
                    if (should) {
                        double dy = Math.sqrt(actor.getSensitivity()) * Math.sin(actor.getVectorAngle());
                        double dx = Math.sqrt(actor.getSensitivity()) * Math.cos(actor.getVectorAngle());

                        int newY = Math.min(Math.max((int)Math.round(actor.getIndex().y() + dy), 0), wave.length - 1);
                        int newX = Math.min(Math.max((int)Math.round(actor.getIndex().x() + dx), 0), wave[0].length - 1);

                        if (newY == actor.getIndex().y() && newX == actor.getIndex().x()) {
                            retries += 1;

                            System.out.println("Colliding circle has not moved! Is there an infinite loop?");

                            /// Option 1: Remove one of the two colliding, (and spawn a new one at the center. Possibly.)
                            toBeReplaced.add(i);
                            continue;
                        }

                        actor.setIndex(new Index(newY, newX));
                    }

                    run = should || run;
                }
                for (int index : toBeReplaced) {
                    double angleDelta = 0.5 * (0.5 - Actor.random.nextDouble());
                    double theta = (2 * Math.PI) * (angleDelta + (double) index / actors.length);

                    actors[index] = new Actor(
                            new Index(center.y() + (int)(6 * Math.sin(theta)), center.x() + (int)(6 * Math.cos(theta))),
                            theta
                    );
                }
            } while (run);
        } while (redo);

        for (Actor actor : actors) {
            partialCollapse(wave, actor.getIndex(), MapCollapse.Areas.RESCUE_AREA);
        }
        fullCollapse(wave);

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
