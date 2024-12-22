package me.detj.aoc_2024;

import lombok.Value;
import me.detj.utils.ButtonRobot;
import me.detj.utils.Inputs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public class Solver21 {
    public static void main(String[] args) {
        var input = Inputs.readLines("input_21.txt");

        long s1 = calculateComplexities(input);
        System.out.printf("Solution Part 1: %d\n", s1);

        long s2 = calculateComplexities25(input);
        System.out.printf("Solution Part 2: %d\n", s2);
    }

    private static long calculateComplexities(List<String> codes) {
        List<ButtonRobot> robots = List.of(
                ButtonRobot.directionalRobot(),
                ButtonRobot.directionalRobot(),
                ButtonRobot.keyPadRobot()
        );

        long sum = 0;
        for (String code : codes) {
            String humanInput = calcHumanInput(code, robots);
            sum += calculateComplexity(humanInput, code);
        }
        return sum;
    }

    private static int calculateComplexity(String humanInput, String code) {
        String numericComponent = code.substring(0, 3);
        int complexity = Integer.parseInt(numericComponent);
        return humanInput.length() * complexity;
    }

    private static String calcHumanInput(String code, List<ButtonRobot> robots) {
        Set<String> allSequences = calcPresses(robots, robots.size() - 1, Set.of(code));
        return getShortestString(allSequences);
    }

    public static String getShortestString(Collection<String> strings) {
        if (strings == null || strings.isEmpty()) {
            return null;
        }

        List<String> shortests = new ArrayList<>();
        shortests.add(strings.iterator().next());
        for (String str : strings) {
            if (str.length() < shortests.get(0).length()) {
                shortests.clear();
                shortests.add(str);
            } else if (str.length() == shortests.get(0).length()) {
                shortests.add(str);
            }
        }
        return shortests.get(0);
    }

    private static Set<String> calcPresses(List<ButtonRobot> robots, int i, Set<String> prevInputs) {
        ButtonRobot robot = robots.get(i);

        Set<String> nextInputs = new HashSet<>();
        for (String input : prevInputs) {
            nextInputs.addAll(robot.possibleSequencesToEnter(input));
        }

        if (i == 0) {
            return nextInputs;
        } else {
            return calcPresses(robots, i - 1, nextInputs);
        }
    }

    private static long calculateComplexities25(List<String> codes) {
        List<ButtonRobot> robots = List.of(
                ButtonRobot.directionalRobot(),
                ButtonRobot.keyPadRobot()
        );

        long sum = 0;
        for (String code : codes) {
            Set<String> allSequences = calcPresses(robots, robots.size() - 1, Set.of(code));

            long shortest = Long.MAX_VALUE;
            for(String sequence : allSequences) {
                shortest = Math.min(shortest, shortestSequence(sequence, 24));
            }

            String numericComponent = code.substring(0, 3);
            long complexity = Long.parseLong(numericComponent);
            sum += complexity * shortest;
        }
        return sum;
    }

    private static long shortestSequence(String finalSeq, int keypads) {
        //Map<ButtonState, Long> frequences = calcFrequencies(finalSeq);

        Map<ButtonState, List<Map<ButtonState, Long>>> allIncreases = computeIncreasesEachIteration();

        List<Map<ButtonState, Map<ButtonState, Long>>> flattenedIncreases = flattenIncreases(allIncreases);

        long shortest = Long.MAX_VALUE;
        for(Map<ButtonState, Map<ButtonState, Long>> increases : flattenedIncreases) {
            Map<ButtonState, Long> frequences = calcFrequencies(finalSeq);
            for(int i = 0; i < keypads; i++) {
                Map<ButtonState, Long> newFrequencies = new HashMap<>();
                frequences.forEach((buttonState, freq) -> {
                    increases.get(buttonState).forEach((higherState, num) -> {
                        long newFreq = freq * num;
                        newFrequencies.put(higherState, newFrequencies.getOrDefault(higherState, 0L) + newFreq);
                    });
                });
                frequences = newFrequencies;
            }

            AtomicLong sum = new AtomicLong(0);
            frequences.values().forEach(sum::addAndGet);
            shortest = Math.min(shortest, sum.get());
        }
        return shortest;
    }

    private static List<Map<ButtonState, Map<ButtonState, Long>>> flattenIncreases(
            Map<ButtonState, List<Map<ButtonState, Long>>> allIncreases) {
        List<Map<ButtonState, Map<ButtonState, Long>>> result = new ArrayList<>();

        result.add(new HashMap<>());

        allIncreases.forEach((fromState, toStates) -> {
            if(toStates.size() == 1) {
                result.forEach(res -> res.put(fromState, toStates.get(0)));
            } else {
                int startResultSize = result.size();

                // create a new entry per new path
                for(int i = 1; i < toStates.size(); i++) {
                    for(int j = 0; j < startResultSize; j++) {
                        result.add(new HashMap<>(result.get(j)));
                    }
                }

                // add i to each
                for(int i = 0; i < toStates.size(); i++) {
                    Map<ButtonState, Long> newToStates = toStates.get(i);
                    for(int j = 0; j < startResultSize; j++) {
                        int index =  j + startResultSize * i;
                        result.get(index).put(fromState, newToStates);
                    }
                }
            }
        });
        return result;
    }

    private static void flattenIncreasesRecursive(Map<ButtonState, List<Map<ButtonState, Long>>> allIncreases,
                                                  Map<ButtonState, Map<ButtonState, Long>> current,
                                                  List<Map<ButtonState, Map<ButtonState, Long>>> result) {
        if (current.size() == allIncreases.size()) {
            result.add(new HashMap<>(current));
            return;
        }

        for (Map.Entry<ButtonState, List<Map<ButtonState, Long>>> entry : allIncreases.entrySet()) {
            if (!current.containsKey(entry.getKey())) {
                for (Map<ButtonState, Long> map : entry.getValue()) {
                    current.put(entry.getKey(), map);
                    flattenIncreasesRecursive(allIncreases, current, result);
                    current.remove(entry.getKey());
                }
            }
        }
    }

    private static long shortestSequenceRecursive(String finalSeq, int keypads) {
        Map<ButtonState, Long> frequences = calcFrequencies(finalSeq);
        Map<ButtonState, List<Map<ButtonState, Long>>> increases = computeIncreasesEachIteration();

        List<Map<ButtonState, Long>> allFrequencies = calcRecursively(frequences, increases, keypads);

        //todo get one with shortest frequencies

        AtomicLong sum = new AtomicLong(0);

        frequences.values().forEach(sum::addAndGet);

        return sum.get();
    }

    private static List<Map<ButtonState, Long>> calcRecursively(
            Map<ButtonState, Long> frequences,
            Map<ButtonState, List<Map<ButtonState, Long>>> increases,
            int remainingKeypads) {

        List<Map<ButtonState, Long>> buttons = new ArrayList<>();

        Map<ButtonState, Long> newFrequencies = new HashMap<>();
        frequences.forEach((buttonState, freq) -> {
            increases.get(buttonState).get(0).forEach((higherState, num) -> {
                long newFreq = freq * num;
                newFrequencies.put(higherState, newFrequencies.getOrDefault(higherState, 0L) + newFreq);
            });
        });

        // to do return buttons
        return null;
    }

    private static Map<ButtonState, List<Map<ButtonState,Long>>> computeIncreasesEachIteration() {
        ButtonRobot robot = ButtonRobot.directionalRobot();

        Map<ButtonState, List<Map<ButtonState,Long>>> increases = new HashMap<>();
        for(ButtonState state : ButtonState.ALL) {
            robot.goToButton(state.prevCharacter);
            Set<String> paths = robot.goToButton(state.character);

            // for now just take first, maybe we need to try both paths?
            List<Map<ButtonState, Long>> frequencies = new ArrayList<>();
            for(String path : paths) {
                frequencies.add(calcFrequencies(path));
            }
            increases.put(state, frequencies);
        }
        return increases;
    }

    private static Map<ButtonState, Long> calcFrequencies(String finalSeq) {
        Map<ButtonState, Long> frequences = new HashMap<>();
        char prev = 'A';
        for(char c : finalSeq.toCharArray()) {
            ButtonState buttonState = ButtonState.of(c, prev);
            frequences.put(buttonState, frequences.getOrDefault(buttonState, 0L) +1);
            prev = c;
        }
        return frequences;
    }

    @Value
    private static class ButtonState {

        private static final List<ButtonState> ALL;

        static {
            List<ButtonState> states = new ArrayList<>();
            char[] possibleValues = {'<', '^', '>', 'v', 'A'};
            for (char c : possibleValues) {
                for (char p : possibleValues) {
                    states.add(new ButtonState(c, p));
                }
            }
            ALL = states;
        }


        char character;
        char prevCharacter;

        public static ButtonState of(char c, char prev) {
            return new ButtonState(c, prev);
        }
    }
}