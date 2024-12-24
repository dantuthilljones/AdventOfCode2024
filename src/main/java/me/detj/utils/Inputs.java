package me.detj.utils;

import lombok.SneakyThrows;
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
        URL url = Utils.class.getResource("/" + path);
        return Paths.get(url.toURI());
    }

    public static Pair<List<Integer>> parseListOfPairs(String file) {
        List<String> lines = readLines(file);

        List<Integer> l0 = new ArrayList<>(lines.size());
        List<Integer> l1 = new ArrayList<>(lines.size());

        for (String line : lines) {
            String[] row = StringUtils.split(line, "    ");
            l0.add(Integer.parseInt(row[0]));
            l1.add(Integer.parseInt(row[1]));
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
                rules.add(Point.of(Integer.parseInt(split[0]), Integer.parseInt(split[1])));
            } else {
                String[] split = StringUtils.split(line, ",");
                List<Integer> pages = Arrays.stream(split).map(Integer::parseInt).toList();
                manuals.add(pages);
            }
        }

        return new DTPair<>(rules, manuals);
    }

    public static Grid<Character> parseCharGrid(String file) {
        return new Grid<>(parseCharMatrix(file));
    }

    public static List<DTPair<Long, List<Long>>> parseLabelledList(String file) {
        List<String> lines = readLines(file);

        List<DTPair<Long, List<Long>>> result = new ArrayList<>();
        for (String line : lines) {
            String[] split = line.split(":");
            long label = Long.parseLong(split[0]);

            List<Long> values = Arrays.stream(split[1].trim().split(" ")).map(Long::parseLong).toList();

            result.add(new DTPair<>(label, values));
        }

        return result;
    }

    public static List<Integer> parseDenseIntList(String file) {
        String content = readFile(file);
        List<Integer> list = new ArrayList<>(content.length());
        for (char c : content.toCharArray()) {
            list.add(Integer.parseInt("" + c));
        }
        return list;
    }

    public static Grid<Integer> parseDenseIntGrid(String file) {
        var chars = parseCharMatrix(file);
        List<List<Integer>> grid = chars.stream()
                .map(row -> row.stream().map(c -> Integer.parseInt(c.toString())).collect(Collectors.toList()))
                .collect(Collectors.toList());
        return new Grid<>(grid);
    }

    public static List<Long> parseIntList(String file) {
        String content = readFile(file);
        List<Long> list = new ArrayList<>();
        for (String num : content.split(" ")) {
            list.add(Long.parseLong(num));
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
        return new Point(Integer.parseInt(match.group(1)), Integer.parseInt(match.group(2)));
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
        Point start = new Point(Integer.parseInt(match.group(1)), Integer.parseInt(match.group(2)));
        Point velocity = new Point(Integer.parseInt(match.group(3)), Integer.parseInt(match.group(4)));
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

        int a = Integer.parseInt(getComputerInput(lines.get(0)));
        int b = Integer.parseInt(getComputerInput(lines.get(1)));
        int c = Integer.parseInt(getComputerInput(lines.get(2)));

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
                    return new Point(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
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
        for(String line : strings) {
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

    public static WireProblem parseWireProblem(String file ) {
        boolean readingWires = true;
        List<String> lines = readLines(file);

        Map<String, Boolean> initialWires = new HashMap<>();
        List<WireProblem.Gate> gates = new ArrayList<>();

        for(String line : lines) {
            if(line.isEmpty()) {
                readingWires = false;
                continue;
            }

            if(readingWires) {
                String[] split = line.split(":");
                initialWires.put(split[0], split[1].trim().equals("1"));
            } else {
                String[] split = line.split(" ");
                String left = split[0];
                String gate = split[1];
                String right = split[2];
                // skip the arrow
                String out = split[4];
                gates.add(new WireProblem.Gate(left, gate, right, out));
            }
        }
        return new WireProblem(initialWires, gates);
    }
}
