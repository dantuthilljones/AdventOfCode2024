package me.detj.solvers;

import me.detj.utils.Inputs;
import me.detj.utils.Utils;

public class Solver09 {
    public static void main(String[] args) {
        var diskMap = Inputs.parseDenseIntList("input_09.txt");

        long checksum = Utils.compactifyAndChecksum(diskMap);
        System.out.printf("Solution Part 1: %d\n", checksum);

        long checksumWithoutFragmentation = Utils.compactifyWithoutFragmentationAndChecksum(diskMap);
        System.out.printf("Solution Part 2: %d\n", checksumWithoutFragmentation);
    }
}
