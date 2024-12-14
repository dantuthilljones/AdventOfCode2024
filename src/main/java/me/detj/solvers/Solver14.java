package me.detj.solvers;

import me.detj.utils.Inputs;
import me.detj.utils.Point;
import me.detj.utils.Utils;
import org.apache.commons.collections4.SetUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Solver14 {
    public static void main(String[] args) {
        var input = Inputs.parsePointPairs("input_14.txt");

        long s1 = Utils.calculateSafetyFactor(input, new Point(101, 103), 100);
        System.out.printf("Solution Part 1: %d\n", s1);

        Utils.printRobotAfter(input, new Point(101, 103), 7520);
        //System.out.printf("Solution Part 2: %d\n", s2);
    }
    
    /*
    *
    * Skinny pattern: 103
    * 1
    * 104
    * 207
    * 310
    *
    * 103s+1
    *
    * Fat Pattern: 101
    * 46
    * 147
    * 248
    *
    * 101f+46
    *
    * 103s+1=101f+46
    * */
    public static void main3(String[] args) {
        //103s+1
        Set<Long> sValues = new HashSet<>();
        for (long s = 0; s < 100_000; s++) {
            sValues.add(s * 103 + 1);
        }

        //101f+46
        Set<Long> fValues = new HashSet<>();
        for (long f = 0; f < 100_000; f++) {
            fValues.add(f * 101 + 46);
        }

        Set<Long> intersection = SetUtils.intersection(sValues, fValues);

        List<Long> sorted = intersection.stream()
                .sorted()
                .toList();

        for (Long n : sorted) {
            System.out.println(n);
        }
    }
}
