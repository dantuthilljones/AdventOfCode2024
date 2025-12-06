package me.detj.aoc_2025;

import me.detj.utils.Grid;
import me.detj.utils.Inputs;
import me.detj.utils.Pair;

import java.io.IOException;
import java.util.*;

import static java.lang.Long.parseLong;

public class Solver06 {

    public static void main(String[] args) throws IOException {
        String file = "2025/input_06.txt";
        var input = Inputs.parseColumns(file);
        System.out.printf("Solution 1: %d\n", cephalopodMaths(input));

        var input2 = Inputs.parseCharGridWithPadding(file);
        System.out.printf("Solution 2: %d\n", cephalopodMaths2(input2));
    }

    private static long cephalopodMaths(List<List<String>> input) {
        long sum = 0;
        for (int col = 0; col < input.get(0).size(); col++) {
            List<Long> numbers = new ArrayList<>();
            for (int row = 0; row < input.size() - 1; row++) {
                String cell = input.get(row).get(col);
                numbers.add(parseLong(cell));
            }
            String operation = input.getLast().get(col);

            if (operation.equals("*")) {
                long result = numbers.stream()
                        .mapToLong(n -> n)
                        .reduce(1, (a, b) -> a * b);
                sum += result;
            } else if (operation.equals("+")) {
                sum += numbers.stream()
                        .mapToLong(n -> n)
                        .sum();
            }
        }
        return sum;
    }

    private static long cephalopodMaths2(Grid<Character> input) {
        List<Character> opRow = input.getRow(0);


        long result = 0;

        for (Pair<Integer> colRange : getProblemCols(input)) {

            List<Long> numbers = new ArrayList<>();
            for (int col = colRange.getLeft(); col <= colRange.getRight(); col++) {
                numbers.add(getNumForCol(input, col));
            }

            char operation = input.get(colRange.getLeft(), 0);
            if (operation == '*') {
                result += numbers.stream()
                        .mapToLong(n -> n)
                        .reduce(1, (a, b) -> a * b);
            } else if (operation == '+') {
                result += numbers.stream()
                        .mapToLong(n -> n)
                        .sum();
            }
        }


        return result;
    }

    private static long getNumForCol(Grid<Character> input, int col) {
        long num = 0;
        for (int row = input.getHeight() - 1; row > 0; row--) {
            char cell = input.get(col, row);
            if (cell != ' ') {
                long val = parseLong(String.valueOf(cell));
                num = num * 10 + val;
            }
        }
        return num;
    }

    private static List<Pair<Integer>> getProblemCols(Grid<Character> input) {
        List<Pair<Integer>> results = new ArrayList<>();

        List<Character> opRow = input.getRow(0);
        int leftIndex = 0;
        for (int rightIndex = 1; rightIndex < opRow.size(); rightIndex++) {
            if (input.get(rightIndex, 0) != ' ') {
                results.add(new Pair<>(leftIndex, rightIndex - 2));
                leftIndex = rightIndex;
            }
        }

        results.add(new Pair<>(leftIndex, opRow.size() - 1));

        return results;
    }
}
