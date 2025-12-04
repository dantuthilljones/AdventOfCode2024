package me.detj.aoc_2024;

import me.detj.utils.Inputs;

import java.util.ArrayList;
import java.util.List;

public class Solver02 {

    public static void main(String[] args) {
        var reports = Inputs.parseListOfList("2024/input_02.txt");
        int safeReports = countSafeReports(reports, false);
        System.out.printf("Safe Reports: %d\n", safeReports);

        int safeReportsWithDampener = countSafeReports(reports, true);
        System.out.printf("Safe Reports With Dampener: %d\n", safeReportsWithDampener);
    }

    private static int countSafeReports(List<List<Integer>> reports, boolean allowRemoval) {
        int safeReports = 0;
        for (var report : reports) {
            if (isReportSafe(report, 1, 3, allowRemoval)) {
                safeReports++;
            }
        }
        return safeReports;
    }

    private static boolean isReportSafe(List<Integer> report, int minDelta, int maxDelta, boolean allowRemoval) {
        if (allowRemoval && isReportSafe(removeElement(report, 0), minDelta, maxDelta, false)) {
            return true;
        }

        if (report.get(0).equals(report.get(1))) {
            // in case first 2 numbers are the same
            return allowRemoval && isReportSafe(removeElement(report, 1), minDelta, maxDelta, false);
        }
        boolean increasing = report.get(0) < report.get(1);

        for (int i = 1; i < report.size(); i++) {
            int previous = report.get(i - 1);
            int current = report.get(i);

            if (isLevelUnsafe(increasing, previous, current, minDelta, maxDelta)) {
                // Check if removing this or the previous element makes it pass
                return allowRemoval && (isReportSafe(removeElement(report, i), minDelta, maxDelta, false)
                        || isReportSafe(removeElement(report, i - 1), minDelta, maxDelta, false));
            }
        }
        return true;
    }

    private static boolean isLevelUnsafe(boolean increasing, int previous, int current, int minDelta, int maxDelta) {
        return !isLevelSafe(increasing, previous, current, minDelta, maxDelta);
    }

    private static boolean isLevelSafe(boolean increasing, int previous, int current, int minDelta, int maxDelta) {
        int delta = current - previous;
        // Check direction didn't change
        if ((increasing && delta <= 0) || (!increasing && delta >= 0)) {
            return false;
        }

        // Check change isn't too large
        delta = Math.abs(delta);
        return delta >= minDelta && delta <= maxDelta;
    }

    // Could make a list view here to make it O(1) time and memory complexity instead of O(n)
    private static List<Integer> removeElement(List<Integer> report, int index) {
        List<Integer> result = new ArrayList<>(report);
        result.remove(index);
        return result;
    }
}
