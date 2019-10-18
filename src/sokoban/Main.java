package sokoban;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.Collectors;
import sokoban.exception.BadMapException;
import sokoban.solvers.Heuristics;
import sokoban.solvers.InformedSolver;

/**
 * @author Kristian Thomassen, Harry Keightley
 */
public class Main {
    
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Incorrect arguments");
            System.exit(1);
        }

        /* Load file */
        String contents = "";
        try (BufferedReader br = new BufferedReader(new FileReader(args[0]))) {
            contents = br.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            System.out.println("IO Error");
            System.exit(1);
        }

        /* Parse Map */
        SokobanMap map = null;
        try {
            map = new SokobanMap(contents);
        } catch (BadMapException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        System.out.println(map);

        /* Solve map */
        //SokobanSolver solver = new InformedSolver(Heuristics::freeBoxesToClosestFreeTargets);
        SokobanSolver solver = new InformedSolver(Heuristics::boxesToClosestTarget);
        
        SearchSolution solution = solver.solve(map);
        
        System.out.println(solution);
        
        // Write to file
        if (args.length == 2) {
            try (BufferedWriter br = new BufferedWriter(new FileWriter(args[1]))) {
                br.write(solution.toString());
            } catch (IOException e) {
                System.out.println("IO Error when writing to file");
                System.exit(1);
            }
        }
    }

}
