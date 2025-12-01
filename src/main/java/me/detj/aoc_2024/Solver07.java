package me.detj.aoc_2024;

import me.detj.utils.DTPair;
import me.detj.utils.Inputs;

import java.util.List;

public class Solver07 {
    public static void main(String[] args) {
        var input = Inputs.parseLabelledList("2024/input_07.txt");

        long calibration = canEvaluateToLabel(input);
        System.out.printf("Solution Part 1: %d\n", calibration);

        long calibrationWithConcat = canEvaluateToLabelWithConcatenation(input);
        System.out.printf("Solution Part 2: %d\n", calibrationWithConcat);
    }

    private static long canEvaluateToLabel(List<DTPair<Long, List<Long>>> labelledLists) {
        long sum = 0;
        for (var labelledList : labelledLists) {
            if (canEvaluateToLabel(labelledList)) {
                sum += labelledList.getLeft();
            }
        }
        return sum;
    }

    private static boolean canEvaluateToLabel(DTPair<Long, List<Long>> labelledList) {
        long label = labelledList.getLeft();
        List<Long> values = labelledList.getRight();
        return canEvaluateToLabel(label, values, values.get(0), 1);
    }

    private static boolean canEvaluateToLabel(long label, List<Long> values, long current, int index) {
        // terminating condition
        if (index == values.size()) {
            return label == current;
        }

        return canEvaluateToLabel(label, values, current + values.get(index), index + 1)
                || canEvaluateToLabel(label, values, current * values.get(index), index + 1);

    }

    private static long canEvaluateToLabelWithConcatenation(List<DTPair<Long, List<Long>>> labelledLists) {
        long sum = 0;
        for (var labelledList : labelledLists) {
            if (canEvaluateToLabelWithConcatenation(labelledList)) {
                sum += labelledList.getLeft();
            }
        }
        return sum;
    }

    private static boolean canEvaluateToLabelWithConcatenation(DTPair<Long, List<Long>> labelledList) {
        long label = labelledList.getLeft();
        List<Long> values = labelledList.getRight();

        return canEvaluateToLabelWithConcatenation(label, values, values.get(0), 1);
    }

    private static boolean canEvaluateToLabelWithConcatenation(long label, List<Long> values, long current, int index) {
        // terminating condition
        if (index == values.size()) {
            return label == current;
        }

        return canEvaluateToLabelWithConcatenation(label, values, current + values.get(index), index + 1)
                || canEvaluateToLabelWithConcatenation(label, values, current * values.get(index), index + 1)
                || canEvaluateToLabelWithConcatenation(label, values, concatenate(current, values.get(index)), index + 1);
    }

    private static long concatenate(long current, long next) {
        return Long.parseLong(String.valueOf(current) + next);
    }
}
