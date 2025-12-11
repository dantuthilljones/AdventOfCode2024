package me.detj.aoc_2025;

import me.detj.utils.BinaryGrid;
import me.detj.utils.Grid;
import me.detj.utils.Inputs;
import me.detj.utils.Point;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

public class Solver09BooleanGrid {

    public static void main(String[] args) throws IOException {
        String file = "2025/input_09.txt";
        var input = Inputs.parseListOfPoints(file);
        System.out.printf("Solution 2: %d\n", largestRectangle2(input));
    }


    private static long largestRectangle2(List<Point> input) {
        int maxX = input.stream().mapToInt(Point::getX).max().orElse(0);
        int maxY = input.stream().mapToInt(Point::getY).max().orElse(0);

        BinaryGrid grid = BinaryGrid.create(maxX + 2, maxY + 2);
        for (int i = 0; i < input.size(); i++) {
            Point p1 = input.get(i);
            Point p2 = input.get((i + 1) % input.size());
            drawLine(grid, p1, p2);
//            grid.print();
//            int debug = 0;
        }

        BinaryGrid outsideTiles = fillOutsideTiles(grid);

        return largestRectangle(input, outsideTiles);
    }

    private static void drawLine(BinaryGrid grid, Point p1, Point p2) {
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
            grid.set(current, true);
            current = direction.apply(current);
        }
        grid.set(p1, true);
        grid.set(p2, true);
    }

    private static BinaryGrid fillOutsideTiles(BinaryGrid grid) {
        BinaryGrid outsideTiles = BinaryGrid.create(grid.getWidth(), grid.getHeight());
        visitOutsideFillKeepTrying(grid, outsideTiles);
        return outsideTiles;
    }

    private static void visitOutsideFillKeepTrying(BinaryGrid grid, BinaryGrid outsideTiles) {
        outsideTiles.set(0, 0, true);


        long iteration = 0;
        while (true) {
            fillWithFirstStartLogic(grid, outsideTiles);
            Point startPoint = findNewStartPoint(grid, outsideTiles);
            if (iteration % 1 == 0) {
                System.out.printf("Outside fill iteration %d, starting at %s\n", iteration, startPoint);
                long outsideTilesCount = outsideTiles.countTrue();
                long totalTiles = outsideTiles.getWidth() * outsideTiles.getHeight();
                System.out.printf("  Total tiles: %d\n", totalTiles);
                System.out.printf("  Outside tiles so far: %d (%.5f%%)\n", outsideTilesCount, (outsideTilesCount * 100.0) / totalTiles);
            }

            try {
                if(startPoint!= null) {
                    visitOutsideFill(grid, outsideTiles, startPoint);
                }
                break;
            } catch (StackOverflowError e) {
                System.out.printf("  Stack overflow on iteration %d, trying again...\n", iteration);
            }
            iteration++;
        }
        System.out.print("Found no start point, must be fully filled!\n");
//        grid.print();
//        int debug = 0;
    }

    private static void fillWithFirstStartLogic(BinaryGrid grid, BinaryGrid outsideTiles) {
        long start = System.currentTimeMillis();
        long filled = 0;
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                Point p = new Point(x, y);

                // if already flagged as outside or on a line
                if (outsideTiles.get(p) || grid.get(p)) {
                    continue;
                }

                for (var dir : Point.BASIC_DIRECTIONS) {
                    Point neighbor = dir.apply(p);
                    if (outsideTiles.inBounds(neighbor) && outsideTiles.get(neighbor)) {
                        outsideTiles.set(p, true);
                        filled++;
                        break;
                    }
                }
            }
        }

        for (int x = (int) grid.getWidth() -1; x <= 0; x--) {
            for (int y = (int) grid.getHeight(); y <= 0; y--) {
                Point p = new Point(x, y);
                // if already flagged as outside or on a line
                if (outsideTiles.get(p) || grid.get(p)) {
                    continue;
                }

                for (var dir : Point.BASIC_DIRECTIONS) {
                    Point neighbor = dir.apply(p);
                    if (outsideTiles.inBounds(neighbor) && outsideTiles.get(neighbor)) {
                        outsideTiles.set(p, true);
                        filled++;
                        break;
                    }
                }
            }
        }
        long end = System.currentTimeMillis();
        System.out.printf("fillWithFirstStartLogic pass took %d ms. Filled %d\n", (end - start), filled);
    }

    // new starting point will have 1 neighbor which is an outside tile, and won't be filled yet
    // And won't be on a line tile
    private static Point findNewStartPoint(BinaryGrid grid, BinaryGrid outsideTiles) {
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                Point p = new Point(x, y);

                // if already flagged as outside or on a line
                if (outsideTiles.get(p) || grid.get(p)) {
                    continue;
                }

                for (var dir : Point.BASIC_DIRECTIONS) {
                    Point neighbor = dir.apply(p);
                    if (outsideTiles.inBounds(neighbor) && outsideTiles.get(neighbor)) {
                        return p;
                    }
                }
            }
        }
        return null;
    }


    private static void visitOutsideFill(BinaryGrid grid, BinaryGrid outsideTiles, Point point) {
        if (!outsideTiles.inBounds(point)) {
            return;
        }

        boolean outsideValue = outsideTiles.get(point);
        if (outsideValue) {
            // Already visited
            return;
        }

        boolean gridValue = grid.get(point);
        if (gridValue) {
            // Hit a green/red tile
            return;
        }

        outsideTiles.set(point, true);

        for (var dir : Point.BASIC_DIRECTIONS) {
            Point neighbor = dir.apply(point);
            visitOutsideFill(grid, outsideTiles, neighbor);
        }
    }


    private static long largestRectangle(List<Point> input, BinaryGrid outsideTiles) {
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
                    if (outsideTiles.get(x, minY) || outsideTiles.get(x, maxY)) {
                        continue check;
                    }
                }

                for (int y = minY; y <= maxY; y++) {
                    if (outsideTiles.get(minX, y) || outsideTiles.get(maxX, y)) {
                        continue check;
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
