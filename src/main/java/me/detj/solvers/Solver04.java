package me.detj.solvers;

import me.detj.utils.Inputs;
import me.detj.utils.Utils;

import java.util.List;

public class Solver04 {
    public static void main(String[] args) {
        var wordSearch = Inputs.parseCharMatrix("input_04.txt");
        int xMasses = Utils.searchForWord(wordSearch, List.of('X', 'M', 'A', 'S'));
        System.out.printf("XMAS Occurrences: %d\n", xMasses);

        int xShapedMasses = Utils.searchForX(wordSearch, List.of('M', 'A', 'S'));
        System.out.printf("X Shaped MAS Occurrences: %d\n", xShapedMasses);
    }
}
