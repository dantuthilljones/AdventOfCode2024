package me.detj.aoc_2025;

import me.detj.utils.Grid;
import me.detj.utils.Inputs;
import me.detj.utils.Point;

import java.io.IOException;

import static me.detj.utils.Point.ALL_DIRECTIONS;

public class Solver04 {

    public static void main(String[] args) throws IOException {
        var input = Inputs.parseCharGrid("2025/input_04.txt");
        System.out.printf("Solution 1: %d\n", calcAccessible(input));
        System.out.printf("Solution 2: %d\n", calcAccessibleWithRemoves(input));
    }

    private static int calcAccessible(Grid<Character> input) {
        int accessible = 0;

        for (Point roll : input.findAll('@')) {
            int neighborRolls = 0;
            for (var d : ALL_DIRECTIONS) {
                Point neighbor = d.apply(roll);
                if (input.inBounds(neighbor) && input.get(neighbor) == '@') {
                    neighborRolls++;
                }
            }
            if (neighborRolls < 4) {
                accessible++;
            }
        }
        return accessible;
    }

    private static int calcAccessibleWithRemoves(Grid<Character> input) {
        Grid<Character> original = input.shallowCopy();
        while(true) {
            boolean removed = false;
            for (Point roll : input.findAll('@')) {
                int neighborRolls = 0;
                for (Point neighbor : roll.getAllNeighbors()) {
                    if (input.inBounds(neighbor) && input.get(neighbor) == '@') {
                        neighborRolls++;
                    }
                }
                if (neighborRolls < 4) {
                    input.set(roll, '.');
                    removed = true;
                }
            }
            if (!removed) {
                break;
            }
        }
        return original.count('@') - input.count('@');
    }
}
