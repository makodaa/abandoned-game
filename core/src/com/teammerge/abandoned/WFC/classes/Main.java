package com.teammerge.abandoned.WFC.classes;

public class Main {
    public static void main(String[] args) {
        MapCollapse simple = new MapCollapse();
        Board board = Board.generate(8, 24);

        int[][] wave = simple.generateWave(board);
        simple.fullCollapse(wave);

//        simple.computePropagation(wave, new Index(1, 1), 0);
//        simple.partialCollapse(wave, new Index(1, 1), 0);
        System.out.println(simple.render(wave));
    }
}
