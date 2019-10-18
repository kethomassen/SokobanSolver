package sokoban.util;

import java.util.HashMap;

/**
 * @author Kristian Thomassen, Harry Keightley
 */
public enum Tile {
    FREE_SPACE(' '),
    WALL('#'),
    TARGET('T'),
    BOX('B'),
    BOX_ON_TARGET('b'),
    PLAYER('P'),
    PLAYER_ON_TARGET('p');

    private static HashMap<Character, Tile> map = new HashMap<>();

    static {
        for (Tile tile : values()) {
            map.put(tile.getSymbol(), tile);
        }
    }

    private final char symbol;

    private Tile(char symbol) {
        this.symbol = symbol;
    }

    public char getSymbol() {
        return this.symbol;
    }

    public String toString() {
        return Character.toString(getSymbol());
    }

    public static Tile getTileFromSymbol(char symbol) {
        Tile found = map.get(symbol);

        if (found == null) {
            throw new RuntimeException("Unknown symbol in map: " + symbol);
        }

        return found;
    }

    public boolean isBoxType() {
        return this == BOX || this == BOX_ON_TARGET;
    }

    public boolean isPlayerType() {
        return this == PLAYER|| this == PLAYER_ON_TARGET;
    }

    public boolean isTargetType() {
        return this == TARGET || this == PLAYER_ON_TARGET || this == BOX_ON_TARGET;
    }

}
