package me.detj.utils;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.OrderedMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;

import java.util.*;
import java.util.function.Function;

import static java.util.Collections.swap;

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

    public static int countMiddleNumbersOfValidUpdates(List<List<Integer>> pages, List<Point> rules) {
        int sum = 0;
        for (List<Integer> page : pages) {
            if (pageInRightOrder(page, rules)) {
                sum += page.get(page.size() / 2);
            }
        }
        return sum;
    }

    public static int countMiddleNumbersOfFixedUpdates(List<List<Integer>> pages, List<Point> rules) {
        int sum = 0;
        for (List<Integer> page : pages) {
            if (pageInRightOrder(page, rules)) {
                continue;
            }

            page = fixPage(page, rules);
            sum += page.get(page.size() / 2);
        }
        return sum;
    }

    private static List<Integer> fixPage(List<Integer> page, List<Point> rules) {
        page = new ArrayList<>(page);
        MultiValuedMap<Integer, Integer> rulesMap = generateRulesMap(rules);

        while (!pageInRightOrder(page, rules)) {
            for (int i = 0; i < page.size(); i++) {
                swapIfRulesSaySo(page, i, rulesMap);
            }
        }
        return page;
    }

    private static void swapIfRulesSaySo(List<Integer> pages, int i, MultiValuedMap<Integer, Integer> rulesMap) {
        int page = pages.get(i);
        Collection<Integer> mustBeAfter = rulesMap.get(page);
        if (mustBeAfter.isEmpty()) {
            return;
        }

        for (int j = 0; j < pages.size(); j++) {
            int otherPage = pages.get(j);
            if (mustBeAfter.contains(otherPage)) {
                swap(pages, i, j);
                return;// need to return because the page has changed index
            }
        }


    }

    private static MultiValuedMap<Integer, Integer> generateRulesMap(List<Point> rules) {
        MultiValuedMap<Integer, Integer> rulesMap = new HashSetValuedHashMap<>();
        for (Point rule : rules) {
            rulesMap.put(rule.getX(), rule.getY());
        }
        return rulesMap;
    }

    private static boolean pageInRightOrder(List<Integer> page, List<Point> rules) {
        for (Point rule : rules) {
            if (ruleBroken(page, rule)) {
                return false;
            }
        }
        return true;
    }

    private static boolean ruleBroken(List<Integer> pages, Point rule) {
        boolean seenY = false;

        for (Integer page : pages) {
            if (page.equals(rule.getY())) {
                seenY = true;
            } else if (seenY && page.equals(rule.getX())) {
                return true;
            }
        }
        return false;
    }

    public static int countGuardPositions(Grid<Character> map) {
        Point guardPosition = map.findFirst('^');

        int guardMove = 0;
        List<Function<Point, Point>> guardMoves = List.of(
                Point::moveUp,
                Point::moveRight,
                Point::moveDown,
                Point::moveLeft
        );

        map.set(guardPosition, 'X');

        while (map.inBounds(guardPosition)) {
            Function<Point, Point> move = guardMoves.get(guardMove);
            Point nextPosition = move.apply(guardPosition);

            //If we hit an obstacle, change the guard direction
            if (map.pointEquals(nextPosition, '#')) {
                guardMove = (guardMove + 1) % 4;
            } else {
                guardPosition = nextPosition;
            }
            map.set(guardPosition, 'X');
        }

        // Now guard is out of bounds
        return map.count('X');
    }

    public static int countObstacleLoops(Grid<Character> original) {
        Grid<Character> map = original.shallowCopy();
        Point guardPosition = map.findFirst('^');

        int loops = 0;

        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                if (guardPosition.equals(new Point(x, y))) {
                    continue;
                }
                map.set(x, y, '#');
                if (guardLoops(map)) {
                    loops++;
                }
                map.set(x, y, original.get(x, y));
            }
        }

        return loops;
    }


    public static boolean guardLoops(Grid<Character> map) {
        Point guardPosition = map.findFirst('^');

        int guardDirection = 0;
        List<Function<Point, Point>> guardMoves = List.of(
                Point::moveUp,
                Point::moveRight,
                Point::moveDown,
                Point::moveLeft
        );

        Set<DTPair<Point, Integer>> history = new HashSet<>();
        while (map.inBounds(guardPosition)) {
            DTPair<Point, Integer> positionAndDirection = new DTPair<>(guardPosition, guardDirection);
            if (!history.add(positionAndDirection)) {
                return true;
            }

            Function<Point, Point> move = guardMoves.get(guardDirection);
            Point nextPosition = move.apply(guardPosition);
            //If we hit an obstacle, change the guard direction
            if (map.pointEquals(nextPosition, '#')) {
                guardDirection = (guardDirection + 1) % 4;
                continue;
            }

            // Move to the next position
            guardPosition = nextPosition;
        }

        // If we ended up out of bounds then there was no loop
        return false;
    }

    public static long canEvaluateToLabel(List<DTPair<Long, List<Long>>> labelledLists) {
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

    public static long canEvaluateToLabelWithConcatenation(List<DTPair<Long, List<Long>>> labelledLists) {
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

    public static int countAntiNodes(Grid<Character> map) {
        Set<Point> antiNodes = new HashSet<>();

        MultiValuedMap<Character, Point> freqToAntenna = map.pointsByValue();

        for (Character freq : freqToAntenna.keySet()) {
            if (freq.equals('.')) {
                continue;
            }

            List<Point> antennas = (List<Point>) freqToAntenna.get(freq);
            for (int firstIndex = 0; firstIndex < antennas.size() - 1; firstIndex++) {
                for (int secondIndex = firstIndex + 1; secondIndex < antennas.size(); secondIndex++) {
                    Point first = antennas.get(firstIndex);
                    Point second = antennas.get(secondIndex);

                    Point difference = second.minus(first);

                    Point antiNode1 = second.plus(difference);
                    if (map.inBounds(antiNode1)) {
                        antiNodes.add(antiNode1);
                    }

                    Point antiNode2 = first.minus(difference);
                    if (map.inBounds(antiNode2)) {
                        antiNodes.add(antiNode2);
                    }
                }
            }
        }
        return antiNodes.size();
    }

    public static int countAntiNodesWithHarmonics(Grid<Character> map) {
        Set<Point> antiNodes = new HashSet<>();

        MultiValuedMap<Character, Point> freqToAntenna = map.pointsByValue();

        for (Character freq : freqToAntenna.keySet()) {
            if (freq.equals('.')) {
                continue;
            }

            List<Point> antennas = (List<Point>) freqToAntenna.get(freq);
            for (int firstIndex = 0; firstIndex < antennas.size() - 1; firstIndex++) {
                for (int secondIndex = firstIndex + 1; secondIndex < antennas.size(); secondIndex++) {
                    Point first = antennas.get(firstIndex);
                    Point second = antennas.get(secondIndex);

                    Point difference = second.minus(first);

                    Point antiNode1 = second;
                    while(map.inBounds(antiNode1)) {
                        antiNodes.add(antiNode1);
                        antiNode1 = antiNode1.plus(difference);
                    }
                    Point antiNode2 = first;
                    while(map.inBounds(antiNode2)) {
                        antiNodes.add(antiNode2);
                        antiNode2 = antiNode2.minus(difference);
                    }
                }
            }
        }
        return antiNodes.size();
    }
}
