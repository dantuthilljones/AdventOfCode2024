package me.detj.aoc_2024;

import me.detj.utils.Grid;
import me.detj.utils.Inputs;
import me.detj.utils.MazePostion;
import me.detj.utils.MazeResult;
import me.detj.utils.Point;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;

public class Solver16 {
    public static void main(String[] args) {
        var input = Inputs.parseCharGrid("2024/input_16.txt");

        MazeResult result = calculateReindeerMaze(input);
        System.out.printf("Solution Part 1: %d\n", result.getCost());
        System.out.printf("Solution Part 2: %d\n", result.getPointsInPaths().size());
    }

    private static void drawMaze(Grid<Character> maze, Grid<Integer[]> distances) {
        Grid<Character> toDraw = maze.shallowCopy();

        distances.forEachPoint((point, distance) -> {
            int minIndex = -1;
            int minDistance = Integer.MAX_VALUE;
            for (int i = 0; i < distance.length; i++) {
                if (distance[i] < minDistance) {
                    minDistance = distance[i];
                    minIndex = i;
                }
            }
            if (minIndex != -1) {
                char c = switch (minIndex) {
                    case 0 -> '^';
                    case 1 -> '>';
                    case 2 -> 'v';
                    case 3 -> '<';
                    default -> throw new IllegalArgumentException();
                };
                toDraw.set(point, c);
            }
        });

        toDraw.print();
    }

    private static void step(Grid<Character> maze, Grid<List<List<MazePostion>>> scores, PriorityQueue<MazePostion> queue, MazePostion position) {
        // Can't step through walls
        if (maze.pointEquals(position.getPoint(), '#')) {
            return;
        }

        // If we've found this position for cheaper, skip
        List<MazePostion> bestPaths = scores.get(position.getPoint()).get(position.getDirection());
        if (!bestPaths.isEmpty() && bestPaths.get(0).getScore() < position.getScore()) {
            return;
        }

        // We've found a new best path
        // Clear the list if the new path is better, otherwise we just add this new path
        if (!bestPaths.isEmpty() && bestPaths.get(0).getScore() > position.getScore()) {
            bestPaths.clear();
        }
        bestPaths.add(position);

        // step forwards
        Point forwards = Point.BASIC_DIRECTIONS.get(position.getDirection()).apply(position.getPoint());
        List<MazePostion> path = new ArrayList<>(position.getPath());
        path.add(position);
        MazePostion forwardPosition = new MazePostion(forwards, position.getDirection(), position.getScore() + 1, path);
        queue.add(forwardPosition);

        // rotate right
        int right = (position.getDirection() + 1) % 4;
        List<MazePostion> rightPath = new ArrayList<>(position.getPath());
        rightPath.add(position);
        queue.add(new MazePostion(position.getPoint(), right, position.getScore() + 1000, rightPath));

        // rotate left
        int left = (position.getDirection() + 3) % 4;
        List<MazePostion> leftPath = new ArrayList<>(position.getPath());
        leftPath.add(position);
        queue.add(new MazePostion(position.getPoint(), left, position.getScore() + 1000, leftPath));
    }

    private static MazeResult calculateReindeerMaze(Grid<Character> maze) {
        Point start = maze.findFirst('S');
        Point end = maze.findFirst('E');

        // Initialize objects which keep track of score and path. The values are an array of positions where index = direction (0 = up, 1 = right, 2 = down, 3 = left)
        Grid<List<List<MazePostion>>> distances = Grid.ofSupplier(maze.getWidth(), maze.getHeight(), () -> List.of(
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()
        ));

        PriorityQueue<MazePostion> queue = new PriorityQueue<>(Comparator.comparing(MazePostion::getScore));
        queue.add(new MazePostion(start, 1, 0, List.of()));

        while (!queue.isEmpty()) {
            MazePostion position = queue.poll();
            step(maze, distances, queue, position);
            //drawMaze(maze, distances);
        }

        int min = distances.get(end).stream()
                .flatMap(List::stream)
                .mapToInt(distance -> distance.getScore())
                .min()
                .getAsInt();

        List<List<MazePostion>> paths = distances.get(end).stream()
                .flatMap(List::stream)
                .filter(distance -> distance.getScore() == min)
                .map(MazePostion::getPath)
                .toList();

        Set<Point> points = paths.stream()
                .flatMap(List::stream)
                .map(MazePostion::getPoint)
                .collect(Collectors.toSet());
        points.add(end);

        drawPointsOnMaze(maze, points);

        return new MazeResult(paths, points, min);
    }

    private static void drawPointsOnMaze(Grid<Character> maze, Set<Point> points) {
        Grid<Character> toDraw = maze.shallowCopy();
        points.forEach(point -> toDraw.set(point, 'O'));
        toDraw.print();
    }
}
