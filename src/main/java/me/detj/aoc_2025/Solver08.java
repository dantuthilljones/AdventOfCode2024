package me.detj.aoc_2025;

import me.detj.utils.Grid;
import me.detj.utils.Inputs;
import me.detj.utils.Pair;
import me.detj.utils.Point;
import me.detj.utils.Point3D;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Solver08 {

    public static void main(String[] args) throws IOException {
        String file = "2025/input_08.txt";
        var input = Inputs.parse3DPoints(file);
        System.out.printf("Solution 1: %d\n", largestCircuitSizes(input));
        System.out.printf("Solution 2: %d\n", largestCircuitSizes2(input));
    }

    private static long largestCircuitSizes(List<Point3D> input) {
        List<Set<Point3D>> circuits = new ArrayList<>();

        for (Pair<Point3D> pairs : getClosest(input, 1000)) {
            Point3D p1 = pairs.getLeft();
            Point3D p2 = pairs.getRight();

            Set<Point3D> p1Set = getSet(circuits, p1);
            Set<Point3D> p2Set = getSet(circuits, p2);

            if (p1Set != null && p1Set == p2Set) {
                continue;
            } else if (p1Set == null && p2Set == null) {
                Set<Point3D> circuit = new HashSet<>();
                circuit.add(p1);
                circuit.add(p2);
                circuits.add(circuit);
            } else if (p1Set != null && p2Set == null) {
                p1Set.add(p2);
            } else if (p1Set == null && p2Set != null) {
                p2Set.add(p1);
            } else if (p1Set != null && p2Set != null) {
                // merge sets
                p1Set.addAll(p2Set);
                circuits.remove(p2Set);
            }

        }

        circuits.sort(Comparator.comparingInt(Set::size));

        long s = 1;
        for (int i = 0; i < 3; i++) {
            s *= circuits.get(circuits.size() - 1 - i).size();
        }

        return s;
    }

    private static Set<Point3D> getSet(List<Set<Point3D>> circuits, Point3D point) {
        for (Set<Point3D> circuit : circuits) {
            if (circuit.contains(point)) {
                return circuit;
            }
        }
        return null;
    }

    private static Collection<Pair<Point3D>> getClosest(List<Point3D> input, int n) {
        TreeSet<Pair<Point3D>> closest = new TreeSet<>(Comparator.comparing(pair -> pair.getLeft().distanceTo(pair.getRight())));
        for (int i = 0; i < input.size() - 1; i++) {
            for (int j = i + 1; j < input.size(); j++) {
                Point3D p1 = input.get(i);
                Point3D p2 = input.get(j);
                closest.add(new Pair<>(p1, p2));

                if (closest.size() > n) {
                    closest.pollLast();
                }
            }
        }
        return closest;
    }


    private static long largestCircuitSizes2(List<Point3D> input) {
        List<Set<Point3D>> circuits = new ArrayList<>();

        for (Pair<Point3D> pairs : getClosest(input, 100_000_000)) {
            Point3D p1 = pairs.getLeft();
            Point3D p2 = pairs.getRight();

            Set<Point3D> p1Set = getSet(circuits, p1);
            Set<Point3D> p2Set = getSet(circuits, p2);

            if (p1Set != null && p1Set == p2Set) {
                continue;
            } else if (p1Set == null && p2Set == null) {
                Set<Point3D> circuit = new HashSet<>();
                circuit.add(p1);
                circuit.add(p2);
                circuits.add(circuit);
            } else if (p1Set != null && p2Set == null) {
                p1Set.add(p2);
            } else if (p1Set == null && p2Set != null) {
                p2Set.add(p1);
            } else if (p1Set != null && p2Set != null) {
                // merge sets
                p1Set.addAll(p2Set);
                circuits.remove(p2Set);
            }

            if (circuits.size() == 1 && circuits.getFirst().size() == 1000) {
                return (long) p1.getX() * (long) p2.getX();
            }
        }

        return -1;
    }

}
