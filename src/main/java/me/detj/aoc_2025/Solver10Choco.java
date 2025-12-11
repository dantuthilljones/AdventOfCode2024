package me.detj.aoc_2025;

import me.detj.utils.Inputs;
import me.detj.utils.Schematic;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.variables.IntVar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Solver10Choco {

    static void main() throws IOException {
        String file = "2025/input_10.txt";
        var input = Inputs.parseSchematics(file);
        System.out.printf("Solution 2: %d\n", fewestPresses2(input));
    }

    private static long fewestPresses2(List<Schematic> input) {
        int threads = 16;
        try (ExecutorService executorService = Executors.newFixedThreadPool(threads)) {
            List<Future<Long>> futures = new ArrayList<>();

            AtomicInteger doneCounter = new AtomicInteger(0);
            for (int i = 0; i < input.size(); i++) {
                final int iFinal = i;
                Schematic schematic = input.get(i);
                futures.add(executorService.submit(() -> {
                    long presses = calcFewestPresses2(schematic);
                    int done = doneCounter.incrementAndGet();
                    System.out.printf("Completed schematic %d. %d have completed and %d remain\n", iFinal + 1, done, input.size() - done);
                    return presses;
                }));
            }

            long presses = 0;
            for (Future<Long> future : futures) {
                try {
                    presses += future.get();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
            }

            return presses;
        }
    }

    private static long calcFewestPresses2(Schematic schematic) {
        Model model = new Model(schematic.toString());

        int maxPressesTotal = 0;

        IntVar[] buttonPressVars = new IntVar[schematic.getButtons().size()];
        for (int i = 0; i < buttonPressVars.length; i++) {
            List<Integer> targetsEffectedByButton = schematic.getButtons().get(i);

            int maxPresses = targetsEffectedByButton.stream().mapToInt(targetI -> schematic.getJoltages().get(targetI)).max().getAsInt();
            maxPressesTotal += maxPresses;

            String str = targetsEffectedByButton.stream().map(Object::toString).collect(Collectors.joining(",", "(", ")"));
            buttonPressVars[i] = model.intVar("Button_Press_" + i + "_" + str, 0, maxPresses);
        }

        IntVar[] targets = new IntVar[schematic.getJoltages().size()];
        for (int i = 0; i < targets.length; i++) {
            int target = schematic.getJoltages().get(i);

            // get the buttons which effect this target
            // then say target is equal to the sum of those button presses
            List<IntVar> buttonsAffectingTarget = new ArrayList<>();
            for (int j = 0; j < schematic.getButtons().size(); j++) {
                if (schematic.getButtons().get(j).contains(i)) {
                    buttonsAffectingTarget.add(buttonPressVars[j]);
                }
            }

            IntVar[] asArray = buttonsAffectingTarget.toArray(new IntVar[0]);
            model.sum(asArray, "=", target).post();
        }

        int minMaxPresses = schematic.getJoltages().stream().mapToInt(Integer::intValue).max().getAsInt();

        IntVar totalPresses = model.intVar("TotalPresses", minMaxPresses, maxPressesTotal);
        model.sum(buttonPressVars, "=", totalPresses).post();
        Solution solution = model.getSolver().findOptimalSolution(totalPresses, false);
        if (solution != null) {
            System.out.println(solution.toString());
        }

        return solution.getIntVal(totalPresses);
    }


}
