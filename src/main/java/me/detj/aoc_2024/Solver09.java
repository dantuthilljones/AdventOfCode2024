package me.detj.aoc_2024;

import me.detj.utils.Inputs;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.swap;

public class Solver09 {
    public static void main(String[] args) {
        var diskMap = Inputs.parseDenseIntList("input_09.txt");

        long checksum = compactifyAndChecksum(diskMap);
        System.out.printf("Solution Part 1: %d\n", checksum);

        long checksumWithoutFragmentation = compactifyWithoutFragmentationAndChecksum(diskMap);
        System.out.printf("Solution Part 2: %d\n", checksumWithoutFragmentation);
    }

    private static long compactifyAndChecksum(List<Integer> diskMap) {
        List<Integer> disk = calculateDiskLayout(diskMap);
        disk = compactifyDisk(disk);
        return calculateChecksum(disk);
    }

    private static List<Integer> calculateDiskLayout(List<Integer> diskMap) {
        List<Integer> disk = new ArrayList<>();
        for (int i = 0; i < diskMap.size(); i++) {

            // if i is even, it is a file, if i is odd, it is free space
            int value = i % 2 == 0 ? i / 2 : -1;

            for (int j = 0; j < diskMap.get(i); j++) {
                disk.add(value);
            }
        }
        return disk;
    }

    private static List<Integer> compactifyDisk(List<Integer> disk) {
        disk = new ArrayList<>(disk);
        int left = 0;
        int right = disk.size() - 1;

        while (left < right) {
            if (disk.get(right) != -1) {
                while (disk.get(left) != -1 && left < right) {
                    left++;
                }
                swap(disk, left, right);
            }
            right--;
        }
        return disk;
    }

    private static long calculateChecksum(List<Integer> disk) {
        long sum = 0;
        for (int i = 0; i < disk.size(); i++) {
            long value = disk.get(i);
            sum += i * (value != -1 ? value : 0);
        }
        return sum;
    }

    public static long compactifyWithoutFragmentationAndChecksum(List<Integer> diskMap) {
        List<Integer> disk = calculateDiskLayout(diskMap);
        disk = compactifyDiskWithoutFragmentation(disk);
        return calculateChecksum(disk);
    }


    private static List<Integer> compactifyDiskWithoutFragmentation(List<Integer> disk) {
        disk = new ArrayList<>(disk);

        int i = disk.size() - 1;
        while (i >= 0) {
            if (disk.get(i) == -1) {
                i--;
                continue;
            }

            int startOfBlock = getStartOfBlock(disk, i);
            int sizeOfBlock = i - startOfBlock + 1;

            // find block that fits
            int newBlockStartIndex = findFreeBlock(disk, startOfBlock, sizeOfBlock);
            if (startOfBlock != -1 && newBlockStartIndex != -1) {
                swapBlock(disk, startOfBlock, newBlockStartIndex, sizeOfBlock);
            }
            i = startOfBlock - 1;
        }
        return disk;
    }

    private static void swapBlock(List<Integer> disk, int startOfBlock, int newBlockStartIndex, int sizeOfBlock) {
        for (int i = 0; i < sizeOfBlock; i++) {
            swap(disk, startOfBlock + i, newBlockStartIndex + i);
        }
    }

    private static int findFreeBlock(List<Integer> disk, int maxIndex, int sizeOfBlock) {
        for (int i = 0; i < maxIndex; i++) {
            if (disk.get(i) != -1) {
                continue;
            }
            if (isBlockFree(disk, i, sizeOfBlock)) {
                return i;
            } else {
                i++;
            }
        }
        return -1;
    }

    private static boolean isBlockFree(List<Integer> disk, int blockStartIndex, int sizeOfBlock) {
        for (int j = blockStartIndex; j < blockStartIndex + sizeOfBlock; j++) {
            if (disk.get(j) != -1) {
                return false;
            }
        }
        return true;
    }

    private static int getStartOfBlock(List<Integer> disk, int i) {
        int j;
        for (j = i; j >= 0; j--) {
            if (j == 0 || !disk.get(j - 1).equals(disk.get(i))) {
                break;
            }
        }
        return j;
    }
}
