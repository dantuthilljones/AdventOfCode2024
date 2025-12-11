package me.detj.aoc_2025;

import me.detj.utils.Inputs;
import org.apache.commons.collections4.MultiValuedMap;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Solver11 {

    public static void main(String[] args) throws IOException {
        String file = "2025/input_11.txt";
        var input = Inputs.parseCables(file);

        long part1Start = System.currentTimeMillis();
        long solutionPart1 = countPaths(input, "you", "out");
        long part1Duration = System.currentTimeMillis() - part1Start;
        System.out.printf("Solution 1: %d (%dms)\n", solutionPart1, part1Duration);

        long part2Start = System.currentTimeMillis();
        long solutionPart2 = countPaths2(input);
        long part2Duration = System.currentTimeMillis() - part2Start;
        System.out.printf("Solution 2: %d (%dms)\n", solutionPart2, part2Duration);
    }

    private static long countPaths(MultiValuedMap<String, String> input, String start, String end) {
        Map<String, Long> pathsToOut = new HashMap<>();
        return visit(input, start, end, pathsToOut);
    }

    private static long visit(MultiValuedMap<String, String> input, String current, String end, Map<String, Long> pathsToOut) {
        if (pathsToOut.containsKey(current)) {
            return pathsToOut.get(current);
        }
        if (current.equals(end)) {
            return 1;
        }
        long result = 0;
        for (String neighbor : input.get(current)) {
            result += visit(input, neighbor, end, pathsToOut);
        }
        pathsToOut.put(current, result);
        return result;
    }

    private static long countPaths2(MultiValuedMap<String, String> input) {
        // S_F means number of paths from S to F
        // SF means number of paths from S to F not via D or O

        long D_F = countPaths(input, "dac", "fft");
        long F_D = countPaths(input, "fft", "dac");

        long S_F = countPaths(input, "svr", "fft");
        long S_D = countPaths(input, "svr", "dac");

        long D_O = countPaths(input, "dac", "out");
        long F_O = countPaths(input, "fft", "out");

        long SF = S_F - S_D*D_F;
        long SD = S_D - S_F*F_D;
        long FD = F_D; // no paths from F to D via S or O so can just take F_D
        long DF = D_F; // no paths from D to F via S or O so can just take D_F
        long DO = D_O - D_F * F_O;
        long FO = F_O - F_D * D_O;

        long SFDO = SF * FD * DO;
        long SDFO = SD * DF * FO;

        return SFDO + SDFO;
    }

}
