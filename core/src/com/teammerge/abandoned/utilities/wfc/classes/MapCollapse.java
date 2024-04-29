package com.teammerge.abandoned.utilities.wfc.classes;

import com.teammerge.abandoned.utilities.wfc.enums.AreaType;
import com.teammerge.abandoned.records.Index;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;


public class MapCollapse extends BacktrackingWaveFunctionCollapse {
    public String renderWave(int[][] wave) {
        StringBuilder buffer = new StringBuilder();
        for (int[] row : wave) {
            for (int active : row) {
                if (Superpositions.isInvalid(active)) {
                    buffer.append("! ");
                } else if (Superpositions.isCollapsed(active)) {
                    buffer.append(AreaType.from(Superpositions.getSingle(active)).getAlias());
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
                buffer.append(AreaType.from(active).getAlias());
                buffer.append(" ");
            }
            buffer.append('\n');
        }

        return buffer.toString();
    }

    public String renderProbability(double[][] map) {
        StringBuilder buffer = new StringBuilder();
        for (int y = 0; y < map.length; ++y) {
            double[] row = map[y];
            for (int x = 0; x < row.length; ++x) {
                double active = row[x];

                if (y == map.length / 2 && x == map[y].length / 2) {
                    buffer.append("+");
                } else if (active <= 0) {
                    buffer.append(" ");
                } else {
                    buffer.append(Character.toString(64 + (int)Math.floor(active * 26)));
                }
                buffer.append(" ");
            }
            buffer.append('\n');
        }

        return buffer.toString();
    }

    @Override
    protected int[] getWeights() { return new int[]{0, 3, 1, 5, 2, 5, 5, 5}; }

    public int[][] generateWave(Board board) {
        int height = board.size();
        int width = board.get(0).size();

        int[][] wave = new int[height][width];
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                wave[y][x] = Superpositions.universal(AreaType.values());
            }
        }

        return wave;
    }

    /**
     * Returns a matrix (2d array) of integers which represent values in the [MapCollapse.Areas]
     * @param width Represents the horizontal size of the matrix.
     * @param height Represents the vertical size of the matrix.
     * @return the generated map
     */
    public int[][] generateAreas(int width, int height) {
        Board board = Board.generate(height, width);
        Actor.Location origin = new Actor.Location((double)height / 2, (double)width / 2);

        int[][] wave = generateWave(board);

        Actor[] actors = new Actor[1 + ((width + height) / 2) / 4];

        actors[0] = new Actor(Actor.Location.fromPoint(height / 2, width / 2));
        actors[0].setSensitivity(24);

        for (int i = 1; i < actors.length; ++i) {
            actors[i] = new Actor(
                    Actor.Location.fromPoint(
                            Utils.random.nextInt(0, height),
                            Utils.random.nextInt(0, width)));
        }

        Actor.Location[] previousMovements = new Actor.Location[actors.length];
        boolean change = false;
        do {
            change = false;

            Actor.Location[] movements = new Actor.Location[actors.length];
            for (int i = 1; i < actors.length; ++i) {
                Actor actor = actors[i];
                Actor.Location movement = actor.separate(actors);

                movements[i] = movement;
            }

            for (int i = 1; i < actors.length; ++i) {
                actors[i].getLocation()
                        .addToSelf(movements[i])
                        .constrainSelfX(0, width - 1)
                        .constrainSelfY(0, height - 1);
            }

            for (int i = 1; i < movements.length; ++i) {
                change = change || !movements[i].equals(previousMovements[i]);
            }

            previousMovements = movements;
        } while (change);

        for (Actor actor : Arrays.stream(actors).skip(1).toList()) {
            if (actor.getLocation().squareDistance(origin) <= 6 * 6)
                continue;

            partialCollapse(wave, actor.getLocation().asIndex(), AreaType.RESCUE_AREA.ordinal());
        }

        fullCollapse(wave);

        for (int y = 0; y < wave.length; ++y) {
            for (int x = 0; x < wave[y].length; ++x) {
                wave[y][x] = Superpositions.getSingle(wave[y][x]);
            }
        }

        return wave;
    }

    public double[][] generateRescueProbabilityMatrix(int[][] map) {
        int height = map.length;
        int width = map[0].length;

        Index center = new Index(height / 2, width / 2);
        ArrayList<Index> allRescueAreas = new ArrayList<>();
        for (int y = 0; y < map.length; ++y) {
            for (int x = 0; x < map[y].length; ++x) {
                if (AreaType.from(map[y][x]) == AreaType.RESCUE_AREA) {
                    allRescueAreas.add(new Index(y, x));
                }
            }
        }

        double[][] rescueProbabilityMatrix = new double[height][width];
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

                if (squareDistance <= 1e-16) {
                    rescueProbabilityMatrix[y][x] = 1.0;
                } else {
                    rescueProbabilityMatrix[y][x] = Math.max(
                            rescueProbabilityMatrix[y][x],
                            (radiusOfArea - Math.sqrt(squareDistance)) / radiusOfArea
                    );
                }

                queue.addAll(Arrays.asList(neighbors));
            }

        }

        return rescueProbabilityMatrix;
    }

    public Area[][] generateMap(int width, int height) {
        int[][] map = generateAreas(width, height);
        double[][] rescueProbabilityMatrix = generateRescueProbabilityMatrix(map);

        Area[][] areas = new Area[height][width];
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                areas[y][x] = new Area(
                        AreaType.from(map[y][x]),
                        rescueProbabilityMatrix[y][x]
                );
            }
        }

        System.out.println(renderProbability(rescueProbabilityMatrix));

        return areas;
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
                    .map((possibleTile) ->  AreaType.from(possibleTile).getCompatibilities())
                    .reduce(Superpositions.empty(), Superpositions::union);
            final int notAllowed = Superpositions.difference(AreaType.UNIVERSAL, allowed);

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
