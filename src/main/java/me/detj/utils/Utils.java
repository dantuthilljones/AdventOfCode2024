package me.detj.utils;

import java.util.*;
import java.util.function.Function;

public class Utils {

    public static Map<Integer, Integer> countFrequencies(Collection<Integer> input) {
        Map<Integer, Integer> counts = new HashMap<>();
        for (Integer num : input) {
            counts.merge(num, 1, Integer::sum);
        }
        return counts;
    }

    public static long calculateSimilarity(List<Integer> left, List<Integer> right) {
        Collections.sort(left);
        Collections.sort(right);

        Map<Integer, Integer> frequencies = Utils.countFrequencies(right);

        long similarity = 0;
        for (int num : left) {
            similarity += (long) num * frequencies.getOrDefault(num, 0);
        }

        return similarity;
    }

    public static int calculateDistance(List<Integer> left, List<Integer> right) {
        Collections.sort(left);
        Collections.sort(right);

        int totalDistance = 0;

        for (int i = 0; i < left.size(); i++) {
            totalDistance += Math.abs(left.get(i) - right.get(i));
        }

        return totalDistance;
    }

    public static int countSafeReports(List<List<Integer>> reports, boolean allowRemoval) {
        int safeReports = 0;
        for (var report : reports) {
            if (isReportSafe(report, 1, 3, allowRemoval)) {
                safeReports++;
            }
        }
        return safeReports;
    }

    public static boolean isReportSafe(List<Integer> report, int minDelta, int maxDelta, boolean allowRemoval) {
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

    public static int searchForX(List<List<Character>> wordSearch, List<Character> word) {
        int found = 0;
        for (int y = 0; y < wordSearch.size(); y++) {
            for (int x = 0; x < wordSearch.get(0).size(); x++) {
                Point point = Point.of(x, y);
                // Match diagonal direction '\'
                boolean downRight = findWord(wordSearch, word, point, Point::moveDownRight);
                boolean upLeft = findWord(wordSearch, word, point.moveDownRight(word.size() - 1), Point::moveUpLeft);

                // Match diagonal direction '/'
                boolean upRight = findWord(wordSearch, word, point.moveDown(word.size() - 1), Point::moveUpRight);
                boolean downLeft = findWord(wordSearch, word, point.moveRight(word.size() - 1), Point::moveDownLeft);

                if ((downRight || upLeft) && (upRight || downLeft)) {
                    found++;
                }
            }
        }
        return found;
    }

    public static int searchForWord(List<List<Character>> wordSearch, List<Character> word) {
        int found = 0;
        for (int y = 0; y < wordSearch.size(); y++) {
            for (int x = 0; x < wordSearch.get(0).size(); x++) {
                for (Function<Point, Point> directions : allDirections()) {
                    if (findWord(wordSearch, word, Point.of(x, y), directions)) {
                        found++;
                    }
                }
            }
        }
        return found;
    }

    public static List<Function<Point, Point>> allDirections() {
        return List.of(
                Point::moveRight,
                Point::moveDownRight,
                Point::moveDown,
                Point::moveDownLeft,
                Point::moveLeft,
                Point::moveUpLeft,
                Point::moveUp,
                Point::moveUpRight
        );
    }

    private static boolean findWord(List<List<Character>> wordSearch, List<Character> word, Point point, Function<Point, Point> pointStepper) {
        for (Character character : word) {
            if (!safeCheckEquals(wordSearch, character, point)) {
                return false;
            }
            point = pointStepper.apply(point);
        }
        return true;
    }

    private static boolean safeCheckEquals(List<List<Character>> wordSearch, Character c, Point point) {
        if (point.getY() < 0 || point.getY() >= wordSearch.size()) {
            return false;
        }
        if (point.getX() < 0 || point.getX() >= wordSearch.get(0).size()) {
            return false;
        }
        return wordSearch.get(point.getY()).get(point.getX()).equals(c);
    }
}
