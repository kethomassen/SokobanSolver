package sokoban.solvers;

import sokoban.SokobanMap;
import sokoban.util.GridPosition;

import java.util.HashSet;

public class Heuristics {

    public static int boxesToClosestTarget(SokobanMap map) {

        int sum = 0;
        int closestPlayerDist = Integer.MAX_VALUE;

        // Go through every box
        for (GridPosition box : map.getBoxLocations()) {
            int closestDistance = Integer.MAX_VALUE;

            // Go through every target and find closest
            for (GridPosition target : map.getTargetLocations()) {
                int dist = box.distanceTo(target);

                if (dist < closestDistance) {
                    closestDistance = dist;
                }
            }

            int distToPlayer = box.distanceTo(map.getPlayerLocation());
            if (distToPlayer < closestPlayerDist) {
                closestPlayerDist = distToPlayer;
            }

            sum += closestDistance;
        }

        // - 1 from the end so that heuristic is admissible.
        return sum + closestPlayerDist - 1;
    }

    public static int freeBoxesToClosestFreeTargets(SokobanMap map) {

        int sum = 0;
        int closestPlayerDist = Integer.MAX_VALUE;

        // Go through every box
        HashSet<GridPosition> freeBoxes = new HashSet<>();
        for (GridPosition box : map.getBoxLocations()) {
            if (!map.getTargetLocations().contains(box)) {
                freeBoxes.add(box);
            }
        }

        HashSet<GridPosition> freeTargets = new HashSet<>();
        for (GridPosition target : map.getTargetLocations()) {
            if (!map.getBoxLocations().contains(target)) {
                freeTargets.add(target);
            }
        }

        for (GridPosition box : freeBoxes) {
            int closestDistance = Integer.MAX_VALUE;

            // Go through every target and find closest
            for (GridPosition target : freeTargets) {
                int dist = box.distanceTo(target);

                if (dist < closestDistance) {
                    closestDistance = dist;
                }
            }

            int distToPlayer = box.distanceTo(map.getPlayerLocation());
            if (distToPlayer < closestPlayerDist) {
                closestPlayerDist = distToPlayer;
            }

            sum += closestDistance;
        }

        // - 1 from the end so that heuristic is admissible.
        return sum + closestPlayerDist - 1;
    }
    
}
