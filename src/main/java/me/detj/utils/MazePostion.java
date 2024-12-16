package me.detj.utils;

import lombok.Value;

import java.util.List;

@Value
public class MazePostion {
    Point point;
    int direction;
    int score;
    List<MazePostion> path;
}
