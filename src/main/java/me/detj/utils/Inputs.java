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
import java.util.List;

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

        List<List<Character>> results = new ArrayList<>(lines.size());
        for (int i = lines.size() -1; i >= 0; i--) {
            String line = lines.get(i);
            List<Character> chars = line.chars().mapToObj(c -> (char) c).toList();
            results.add(chars);
        }
        return results;
    }
}
