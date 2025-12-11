package me.detj.aoc_2025;

import lombok.Data;
import lombok.Value;
import me.detj.utils.Inputs;
import me.detj.utils.Point;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Solver09Lines {

    public static void main(String[] args) throws IOException {
        String file = "2025/input_09.txt";
        var input = Inputs.parseListOfPoints(file);
        System.out.printf("Solution 1: %d\n", largestRectangle(input));
        long start = System.currentTimeMillis();
        System.out.printf("Solution 2: %d\n", largestRectangle2(input));
        long end = System.currentTimeMillis();
        System.out.printf("Solution 2: runtime: %dms\n", end - start);
    }

    private static long largestRectangle(List<Point> input) {
        long largestArea = 0;

        for (int i = 0; i < input.size() - 1; i++) {
            for (int j = i + 1; j < input.size(); j++) {
                Point p1 = input.get(i);
                Point p2 = input.get(j);

                long x = Math.abs((long) p1.getX() - p2.getX()) + 1;
                long y = Math.abs((long) p1.getY() - p2.getY()) + 1;

                long area = x * y;
                if (area > largestArea) {
                    largestArea = area;
                }
            }
        }
        return largestArea;

    }

    // largest rectangle that does not cross any lines formed by the input points
    // rectangle can touch the lines, but not cross them
    private static long largestRectangle2(List<Point> input) {
        List<Line> lines = getLines(input);
        boolean insideDirection = findInsideDirection(new ArrayList<>(lines));
        for (Line line : lines) {
            line.insideDirection = insideDirection;
        }


        long largestArea = 0;
        for (int i = 0; i < input.size() - 1; i++) {
            for (int j = i + 1; j < input.size(); j++) {
                Point p1 = input.get(i);
                Point p2 = input.get(j);

                Rectangle rectangle = Rectangle.fromPoints(p1, p2);
                if (!rectIsInternal(lines, rectangle)) {
                    continue;
                }


                long area = rectangle.getArea();
                if (area > largestArea) {
                    largestArea = area;
                }
            }
        }


        return largestArea;
    }

    private static List<Line> getLines(List<Point> points) {
        ArrayList<Line> lines = new ArrayList<>(points.size());
        for (int i = 0; i < points.size(); i++) {
            Point p1 = points.get(i);
            Point p2 = points.get((i + 1) % points.size());
            lines.add(new Line(p1, p2));
        }
        return lines;
    }

    // check first 100 lines, if any of these lines have a right angled line which hits the outside of the shape
    // without crossing another line, then the shape is oriented inside left
    private static boolean findInsideDirection(ArrayList<Line> lines) {
        // check first 100 lines, if any of these lines have a right angled line which hits the outside of the shape
        // without crossing another line, then the shape is oriented inside left
        for (int i = 0; i < Math.min(100, lines.size()); i++) {
            Line line = lines.get(i);
            Line lineRightToEdge = line.clockwise90Degrees();

            ArrayList<Line> linesForCheck = new ArrayList<>(lines);
            linesForCheck.remove(line);

            if (!lineCrossesAny(lineRightToEdge, linesForCheck)) {
                return INSIDE_LEFT;
            }
        }
        return INSIDE_RIGHT;
    }

    private static boolean lineCrossesAny(Line line, List<Line> lines) {
        for (Line other : lines) {
            if (line.linesTouch(other)) {
                return true;
            }
        }
        return false;
    }

    private static boolean rectIsInternal(List<Line> lines, Rectangle rect) {
        for (Line line : lines) {
            if (rectCrossesLineOnOutside(line, rect)) {
                return false;
            }
        }
        return true;
    }

    private static boolean rectCrossesLineOnOutside(Line line, Rectangle rect) {
        Line lineToCheck = line.insideDirection != INSIDE_LEFT ? line.shiftRight() : line.shiftLeft();
        lineToCheck = lineToCheck.shrink();

        return rect.lineIntersects(lineToCheck);
    }

    private static boolean INSIDE_LEFT = true;
    private static boolean INSIDE_RIGHT = false;

    @Data
    private static class Line {
        Point start;
        Point end;

        boolean insideDirection = false;

        public Line(Point start, Point end) {
            this.start = start;
            this.end = end;
        }

        boolean isVertical() {
            return start.getX() == end.getX();
        }

        boolean isHorizontal() {
            return start.getY() == end.getY();
        }

        boolean isUpward() {
            return end.getY() > start.getY();
        }

        boolean isDownward() {
            return end.getY() < start.getY();
        }

        boolean isRightward() {
            return end.getX() > start.getX();
        }

        boolean isLeftward() {
            return end.getX() < start.getX();
        }

        Line shrink() {
            Point newStart = start;
            Point newEnd = end;
            if (isRightward()) {
                newStart = new Point(start.getX() + 1, start.getY());
                newEnd = new Point(end.getX() - 1, end.getY());
            } else if (isLeftward()) {
                newStart = new Point(start.getX() - 1, start.getY());
                newEnd = new Point(end.getX() + 1, end.getY());
            } else if (isUpward()) {
                newStart = new Point(start.getX(), start.getY() + 1);
                newEnd = new Point(end.getX(), end.getY() - 1);
            } else { // downward
                newStart = new Point(start.getX(), start.getY() - 1);
                newEnd = new Point(end.getX(), end.getY() + 1);
            }
            return new Line(newStart, newEnd);
        }

        boolean linesTouch(Line other) {
            // Standard line segment intersection check for orthogonal lines.
            int x1 = this.start.getX(), y1 = this.start.getY();
            int x2 = this.end.getX(), y2 = this.end.getY();
            int x3 = other.start.getX(), y3 = other.start.getY();
            int x4 = other.end.getX(), y4 = other.end.getY();

            // Ensure ranges are ordered
            if (x1 > x2) {
                int temp = x1;
                x1 = x2;
                x2 = temp;
            }
            if (y1 > y2) {
                int temp = y1;
                y1 = y2;
                y2 = temp;
            }
            if (x3 > x4) {
                int temp = x3;
                x3 = x4;
                x4 = temp;
            }
            if (y3 > y4) {
                int temp = y3;
                y3 = y4;
                y4 = temp;
            }

            // Check for bounding box overlap first
            if (x2 < x3 || x1 > x4 || y2 < y3 || y1 > y4) {
                return false;
            }

            // If both are vertical and at the same X
            if (this.isVertical() && other.isVertical() && x1 == x3) {
                return y1 <= y4 && y3 <= y2; // Check for Y overlap
            }
            // If both are horizontal and at the same Y
            if (this.isHorizontal() && other.isHorizontal() && y1 == y3) {
                return x1 <= x4 && x3 <= x2; // Check for X overlap
            }
            // If one is vertical and one is horizontal
            if (this.isVertical() && other.isHorizontal()) {
                return x1 >= x3 && x1 <= x4 && y3 >= y1 && y3 <= y2;
            }
            if (this.isHorizontal() && other.isVertical()) {
                return x3 >= x1 && x3 <= x2 && y1 >= y3 && y1 <= y4;
            }

            return false; // Should not happen with orthogonal lines if logic is correct
        }

        // line 90 degrees from this line, to the edge of the coordinate system
        // starting at the start of this line
        Line clockwise90Degrees() {
            if (isRightward()) {
                return new Line(start, new Point(start.getX(), 0));
            } else if (isLeftward()) {
                return new Line(start, new Point(start.getX(), Integer.MAX_VALUE));
            } else if (isUpward()) {
                return new Line(start, new Point(Integer.MAX_VALUE, start.getY()));
            } else { // downward
                return new Line(start, new Point(0, start.getY()));
            }
        }

        Line shiftRight() {
            if (isRightward()) {
                return new Line(new Point(start.getX(), start.getY() - 1), new Point(end.getX(), end.getY() - 1));
            } else if (isLeftward()) {
                return new Line(new Point(start.getX(), start.getY() + 1), new Point(end.getX(), end.getY() + 1));
            } else if (isUpward()) {
                return new Line(new Point(start.getX() + 1, start.getY()), new Point(end.getX() + 1, end.getY()));
            } else { // downward
                return new Line(new Point(start.getX() - 1, start.getY()), new Point(end.getX() - 1, end.getY()));
            }
        }

        Line shiftLeft() {
            if (isRightward()) {
                return new Line(new Point(start.getX(), start.getY() + 1), new Point(end.getX(), end.getY() + 1));
            } else if (isLeftward()) {
                return new Line(new Point(start.getX(), start.getY() - 1), new Point(end.getX(), end.getY() - 1));
            } else if (isUpward()) {
                return new Line(new Point(start.getX() - 1, start.getY()), new Point(end.getX() - 1, end.getY()));
            } else { // downward
                return new Line(new Point(start.getX() + 1, start.getY()), new Point(end.getX() + 1, end.getY()));
            }
        }
    }

    @Value
    private static class Rectangle {
        Point bottomLeft;
        Point topRight;

        private static Rectangle fromPoints(Point p1, Point p2) {
            Point bottomLeft = new Point(Math.min(p1.getX(), p2.getX()), Math.min(p1.getY(), p2.getY()));
            Point topRight = new Point(Math.max(p1.getX(), p2.getX()), Math.max(p1.getY(), p2.getY()));
            return new Rectangle(bottomLeft, topRight);
        }

        long getArea() {
            long width = (long) topRight.getX() - (long) bottomLeft.getX() + 1;
            long height = (long) topRight.getY() - (long) bottomLeft.getY() + 1;
            return width * height;
        }

        public boolean lineIntersects(Line lineToCheck) {
            int rectMinX = bottomLeft.getX();
            int rectMaxX = topRight.getX();
            int rectMinY = bottomLeft.getY();
            int rectMaxY = topRight.getY();

            int lineMinX = Math.min(lineToCheck.start.getX(), lineToCheck.end.getX());
            int lineMaxX = Math.max(lineToCheck.start.getX(), lineToCheck.end.getX());
            int lineMinY = Math.min(lineToCheck.start.getY(), lineToCheck.end.getY());
            int lineMaxY = Math.max(lineToCheck.start.getY(), lineToCheck.end.getY());

            boolean xOverlap = rectMinX <= lineMaxX && rectMaxX >= lineMinX;
            boolean yOverlap = rectMinY <= lineMaxY && rectMaxY >= lineMinY;

            return xOverlap && yOverlap;
        }
    }
}
