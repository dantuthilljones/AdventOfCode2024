package me.detj.utils;

import lombok.AllArgsConstructor;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
public class Grid<T> {

    private final List<List<T>> cells;

    public T get(Point point) {
        return get(point.getX(), point.getY());
    }

    public T get(int x, int y) {
        if (!inBounds(x, y)) {
            return null;
        }
        return cells.get(y).get(x);
    }

    public void set(Point point, T value) {
        set(point.getX(), point.getY(), value);
    }

    public void set(int x, int y, T value) {
        if (inBounds(x, y)) {
            cells.get(y).set(x, value);
        }
    }

    public boolean inBounds(Point point) {
        return inBounds(point.getX(), point.getY());
    }

    public boolean inBounds(int x, int y) {
        return x >= 0 && x < getWidth() && y >= 0 && y < getHeight();
    }

    public int getWidth() {
        return cells.get(0).size();
    }

    public int getHeight() {
        return cells.size();
    }

    public boolean pointEquals(Point point, T value) {
        return pointEquals(point.getX(), point.getY(), value);
    }

    public boolean pointEquals(int x, int y, T value) {
        return Objects.equals(get(x, y), value);
    }

    public boolean pointsAreEqual(Point point1, Point point2) {
        return pointEquals(point1, get(point2));
    }

    public int count(T value) {
        int count = 0;
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                if (pointEquals(x, y, value)) {
                    count++;
                }
            }
        }
        return count;
    }

    public MultiValuedMap<T, Point> pointsByValue() {
        MultiValuedMap<T, Point> points = new ArrayListValuedHashMap<>();
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                T value = get(x, y);
                if (value != null) {
                    points.put(value, new Point(x, y));
                }
            }
        }
        return points;
    }

    public List<Point> findAll(T toFind) {
        List<Point> points = new ArrayList<>();
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                if (pointEquals(x, y, toFind)) {
                    points.add(new Point(x, y));
                }
            }
        }
        return points;
    }

    public Point findFirst(T toFind) {
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                if (pointEquals(x, y, toFind)) {
                    return new Point(x, y);
                }
            }
        }
        return null;
    }

    public Grid<T> shallowCopy() {
        List<List<T>> cells = new ArrayList<>(this.cells.size());
        for (int i = 0; i < getHeight(); i++) {
            cells.add(new ArrayList<>(this.cells.get(i)));
        }
        return new Grid<>(cells);
    }
}
