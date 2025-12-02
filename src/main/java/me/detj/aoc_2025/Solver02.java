package me.detj.aoc_2025;

import me.detj.utils.Inputs;
import me.detj.utils.Pair;

import java.io.IOException;
import java.util.List;

public class Solver02 {

    public static void main(String[] args) throws IOException {
        var input = Inputs.parseListOfPairsWithDashes("2025/input_02.txt");
        System.out.printf("Solution 1: %d\n", countProductIds(input));
        System.out.printf("Solution 2: %d\n", countProductIdsPart2(input));
    }

    private static long countProductIds(List<Pair<Long>> ranges) {
        long count = 0;
        for (Pair<Long> range : ranges) {
            for (long number = range.getLeft(); number <= range.getRight(); number++) {
                if (!isValid(number)) {
                    System.out.println("Invalid number: " + number);
                    count += number;
                }
            }
        }
        return count;
    }

    private static boolean isValid(long number) {
        return isValid(String.valueOf(number));
    }

    private static boolean isValid(String str) {
        if (str.length() % 2 == 1) {
            return true;
        }

        String left = str.substring(0, str.length() / 2);
        String right = str.substring(str.length() / 2);
        return !left.equals(right);
    }

    private static long countProductIdsPart2(List<Pair<Long>> ranges) {
        long count = 0;
        for (Pair<Long> range : ranges) {
            for (long number = range.getLeft(); number <= range.getRight(); number++) {
                if (isInvalidPart2(number)) {
                    System.out.println("Invalid number: " + number);
                    count += number;
                }
            }
        }
        return count;
    }

    private static boolean isInvalidPart2(long number) {
        return isInvalidPart2(String.valueOf(number));
    }

    private static boolean isInvalidPart2(String number) {
        for (int pieces = 2; pieces <= number.length(); pieces++) {
            String[] splits = Split(number, pieces);
            if (splits == null) {
                continue;
            }
            if (isAllEqual(splits)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isAllEqual(String[] splits) {
        boolean allEqual = true;
        for (int i = 1; i < splits.length; i++) {
            if (!splits[i].equals(splits[0])) {
                allEqual = false;
            }
        }
        return allEqual;
    }

    private static String[] Split(String number, int pieces) {
        if (number.length() % pieces != 0) {
            return null;
        }
        String[] splits = new String[pieces];
        int pieceLength = number.length() / pieces;
        for (int i = 0; i < pieces; i++) {
            splits[i] = number.substring(i * pieceLength, (i + 1) * pieceLength);
        }
        return splits;
    }
}
