package me.detj.utils;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
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
                for (Function<Point, Point> directions : Point.ALL_DIRECTIONS) {
                    if (findWord(wordSearch, word, Point.of(x, y), directions)) {
                        found++;
                    }
                }
            }
        }
        return found;
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

        // Important that the directions are in this order: UP, RIGHT, DOWN, LEFT
        List<Function<Point, Point>> guardMoves = Point.BASIC_DIRECTIONS;

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
                    while (map.inBounds(antiNode1)) {
                        antiNodes.add(antiNode1);
                        antiNode1 = antiNode1.plus(difference);
                    }
                    Point antiNode2 = first;
                    while (map.inBounds(antiNode2)) {
                        antiNodes.add(antiNode2);
                        antiNode2 = antiNode2.minus(difference);
                    }
                }
            }
        }
        return antiNodes.size();
    }

    public static long compactifyAndChecksum(List<Integer> diskMap) {
        List<Integer> disk = calculateDiskLayout(diskMap);
        disk = compactifyDisk(disk);
        return calculateChecksum(disk);
    }

    private static List<Integer> calculateDiskLayout(List<Integer> diskMap) {
        List<Integer> disk = new ArrayList<>();
        for (int i = 0; i < diskMap.size(); i++) {

            // if i is even, it is a file, if i is odd, it is free space
            int value = i % 2 == 0 ? i / 2 : -1;

            for (int j = 0; j < diskMap.get(i); j++) {
                disk.add(value);
            }
        }
        return disk;
    }

    private static List<Integer> compactifyDisk(List<Integer> disk) {
        disk = new ArrayList<>(disk);
        int left = 0;
        int right = disk.size() - 1;

        while (left < right) {
            if (disk.get(right) != -1) {
                while (disk.get(left) != -1 && left < right) {
                    left++;
                }
                swap(disk, left, right);
            }
            right--;
        }
        return disk;
    }

    private static long calculateChecksum(List<Integer> disk) {
        long sum = 0;
        for (int i = 0; i < disk.size(); i++) {
            long value = disk.get(i);
            sum += i * (value != -1 ? value : 0);
        }
        return sum;
    }

    public static long compactifyWithoutFragmentationAndChecksum(List<Integer> diskMap) {
        List<Integer> disk = calculateDiskLayout(diskMap);
        disk = compactifyDiskWithoutFragmentation(disk);
        return calculateChecksum(disk);
    }


    private static List<Integer> compactifyDiskWithoutFragmentation(List<Integer> disk) {
        disk = new ArrayList<>(disk);

        int i = disk.size() - 1;
        while (i >= 0) {
            if (disk.get(i) == -1) {
                i--;
                continue;
            }

            int startOfBlock = getStartOfBlock(disk, i);
            int sizeOfBlock = i - startOfBlock + 1;

            // find block that fits
            int newBlockStartIndex = findFreeBlock(disk, startOfBlock, sizeOfBlock);
            if (startOfBlock != -1 && newBlockStartIndex != -1) {
                swapBlock(disk, startOfBlock, newBlockStartIndex, sizeOfBlock);
            }
            i = startOfBlock - 1;
        }
        return disk;
    }

    private static void swapBlock(List<Integer> disk, int startOfBlock, int newBlockStartIndex, int sizeOfBlock) {
        for (int i = 0; i < sizeOfBlock; i++) {
            swap(disk, startOfBlock + i, newBlockStartIndex + i);
        }
    }

    private static int findFreeBlock(List<Integer> disk, int maxIndex, int sizeOfBlock) {
        for (int i = 0; i < maxIndex; i++) {
            if (disk.get(i) != -1) {
                continue;
            }
            if (isBlockFree(disk, i, sizeOfBlock)) {
                return i;
            } else {
                i++;
            }
        }
        return -1;
    }

    private static boolean isBlockFree(List<Integer> disk, int blockStartIndex, int sizeOfBlock) {
        for (int j = blockStartIndex; j < blockStartIndex + sizeOfBlock; j++) {
            if (disk.get(j) != -1) {
                return false;
            }
        }
        return true;
    }

    private static int getStartOfBlock(List<Integer> disk, int i) {
        int j;
        for (j = i; j >= 0; j--) {
            if (j == 0 || !disk.get(j - 1).equals(disk.get(i))) {
                break;
            }
        }
        return j;
    }

    public static int scoreTrails(Grid<Integer> map) {
        List<Point> startPoints = map.findAll(0);

        int score = 0;
        for (Point start : startPoints) {
            Set<Point> peaks = new HashSet<>();
            reachablePeaks(map, start, peaks);
            score += peaks.size();
        }
        return score;
    }

    public static int scoreTrailsDistinct(Grid<Integer> map) {
        List<Point> startPoints = map.findAll(0);

        int score = 0;
        for (Point start : startPoints) {
            List<Point> peaks = new ArrayList<>();
            reachablePeaks(map, start, peaks);
            score += peaks.size();
        }
        return score;
    }

    private static void reachablePeaks(Grid<Integer> map, Point current, Collection<Point> peaks) {
        Integer height = map.get(current);

        if (height == 9) {
            peaks.add(current);
            return;
        }

        for (Function<Point, Point> direction : Point.BASIC_DIRECTIONS) {
            Point next = direction.apply(current);
            if (map.inBounds(next) && map.pointEquals(next, height + 1)) {
                reachablePeaks(map, next, peaks);
            }
        }
    }

    public static int countStonesAfterBlinking(List<Long> stones, int times) {
        LinkedList<Long> linkedStones = new LinkedList<>(stones);
        for (int i = 0; i < times; i++) {
            blinkStones(linkedStones);
        }
        return linkedStones.size();
    }

    public static void blinkStones(LinkedList<Long> stones) {
        ListIterator<Long> iterator = stones.listIterator();

        while (iterator.hasNext()) {
            long num = iterator.next();

            // if 0, set to 1
            if (num == 0) {
                iterator.set(1L);
                continue;
            }

            // if even number of digits, split in two
            String string = String.valueOf(num);
            if (string.length() % 2 == 0) {
                int half = string.length() / 2;
                long firstHalf = Long.parseLong(string.substring(0, half));
                long secondHalf = Long.parseLong(string.substring(half));
                iterator.set(firstHalf);
                iterator.add(secondHalf);
                continue;
            }

            iterator.set(num * 2024);
        }
    }

    public static long countStonesAfterBlinkingDP(List<Long> stones, int times) {
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
