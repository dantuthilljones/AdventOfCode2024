package me.detj.utils;

import lombok.NonNull;
import lombok.SneakyThrows;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;

import javax.swing.text.Position;
import java.io.BufferedReader;
import java.io.InputStreamReader;
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

    public static int priceFence(Grid<Character> garden) {
        int price = 0;
        Set<Point> visited = new HashSet<>();
        for (int y = 0; y < garden.getHeight(); y++) {
            for (int x = 0; x < garden.getWidth(); x++) {
                Point current = Point.of(x, y);
                Pair<Integer> areaAndPerimeter = calcAreaAndPerimeter(garden, visited, new HashSet<>(), current, current);
                price += areaAndPerimeter.getLeft() * areaAndPerimeter.getRight();
            }
        }
        return price;
    }

    private static Pair<Integer> calcAreaAndPerimeter(Grid<Character> garden, Set<Point> visitedGlobal, Set<Point> visitedGarden, Point start, Point current) {
        if (garden.pointsAreEqual(start, current)) {
            boolean visited = !visitedGlobal.add(current) | !visitedGarden.add(current);
            if (visited) {
                return new Pair<>(0, 0);
            }
        } else {
            return new Pair<>(0, 0);
        }

        Pair<Integer> areaAndPerimeter = new Pair<>(1, 0);
        for (Function<Point, Point> direction : Point.BASIC_DIRECTIONS) {
            Point neighbor = direction.apply(current);

            // Skip if we've already visited this neighbor in this garden
            if (visitedGarden.contains(neighbor)) {
                continue;
            }

            Pair<Integer> neighborAreaAndPerimeter = calcAreaAndPerimeter(garden, visitedGlobal, visitedGarden, start, neighbor);

            if (neighborAreaAndPerimeter.getLeft() == 0 && neighborAreaAndPerimeter.getRight() == 0) {
                // if neighbor is not part of the garden, add a fence
                areaAndPerimeter = add(areaAndPerimeter, new Pair<>(0, 1));
            } else {
                // if neighbor is part of the garden, add their area and perimeter
                areaAndPerimeter = add(areaAndPerimeter, neighborAreaAndPerimeter);
            }
        }

        return areaAndPerimeter;
    }

    private static Pair<Integer> add(Pair<Integer> p1, Pair<Integer> p2) {
        return new Pair<>(p1.getLeft() + p2.getLeft(), p1.getRight() + p2.getRight());
    }

    public static int priceFenceWithDiscount(Grid<Character> garden) {
        int price = 0;
        Set<Point> visited = new HashSet<>();
        for (int y = 0; y < garden.getHeight(); y++) {
            for (int x = 0; x < garden.getWidth(); x++) {
                Point current = Point.of(x, y);

                Map<Point, boolean[]> region = new HashMap<>();
                calcAreaAndPerimeterDiscount(garden, visited, current, current, region);

                int perimeter = calculateDiscountedPerimeter(region);
                int area = region.size();
                price += area * perimeter;
            }
        }
        return price;
    }

    private static final int UP = 0, RIGHT = 1, DOWN = 2, LEFT = 3;
    private static final List<Integer> DIRECTIONS = List.of(UP, RIGHT, DOWN, LEFT);

    private static void calcAreaAndPerimeterDiscount(Grid<Character> garden, Set<Point> visitedGlobal, Point start, Point current, Map<Point, boolean[]> region) {
        if (garden.pointsAreEqual(start, current)) {
            boolean visited = !visitedGlobal.add(current) | region.containsKey(current);
            if (visited) {
                return;
            }
        } else {
            return;
        }

        boolean[] fences = {true, true, true, true};
        for (Integer direction : DIRECTIONS) {
            Function<Point, Point> directionFn = Point.BASIC_DIRECTIONS.get(direction);
            Point neighbor = directionFn.apply(current);

            calcAreaAndPerimeterDiscount(garden, visitedGlobal, start, neighbor, region);

            if (garden.pointsAreEqual(start, neighbor)) {
                fences[direction] = false;
            }
        }

        region.put(current, fences);
    }


    private static int calculateDiscountedPerimeter(Map<Point, boolean[]> region) {
        Set<DTPair<Point, Integer>> visited = new HashSet<>();
        List<List<DTPair<Point, Integer>>> joinedFences = new ArrayList<>();

        region.forEach((point, fences) -> {
            for (Integer direction : DIRECTIONS) {
                if (!fences[direction]) continue;

                if (!visited.add(new DTPair<>(point, direction))) {
                    continue;
                }

                List<DTPair<Point, Integer>> joinedFence = new ArrayList<>();
                joinedFence.add(new DTPair<>(point, direction));

                // check along the fence in the direction and the opposite direction
                for (int checkDirection : new int[]{(direction + 1) % 4, (direction + 3) % 4}) {
                    Point current = point;
                    while (true) {
                        current = Point.BASIC_DIRECTIONS.get(checkDirection).apply(current);
                        boolean[] currentFences = region.get(current);
                        if (currentFences == null || !currentFences[direction]) {
                            break;
                        }
                        DTPair<Point, Integer> fence = new DTPair<>(current, direction);
                        visited.add(fence);
                        joinedFence.add(fence);
                    }
                }
                joinedFences.add(joinedFence);
            }
        });

        return joinedFences.size();
    }

    public static int fewestTokens(List<ClawMachine> clawMachines) {
        int sum = 0;
        for (ClawMachine machine : clawMachines) {
            int tokens = tokensForPrize(machine);
            if (tokens != -1) {
                sum += tokens;
            }
        }
        return sum;
    }

    public static int tokensForPrize(ClawMachine machine) {
        //Grid<Integer> dp = Grid.of(machine.getPrize().getX(), machine.getPrize().getY(), -1);

        int min = Integer.MAX_VALUE;
        for (int a = 0; a < 100; a++) {
            for (int b = 0; b < 100; b++) {
                Point position = machine.getA().times(a).plus(machine.getB().times(b));
                int tokens = a * 3 + b;

                if (position.equals(machine.getPrize())) {
                    System.out.printf("%s: %s\n", machine.getPrize(), position);
                }

                if (position.equals(machine.getPrize()) && min > tokens) {
                    min = tokens;
                }
            }
        }

        if (min == Integer.MAX_VALUE) {
            return -1;
        }
        return min;
    }


    public static long fewestTokensBigger(List<ClawMachine> clawMachines) {
        long sum = 0;
        for (ClawMachine machine : clawMachines) {
            long tokens = tokensForPrizeBigger(machine);
            if (tokens != -1) {
                sum += tokens;
            }
        }
        return sum;
    }

    public static long tokensForPrizeBigger(ClawMachine machine) {
        LClawMachine lMachine = fixRounding(machine);

        double[] prize = toVector(lMachine.getPrize());
        double[][] matrix = toMatrix(lMachine.getA(), lMachine.getB());
        double[][] inverse = getInverse(matrix);
        double[] ans = multiply(prize, inverse);

        long as = Math.round(ans[0]);
        long bs = Math.round(ans[1]);

        if (lMachine.getA().times(as).plus(lMachine.getB().times(bs)).equals(lMachine.getPrize())) {
            return as * 3 + bs;
        }

        return -1;
    }

    private static final long CORRECTION = 10000000000000L;
    //private static final long CORRECTION = 0;

    private static LClawMachine fixRounding(ClawMachine machine) {
        LPoint a = LPoint.from(machine.getA());
        LPoint b = LPoint.from(machine.getB());
        LPoint prize = new LPoint(CORRECTION + machine.getPrize().getX(), CORRECTION + machine.getPrize().getY());
        return new LClawMachine(a, b, prize);
    }

    private static double[] toVector(LPoint p) {
        return new double[]{p.getX(), p.getY()};
    }

    private static double[][] toMatrix(LPoint a, LPoint b) {
        return new double[][]{
                toVector(a),
                toVector(b),
        };
    }

    private static double[][] getInverse(double[][] matrix) {
        double determinant = matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];
        return new double[][]{
                {(1.0 / determinant) * matrix[1][1], (1.0 / determinant) * -matrix[0][1]},
                {(1.0 / determinant) * -matrix[1][0], (1.0 / determinant) * matrix[0][0]}
        };
    }

    private static double[] multiply(double[] vector, double[][] matrix) {
        return new double[]{
                vector[0] * matrix[0][0] + vector[1] * matrix[1][0],
                vector[0] * matrix[0][1] + vector[1] * matrix[1][1]
        };
    }

    public static int calculateSafetyFactor(List<Pair<Point>> robots, Point gridSize, int seconds) {
        List<Point> robotPositions = robots.stream()
                .map(robot -> calculatePosition(robot, gridSize, seconds))
                .toList();

        int[][] quadrantCounts = {
                {0, 0},
                {0, 0}
        };

        for (Point position : robotPositions) {
            int midX = gridSize.getX() / 2;
            int midY = gridSize.getY() / 2;

            // don't count the robot if it's on the mid line
            if ((position.getX() == midX && gridSize.getX() % 2 == 1)
                    || (position.getY() == midY && gridSize.getY() % 2 == 1)) {
                continue;
            }

            int x = (position.getX() - 1) / (gridSize.getX() / 2);
            int y = (position.getY() - 1) / (gridSize.getY() / 2);

            quadrantCounts[y][x]++;
        }

        return quadrantCounts[0][0] * quadrantCounts[0][1] * quadrantCounts[1][0] * quadrantCounts[1][1];
    }

    private static Point calculatePosition(Pair<Point> robot, Point gridSize, int seconds) {
        Point startPosition = robot.getLeft();
        Point velocity = robot.getRight();

        Point totalMoved = velocity.times(seconds);
        Point finalPosition = startPosition.plus(totalMoved);

        finalPosition = new Point(
                finalPosition.getX() % gridSize.getX(),
                finalPosition.getY() % gridSize.getY()
        );

        finalPosition = new Point(
                finalPosition.getX() < 0 ? gridSize.getX() + finalPosition.getX() : finalPosition.getX(),
                finalPosition.getY() < 0 ? gridSize.getY() + finalPosition.getY() : finalPosition.getY()
        );

        return finalPosition;
    }

    @SneakyThrows
    public static void printRobots(List<Pair<Point>> robots, Point gridSize, int seconds) {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        for (int i = 0; i < seconds; i++) {

            Grid<Character> grid = Grid.of(gridSize.getX(), gridSize.getY(), '.');
            int finalI = i;
            robots.stream()
                    .map(robot -> calculatePosition(robot, gridSize, finalI))
                    .forEach(point -> grid.set(point, '#'));


            System.out.printf("Grid %d:\n", i);
            grid.print();
            System.out.printf("Grid %d:\n", i);
            System.out.println();
            reader.readLine();
        }
    }

    public static void printRobotAfter(List<Pair<Point>> robots, Point gridSize, int seconds) {
        Grid<Character> grid = Grid.of(gridSize.getX(), gridSize.getY(), '.');
        robots.stream()
                .map(robot -> calculatePosition(robot, gridSize, seconds))
                .forEach(point -> grid.set(point, '#'));

        System.out.printf("Grid %d:\n", seconds);
        grid.print();
        System.out.printf("Grid %d:\n", seconds);
    }

    public static long calculateLanternFishBoxPositions(DTPair<Grid<Character>, List<Character>> input, boolean print) {
        Grid<Character> warehouse = input.getLeft();
        List<Point> boxes = warehouse.findAll('O');
        long sum = 0;
        for (Point box : boxes) {
            int distanceFromTop = warehouse.getHeight() - box.getY() - 1;
            int distanceFromLeft = box.getX();
            sum += distanceFromTop * 100L + distanceFromLeft;
        }
        return sum;
    }

    private static Grid<Character> simulateLanternFishWarehouse(DTPair<Grid<Character>, List<Character>> input, boolean print) {
        Grid<Character> warehouse = input.getLeft().shallowCopy();
        List<Character> instructions = input.getRight();

        warehouse.print();
        Point robot = warehouse.findFirst('@');
        warehouse.set(robot, '.');

        for (Character instruction : instructions) {
            try {
                Function<Point, Point> direction = getDirection(instruction);
                Point nextPoint = direction.apply(robot);

                // Can't go through wall
                if (warehouse.pointEquals(nextPoint, '#')) {
                    continue;
                }

                if (warehouse.pointEquals(nextPoint, 'O')) {
                    // Check if we can push all the boxes
                    if (!pushBoxes(warehouse, nextPoint, direction)) {
                        nextPoint = robot;
                    }
                }
                robot = nextPoint;
            } finally {
                if (print) {
                    System.out.println("Move " + instruction);
                    printWarehouse(warehouse, robot);
                }
            }
        }

        warehouse.set(robot, '@');
        return warehouse;
    }

    private static void printWarehouse(Grid<Character> warehouse, Point robot) {
        warehouse = warehouse.shallowCopy();
        warehouse.set(robot, '@');
        warehouse.print();
        System.out.println();
    }

    private static Function<Point, Point> getDirection(Character c) {
        return switch (c) {
            case '^' -> Point::moveUp;
            case '>' -> Point::moveRight;
            case '<' -> Point::moveLeft;
            case 'v' -> Point::moveDown;
            default -> throw new IllegalArgumentException("" + c);
        };
    }

    private static boolean pushBoxes(Grid<Character> warehouse, Point start, Function<Point, Point> direction) {
        Point box = start;
        while (warehouse.inBounds(box)) {
            switch (warehouse.get(box)) {
                case '.' -> {
                    warehouse.set(box, 'O');
                    warehouse.set(start, '.');
                    return true;
                }
                case '#' -> {
                    return false;
                }
                case 'O' -> box = direction.apply(box);
                default -> throw new IllegalArgumentException("" + warehouse.get(box));
            }
        }
        return false;
    }

    public static long calculateLanternFishBoxPositionsDouble(DTPair<Grid<Character>, List<Character>> input, boolean print) {
        Grid<Character> warehouse = makeWarehouseThicc(input.getLeft());
        List<Point> boxes = warehouse.findAll('O');
        long sum = 0;
        for (Point box : boxes) {
            int distanceFromTop = warehouse.getHeight() - box.getY() - 1;
            int distanceFromLeft = box.getX();
            sum += distanceFromTop * 100L + distanceFromLeft;
        }
        return sum;
    }

    private static Grid<Character> makeWarehouseThicc(Grid<Character> warehouse) {
        Grid<Character> doubleWide = Grid.of(warehouse.getWidth() * 2, warehouse.getWidth(), '.');
        warehouse.forEachPoint((Point point, Character value) -> {
            Point p1 = new Point(point.getX() * 2, point.getY());
            Point p2 = new Point(point.getX() * 2 + 1, point.getY());
            switch (value) {
                case '#' -> {
                    doubleWide.set(p1, '#');
                    doubleWide.set(p2, '#');
                }
                case 'O' -> {
                    doubleWide.set(p1, 'O');
                    doubleWide.set(p2, 'O');
                }
                case '@' -> {
                    doubleWide.set(p1, '@');
                    doubleWide.set(p2, '.');
                }
            }
        });
        return doubleWide;
    }

    private static Grid<Character> simulateLanternFishWarehouseThicc(DTPair<Grid<Character>, List<Character>> input, boolean print) {
        Grid<Character> warehouse = input.getLeft().shallowCopy();
        List<Character> instructions = input.getRight();

        warehouse.print();
        Point robot = warehouse.findFirst('@');
        warehouse.set(robot, '.');

        for (Character instruction : instructions) {
            try {
                Function<Point, Point> direction = getDirection(instruction);
                Point nextPoint = direction.apply(robot);

                // Can't go through wall
                if (warehouse.pointEquals(nextPoint, '#')) {
                    continue;
                }

                if (warehouse.pointEquals(nextPoint, 'O')) {
                    // Check if we can push all the boxes
                    if (!pushBoxesThicc(warehouse, nextPoint, direction)) {
                        nextPoint = robot;
                    }
                }
                robot = nextPoint;
            } finally {
                if (print) {
                    System.out.println("Move " + instruction);
                    printWarehouse(warehouse, robot);
                }
            }
        }

        warehouse.set(robot, '@');
        return warehouse;
    }


    private static boolean pushBoxesThicc(Grid<Character> warehouse, Point start, char instruction) {
        Function<Point, Point> direction = getDirection(instruction);
        if (instruction == '<' || instruction == '>') {
            return pushBoxesThiccHorizontal(warehouse, start, direction);
        } else {
            return pushBoxesThiccVertical(warehouse, start, direction);
        }
        return false;
    }

    private static boolean pushBoxesThiccHorizontal(Grid<Character> warehouse, Point start, Function<Point, Point> direction) {
        Point point = start;
        while (warehouse.inBounds(point)) {
            if (warehouse.pointEquals(point, '.')) {
                shiftBoxesHorizontal(warehouse, start, point, direction);
                return true;
            }
            point = direction.apply(start);
        }
        return false;
    }

    private static void shiftBoxesHorizontal(Grid<Character> warehouse, Point start, Point end, Function<Point, Point> direction) {
        List<DTPair<Point, Character>> newValues = new ArrayList<>();
        Point current = start;
        while (!start.equals(end)) {
            Point next = direction.apply(current);
            newValues.add(new DTPair<>(next, warehouse.get(current)));
            current = next;
        }

        for (DTPair<Point, Character> newValue : newValues) {
            Point point = newValue.getLeft();
            Character value = newValue.getRight();
            warehouse.set(point, value);
        }
    }

    private static boolean pushBoxesThiccVertical(Grid<Character> warehouse, Point start, Function<Point, Point> direction) {
        Point box = start;
        while (warehouse.inBounds(box)) {
            switch (warehouse.get(box)) {
                case '.' -> {
                    warehouse.set(box, 'O');
                    warehouse.set(start, '.');
                    return true;
                }
                case '#' -> {
                    return false;
                }
                case 'O' -> box = direction.apply(box);
                default -> throw new IllegalArgumentException("" + warehouse.get(box));
            }
        }
        return false;
    }



    private static Set<Point> canPushBoxesThiccVertical(Grid<Character> warehouse, Point start, Function<Point, Point> direction) {
        Set<Point> boxesToBePushed = new HashSet<>();

        Point box = start;
        while (warehouse.inBounds(box)) {
            switch (warehouse.get(box)) {
                case '.' -> {
                    warehouse.set(box, 'O');
                    warehouse.set(start, '.');
                    return true;
                }
                case '#' -> {
                    return false;
                }
                case 'O' -> box = direction.apply(box);
                default -> throw new IllegalArgumentException("" + warehouse.get(box));
            }
        }
        return false;
    }

    private boolean calculateBoxesToPush(Grid<Character> warehouse, Point current, Function<Point, Point> direction, Set<Point> boxes) {
        
    }

}
