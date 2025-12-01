package me.detj.aoc_2024;

import me.detj.utils.Grid;
import me.detj.utils.Inputs;
import me.detj.utils.Point;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class Solver10 {
    public static void main(String[] args) {
        var map = Inputs.parseDenseIntGrid("2024/input_10.txt");

        long score = scoreTrails(map);
        System.out.printf("Solution Part 1: %d\n", score);

        long scoreDistinct = scoreTrailsDistinct(map);
        System.out.printf("Solution Part 2: %d\n", scoreDistinct);
    }

    private static int scoreTrails(Grid<Integer> map) {
        List<Point> startPoints = map.findAll(0);

        int score = 0;
        for (Point start : startPoints) {
            Set<Point> peaks = new HashSet<>();
            reachablePeaks(map, start, peaks);
            score += peaks.size();
        }
        return score;
    }

    private static int scoreTrailsDistinct(Grid<Integer> map) {
        List<Point> startPoints = map.findAll(0);

        int score = 0;
        for (Point start : startPoints) {
            List<Point> peaks = new ArrayList<>();
            reachablePeaks(map, start, peaks);
            score += peaks.size();
        }
        return score;
    }

    private static void reachablePeaks(Grid<Integer> map, Point current, Collection<Point> peaks) {
        Integer height = map.get(current);

        if (height == 9) {
            peaks.add(current);
            return;
        }

        for (Function<Point, Point> direction : Point.BASIC_DIRECTIONS) {
            Point next = direction.apply(current);
            if (map.inBounds(next) && map.pointEquals(next, height + 1)) {
                reachablePeaks(map, next, peaks);
            }
        }
    }
}
