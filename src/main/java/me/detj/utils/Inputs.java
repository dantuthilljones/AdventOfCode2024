package me.detj.utils;

import lombok.SneakyThrows;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.apache.commons.lang3.StringUtils;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;

public class Inputs {
    @SneakyThrows()
    public static String readFile(String path) {
        return Files.readString(getPath(path));
    }

    @SneakyThrows()
    public static List<String> readLines(String path) {
        return Files.readAllLines(getPath(path));
    }

    private static Path getPath(String path) throws URISyntaxException {
        URL url = Inputs.class.getResource("/" + path);
        return Paths.get(url.toURI());
    }

    public static Pair<List<Integer>> parseListOfPairs(String file) {
        List<String> lines = readLines(file);

        List<Integer> l0 = new ArrayList<>(lines.size());
        List<Integer> l1 = new ArrayList<>(lines.size());

        for (String line : lines) {
            String[] row = StringUtils.split(line, "    ");
            l0.add(parseInt(row[0]));
            l1.add(parseInt(row[1]));
        }
        return new Pair<>(l0, l1);
    }

    public static List<List<Integer>> parseListOfList(String file) {
        List<String> lines = readLines(file);

        List<List<Integer>> results = new ArrayList<>(lines.size());
        for (String line : lines) {
            String[] row = StringUtils.split(line, " ");
            results.add(
                    Arrays.stream(row)
                            .map(Integer::parseInt)
                            .toList());
        }
        return results;
    }

    public static List<List<Character>> parseCharMatrix(String file) {
        List<String> lines = readLines(file);
        return parseCharMatrix(lines);
    }

    private static List<List<Character>> parseCharMatrix(List<String> lines) {
        List<List<Character>> results = new ArrayList<>(lines.size());
        for (int i = lines.size() - 1; i >= 0; i--) {
            String line = lines.get(i);
            List<Character> chars = line.chars().mapToObj(c -> (char) c).collect(Collectors.toList());
            results.add(chars);
        }
        return results;
    }

    public static DTPair<List<Point>, List<List<Integer>>> parsePageRules(String file) {
        boolean start = true;

        List<Point> rules = new ArrayList<>();
        List<List<Integer>> manuals = new ArrayList<>();

        for (String line : readLines(file)) {
            if (line.isEmpty()) {
                start = false;
                continue;
            }

            if (start) {
                String[] split = StringUtils.split(line, "|");
                rules.add(Point.of(parseInt(split[0]), parseInt(split[1])));
            } else {
                String[] split = StringUtils.split(line, ",");
                List<Integer> pages = Arrays.stream(split).map(Integer::parseInt).toList();
                manuals.add(pages);
            }
        }

        return new DTPair<>(rules, manuals);
    }


    public static Grid<Character> parseCharGridWithPadding(String file) {
        List<List<Character>> matrix = parseCharMatrix(file);

        int maxLength = matrix.stream()
                .mapToInt(row -> row.size())
                .max()
                .getAsInt();

        for (List<Character> row : matrix) {
            while (row.size() < maxLength) {
                row.add(' ');
            }
        }

        return new Grid<>(matrix);
    }

    public static Grid<Character> parseCharGrid(String file) {
        return new Grid<>(parseCharMatrix(file));
    }

    public static List<DTPair<Long, List<Long>>> parseLabelledList(String file) {
        List<String> lines = readLines(file);

        List<DTPair<Long, List<Long>>> result = new ArrayList<>();
        for (String line : lines) {
            String[] split = line.split(":");
            long label = parseLong(split[0]);

            List<Long> values = Arrays.stream(split[1].trim().split(" ")).map(Long::parseLong).toList();

            result.add(new DTPair<>(label, values));
        }

        return result;
    }

    public static List<Integer> parseDenseIntList(String file) {
        String content = readFile(file);
        List<Integer> list = new ArrayList<>(content.length());
        for (char c : content.toCharArray()) {
            list.add(parseInt("" + c));
        }
        return list;
    }

    public static Grid<Integer> parseDenseIntGrid(String file) {
        var chars = parseCharMatrix(file);
        List<List<Integer>> grid = chars.stream()
                .map(row -> row.stream().map(c -> parseInt(c.toString())).collect(Collectors.toList()))
                .collect(Collectors.toList());
        return new Grid<>(grid);
    }

    public static List<Long> parseIntList(String file) {
        String content = readFile(file);
        List<Long> list = new ArrayList<>();
        for (String num : content.split(" ")) {
            list.add(parseLong(num));
        }
        return list;
    }

    public static List<ClawMachine> parseClawMachines(String file) {
        List<ClawMachine> machines = new ArrayList<>();

        List<String> lines = readLines(file);
        for (int i = 0; i < lines.size(); i += 4) {
            Point a = parsePoint(lines.get(i));
            Point b = parsePoint(lines.get(i + 1));
            Point prize = parsePoint(lines.get(i + 2));

            machines.add(new ClawMachine(a, b, prize));
        }

        return machines;
    }


    /*
     * Matches lines like:
     *
     * Button A: X+30, Y+84
     * Button B: X+74, Y+60
     * Prize: X=2358, Y=2628
     * */
    private static final Pattern CLAW_REGEX = Pattern.compile(".*\\D(\\d+), Y\\D(\\d+)");

    private static Point parsePoint(String line) {
        Matcher match = CLAW_REGEX.matcher(line);
        if (!match.matches()) {
            throw new IllegalArgumentException("Invalid line: " + line);
        }
        return new Point(parseInt(match.group(1)), parseInt(match.group(2)));
    }

    public static List<Pair<Point>> parsePointPairs(String file) {
        List<String> lines = readLines(file);

        List<Pair<Point>> result = new ArrayList<>();
        for (String line : lines) {
            result.add(parsePointPair(line));
        }

        return result;
    }

    /*
     * Matches lines like:
     *
     * p=9,5 v=-3,-3
     * */
    private static final Pattern POINT_PAIR_REGEX = Pattern.compile(".*=(-?\\d+),(-?\\d+).*=(-?\\d+),(-?\\d+)");

    private static Pair<Point> parsePointPair(String line) {
        Matcher match = POINT_PAIR_REGEX.matcher(line);
        if (!match.matches()) {
            throw new IllegalArgumentException("Invalid line: " + line);
        }
        Point start = new Point(parseInt(match.group(1)), parseInt(match.group(2)));
        Point velocity = new Point(parseInt(match.group(3)), parseInt(match.group(4)));
        return new Pair<>(start, velocity);
    }

    public static DTPair<Grid<Character>, List<Character>> parseLanternFishWarehouse(String file) {
        List<String> lines = readLines(file);

        List<String> warehouseLines = new ArrayList<>();
        List<Character> directions = new ArrayList<>();

        boolean hitMidpoint = false;
        for (String line : lines) {
            if (line.isEmpty()) {
                hitMidpoint = true;
                continue;
            }

            if (hitMidpoint) {
                for (char c : line.toCharArray()) {
                    directions.add(c);
                }
            } else {
                warehouseLines.add(line);
            }
        }

        List<List<Character>> warehouse = parseCharMatrix(warehouseLines);
        return new DTPair<>(new Grid<>(warehouse), directions);
    }

    /*
     * Matches the second part of the line e.g.
     *
     * 'Register A: 28422061' -> '28422061'
     * 'Register B: 0' -> '0'
     * 'Register C: 0' -> '0'
     * 'Program: 2,4,1,1,7,5,1,5,4,2,5,5,0,3,3,0' -> '2,4,1,1,7,5,1,5,4,2,5,5,0,3,3,0'
     *
     * */
    private static final Pattern COMPUTER_INPUT = Pattern.compile(".*: (.*)");

    private static String getComputerInput(String line) {
        Matcher match = COMPUTER_INPUT.matcher(line);
        if (!match.matches()) {
            throw new IllegalArgumentException("Invalid line: " + line);
        }
        return match.group(1);
    }

    public static OpcodeComputer parseComputer(String file) {
        List<String> lines = readLines(file);

        int a = parseInt(getComputerInput(lines.get(0)));
        int b = parseInt(getComputerInput(lines.get(1)));
        int c = parseInt(getComputerInput(lines.get(2)));

        List<Integer> program = Arrays.stream(getComputerInput(lines.get(4)).split(","))
                .map(Integer::parseInt)
                .toList();

        return OpcodeComputer.of(a, b, c, program);
    }

    public static List<Point> parseListOfPoints(String file) {
        return readLines(file)
                .stream()
                .map(line -> {
                    String[] split = line.split(",");
                    return new Point(parseInt(split[0]), parseInt(split[1]));
                })
                .toList();
    }

    public static TowelProblem parseTowelProblem(String file) {
        List<String> lines = readLines(file);
        List<String> towels = Arrays.stream(lines.get(0).split(", ")).toList();

        List<String> patterns = new ArrayList<>();
        for (int i = 2; i < lines.size(); i++) {
            patterns.add(lines.get(i));
        }
        return new TowelProblem(towels, patterns);
    }

    public static List<Long> parseListOfNumbers(String file) {
        return readLines(file)
                .stream()
                .map(Long::parseLong)
                .toList();
    }

    public static Map<String, Set<String>> parseConnectedComputers(String file) {
        List<String> strings = readLines(file);
        Map<String, Set<String>> connections = new HashMap<>();
        for (String line : strings) {
            String[] split = line.split("-");
            String c1 = split[0];
            String c2 = split[1];

            connections.computeIfAbsent(c1, k -> new HashSet<>())
                    .add(c2);
            connections.computeIfAbsent(c2, k -> new HashSet<>())
                    .add(c1);
        }

        return connections;
    }

    public static WireProblem parseWireProblem(String file) {
        boolean readingWires = true;
        List<String> lines = readLines(file);

        Map<String, Boolean> initialWires = new HashMap<>();
        List<WireProblem.Gate> gates = new ArrayList<>();

        for (String line : lines) {
            if (line.isEmpty()) {
                readingWires = false;
                continue;
            }

            if (readingWires) {
                String[] split = line.split(":");
                initialWires.put(split[0], split[1].trim().equals("1"));
            } else {
                String[] split = line.split(" ");
                gates.add(WireProblem.gateFromString(line));
            }
        }
        return new WireProblem(initialWires, gates);
    }

    public static LockProblem parseKeysAndLocks(String file) {
        List<String> lines = readLines(file);

        List<List<Integer>> locks = new ArrayList<>();
        List<List<Integer>> keys = new ArrayList<>();

        for (int i = 0; i < lines.size(); i += 8) {
            // if first line is all # we have a lock
            if (lines.get(i).chars().allMatch(c -> c == '#')) {
                locks.add(parseLock(lines.subList(i, i + 7)));
            } else {
                keys.add(parseKey(lines.subList(i, i + 7)));
            }
        }

        return new LockProblem(keys, locks);
    }

    private static List<Integer> parseLock(List<String> strings) {
        List<Integer> lock = new ArrayList<>(5);

        // initialise to 0
        for (int i = 0; i < 5; i++) {
            lock.add(0);
        }

        for (int i = 1; i <= 6; i++) {
            String line = strings.get(i);
            for (int j = 0; j < 5; j++) {
                char c = line.charAt(j);
                if (c == '#') {
                    lock.set(j, lock.get(j) + 1);
                }
            }
        }
        return lock;
    }

    private static List<Integer> parseKey(List<String> strings) {
        List<Integer> lock = new ArrayList<>(5);

        // initialise to 0
        for (int i = 0; i < 5; i++) {
            lock.add(0);
        }

        for (int i = 0; i < 6; i++) {
            String line = strings.get(i);
            for (int j = 0; j < 5; j++) {
                char c = line.charAt(j);
                if (c == '#') {
                    lock.set(j, lock.get(j) + 1);
                }
            }
        }
        return lock;
    }

    public static List<DTPair<Character, Integer>> parseDialRotations(String file) {
        List<String> lines = readLines(file);
        List<DTPair<Character, Integer>> result = new ArrayList<>();
        for (String line : lines) {
            char c = line.charAt(0);
            int rotation = parseInt(line.substring(1).trim());
            result.add(new DTPair<>(c, rotation));
        }

        return result;
    }

    public static List<Pair<Long>> parseListOfPairsWithDashes(String file) {
        List<String> lines = readLines(file);

        List<Pair<Long>> result = new ArrayList<>();

        for (String line : lines) {
            String[] pairs = StringUtils.split(line, ",");
            for (String pair : pairs) {
                String[] nums = StringUtils.split(pair, "-");
                result.add(new Pair<>(parseLong(nums[0]), parseLong(nums[1])));
            }
        }
        return result;
    }


    public static List<List<Character>> parseCharLists(String file) {
        List<String> lines = readLines(file);
        List<List<Character>> result = new ArrayList<>();

        for (String line : lines) {
            List<Character> chars = new ArrayList<>();
            for (char c : line.toCharArray()) {
                chars.add(c);
            }
            result.add(chars);
        }
        return result;
    }


    public static DTPair<List<Pair<Long>>, List<Long>> parseIngredients(String file) {
        List<String> lines = readLines(file);

        List<Pair<Long>> ranges = new ArrayList<>();
        List<Long> numbers = new ArrayList<>();

        boolean readingLongs = false;

        for (String line : lines) {
            if (!readingLongs) {
                if (line.isEmpty()) {
                    readingLongs = true;
                    continue;
                }
                String[] split = line.split("-");
                long left = parseLong(split[0]);
                long right = parseLong(split[1]);
                ranges.add(new Pair<>(left, right));
            } else {
                numbers.add(parseLong(line));
            }
        }
        return new DTPair<>(ranges, numbers);
    }


    public static List<List<String>> parseColumns(String file) {
        List<String> lines = readLines(file);

        List<List<String>> rows = new ArrayList<>();
        for (String line : lines) {
            line = line.trim();

            List<String> row = new ArrayList<>();

            for (String s : line.split(" ")) {
                if (!s.isBlank()) {
                    row.add(s);
                }
            }
            rows.add(row);
        }
        return rows;
    }


    public static List<Schematic> parseSchematics(String file) {
        return readLines(file).stream().map(Inputs::parseSchematic).toList();
    }

    private static Schematic parseSchematic(String line) {
        String[] split = line.split(" ");

        List<Boolean> indicators = Arrays.stream(split[0]
                        .replace("[", "")
                        .replace("]", "")
                        .split(""))
                .map(s -> s.equals("#"))
                .toList();

        List<List<Integer>> buttons = new ArrayList<>();
        for (int i = 1; i < split.length - 1; i++) {
            String[] buttonStrings = split[i]
                    .replace("(", "")
                    .replace(")", "")
                    .split(",");
            buttons.add(Arrays.stream(buttonStrings)
                    .map(Integer::parseInt)
                    .toList());
        }

        List<Integer> joltages = Arrays.stream(
                        split[split.length - 1]
                                .replace("{", "")
                                .replace("}", "")
                                .split(","))
                .map(Integer::parseInt)
                .toList();

        return new Schematic(indicators, buttons, joltages);
    }

    public static List<Point3D> parse3DPoints(String file) {
        throw new UnsupportedOperationException("Accidentally deleted!");
    }

    public static MultiValuedMap<String, String> parseCables(String file) {
        MultiValuedMap<String, String> cables = new HashSetValuedHashMap<>();
        for (String line : readLines(file)) {
            String[] split = line.split(" ");
            String from = split[0].replace(":", "");
            for (int i = 1; i < split.length; i++) {
                String to = split[i];
                cables.put(from, to);
            }
        }
        return cables;
    }

    public static PresentFitProblem parsePresentFitProblem(String file) {
        List<PresentFitProblem.Present> presents = new ArrayList<>();
        List<PresentFitProblem.PresentArea> presentAreas = new ArrayList<>();

        List<String> lines = readLines(file);
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.charAt(1) == ':') {
                presents.add(parsePresent(lines, i + 1));
                i += 4;
            } else {
                // Must be a problem
                String[] split = line.split(":");
                String[] widths = split[0].split("x");
                int width = parseInt(widths[0]);
                int height = parseInt(widths[1]);

                String[] presentCountsStr = split[1].trim().split(" ");
                List<Integer> presentCounts = Arrays.stream(presentCountsStr)
                        .map(Integer::parseInt)
                        .toList();
                presentAreas.add(new PresentFitProblem.PresentArea(width, height, presentCounts));
            }
        }

        return new PresentFitProblem(presents, presentAreas);
    }

    private static PresentFitProblem.Present parsePresent(List<String> lines, int startIndex) {
        boolean[][] present = new boolean[3][3];
        for (int i = 0; i < 3; i++) {
            char[] chars = lines.get(startIndex + i).toCharArray();
            for (int j = 0; j < 3; j++) {
                present[i][j] = chars[j] == '#';
            }
        }
        return new PresentFitProblem.Present(present);
    }
}
