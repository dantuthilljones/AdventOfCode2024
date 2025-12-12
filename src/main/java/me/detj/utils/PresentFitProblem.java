package me.detj.utils;

import lombok.Value;

import java.util.List;

@Value
public class PresentFitProblem {

    List<Present> presents;
    List<PresentArea> areas;

    @Value
    public static class Present {
        boolean [][] grid;

        public boolean get(int x, int y) {
            return grid[x][y];
        }
    }

    @Value
    public static class PresentArea {
        int width;
        int height;
        List<Integer> presentCounts;
    }

}
