package me.detj.aoc_2024;

import lombok.SneakyThrows;
import me.detj.utils.Grid;
import me.detj.utils.Inputs;
import me.detj.utils.Pair;
import me.detj.utils.Point;
import org.apache.commons.collections4.SetUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Solver14 {
    public static void main(String[] args) {
        var input = Inputs.parsePointPairs("2024/input_14.txt");

        long s1 = calculateSafetyFactor(input, new Point(101, 103), 100);
        System.out.printf("Solution Part 1: %d\n", s1);

        printRobotAfter(input, new Point(101, 103), 7520);
        //System.out.printf("Solution Part 2: %d\n", s2);
    }

    /*
     *
     * Skinny pattern: 103
     * 1
     * 104
     * 207
     * 310
     *
     * 103s+1
     *
     * Fat Pattern: 101
     * 46
     * 147
     * 248
     *
     * 101f+46
     *
     * 103s+1=101f+46
     * */
    public static void main3(String[] args) {
        //103s+1
        Set<Long> sValues = new HashSet<>();
        for (long s = 0; s < 100_000; s++) {
            sValues.add(s * 103 + 1);
        }

        //101f+46
        Set<Long> fValues = new HashSet<>();
        for (long f = 0; f < 100_000; f++) {
            fValues.add(f * 101 + 46);
        }

        Set<Long> intersection = SetUtils.intersection(sValues, fValues);

        List<Long> sorted = intersection.stream()
                .sorted()
                .toList();

        for (Long n : sorted) {
            System.out.println(n);
        }
    }

    private static int calculateSafetyFactor(List<Pair<Point>> robots, Point gridSize, int seconds) {
        List<Point> robotPositions = robots.stream()
                .map(robot -> calculatePosition(robot, gridSize, seconds))
                .toList();

        int[][] quadrantCounts = {
                {0, 0},
                {0, 0}
        };

        for (Point position : robotPositions) {
            int midX = gridSize.getX() / 2;
            int midY = gridSize.getY() / 2;

            // don't count the robot if it's on the mid line
            if ((position.getX() == midX && gridSize.getX() % 2 == 1)
                    || (position.getY() == midY && gridSize.getY() % 2 == 1)) {
                continue;
            }

            int x = (position.getX() - 1) / (gridSize.getX() / 2);
            int y = (position.getY() - 1) / (gridSize.getY() / 2);

            quadrantCounts[y][x]++;
        }

        return quadrantCounts[0][0] * quadrantCounts[0][1] * quadrantCounts[1][0] * quadrantCounts[1][1];
    }

    private static Point calculatePosition(Pair<Point> robot, Point gridSize, int seconds) {
        Point startPosition = robot.getLeft();
        Point velocity = robot.getRight();

        Point totalMoved = velocity.times(seconds);
        Point finalPosition = startPosition.plus(totalMoved);

        finalPosition = new Point(
                finalPosition.getX() % gridSize.getX(),
                finalPosition.getY() % gridSize.getY()
        );

        finalPosition = new Point(
                finalPosition.getX() < 0 ? gridSize.getX() + finalPosition.getX() : finalPosition.getX(),
                finalPosition.getY() < 0 ? gridSize.getY() + finalPosition.getY() : finalPosition.getY()
        );

        return finalPosition;
    }

    @SneakyThrows
    private static void printRobots(List<Pair<Point>> robots, Point gridSize, int seconds) {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        for (int i = 0; i < seconds; i++) {

            Grid<Character> grid = Grid.of(gridSize.getX(), gridSize.getY(), '.');
            int finalI = i;
            robots.stream()
                    .map(robot -> calculatePosition(robot, gridSize, finalI))
                    .forEach(point -> grid.set(point, '#'));


            System.out.printf("Grid %d:\n", i);
            grid.print();
            System.out.printf("Grid %d:\n", i);
            System.out.println();
            reader.readLine();
        }
    }

    private static void printRobotAfter(List<Pair<Point>> robots, Point gridSize, int seconds) {
        Grid<Character> grid = Grid.of(gridSize.getX(), gridSize.getY(), '.');
        robots.stream()
                .map(robot -> calculatePosition(robot, gridSize, seconds))
                .forEach(point -> grid.set(point, '#'));

        System.out.printf("Grid %d:\n", seconds);
        grid.print();
        System.out.printf("Grid %d:\n", seconds);
    }
}
