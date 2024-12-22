package me.detj.aoc_2024;

import me.detj.utils.Grid;
import me.detj.utils.Inputs;
import me.detj.utils.Point;
import org.apache.commons.collections4.MultiValuedMap;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Solver08 {
    public static void main(String[] args) {
        var map = Inputs.parseCharGrid("input_08.txt");

        int antiNodes = countAntiNodes(map.shallowCopy());
        System.out.printf("Solution Part 1: %d\n", antiNodes);

        int antiNodesWithHarmonics = countAntiNodesWithHarmonics(map.shallowCopy());
        System.out.printf("Solution Part 2: %d\n", antiNodesWithHarmonics);
    }

    private static int countAntiNodes(Grid<Character> map) {
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

    private static int countAntiNodesWithHarmonics(Grid<Character> map) {
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
}
