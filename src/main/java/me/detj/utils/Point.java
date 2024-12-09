package me.detj.utils;

import lombok.Value;

@Value
public class Point {
    int x;
    int y;

    public static Point of(int x, int y) {
        return new Point(x, y);
    }

    public Point moveRight() {
        return moveRight(1);
    }

    public Point moveRight(int amount) {
        return of(x + amount, y);
    }

    public Point moveLeft() {
        return moveLeft(1);
    }

    public Point moveLeft(int amount) {
        return moveRight(-amount);
    }

    public Point moveUp() {
        return moveUp(1);
    }

    public Point moveUp(int amount) {
        return of(x, y + amount);
    }

    public Point moveDown() {
        return moveDown(1);
    }

    public Point moveDown(int amount) {
        return moveUp(-amount);
    }

    public Point moveUpRight() {
        return moveUpRight(1);
    }

    public Point moveUpRight(int amount) {
        return of(x + amount, y + amount);
    }

    public Point moveUpLeft() {
        return moveUpLeft(1);
    }

    public Point moveUpLeft(int amount) {
        return of(x - amount, y + amount);
    }

    public Point moveDownRight() {
        return moveDownRight(1);
    }

    public Point moveDownRight(int amount) {
        return of(x + amount, y - amount);
    }

    public Point moveDownLeft() {
        return moveDownLeft(1);
    }

    public Point moveDownLeft(int amount) {
        return of(x - amount, y - amount);
    }

    public Point move(int x, int y) {
        return of(this.x + x, this.y + y);
    }

    public Point minus(Point other) {
        return of(x - other.getX(), y - other.getY());
    }

    public Point plus(Point other) {
        return of(x + other.getX(), y + other.getY());
    }
}
