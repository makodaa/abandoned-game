package com.teammerge.abandoned.WFC.classes;

import java.util.ArrayList;

public class Board extends ArrayList<ArrayList<Integer>> {
    static Board generate(int height, int width) {
        Board board = new Board();
        for (int y = 0; y < height; ++y) {
            ArrayList<Integer> inner = new ArrayList<>();
            for (int x = 0; x < width; ++x) {
                inner.add(0);
            }
            board.add(inner);
        }

        return board;
    }

}
