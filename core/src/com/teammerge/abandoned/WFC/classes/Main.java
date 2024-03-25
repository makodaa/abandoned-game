package com.teammerge.abandoned.WFC.classes;

public class Main {
    public static void main(String[] args) {
        MapCollapse simple = new MapCollapse();
        Board board = Board.generate(4, 16);

        int[][] wave = simple.generateWave(board);
        simple.fullCollapse(wave);
        System.out.println(simple.render(wave));
    }
}
