package me.detj.aoc_2024;

import me.detj.utils.Grid;
import me.detj.utils.Inputs;
import me.detj.utils.MazePostion;
import me.detj.utils.Point;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.function.Function;

public class Solver18 {
    public static void main(String[] args) {
        var input = Inputs.parseListOfPoints("input_18.txt");

        int s1 = lengthOfShortestPathInComputer(input, 1024);
        System.out.printf("Solution Part 1: %d\n", s1);

        Point s2 = lastWithSolution(input);
        System.out.printf("Solution Part 2: %s\n", s2);
    }

    private static int lengthOfShortestPathInComputer(List<Point> positions, int firstBytes) {
        Grid<Character> map = Grid.of(71, 71, '.');
        for (int i = 0; i < firstBytes; i++) {
            Point position = positions.get(i);
            map.set(position, '#');
        }

        Point start = new Point(0, 0);
        Point exit = new Point(70, 70);

        // Initialize objects which keep track of score and path. The values are an array of positions where index = direction (0 = up, 1 = right, 2 = down, 3 = left)
        Grid<Integer> distances = Grid.of(map.getWidth(), map.getHeight(), Integer.MAX_VALUE);

        PriorityQueue<MazePostion> queue = new PriorityQueue<>(Comparator.comparing(MazePostion::getScore));
        queue.add(new MazePostion(start, 0, 0, List.of()));

        while (!queue.isEmpty()) {
            MazePostion position = queue.poll();
            stepComputer(map, distances, queue, position);
            //drawMaze(maze, distances);
        }

        return distances.get(exit);

    }

    private static void stepComputer(Grid<Character> maze, Grid<Integer> distances, PriorityQueue<MazePostion> queue, MazePostion position) {
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
            MazePostion nextPosition = new MazePostion(next, 0, position.getScore() + 1, List.of());
            queue.add(nextPosition);
        }
    }

    private static Point lastWithSolution(List<Point> positions) {
        for (int i = 1024; i < positions.size(); i++) {
            if (lengthOfShortestPathInComputer(positions, i) == Integer.MAX_VALUE) {
                return positions.get(i - 1);
            }
        }
        return null;
    }
}
