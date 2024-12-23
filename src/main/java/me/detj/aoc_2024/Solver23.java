package me.detj.aoc_2024;

import lombok.Value;
import me.detj.utils.Inputs;
import org.apache.commons.collections4.SetUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Solver23 {
    public static void main(String[] args) {
        var input = Inputs.parseConnectedComputers("input_23.txt");

        long s1 = calculateNumberPossiblyContainingChief(input);
        System.out.printf("Solution Part 1: %d\n", s1);

        String s2 = calculatePassword(input);
        System.out.printf("Solution Part 2: %s\n", s2);
    }

    private static int calculateNumberPossiblyContainingChief(Map<String, Set<String>> network) {
        Set<String> doneComputers = new HashSet<>();

        Set<String> triples = new HashSet<>();

        network.forEach((computer, connections) -> {
            for (String c1 : connections) {
                if (doneComputers.contains(c1)) continue;
                for (String c2 : connections) {
                    if (c1.equals(c2) || doneComputers.contains(c2) || !network.get(c1).contains(c2)
                            || !anyStartWithT(computer, c1, c2)) continue;

                    String tripleString = Stream.of(computer, c1, c2).sorted().collect(Collectors.joining(","));
                    triples.add(tripleString);
                }
            }
            doneComputers.add(computer);
        });

        return triples.size();

    }

    private static boolean anyStartWithT(String... computers) {
        for (String computer : computers) {
            if (computer.startsWith("t")) return true;
        }
        return false;
    }

    private static String calculatePassword(Map<String, Set<String>> network) {

        Set<Network> networks = initialNetworks(network);


        for (int i = 0; i < 1000; i++) {
            if(networks.size() == 1 && networks.iterator().next().allConnectedTo.isEmpty()) {
                return networks.iterator().next().computers.stream().sorted().collect(Collectors.joining(","));
            }

            Set<String> seenNetworks = new HashSet<>();
            Set<Network> nextNetworks = new HashSet<>();

            for (Network nw : networks) {
                for (String toAdd : nw.allConnectedTo) {
                    Network newNetwork = new Network(
                            new HashSet<>(SetUtils.union(nw.computers, Set.of(toAdd))),
                            getAllConnectedTo(network, SetUtils.union(nw.computers, Set.of(toAdd))));
                    if(!seenNetworks.add(newNetwork.computers.toString())) continue;
                    nextNetworks.add(newNetwork);
                }
            }

            networks = nextNetworks;
        }

        return null;
    }

    private static Set<Network> initialNetworks(Map<String, Set<String>> network) {
        Set<String> setsOfSize3 = getSetsOfSize3(network);
        Set<Set<String>> sets = setsOfSize3.stream()
                .map(s -> Set.of(s.split(",")))
                .collect(Collectors.toSet());

        Set<Network> networks = new HashSet<>();

        for (Set<String> set : sets) {
            Set<String> allConnectedTo = getAllConnectedTo(network, set);
            networks.add(new Network(set, allConnectedTo));
        }
        return networks;
    }

    private static Set<String> getAllConnectedTo(Map<String, Set<String>> network, Set<String> subNetwork) {
        Set<String> allConnectedTo = new HashSet<>(network.get(subNetwork.iterator().next()));
        for (String computer : subNetwork) {
            Set<String> connections = network.get(computer);
            allConnectedTo = SetUtils.intersection(allConnectedTo, connections);
        }
        allConnectedTo = new HashSet<>(allConnectedTo); // Copy to hashset so it's not a view
        return allConnectedTo;
    }

    private static Set<String> getSetsOfSize3(Map<String, Set<String>> network) {
        Set<String> doneComputers = new HashSet<>();

        Set<String> triples = new HashSet<>();

        network.forEach((computer, connections) -> {
            for (String c1 : connections) {
                if (doneComputers.contains(c1)) continue;
                for (String c2 : connections) {
                    if (c1.equals(c2) || doneComputers.contains(c2) || !network.get(c1).contains(c2))
                        continue;

                    String tripleString = Stream.of(computer, c1, c2).sorted().collect(Collectors.joining(","));
                    triples.add(tripleString);
                }
            }
            doneComputers.add(computer);
        });

        return triples;
    }

    @Value
    private static class Network {
        Set<String> computers;
        Set<String> allConnectedTo;
    }
}
