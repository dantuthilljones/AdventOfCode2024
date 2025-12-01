package me.detj.aoc_2024;

import me.detj.utils.Inputs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Solver11 {
    public static void main(String[] args) {
        var stones = Inputs.parseIntList("2024/input_11.txt");

        long numStones = countStonesAfterBlinking(stones, 25);
        System.out.printf("Solution Part 1: %d\n", numStones);

        long numStones75 = countStonesAfterBlinking(stones, 75);
        System.out.printf("Solution Part 2: %d\n", numStones75);
    }

    private static long countStonesAfterBlinking(List<Long> stones, int times) {
        Map<Long, Map<Long, Long>> stoneNumToSplit = new HashMap<>();
        long totalStones = 0;

        for (long stone : stones) {
            totalStones += calculateStoneNumberOfSplits(stoneNumToSplit, stone, times);
        }

        return totalStones;
    }

    private static long calculateStoneNumberOfSplits(Map<Long, Map<Long, Long>> stoneNumToSplit, long num, long iterations) {
        // last iteration
        if (iterations == 1) {
            if (num == 0) {
                return 1;
            }
            String string = String.valueOf(num);
            if (string.length() % 2 == 0) {
                return 2;
            } else {
                return 1;
            }
        }

        Map<Long, Long> stonesAfterIterations = stoneNumToSplit.computeIfAbsent(num, k -> new HashMap<>());
        Long stones = stonesAfterIterations.get(iterations);

        if (stones != null) {
            return stones;
        }

        if (num == 0) {
            stones = calculateStoneNumberOfSplits(stoneNumToSplit, 1, iterations - 1);
            stonesAfterIterations.put(iterations, stones);
            return stones;
        }

        String string = String.valueOf(num);
        if (string.length() % 2 == 0) { // if even number of digits, split in two
            int half = string.length() / 2;
            long firstHalf = Long.parseLong(string.substring(0, half));
            long secondHalf = Long.parseLong(string.substring(half));
            stones = calculateStoneNumberOfSplits(stoneNumToSplit, firstHalf, iterations - 1) + calculateStoneNumberOfSplits(stoneNumToSplit, secondHalf, iterations - 1);
        } else {  // else times by 2024
            stones = calculateStoneNumberOfSplits(stoneNumToSplit, num * 2024, iterations - 1);
        }
        stonesAfterIterations.put(iterations, stones);
        return stones;
    }
}
