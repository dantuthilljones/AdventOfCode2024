package me.detj.aoc_2024;

import lombok.Value;
import me.detj.utils.Inputs;
import me.detj.utils.Pair;
import me.detj.utils.WireCalculator;
import me.detj.utils.WireProblem;
import org.apache.commons.collections4.ListUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static me.detj.utils.WireCalculator.bToString;
import static me.detj.utils.WireCalculator.buildInitialValues;

public class Solver24 {
    public static void main(String[] args) {
        var input = Inputs.parseWireProblem("2024/input_24.txt");

        long s1 = getZNumber(input);
        System.out.printf("Solution Part 1: %d\n", s1);

        String s2 = graphSolution("2024/input_24_solution.txt");
        System.out.printf("Solution Part 2: %s\n", s2);
    }

    private static String graphSolution(String file) {
        List<String> lines = Inputs.readLines(file);

        return lines.stream()
                .map(line -> WireProblem.gateFromString(line))
                .flatMap(graph -> toMermaidString(graph).stream())
                .collect(Collectors.joining("\n"));
    }

    private static List<String> toMermaidString(WireProblem.Gate graph) {
        String opName = graph.getLeft() + graph.getRight() + graph.getOperation() + graph.getOutput();
        return List.of(
                String.format("%s --> %s[%s]", graph.getLeft(), opName, graph.getOperation()),
                String.format("%s --> %s[%s]", graph.getRight(), opName, graph.getOperation()),
                String.format("%s --> %s", opName, graph.getOutput())
        );
    }

    private static long getZNumber(WireProblem input) {
        WireCalculator wireCalculator = WireCalculator.fromGates(input.getGates());
        return wireCalculator.calculate(input.getInitialValues());
    }

    private static String findSwaps(WireProblem input) {
        WireCalculator wireCalculator = WireCalculator.fromGates(input.getGates());

        doAddition(wireCalculator, 0, 0);
        doAddition(wireCalculator, 123456, 0);
        doAddition(wireCalculator, 123456, 1);

        doAddition(wireCalculator, 0, 123456);
        doAddition(wireCalculator, 1, 123456);
        return null;
    }

    private static List<WireProblem.Gate> findSuspiciousGates(WireProblem input) {
        WireCalculator wireCalculator = WireCalculator.fromGates(input.getGates());

        List<Long> badPathsX0 = new ArrayList<>();
        List<Long> badPathsXY = new ArrayList<>();
        List<Long> badPaths0Y = new ArrayList<>();
        long start = 1;
        for (int i = 0; i < 44; i++) {
            long inputX = start << i;
            long out = doAddition(wireCalculator, inputX, 0);
            if (out != inputX) {
                badPathsX0.add(inputX);
            }

            out = doAddition(wireCalculator, 0, inputX);
            if (out != inputX) {
                badPaths0Y.add(inputX);
            }

            out = doAddition(wireCalculator, inputX, inputX);
            if (out != inputX + inputX) {
                badPathsXY.add(inputX);
            }
        }

        System.out.print("Bad Paths X + 0:\n");
        for (long l : badPathsX0) {
            System.out.println(bToString(l));
        }

        System.out.print("Bad Paths X + Y:\n");
        for (long l : badPathsXY) {
            System.out.println(bToString(l));
        }

        Set<String> badGates = new HashSet<>();

        System.out.println("Looking for bad gates with x + 0");
        for (long l : badPathsX0) {
            Map<String, Boolean> ivs = buildInitialValues(l, 0);
            ivs.forEach((wire, value) -> {
                if (value) {
                    String outputWire = "z" + wire.substring(1, 3);
                    List<String> gates = wireCalculator.gatesAlongPath(wire, outputWire);
                    System.out.printf("Gates between %s and %s:\n%s\n", wire, outputWire, String.join(",", gates));
                    badGates.addAll(gates);
                }
            });
        }

        System.out.println("Looking for badc gates with 0 + y");
        for (long l : badPaths0Y) {
            Map<String, Boolean> ivs = buildInitialValues(0, l);
            ivs.forEach((wire, value) -> {
                if (value) {
                    String outputWire = "z" + wire.substring(1, 3);
                    List<String> gates = wireCalculator.gatesAlongPath(wire, outputWire);
                    System.out.printf("Gates between %s and %s:\n%s\n", wire, outputWire, String.join(",", gates));
                    badGates.addAll(gates);
                }
            });
        }

//        System.out.println("Looking for bad gates with x + x");
//        for (long l : badPathsXY) {
//            Map<String, Boolean> ivs = buildInitialValues(l, 0);
//            ivs.forEach((wire, value) -> {
//                if (value) {
//                    String outputWire = "z" + wire.substring(1, 3);
//                    List<String> gates = wireCalculator.gatesAlongPath(wire, outputWire);
//                    System.out.printf("Gates between %s and %s:\n%s\n", wire, outputWire, String.join(",", gates));
//                }
//            });
//        }

        return badGates.stream()
                .map(WireProblem::gateFromString)
                .toList();
    }

    private static String findViaScore(WireProblem input) {
        List<WireProblem.Gate> gates = input.getGates();
        WireCalculator calc = WireCalculator.fromGates(gates);

//        List<WireProblem.Gate> suspiciousGates = findSuspiciousGates(input);
//        suspiciousGates.forEach(g -> System.out.println(g.toString()));


        System.out.println("## Finding By Score ##");
        System.out.printf("Starting score = %d\n", score(calc));

        List<ThreadRun> swaps = new ArrayList<>();
        //for (int i = 0; i < 4; i++) {
        while (swaps.isEmpty() || swaps.getLast().score != 0) {
            ThreadRun bestSwap = trySwappingMultiThreaded(gates, gates);
            System.out.printf("Found best swap: %s\n", bestSwap);
            swaps.add(bestSwap);
            gates = new ArrayList<>(doSwap(gates, bestSwap.g0, bestSwap.g1));
        }

        System.out.printf("Swaps:\n");
        swaps.forEach(System.out::println);

        WireCalculator wireCalculator = WireCalculator.fromGates(gates);
        for (Long testX : List.of(0L, 12345L, 123443857L, 32132133333L)) {
            for (Long testY : List.of(0L, 12345L, 123443857L, 32132133333L)) {
                long ans = wireCalculator.calculate(testX, testY);
                if (ans != testX + testY) {
                    System.out.printf("Error: %d + %d = %d\n", testX, testX, ans);
                }
            }
        }

        String ans = swaps.stream()
                .flatMap(swap -> Stream.of(swap.g0, swap.g1))
                .map(WireProblem.Gate::getOutput)
                .sorted()
                .collect(Collectors.joining(","));

        return ans;
    }

    private static Pair<WireProblem.Gate> trySwapping(List<WireProblem.Gate> gates, List<WireProblem.Gate> suspiciousGates) {
        long minScore = Long.MAX_VALUE;
        WireProblem.Gate minG0 = null;
        WireProblem.Gate minG1 = null;

        for (int i = 0; i < suspiciousGates.size(); i++) {
            for (int j = i + 1; j < suspiciousGates.size(); j++) {
                WireProblem.Gate g0 = suspiciousGates.get(i);
                WireProblem.Gate g1 = suspiciousGates.get(j);
                WireCalculator calc = swap(gates, g0, g1);

                try {
                    long score = score(calc);
                    if (score < minScore) {
                        minScore = score;
                        minG0 = g0;
                        minG1 = g1;
                        System.out.printf("New min swap %s, %s = %d\n", g0, g1, minScore);
                    }
                } catch (Exception e) {
                    System.out.printf("Exception from swap %s, %s\n", g0, g1);
                    e.printStackTrace();
                }
            }
        }

        return new Pair<>(minG1, minG0);
    }

    private static ThreadRun trySwappingMultiThreaded(List<WireProblem.Gate> gates, List<WireProblem.Gate> suspiciousGates) {
        List<Pair<WireProblem.Gate>> swaps = new ArrayList<>();
        for (int i = 0; i < suspiciousGates.size(); i++) {
            for (int j = i + 1; j < suspiciousGates.size(); j++) {
                WireProblem.Gate g0 = suspiciousGates.get(i);
                WireProblem.Gate g1 = suspiciousGates.get(j);
                swaps.add(new Pair<>(g0, g1));
            }
        }


        int cores = Runtime.getRuntime().availableProcessors();
        System.out.printf("Cores %d\n", cores);

        List<List<Pair<WireProblem.Gate>>> partitions = ListUtils.partition(swaps, swaps.size() / cores);

        System.out.printf("Partitions %d\n", partitions.size());


        List<Future<ThreadRun>> runs = new ArrayList<>();

        int threadNum = 0;
        for (List<Pair<WireProblem.Gate>> partition : partitions) {
            Future<ThreadRun> run = runPartition(gates, threadNum, partition);
            runs.add(run);
            threadNum++;
        }

        List<ThreadRun> results = runs.stream()
                .map(future -> {
                    try {
                        return future.get();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                }).toList();

        ThreadRun bestRun = results.stream()
                .min(Comparator.comparing(run -> run.score))
                .get();

        List<ThreadRun> list = results.stream().filter(run -> run.getScore() == bestRun.getScore())
                .toList();

        if(!list.isEmpty()) {
            System.out.println("Found more best runs:");
            list.forEach(System.out::println);
        }

        return bestRun;
    }

    private static Future<ThreadRun> runPartition(List<WireProblem.Gate> gates, int threadNum, List<Pair<WireProblem.Gate>> swaps) {
        return CompletableFuture.supplyAsync(() -> {
            //System.out.printf("Starting thread %d\n", threadNum);
            long minScore = Long.MAX_VALUE;
            WireProblem.Gate minG0 = null;
            WireProblem.Gate minG1 = null;
            for (Pair<WireProblem.Gate> swap : swaps) {
                WireCalculator calc = swap(gates, swap.getLeft(), swap.getRight());
                try {
                    long score = score(calc);
                    if (score < minScore) {
                        minScore = score;
                        minG0 = swap.getLeft();
                        minG1 = swap.getRight();
                        //System.out.printf("New min swap %s = %d\n", swap, minScore);
                    }
                } catch (Exception e) {
                    System.out.printf("Exception from swap %s\n", swap);
                    e.printStackTrace();
                }

            }
            return new ThreadRun(minG0, minG1, minScore);
        });
    }

    @Value
    private static class ThreadRun {
        WireProblem.Gate g0;
        WireProblem.Gate g1;
        long score;
    }

    private static Collection<WireProblem.Gate> doSwap(List<WireProblem.Gate> gates, WireProblem.Gate g0, WireProblem.Gate g1) {
        Map<String, WireProblem.Gate> gatesIndexed = gates.stream()
                .collect(Collectors.toMap(WireProblem.Gate::toString, g -> g));
        WireProblem.Gate newG0 = new WireProblem.Gate(g0.getLeft(), g0.getOperation(), g0.getRight(), g1.getOutput());
        WireProblem.Gate newG1 = new WireProblem.Gate(g1.getLeft(), g1.getOperation(), g1.getRight(), g0.getOutput());

        gatesIndexed.remove(g0.toString());
        gatesIndexed.remove(g1.toString());

        gatesIndexed.put(newG0.toString(), newG0);
        gatesIndexed.put(newG1.toString(), newG1);
        return gatesIndexed.values();
    }

    private static WireCalculator swap(List<WireProblem.Gate> gates, WireProblem.Gate g0, WireProblem.Gate g1) {
        Collection<WireProblem.Gate> swapped = doSwap(gates, g0, g1);
        return WireCalculator.fromGates(swapped);
    }

    private static long score(WireCalculator calculator) {
        long score = 0;

        long all1s = 35184372088831L;
        if (doAddition(calculator, all1s, all1s) != all1s + all1s) {
            score+=10;
        }
        if (doAddition(calculator, 0, all1s) != all1s) {
            score+=10;
        }
        if (doAddition(calculator, all1s, 0) != all1s) {
            score+=10;
        }

        long all1sLeft = (all1s >> 22 << 23) % (all1s +1);
        if (doAddition(calculator, all1sLeft, 0) != all1sLeft) {
            score+=10;
        }
        if (doAddition(calculator, 0, all1sLeft) != all1sLeft) {
            score+=10;
        }

        long all1sRight = all1s >> 22;
        if (doAddition(calculator, all1sRight, 0) != all1sRight) {
            score+=10;
        }
        if (doAddition(calculator, 0, all1sRight) != all1sRight) {
            score+=10;
        }
        if (doAddition(calculator, all1sLeft, all1sRight) != all1sLeft + all1sRight) {
            score+=10;
        }

        long start = 1;
        for (int i = 0; i < 44; i++) {
            long test = start << i;
            if (doAddition(calculator, test, 0) != test) {
                score++;
            }

            if (doAddition(calculator, 0, test) != test) {
                score++;
            }

            if (doAddition(calculator, test, test) != test + test) {
                score++;
            }

            long nTest = test ^ all1s;
            if (doAddition(calculator, nTest, 0) != nTest) {
                score++;
            }

            if (doAddition(calculator, 0, nTest) != nTest) {
                score++;
            }

            if (doAddition(calculator, nTest, nTest) != nTest + nTest) {
                score++;
            }
        }

        start = 3;
        for (int i = 0; i < 44; i++) {
            long test = start << i;
            long xTest = doAddition(calculator, test, 0);
            if (xTest != test) {
                score++;
            }


            long yTest = doAddition(calculator, 0, test);
            if (yTest != test) {
                score++;
            }

            long xyTest = doAddition(calculator, test, test);
            if (xyTest != test + test) {
                score++;
            }

            long nTest = test ^ all1s;
            long nxTest = doAddition(calculator, nTest, 0);
            if (nxTest != nTest) {
                score++;
            }


            long nyTest = doAddition(calculator, 0, nTest);
            if (nyTest != nTest) {
                score++;
            }

            long nxyTest = doAddition(calculator, nTest, nTest);
            if (nxyTest != nTest + nTest) {
                score++;
            }
        }
        return score;
    }

    private static long doAddition(WireCalculator calculator, long x, long y) {
        return doAddition(calculator, x, y, false);
    }

    private static long doAddition(WireCalculator calculator, long x, long y, boolean print) {
        long result = calculator.calculate(x, y);

        if (print) {
            System.out.println("   " + bToString(x));
            System.out.println(" + " + bToString(y));
            System.out.println(" = " + bToString(result));
            System.out.printf("%d + %d = %d\n", x, y, result);
        }
        return result;
    }
}
