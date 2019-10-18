package sokoban;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import sokoban.exception.BadMapException;
import sokoban.util.Direction;
import sokoban.util.GridPosition;
import sokoban.util.Tile;

/**
 * @author Kristian Thomassen, Harry Keightley
 */
public class SokobanMap {

    private static final Direction[] HORIZONTAL_DIRS = {Direction.LEFT, Direction.RIGHT};
    private static final Direction[] VERTICAL_DIRS = {Direction.DOWN, Direction.UP};
    private static final Direction[] DIRECTIONS = Direction.values();

    private HashSet<GridPosition> wallLocations = new HashSet<>();
    private HashSet<GridPosition> boxLocations = new HashSet<>();
    private HashSet<GridPosition> targetLocations = new HashSet<>();
    private HashSet<GridPosition> deadlockLocations;
    private GridPosition playerLocation;
    private int xMax;
    private int yMax;

    /**
     * Loads Sokoban map from a String (i.e. loaded from a file).
     * 
     * @param input String version of a Sokoban map
     * @throws BadMapException if map string can't be parsed
     */
    public SokobanMap(String input) throws BadMapException {
        String[] lines = input.split(System.lineSeparator());

        if (lines.length == 0) {
            // do something i guess?
            throw new BadMapException("Map is empty");
        }

        this.xMax = lines[0].length();
        this.yMax = lines.length;


        for (int y = 0; y < lines.length; y++) {
            String line = lines[y];

            if (line.length() != this.xMax) {
                throw new BadMapException("Map has inconsistent line lengths");
            }

            for (int x = 0; x < this.xMax; x++) {
                Tile tile = Tile.getTileFromSymbol(line.charAt(x));

                switch (tile) {
                    case BOX:
                    case BOX_ON_TARGET:
                        boxLocations.add(new GridPosition(x, y));
                        break;
                    case PLAYER:
                    case PLAYER_ON_TARGET:
                        if (playerLocation != null) {
                            throw new BadMapException("Multiple players in map");
                        }
                        playerLocation = new GridPosition(x, y);
                        break;
                    case WALL:
                        wallLocations.add(new GridPosition(x, y));
                }

                if (tile.isTargetType()) {
                    targetLocations.add(new GridPosition(x, y));
                }
            }
        }

        if (targetLocations.size() != boxLocations.size()) {
            throw new BadMapException("Number of targets and boxes is different");
        }
        
        deadlockLocations = getDeadlockedPositions();
    }

    /**
     * Private constructor to initialise a Sokoban map
     * with given member variables as its state.
     * 
     * @param newWalls
     * @param newBoxes
     * @param newTargets
     * @param newPlayer
     * @param xMax
     * @param yMax
     */
    private SokobanMap(HashSet<GridPosition> newWalls,
            HashSet<GridPosition> newBoxes,
            HashSet<GridPosition> newTargets,
            HashSet<GridPosition> newDeadlocks,
            GridPosition newPlayer,
            int xMax,
            int yMax) {

        this.wallLocations = newWalls;
        this.boxLocations = newBoxes;
        this.targetLocations = newTargets;
        this.deadlockLocations = newDeadlocks;
        this.playerLocation = newPlayer;
        this.xMax = xMax;
        this.yMax = yMax;
    }

    /**
     * Moves player in a direction and returns new map that would result in
     * that move (Note that this doesn't affect the internal state of the class).
     *
     * Must check that the player can move in that direction first (see canMove()).
     *
     * @param direction
     * @return New map that represents state if player performed that move
     * @throws IllegalArgumentException if player can't move in given direction
     */
    public SokobanMap movePlayer(Direction direction) {
        GridPosition newPos = playerLocation.move(direction);
        Tile tileAtNewPos = getTile(newPos);

        if (tileAtNewPos == Tile.FREE_SPACE || tileAtNewPos == Tile.TARGET) {
            return new SokobanMap(wallLocations,
                    boxLocations,
                    targetLocations,
                    deadlockLocations,
                    newPos,
                    xMax,
                    yMax
            );
        }

        if (tileAtNewPos.isBoxType()) {
            GridPosition newBoxPos = newPos.move(direction);
            HashSet<GridPosition> newBoxes = new HashSet<>(boxLocations);

            newBoxes.remove(newPos);
            newBoxes.add(newBoxPos);

            return new SokobanMap(wallLocations,
                    newBoxes,
                    targetLocations,
                    deadlockLocations,
                    newPos,
                    xMax,
                    yMax
            );
        }

        throw new IllegalArgumentException("Move wasn't checked before performing");
    }


    /**
     * Checks if the Sokoban map is currently in a deadlocked state and is
     * now unsolvable.
     * 
     * @return true if definitely deadlocked, false otherwise. Note this
     *         checking is not perfect and there will be cases where false is
     *         returned even though the map is in a deadlock.
     */
    public boolean isDeadlocked() {
        
        for (GridPosition box : this.boxLocations) {
            if (deadlockLocations.contains(box))
                return true;
        }

        return false;
    }

    /**
     * Returns true iff the supplied position is a corner.
     *
     * A corner is defined as a non-wall surrounded by at least 2 walls
     * on non-axis aligned sides.
     * e.g.
     * ##
     * #x  x is a corner.
     *
     * @param position The gridposition to check
     * @return True iff the position is a corner.
     */
    private boolean isCorner(GridPosition position) {
        if (wallLocations.contains(position)) {
            return false;
        }
        
        int count = 0;
        for (Direction dir : DIRECTIONS) {
            if (wallLocations.contains(position.move(dir))) {
                count++;
            }
            if (count > 2) {
                return true;
            }
        }
        
        return count == 2 &&
                (wallLocations.contains(position.move(Direction.LEFT))
                ^ wallLocations.contains(position.move(Direction.RIGHT)));

    }

    /**
     * Get all corners in the map.
     *
     * @return The set containing all corners.
     */
    private HashSet<GridPosition> getCorners() {
        HashSet<GridPosition> result = new HashSet<>();
        
        for (int x = 1; x < this.xMax - 1; x++) {
            for (int y = 1; y < this.yMax - 1; y++) {
                GridPosition position = new GridPosition(x, y);
                if (isCorner(position)) {
                    result.add(position);
                }
            }
        }
        
        return result;
    }

    /**
     * Returns true iff the two positions are joined by a wall the whole way.
     * @param a
     * @param b
     * @return
     */
    private boolean isContinuousWall(GridPosition a, GridPosition b) {
        for (GridPosition position : GridPosition.getStretch(a, b)) {
            if (!wallLocations.contains(position)) {
                return false;
            }
        }
        
        return true;
    }

    private boolean stretchContainsTargets(GridPosition a, GridPosition b) {
        for (GridPosition position : GridPosition.getStretch(a, b)) {
            if (targetLocations.contains(position))
                return true;
        }
        return false;
    }

    /**
     * Returns true if after applying an action to the two given directions,
     * both the resulting tiles are wall tiles.
     * @param a
     * @param b
     * @param direction
     * @return
     */
    private boolean shareWallInDirection(GridPosition a, GridPosition b, Direction direction) {
        GridPosition nextA = a.move(direction);
        GridPosition nextB = b.move(direction);
        return wallLocations.contains(nextA) && wallLocations.contains(nextB);
    }

    /**
     * Require that the two positions are either on the same x or y coordinate
     * and a != b.
     *
     * This is the most disgusting function I've ever written.
     * @param a
     * @param b
     * @return
     */
    private boolean joinedByContinuousWall(GridPosition a, GridPosition b) {
        boolean sameX = a.getX() == b.getX();
        Direction[] directions = sameX ? HORIZONTAL_DIRS : VERTICAL_DIRS;

        for (Direction direction : directions) {
            GridPosition nextA = a.move(direction);
            GridPosition nextB = b.move(direction);
            if (shareWallInDirection(a, b, direction) &&
                    isContinuousWall(nextA, nextB) &&
                    !stretchContainsTargets(a, b)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get all positions in the map we know are deadlocks from the get go.
     *
     */
    private HashSet<GridPosition> getDeadlockedPositions() {
        HashSet<GridPosition> result = new HashSet<>();
        HashSet<GridPosition> corners = getCorners();

        // Remove corners that are targets
        for (GridPosition target : targetLocations) {
            corners.remove(target);
        }
        result.addAll(corners);

        // Find corners that share a coordinate.
        HashMap<Integer, List<GridPosition>> xCorners = new HashMap<>();
        HashMap<Integer, List<GridPosition>> yCorners = new HashMap<>();
        for (GridPosition position : corners) {
            int x = position.getX();
            int y = position.getY();

            if (!xCorners.containsKey(x)) {
                xCorners.put(x, new ArrayList<>());
            }
            xCorners.get(x).add(position);

            if (!yCorners.containsKey(y)) {
                yCorners.put(y, new ArrayList<>());
            }
            yCorners.get(y).add(position);
        }

        List<List<GridPosition>> matchesList = new ArrayList<>();
        for (Integer x : xCorners.keySet()) {
            List<GridPosition> matches = xCorners.get(x);
            if (matches.size() < 2)
                continue;
            matches.sort(Comparator.comparingInt(GridPosition::getX));
            matchesList.add(matches);
        }

        // loop all y matches
        for (Integer y : yCorners.keySet()) {
            List<GridPosition> matches = yCorners.get(y);
            if (matches.size() < 2)
                continue;
            matches.sort(Comparator.comparingInt(GridPosition::getY));
            matchesList.add(matches);
        }
        

        for (List<GridPosition> matches : matchesList) {
            for (int i = 0; i < matches.size() - 1; i++) {
                GridPosition start = matches.get(i);
                GridPosition end = matches.get(i + 1);
                if (joinedByContinuousWall(start, end)) {
                    result.addAll(GridPosition.getStretch(start, end));
                }
            }
        }
        // If continuous wall between them and no targets, deadlock.
        return result;
    }
    

    /**
     * Checks if map is in goal state (i.e. all boxes on targets).
     * 
     * @return true if goal state
     */
    public boolean isGoalState() {
        return boxLocations.equals(targetLocations);
    }

    /**
     * Represents Sokoban map as a String
     * 
     * @return String version of map
     */
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (int y = 0; y < yMax; y++) {
            for (int x = 0; x < xMax; x++) {
                GridPosition pos = new GridPosition(x, y);
                
                if (wallLocations.contains(pos)) {
                    builder.append(Tile.WALL);
                    continue;
                }

                boolean isBox = boxLocations.contains(pos);
                boolean isTarget = targetLocations.contains(pos);
                boolean isPlayer = playerLocation.equals(pos);

                if (isTarget && isBox) {
                    builder.append(Tile.BOX_ON_TARGET);
                } else if (isTarget && isPlayer) {
                    builder.append(Tile.PLAYER_ON_TARGET);
                } else if (isBox) {
                    builder.append(Tile.BOX);
                } else if (isPlayer) {
                    builder.append(Tile.PLAYER);
                } else if (isTarget) {
                    builder.append(Tile.TARGET);
                } else {
                    builder.append(Tile.FREE_SPACE);
                }
            }

            builder.append(System.lineSeparator());
        }


        return builder.toString();
    }

    /**
     * Checks if player can move in given direction
     *
     * @param direction
     * @return true if player can move in that direction
     */
    public boolean canMove(Direction direction) {
        GridPosition newPos = playerLocation.move(direction);

        if (newPos.getX() >= xMax || newPos.getY() >= yMax) {
            return false;
        }

        Tile tileAtNewPos = getTile(newPos);

        if (tileAtNewPos == Tile.WALL) {
            return false;
        }

        if (tileAtNewPos == Tile.BOX || tileAtNewPos == Tile.BOX_ON_TARGET) {
            GridPosition newBoxPos = newPos.move(direction);
            Tile newBoxPosTile = getTile(newBoxPos);

            if (newBoxPosTile == Tile.WALL || newBoxPosTile == Tile.BOX
                || newBoxPosTile == Tile.BOX_ON_TARGET) {
                return false;
            }
        }

        return true;
    }

    /**
     * Gets the Tile at given position
     *
     * @param position the position to look at
     * @return Tile at the position
     * @throws IndexOutOfBoundsException if out of bounds
     */
    public Tile getTile(GridPosition position) {
        if (position.getX() >= xMax || position.getY() >= yMax) {
            throw new IndexOutOfBoundsException();
        }

        if (wallLocations.contains(position)) {
            return Tile.WALL;
        }

        boolean isTarget = targetLocations.contains(position);
        boolean isBox = boxLocations.contains(position);
        boolean isPlayer = playerLocation.equals(position);

        if (isTarget) {
            if (isPlayer) {
                return Tile.PLAYER_ON_TARGET;
            } else if (isBox) {
                return Tile.BOX_ON_TARGET;
            } else {
                return Tile.TARGET;
            }
        } else if (isBox) {
            return Tile.BOX;
        } else if (isPlayer) {
            return Tile.PLAYER;
        } else {
            return Tile.FREE_SPACE;
        }
    }

    /**
     * Returns the locations of all the targets in the map.
     * 
     * @return locations of targets in map
     */
    public HashSet<GridPosition> getTargetLocations() {
        return targetLocations;
    }

    /**
     * Returns the locations of all the boxes in the map.
     *
     * @return locations of boxes in map
     */
    public HashSet<GridPosition> getBoxLocations() {
        return boxLocations;
    }

    /**
     * Returns the location of the player in the map.
     *
     * @return locations of player in map
     */
    public GridPosition getPlayerLocation() {
        return this.playerLocation;
    }

    /**
     * Two maps are 
     * 
     * @param o other to check
     * @return true if equal, false otherwise.
     */
    public boolean equals(Object o) {
        if (!(o instanceof SokobanMap)) {
            return false;
        }

        SokobanMap other = (SokobanMap) o;

        // can use == for target and wall as reference stays the same between copies
        return other.playerLocation.equals(this.playerLocation)
                && other.targetLocations == this.targetLocations
                && other.boxLocations.equals(this.boxLocations)
                && other.wallLocations == this.wallLocations;
    }

    public int hashCode() {
        return this.boxLocations.hashCode() * this.playerLocation.hashCode();
    }
}
