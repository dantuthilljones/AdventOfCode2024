package me.detj.aoc_2025;

import me.detj.utils.Grid;
import me.detj.utils.Inputs;
import me.detj.utils.Pair;
import me.detj.utils.Point;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Long.parseLong;

public class Solver07 {

    public static void main(String[] args) throws IOException {
        String file = "2025/input_07.txt";
        var input = Inputs.parseCharGrid(file);
        System.out.printf("Solution 1: %d\n", simulate(input.shallowCopy()));
        System.out.printf("Solution 2: %d\n", simulateQuantum(input.shallowCopy()));
    }

    private static int simulate(Grid<Character> input) {
        Point start = input.findFirst('S');
        return simulateBeam(input, start);
    }

    private static int simulateBeam(Grid<Character> input, Point current) {
        if (!input.inBounds(current)) {
            return 0;
        }

        if (input.get(current).equals('^')) {
            return 1 + simulateBeam(input, current.moveLeft()) + simulateBeam(input, current.moveRight());
        } else if (input.get(current).equals('S') || input.get(current).equals('.')) {
            input.set(current, '|');
            return simulateBeam(input, current.moveDown());
        } else if (input.get(current).equals('|')) {
            return 0;
        }

        throw new RuntimeException("Should never reach here!");
    }

    private static long simulateQuantum(Grid<Character> input) {
        Point start = input.findFirst('S');
        return simulateQuantumBeam(input, start);
    }

    private static Map<Point, Long> cache = new HashMap<>();

    private static long simulateQuantumBeam(Grid<Character> input, Point current) {
        Long cached = cache.get(current);
        if (cached != null) {
            return cached;
        }


        long value = -1;
        if (!input.inBounds(current)) {
            value = 1;
        } else if (input.get(current).equals('^')) {
            value = simulateQuantumBeam(input, current.moveLeft()) + simulateQuantumBeam(input, current.moveRight());
        } else if (input.get(current).equals('S') || input.get(current).equals('.')) {
            value = simulateQuantumBeam(input, current.moveDown());
        }

        if (value == -1) {
            throw new RuntimeException("Should never reach here!");
        }

        cache.put(current, value);
        return value;
    }


}
