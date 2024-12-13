package me.detj.utils;

import lombok.Value;

import java.util.List;
import java.util.function.Function;

@Value
public class LPoint {


    public static final List<Function<LPoint, LPoint>> ALL_DIRECTIONS = List.of(
            LPoint::moveRight,
            LPoint::moveDownRight,
            LPoint::moveDown,
            LPoint::moveDownLeft,
            LPoint::moveLeft,
            LPoint::moveUpLeft,
            LPoint::moveUp,
            LPoint::moveUpRight
    );

    public static final List<Function<LPoint, LPoint>> BASIC_DIRECTIONS = List.of(
            LPoint::moveUp,
            LPoint::moveRight,
            LPoint::moveDown,
            LPoint::moveLeft
    );

    long x;
    long y;

    public static LPoint of(long x, long y) {
        return new LPoint(x, y);
    }

    public LPoint moveRight() {
        return moveRight(1);
    }

    public LPoint moveRight(long amount) {
        return of(x + amount, y);
    }

    public LPoint moveLeft() {
        return moveLeft(1);
    }

    public LPoint moveLeft(long amount) {
        return moveRight(-amount);
    }

    public LPoint moveUp() {
        return moveUp(1);
    }

    public LPoint moveUp(long amount) {
        return of(x, y + amount);
    }

    public LPoint moveDown() {
        return moveDown(1);
    }

    public LPoint moveDown(long amount) {
        return moveUp(-amount);
    }

    public LPoint moveUpRight() {
        return moveUpRight(1);
    }

    public LPoint moveUpRight(long amount) {
        return of(x + amount, y + amount);
    }

    public LPoint moveUpLeft() {
        return moveUpLeft(1);
    }

    public LPoint moveUpLeft(long amount) {
        return of(x - amount, y + amount);
    }

    public LPoint moveDownRight() {
        return moveDownRight(1);
    }

    public LPoint moveDownRight(long amount) {
        return of(x + amount, y - amount);
    }

    public LPoint moveDownLeft() {
        return moveDownLeft(1);
    }

    public LPoint moveDownLeft(long amount) {
        return of(x - amount, y - amount);
    }

    public LPoint move(long x, long y) {
        return of(this.x + x, this.y + y);
    }

    public LPoint minus(LPoint other) {
        return of(x - other.getX(), y - other.getY());
    }

    public LPoint plus(LPoint other) {
        return of(x + other.getX(), y + other.getY());
    }

    public LPoint times(long a) {
        return of(x * a, y * a);
    }

    public static LPoint from(Point point) {
        return new LPoint(point.getX(), point.getY());
    }
}
