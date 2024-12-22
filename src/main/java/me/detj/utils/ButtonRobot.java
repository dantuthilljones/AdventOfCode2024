package me.detj.utils;

import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@AllArgsConstructor
public class ButtonRobot {

    private static final boolean X_AXIS = true;
    private static final boolean Y_AXIS = false;

    private Point position;
    private Point emptyButton;
    Map<Character, Point> buttons;

    public Set<String> possibleSequencesToEnter(String sequence) {
        Set<String> sequences = null;
        for (char c : sequence.toCharArray()) {
            Set<String> pathsToButton = goToButton(c);
            if (sequences == null) {
                sequences = pathsToButton;
            } else {
                Set<String> newSequences = new HashSet<>();
                for (String path : sequences) {
                    for (String buttonPath : pathsToButton) {
                        newSequences.add(path + buttonPath);
                    }
                }
                sequences = newSequences;
            }
        }
        return sequences;
    }

    private Set<String> goToButton(Character button) {
        Point buttonPosition = buttons.get(button);
        Set<String> sequencesToButton = new HashSet<>();
        xFirstPath(position, buttonPosition, emptyButton).ifPresent(seq -> sequencesToButton.add(seq + "A"));
        yFirstPath(position, buttonPosition, emptyButton).ifPresent(seq -> sequencesToButton.add(seq + "A"));
        this.position = buttonPosition;
        return sequencesToButton;
//        List<String> sequences = new ArrayList<>();
//        for (String sequenceToButton : sequencesToButton) {
//            String sequence = sequenceToButton + "A";
//            xFirstPath(buttonPosition, position, emptyButton).ifPresent(seq -> sequences.add(sequence + seq));
//            yFirstPath(buttonPosition, position, emptyButton).ifPresent(seq -> sequences.add(sequence + seq));
//        }
//        return sequences;
    }

    private static Optional<String> xFirstPath(Point start, Point target, Point avoid) {
        // The avoid point is always on the corner of the path so
        // we can check if the corner is the avoid point
        Point corner = new Point(target.getX(), start.getY());
        if (corner.equals(avoid)) {
            return Optional.empty();
        }

        StringBuilder xFirst = new StringBuilder();
        int dx = target.getX() - start.getX();
        int dy = target.getY() - start.getY();
        addPresses(xFirst, dx, X_AXIS);
        addPresses(xFirst, dy, Y_AXIS);
        return Optional.of(xFirst.toString());
    }

    private static Optional<String> yFirstPath(Point start, Point target, Point avoid) {
        // The avoid point is always on the corner of the path so
        // we can check if the corner is the avoid point
        Point corner = new Point(start.getX(), target.getY());
        if (corner.equals(avoid)) {
            return Optional.empty();
        }

        StringBuilder yFirst = new StringBuilder();
        int dx = target.getX() - start.getX();
        int dy = target.getY() - start.getY();
        addPresses(yFirst, dy, Y_AXIS);
        addPresses(yFirst, dx, X_AXIS);
        return Optional.of(yFirst.toString());
    }

    public static void addPresses(StringBuilder sequence, int dx, boolean dimension) {
        char c = getChar(dx, dimension);
        for (int i = 0; i < Math.abs(dx); i++) {
            sequence.append(c);
        }
    }

    private static char getChar(int magnitude, boolean dimension) {
        if (magnitude > 0) {
            return dimension == X_AXIS ? '>' : '^';
        } else {
            return dimension == X_AXIS ? '<' : 'v';
        }
    }

    public static ButtonRobot keyPadRobot() {
        Point empty = new Point(0, 0);
        Point a = new Point(2, 0);

        Map<Character, Point> buttons = new HashMap<>();
        buttons.put('A', a);
        buttons.put('0', new Point(1, 0));
        buttons.put('1', new Point(0, 1));
        buttons.put('2', new Point(1, 1));
        buttons.put('3', new Point(2, 1));
        buttons.put('4', new Point(0, 2));
        buttons.put('5', new Point(1, 2));
        buttons.put('6', new Point(2, 2));
        buttons.put('7', new Point(0, 3));
        buttons.put('8', new Point(1, 3));
        buttons.put('9', new Point(2, 3));

        return new ButtonRobot(a, empty, buttons);
    }

    public static ButtonRobot directionalRobot() {
        Point empty = new Point(0, 1);
        Point a = new Point(2, 1);

        Map<Character, Point> buttons = new HashMap<>();
        buttons.put('A', a);
        buttons.put('<', new Point(0, 0));
        buttons.put('v', new Point(1, 0));
        buttons.put('>', new Point(2, 0));
        buttons.put('^', new Point(1, 1));
        return new ButtonRobot(a, empty, buttons);
    }
}
