package me.detj.aoc_2025;

import me.detj.utils.Grid;
import me.detj.utils.Inputs;
import me.detj.utils.Point;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

public class Solver09 {

    public static void main(String[] args) throws IOException {
        String file = "2025/input_09.txt";
        var input = Inputs.parseListOfPoints(file);
        System.out.printf("Solution 1: %d\n", largestRectangle(input));
        System.out.printf("Solution 2: %d\n", largestRectangle2(input));
    }

    private static long largestRectangle(List<Point> input) {
        long largestArea = 0;

        for (int i = 0; i < input.size() - 1; i++) {
            for (int j = i + 1; j < input.size(); j++) {
                Point p1 = input.get(i);
                Point p2 = input.get(j);

                long x = Math.abs((long) p1.getX() - p2.getX()) + 1;
                long y = Math.abs((long) p1.getY() - p2.getY()) + 1;

                long area = x * y;
                if (area > largestArea) {
                    largestArea = area;
                }
            }
        }
        return largestArea;

    }


    private static long largestRectangle2(List<Point> input) {
        int maxX = input.stream().mapToInt(Point::getX).max().orElse(0);
        int maxY = input.stream().mapToInt(Point::getY).max().orElse(0);

        Grid<Character> grid = Grid.of(maxX + 2, maxY + 2, '.');
        for (int i = 0; i < input.size(); i++) {
            Point p1 = input.get(i);
            Point p2 = input.get((i + 1) % input.size());
            drawLine(grid, p1, p2);
            //grid.print();
            //int debug = 0;
        }

        Grid<Boolean> outsideTiles = fillGreens(grid);
        return largestRectangle(input, outsideTiles);
    }

    private static void drawLine(Grid<Character> grid, Point p1, Point p2) {
        Function<Point, Point> direction = null;
        if (p2.getX() > p1.getX()) {
            direction = Point::moveRight;
        } else if (p2.getX() < p1.getX()) {
            direction = Point::moveLeft;
        } else if (p2.getY() > p1.getY()) {
            direction = Point::moveUp;
        } else if (p2.getY() < p1.getY()) {
            direction = Point::moveDown;
        } else {
            throw new RuntimeException("Should not reach here!");
        }

        Point current = p1;
        while (!current.equals(p2)) {
            grid.set(current, 'X');
            current = direction.apply(current);
        }
        grid.set(p1, '#');
        grid.set(p2, '#');
    }

    private static Grid<Boolean> fillGreens(Grid<Character> grid) {
        Grid<Boolean> outsideTiles = Grid.of(grid.getWidth(), grid.getHeight(), false);
        visitNonGreenFill(grid, outsideTiles, new Point(0, 0));
        return outsideTiles;
    }

    private static void visitNonGreenFill(Grid<Character> grid, Grid<Boolean> outsideTiles, Point p) {
        if (!outsideTiles.inBounds(p)) {
            return;
        }

        boolean outsideValue = outsideTiles.get(p);
        if (outsideValue) {
            // Already visited
            return;
        }

        char gridValue = grid.get(p);
        if (gridValue != '.') {
            // Hit a green/red tile
            return;
        }

        outsideTiles.set(p, true);

        for (var dir : Point.BASIC_DIRECTIONS) {
            Point neighbor = dir.apply(p);
            visitNonGreenFill(grid, outsideTiles, neighbor);
        }
    }

    private static long largestRectangle(List<Point> input, Grid<Boolean> outsideTiles) {
        long largestArea = 0;

        for (int i = 0; i < input.size() - 1; i++) {
            check:
            for (int j = i + 1; j < input.size(); j++) {
                Point p1 = input.get(i);
                Point p2 = input.get(j);

                int maxX = Math.max(p1.getX(), p2.getX());
                int minX = Math.min(p1.getX(), p2.getX());
                int minY = Math.min(p1.getY(), p2.getY());
                int maxY = Math.max(p1.getY(), p2.getY());

                for (int x = minX; x <= maxX; x++) {
                    for (int y = minY; y <= maxY; y++) {
                        if (outsideTiles.get(x, y)) {
                            continue check;
                        }
                    }
                }

                long x = Math.abs((long) p1.getX() - p2.getX()) + 1;
                long y = Math.abs((long) p1.getY() - p2.getY()) + 1;
                long area = x * y;
                if (area > largestArea) {
                    largestArea = area;
                }
            }
        }
        return largestArea;

    }
}
