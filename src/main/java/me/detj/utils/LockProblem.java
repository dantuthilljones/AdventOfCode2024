package me.detj.utils;

import lombok.Value;

import java.util.List;

@Value
public class LockProblem {
    List<List<Integer>> keys;
    List<List<Integer>> locks;
}
