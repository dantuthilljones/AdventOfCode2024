package me.detj.aoc_2025;

import me.detj.utils.DTPair;
import me.detj.utils.Grid;
import me.detj.utils.Inputs;
import me.detj.utils.Pair;
import me.detj.utils.Point;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import static me.detj.utils.Point.ALL_DIRECTIONS;

public class Solver05 {

    public static void main(String[] args) throws IOException {
        var input = Inputs.parseIngredients("2025/input_05.txt");
        System.out.printf("Solution 1: %d\n", countFresh(input));
        System.out.printf("Solution 2: %d\n", countAllFresh(input));
    }

    private static int countFresh(DTPair<List<Pair<Long>>, List<Long>> input) {
        List<Pair<Long>> freshRanges = input.getLeft();
        int count = 0;

        for (long ingredient : input.getRight()) {
            for (var range : freshRanges) {
                if (range.getLeft() <= ingredient && ingredient <= range.getRight()) {
                    count++;
                    break;
                }
            }
        }
        return count;
    }

    private static long countAllFresh(DTPair<List<Pair<Long>>, List<Long>> input) {
        List<Pair<Long>> freshRanges = input.getLeft();

        NavigableMap<Long, Long> combinedRanges = new TreeMap<>();

        for(Pair<Long> range : freshRanges) {
            long start = range.getLeft();
            long end = range.getRight();

            Map.Entry<Long, Long> lower = combinedRanges.floorEntry(start);
            if (lower != null && lower.getValue() >= start) {
                start = lower.getKey();
                end = Math.max(end, lower.getValue());
                combinedRanges.remove(lower.getKey());
            }
            Map.Entry<Long, Long> higher = combinedRanges.ceilingEntry(start);
            while (higher != null && higher.getKey() <= end) {
                end = Math.max(end, higher.getValue());
                combinedRanges.remove(higher.getKey());
                higher = combinedRanges.ceilingEntry(start);
            }
            combinedRanges.put(start, end);
        }

        long count = 0;
        for(Map.Entry<Long, Long> entry : combinedRanges.entrySet()) {
            count += (entry.getValue() - entry.getKey() + 1);
        }
        return count;
    }


}
