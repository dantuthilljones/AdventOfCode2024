package me.detj.aoc_2024;

import me.detj.utils.DTPair;
import me.detj.utils.Grid;
import me.detj.utils.Inputs;
import me.detj.utils.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class Solver15 {
    public static void main(String[] args) {
        var input = Inputs.parseLanternFishWarehouse("2024/input_15.txt");

        long s1 = calculateLanternFishBoxPositions(input, false);
        System.out.printf("Solution Part 1: %d\n", s1);

        long s2 = calculateLanternFishBoxPositionsDouble(input, false);
        System.out.printf("Solution Part 2: %d\n", s2);
    }

    private static long calculateLanternFishBoxPositions(DTPair<Grid<Character>, List<Character>> input, boolean print) {
        Grid<Character> warehouse = input.getLeft();
        List<Point> boxes = warehouse.findAll('O');
        long sum = 0;
        for (Point box : boxes) {
            int distanceFromTop = warehouse.getHeight() - box.getY() - 1;
            int distanceFromLeft = box.getX();
            sum += distanceFromTop * 100L + distanceFromLeft;
        }
        return sum;
    }

    private static Grid<Character> simulateLanternFishWarehouse(DTPair<Grid<Character>, List<Character>> input, boolean print) {
        Grid<Character> warehouse = input.getLeft().shallowCopy();
        List<Character> instructions = input.getRight();

        warehouse.print();
        Point robot = warehouse.findFirst('@');
        warehouse.set(robot, '.');

        for (Character instruction : instructions) {
            try {
                Function<Point, Point> direction = getDirection(instruction);
                Point nextPoint = direction.apply(robot);

                // Can't go through wall
                if (warehouse.pointEquals(nextPoint, '#')) {
                    continue;
                }

                if (warehouse.pointEquals(nextPoint, 'O')) {
                    // Check if we can push all the boxes
                    if (!pushBoxes(warehouse, nextPoint, direction)) {
                        nextPoint = robot;
                    }
                }
                robot = nextPoint;
            } finally {
                if (print) {
                    System.out.println("Move " + instruction);
                    printWarehouse(warehouse, robot, null);
                }
            }
        }

        warehouse.set(robot, '@');
        return warehouse;
    }

    private static void printWarehouse(Grid<Character> warehouse, Point robot, Point prev) {
        warehouse = warehouse.shallowCopy();
        if (prev != null) {
            warehouse.set(prev, 'a');
        }
        warehouse.set(robot, '@');
        warehouse.print();
        System.out.println();
    }

    private static Function<Point, Point> getDirection(Character c) {
        return switch (c) {
            case '^' -> Point::moveUp;
            case '>' -> Point::moveRight;
            case '<' -> Point::moveLeft;
            case 'v' -> Point::moveDown;
            default -> throw new IllegalArgumentException("" + c);
        };
    }

    private static boolean pushBoxes(Grid<Character> warehouse, Point start, Function<Point, Point> direction) {
        Point box = start;
        while (warehouse.inBounds(box)) {
            switch (warehouse.get(box)) {
                case '.' -> {
                    warehouse.set(box, 'O');
                    warehouse.set(start, '.');
                    return true;
                }
                case '#' -> {
                    return false;
                }
                case 'O' -> box = direction.apply(box);
                default -> throw new IllegalArgumentException("" + warehouse.get(box));
            }
        }
        return false;
    }

    private static long calculateLanternFishBoxPositionsDouble(DTPair<Grid<Character>, List<Character>> input, boolean print) {
        Grid<Character> warehouse = makeWarehouseThicc(input.getLeft());
        simulateLanternFishWarehouseThicc(warehouse, input.getRight(), print);
        List<Point> boxes = warehouse.findAll('[');
        long sum = 0;
        for (Point box : boxes) {
            int distanceFromTop = warehouse.getHeight() - box.getY() - 1;
            int distanceFromLeft = box.getX();
            sum += distanceFromTop * 100L + distanceFromLeft;
        }
        return sum;
    }

    private static Grid<Character> makeWarehouseThicc(Grid<Character> warehouse) {
        Grid<Character> doubleWide = Grid.of(warehouse.getWidth() * 2, warehouse.getHeight(), '.');
        warehouse.forEachPoint((Point point, Character value) -> {
            Point p1 = new Point(point.getX() * 2, point.getY());
            Point p2 = new Point(point.getX() * 2 + 1, point.getY());
            switch (value) {
                case '#' -> {
                    doubleWide.set(p1, '#');
                    doubleWide.set(p2, '#');
                }
                case 'O' -> {
                    doubleWide.set(p1, '[');
                    doubleWide.set(p2, ']');
                }
                case '@' -> {
                    doubleWide.set(p1, '@');
                    doubleWide.set(p2, '.');
                }
            }
        });
        return doubleWide;
    }

    private static void simulateLanternFishWarehouseThicc(Grid<Character> warehouse, List<Character> instructions, boolean print) {
        if (print) warehouse.print();
        Point robot = warehouse.findFirst('@');
        Point prev = robot;
        warehouse.set(robot, '.');

        int numBoxLeft = warehouse.findAll('[').size();
        int numBoxRight = warehouse.findAll(']').size();
        int numWalls = warehouse.findAll('#').size();

        for (Character instruction : instructions) {
            try {
                Function<Point, Point> direction = getDirection(instruction);
                Point nextPoint = direction.apply(robot);

                // Can't go through wall
                if (warehouse.pointEquals(nextPoint, '#')) {
                    continue;
                }

                if (warehouse.pointEquals(nextPoint, '[') || warehouse.pointEquals(nextPoint, ']')) {
                    // Check if we can push all the boxes
                    if (!pushBoxesThicc(warehouse, nextPoint, instruction)) {
                        nextPoint = robot;
                    }
                }
                prev = robot;
                robot = nextPoint;
            } finally {
                if (print) {
                    System.out.println("Move " + instruction);
                    printWarehouse(warehouse, robot, prev);
                }


                if (warehouse.findAll('[').size() != numBoxLeft) {
                    throw new IllegalArgumentException("[");
                }
                if (warehouse.findAll(']').size() != numBoxRight) {
                    throw new IllegalArgumentException("]");
                }
                if (warehouse.findAll('#').size() != numWalls) {
                    throw new IllegalArgumentException("#");
                }
            }
        }

        warehouse.set(robot, '@');
    }

    private static boolean pushBoxesThicc(Grid<Character> warehouse, Point start, char instruction) {
        Function<Point, Point> direction = getDirection(instruction);
        if (instruction == '<' || instruction == '>') {
            return pushBoxesThiccHorizontal(warehouse, start, direction);
        } else {
            return pushBoxesThiccVertical(warehouse, start, direction);
        }
    }

    private static boolean pushBoxesThiccHorizontal(Grid<Character> warehouse, Point start, Function<Point, Point> direction) {
        Point point = start;
        Set<Point> pointsToMove = new HashSet<>();
        while (warehouse.inBounds(point)) {
            if (warehouse.pointEquals(point, '.')) {
                shiftBoxes(warehouse, pointsToMove, direction);
                return true;
            } else if (warehouse.pointEquals(point, '[') || warehouse.pointEquals(point, ']')) {
                pointsToMove.add(point);
            } else {
                return false;
            }
            point = direction.apply(point);
        }
        return false;
    }

    private static void shiftBoxesHorizontal(Grid<Character> warehouse, Point start, Point end, Function<Point, Point> direction) {
        List<DTPair<Point, Character>> newValues = new ArrayList<>();
        Point current = start;
        while (!current.equals(end)) {
            Point next = direction.apply(current);
            newValues.add(new DTPair<>(next, warehouse.get(current)));
            current = next;
        }

        for (DTPair<Point, Character> newValue : newValues) {
            Point point = newValue.getLeft();
            Character value = newValue.getRight();
            warehouse.set(point, value);
        }
        warehouse.set(start, '.');
    }

    private static boolean pushBoxesThiccVertical(Grid<Character> warehouse, Point start, Function<Point, Point> direction) {
        Set<Point> boxesToBePushed = new HashSet<>();
        if (calculateBoxesToPush(warehouse, start, direction, boxesToBePushed)) {
            shiftBoxes(warehouse, boxesToBePushed, direction);
            return true;
        }
        return false;
    }

    private static void shiftBoxes(Grid<Character> warehouse, Set<Point> boxesToBePushed, Function<Point, Point> direction) {
        Map<Point, Character> newValues = new HashMap<>();
        for (Point point : boxesToBePushed) {
            newValues.put(direction.apply(point), warehouse.get(point));
        }

        for (Point point : boxesToBePushed) {
            if (!newValues.containsKey(point)) {
                newValues.put(point, '.');
            }
        }

        newValues.forEach(warehouse::set);
    }

    private static boolean calculateBoxesToPush(Grid<Character> warehouse, Point current, Function<Point, Point> direction, Set<Point> boxes) {
        if (boxes.contains(current)) {
            return true;
        }
        if (warehouse.pointEquals(current, '[')) {
            boxes.add(current);
            boxes.add(current.moveRight());

            return calculateBoxesToPush(warehouse, direction.apply(current), direction, boxes)
                    && calculateBoxesToPush(warehouse, direction.apply(current.moveRight()), direction, boxes);
        } else if (warehouse.pointEquals(current, ']')) {
            boxes.add(current);
            boxes.add(current.moveLeft());

            return calculateBoxesToPush(warehouse, direction.apply(current), direction, boxes)
                    && calculateBoxesToPush(warehouse, direction.apply(current.moveLeft()), direction, boxes);
        } else {
            return warehouse.pointEquals(current, '.');
        }
    }
}
