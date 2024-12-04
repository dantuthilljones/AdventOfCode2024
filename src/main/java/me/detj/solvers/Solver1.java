package me.detj.solvers;

import me.detj.utils.Inputs;
import me.detj.utils.Utils;

import java.io.IOException;

public class Solver1 {

    public static void main(String[] args) throws IOException {
        var input = Inputs.parseListOfPairs("input_01.txt");
        System.out.printf("Total Distance: %d\n", Utils.calculateDistance(input.getLeft(), input.getRight()));
        System.out.printf("Total Similarity: %d\n", Utils.calculateSimilarity(input.getLeft(), input.getRight()));
    }

}
