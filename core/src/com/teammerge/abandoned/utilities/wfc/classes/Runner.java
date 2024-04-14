package com.teammerge.abandoned.utilities.wfc.classes;

public class Runner {
    private static String drawGrid(double[][] matrix) {
        StringBuilder builder = new StringBuilder();

        for (int y = 0; y < matrix.length; ++y) {
            builder.append("[").append(y < 10 ? "0" : "").append(y).append("] ");
            for (int x = 0; x < matrix[y].length; ++x) {
                builder.append(matrix[y][x] == 0 ? " " : matrix[y][x]).append("  ");
            }
            builder.append("\n");
        }

        return builder.toString();
    }

    public static void main(String[] args) {
        int width = 31;
        int height = 31;

        MapCollapse simple = new MapCollapse();
        int[][] map = simple.generateAreas(31, 31);
        double[][] rescueProbabilityMatrix = simple.generateRescueProbabilityMatrix(map);

        System.out.println(drawGrid(rescueProbabilityMatrix));
        System.out.println(simple.renderMap(map));
    }
}
