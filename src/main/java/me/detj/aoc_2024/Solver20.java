package me.detj.aoc_2024;

import me.detj.utils.Grid;
import me.detj.utils.Inputs;
import me.detj.utils.Point;
import me.detj.utils.RacePosition;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class Solver20 {
    public static void main(String[] args) {
        var input = Inputs.parseCharGrid("2024/input_20.txt");

        int s1 = countCheats(input, 100, 2);
        System.out.printf("Solution Part 1: %d\n", s1);

        long s2 = countCheats(input, 100, 20);
        System.out.printf("Solution Part 2: %d\n", s2);
    }

    private static int countCheats(Grid<Character> racetrack, int minPicos, int cheatDistance) {
        Grid<Integer> distFromStart = calcDistances(racetrack, racetrack.findFirst('S'));
        Grid<Integer> distFromEnd = calcDistances(racetrack, racetrack.findFirst('E'));

        int bestDist = distFromStart.get(racetrack.findFirst('E'));

        AtomicInteger cheats = new AtomicInteger(0);
        racetrack.forEachPoint((start, value) -> {
            if (value == '#') {
                return;
            }
            for (Point possibleEnd : getPointsWithinManhattanDistance(start, cheatDistance)) {
                if (racetrack.pointEquals(possibleEnd, '#') || !racetrack.inBounds(possibleEnd)) {
                    continue;
                }
                int cheatDist = start.manhattanDistance(possibleEnd);
                int dist = cheatDist + distFromStart.get(start) + distFromEnd.get(possibleEnd);
                int improvement = bestDist - dist;
                if (improvement >= minPicos) {
                    cheats.incrementAndGet();
                }
            }
        });

        return cheats.get();
    }

    public static List<Point> getPointsWithinManhattanDistance(Point start, int n) {
        List<Point> points = new ArrayList<>();
        int x = start.getX();
        int y = start.getY();

        for (int dx = -n; dx <= n; dx++) {
            for (int dy = -(n - Math.abs(dx)); dy <= (n - Math.abs(dx)); dy++) {
                points.add(new Point(x + dx, y + dy));
            }
        }

        return points;
    }


    private static Grid<Integer> calcDistances(Grid<Character> racetrack, Point from) {
        // Initialize objects which keep track of score and path. The values are an array of positions where index = direction (0 = up, 1 = right, 2 = down, 3 = left)
        Grid<Integer> distances = Grid.of(racetrack.getWidth(), racetrack.getHeight(), Integer.MAX_VALUE);

        PriorityQueue<RacePosition> queue = new PriorityQueue<>(Comparator.comparing(RacePosition::getScore));
        queue.add(new RacePosition(from, 0));

        while (!queue.isEmpty()) {
            RacePosition position = queue.poll();
            stepDijkstra(racetrack, distances, queue, position);
            //drawMaze(maze, distances);
        }

        return distances;

    }

    private static void stepDijkstra(Grid<Character> maze, Grid<Integer> distances, PriorityQueue<RacePosition> queue, RacePosition position) {
        // Can't step through walls or go out of bounds
        if (!maze.inBounds(position.getPoint()) || maze.pointEquals(position.getPoint(), '#')) {
            return;
        }

        // If we've found this position for cheaper, skip
        if (distances.get(position.getPoint()) <= position.getScore()) {
            return;
        }

        // Update the min distance for this point
        distances.set(position.getPoint(), position.getScore());

        // Step up, down left and right
        for (Function<Point, Point> direction : Point.BASIC_DIRECTIONS) {
            Point next = direction.apply(position.getPoint());
            RacePosition nextPosition = new RacePosition(next, position.getScore() + 1);
            queue.add(nextPosition);
        }
    }
}
