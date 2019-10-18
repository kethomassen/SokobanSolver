package sokoban.util;

/**
 * @author Kristian Thomassen, Harry Keightley
 */
public enum Direction {
    UP('u'),
    DOWN('d'),
    LEFT('l'),
    RIGHT('r');

    private char rep;

    private Direction(char rep) {
        this.rep = rep;
    }

    public char toChar() {
        return this.rep;
    }

    @Override
    public String toString() {
        return Character.toString(toChar());
    }
}
