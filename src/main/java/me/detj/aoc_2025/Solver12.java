package me.detj.aoc_2025;

import me.detj.utils.Inputs;
import me.detj.utils.PresentFitProblem;
import org.apache.commons.collections4.MultiValuedMap;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Solver12 {

    public static void main(String[] args) throws IOException {
        String file = "2025/input_12.txt";
        var input = Inputs.parsePresentFitProblem(file);

        long part1Start = System.currentTimeMillis();
        long solutionPart1 = fitPresents(input);
        long part1Duration = System.currentTimeMillis() - part1Start;
        System.out.printf("Solution 1: %d (%dms)\n", solutionPart1, part1Duration);

//        long part2Start = System.currentTimeMillis();
//        long solutionPart2 = countPaths2(input);
//        long part2Duration = System.currentTimeMillis() - part2Start;
//        System.out.printf("Solution 2: %d (%dms)\n", solutionPart2, part2Duration);
    }

    private static long fitPresents(PresentFitProblem input) {
        long fittable = 0;
        for(PresentFitProblem.PresentArea area : input.getAreas()) {
            if (canFit(input.getPresents(), area)) {
                fittable++;
            }
        }
        return fittable;
    }

    private static boolean canFit(List<PresentFitProblem.Present> presents, PresentFitProblem.PresentArea area) {
        int x = 0;
        int y = 0;

        for(int count : area.getPresentCounts()) {
            for(int i = 0; i < count; i++) {
                if(x + 3 <= area.getWidth()) {
                    x += 3;
                } else if (y + 3 <= area.getHeight()) {
                    x = 0;
                    y += 3;
                } else {
                    return false;
                }
            }
        }

        return true;
    }


}
