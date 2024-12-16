package me.detj.utils;

import lombok.Value;

import java.util.List;
import java.util.Set;

@Value
public class MazeResult {
    List<List<MazePostion>> paths;
    Set<Point> pointsInPaths;
    int cost;
}
