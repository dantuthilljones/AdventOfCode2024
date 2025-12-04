package me.detj.aoc_2025;

import me.detj.utils.Grid;
import me.detj.utils.Inputs;
import me.detj.utils.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Solver03 {

    public static void main(String[] args) throws IOException {
        var input = Inputs.parseDenseIntGrid("2025/input_03.txt");
        System.out.printf("Solution 1: %d\n", calcMaxJoltage(input));
        System.out.printf("Solution 2: %d\n", calcMaxJoltage(input, 12));
    }

    private static long calcMaxJoltage(Grid<Integer> grid) {
        long maxJoltage = 0;
        for (int y = 0; y < grid.getHeight(); y++) {
            int joltage = calcMaxJoltage(grid.getRow(y));
            System.out.println("Row " + y + " max joltage: " + joltage);
            maxJoltage += joltage;
        }
        return maxJoltage;
    }

    private static int calcMaxJoltage(List<Integer> row) {
        int firstMaxIndex = -1;
        int firstMaxValue = -1;
        for(int i = 0; i < row.size() -1; i++) {
            if (row.get(i) > firstMaxValue) {
                firstMaxValue = row.get(i);
                firstMaxIndex = i;
            }
        }

        int secondMaxValue = -1;
        for(int j = firstMaxIndex +1; j < row.size(); j++) {
            if (row.get(j) > secondMaxValue) {
                secondMaxValue = row.get(j);
            }
        }

        return firstMaxValue * 10 + secondMaxValue;
    }

    private static long calcMaxJoltage(Grid<Integer> grid, int digits) {
        long maxJoltage = 0;
        for (int y = 0; y < grid.getHeight(); y++) {
            long joltage = calcMaxJoltage(grid.getRow(y), digits);
            System.out.println("Row " + y + " max joltage: " + joltage);
            maxJoltage += joltage;
        }
        return maxJoltage;
    }

    private static long calcMaxJoltage(List<Integer> row, int digits) {
        List<Integer> digitsList = new ArrayList<>();
        int lastIndex = 0;
        for(int i = digits; i > 0; i--) {
            Pair<Integer> maxPair = calcMaxJoltageRow(row, i, lastIndex);
            lastIndex = maxPair.getLeft() +1;
            digitsList.add(maxPair.getRight());
            row.set(maxPair.getLeft(), -1); // mark as used
        }

        long result = 0;
        for (Integer digit : digitsList) {
            result = result * 10 + digit;
        }
        return result;
    }

    // return the left most largest value, which is before the last "digitsLeft" digits
    private static Pair<Integer> calcMaxJoltageRow(List<Integer> row, int digitsLeft, int startIndex) {
        int maxIndex = -1;
        int maxValue = -1;
        for(int i = startIndex; i <= row.size() -digitsLeft; i++) {
            if (row.get(i) > maxValue) {
                maxValue = row.get(i);
                maxIndex = i;
            }
        }
        return new Pair<>(maxIndex, maxValue);
    }
}
