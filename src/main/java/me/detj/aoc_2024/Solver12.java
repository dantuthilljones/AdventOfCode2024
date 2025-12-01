package me.detj.aoc_2024;

import me.detj.utils.DTPair;
import me.detj.utils.Grid;
import me.detj.utils.Inputs;
import me.detj.utils.Pair;
import me.detj.utils.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class Solver12 {
    public static void main(String[] args) {
        var garden = Inputs.parseCharGrid("2024/input_12.txt");

        long price = priceFence(garden);
        System.out.printf("Solution Part 1: %d\n", price);

        long priceWithDiscount = priceFenceWithDiscount(garden);
        System.out.printf("Solution Part 2: %d\n", priceWithDiscount);
    }

    private static int priceFence(Grid<Character> garden) {
        int price = 0;
        Set<Point> visited = new HashSet<>();
        for (int y = 0; y < garden.getHeight(); y++) {
            for (int x = 0; x < garden.getWidth(); x++) {
                Point current = Point.of(x, y);
                Pair<Integer> areaAndPerimeter = calcAreaAndPerimeter(garden, visited, new HashSet<>(), current, current);
                price += areaAndPerimeter.getLeft() * areaAndPerimeter.getRight();
            }
        }
        return price;
    }

    private static Pair<Integer> calcAreaAndPerimeter(Grid<Character> garden, Set<Point> visitedGlobal, Set<Point> visitedGarden, Point start, Point current) {
        if (garden.pointsAreEqual(start, current)) {
            boolean visited = !visitedGlobal.add(current) | !visitedGarden.add(current);
            if (visited) {
                return new Pair<>(0, 0);
            }
        } else {
            return new Pair<>(0, 0);
        }

        Pair<Integer> areaAndPerimeter = new Pair<>(1, 0);
        for (Function<Point, Point> direction : Point.BASIC_DIRECTIONS) {
            Point neighbor = direction.apply(current);

            // Skip if we've already visited this neighbor in this garden
            if (visitedGarden.contains(neighbor)) {
                continue;
            }

            Pair<Integer> neighborAreaAndPerimeter = calcAreaAndPerimeter(garden, visitedGlobal, visitedGarden, start, neighbor);

            if (neighborAreaAndPerimeter.getLeft() == 0 && neighborAreaAndPerimeter.getRight() == 0) {
                // if neighbor is not part of the garden, add a fence
                areaAndPerimeter = add(areaAndPerimeter, new Pair<>(0, 1));
            } else {
                // if neighbor is part of the garden, add their area and perimeter
                areaAndPerimeter = add(areaAndPerimeter, neighborAreaAndPerimeter);
            }
        }

        return areaAndPerimeter;
    }

    private static Pair<Integer> add(Pair<Integer> p1, Pair<Integer> p2) {
        return new Pair<>(p1.getLeft() + p2.getLeft(), p1.getRight() + p2.getRight());
    }

    private static int priceFenceWithDiscount(Grid<Character> garden) {
        int price = 0;
        Set<Point> visited = new HashSet<>();
        for (int y = 0; y < garden.getHeight(); y++) {
            for (int x = 0; x < garden.getWidth(); x++) {
                Point current = Point.of(x, y);

                Map<Point, boolean[]> region = new HashMap<>();
                calcAreaAndPerimeterDiscount(garden, visited, current, current, region);

                int perimeter = calculateDiscountedPerimeter(region);
                int area = region.size();
                price += area * perimeter;
            }
        }
        return price;
    }

    private static final int UP = 0, RIGHT = 1, DOWN = 2, LEFT = 3;
    private static final List<Integer> DIRECTIONS = List.of(UP, RIGHT, DOWN, LEFT);

    private static void calcAreaAndPerimeterDiscount(Grid<Character> garden, Set<Point> visitedGlobal, Point start, Point current, Map<Point, boolean[]> region) {
        if (garden.pointsAreEqual(start, current)) {
            boolean visited = !visitedGlobal.add(current) | region.containsKey(current);
            if (visited) {
                return;
            }
        } else {
            return;
        }

        boolean[] fences = {true, true, true, true};
        for (Integer direction : DIRECTIONS) {
            Function<Point, Point> directionFn = Point.BASIC_DIRECTIONS.get(direction);
            Point neighbor = directionFn.apply(current);

            calcAreaAndPerimeterDiscount(garden, visitedGlobal, start, neighbor, region);

            if (garden.pointsAreEqual(start, neighbor)) {
                fences[direction] = false;
            }
        }

        region.put(current, fences);
    }


    private static int calculateDiscountedPerimeter(Map<Point, boolean[]> region) {
        Set<DTPair<Point, Integer>> visited = new HashSet<>();
        List<List<DTPair<Point, Integer>>> joinedFences = new ArrayList<>();

        region.forEach((point, fences) -> {
            for (Integer direction : DIRECTIONS) {
                if (!fences[direction]) continue;

                if (!visited.add(new DTPair<>(point, direction))) {
                    continue;
                }

                List<DTPair<Point, Integer>> joinedFence = new ArrayList<>();
                joinedFence.add(new DTPair<>(point, direction));

                // check along the fence in the direction and the opposite direction
                for (int checkDirection : new int[]{(direction + 1) % 4, (direction + 3) % 4}) {
                    Point current = point;
                    while (true) {
                        current = Point.BASIC_DIRECTIONS.get(checkDirection).apply(current);
                        boolean[] currentFences = region.get(current);
                        if (currentFences == null || !currentFences[direction]) {
                            break;
                        }
                        DTPair<Point, Integer> fence = new DTPair<>(current, direction);
                        visited.add(fence);
                        joinedFence.add(fence);
                    }
                }
                joinedFences.add(joinedFence);
            }
        });

        return joinedFences.size();
    }
}
