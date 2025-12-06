package me.detj.aoc_2024;

import me.detj.utils.DTPair;
import me.detj.utils.Grid;
import me.detj.utils.Inputs;
import me.detj.utils.Point;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class Solver06 {
    public static void main(String[] args) {
        var map = Inputs.parseCharGrid("2024/input_06_example.txt");

        int guardPositions = countGuardPositions(map.shallowCopy());
        System.out.printf("Solution Part 1: %d\n", guardPositions);

        int obstacleLoops = countObstacleLoops(map.shallowCopy());
        System.out.printf("Solution Part 2: %d\n", obstacleLoops);
    }

    private static int countGuardPositions(Grid<Character> map) {
        Point guardPosition = map.findFirst('^');

        int guardMove = 0;

        // Important that the directions are in this order: UP, RIGHT, DOWN, LEFT
        List<Function<Point, Point>> guardMoves = Point.BASIC_DIRECTIONS;

        map.set(guardPosition, 'X');

        while (map.inBounds(guardPosition)) {
            Function<Point, Point> move = guardMoves.get(guardMove);
            Point nextPosition = move.apply(guardPosition);

            //If we hit an obstacle, change the guard direction
            if (map.pointEquals(nextPosition, '#')) {
                guardMove = (guardMove + 1) % 4;
            } else {
                guardPosition = nextPosition;
            }
            map.set(guardPosition, 'X');
        }

        // Now guard is out of bounds
        return map.count('X');
    }

    private static int countObstacleLoops(Grid<Character> original) {
        Grid<Character> map = original.shallowCopy();
        Point guardPosition = map.findFirst('^');

        int loops = 0;

        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                if (guardPosition.equals(new Point(x, y))) {
                    continue;
                }
                map.set(x, y, '#');
                if (guardLoops(map)) {
                    loops++;
                }
                map.set(x, y, original.get(x, y));
            }
        }

        return loops;
    }

    private static boolean guardLoops(Grid<Character> map) {
        Point guardPosition = map.findFirst('^');

        int guardDirection = 0;
        List<Function<Point, Point>> guardMoves = List.of(
                Point::moveUp,
                Point::moveRight,
                Point::moveDown,
                Point::moveLeft
        );

        Set<DTPair<Point, Integer>> history = new HashSet<>();
        while (map.inBounds(guardPosition)) {
            DTPair<Point, Integer> positionAndDirection = new DTPair<>(guardPosition, guardDirection);
            if (!history.add(positionAndDirection)) {
                return true;
            }

            Function<Point, Point> move = guardMoves.get(guardDirection);
            Point nextPosition = move.apply(guardPosition);
            //If we hit an obstacle, change the guard direction
            if (map.pointEquals(nextPosition, '#')) {
                guardDirection = (guardDirection + 1) % 4;
                continue;
            }

            // Move to the next position
            guardPosition = nextPosition;
        }

        // If we ended up out of bounds then there was no loop
        return false;
    }
}
