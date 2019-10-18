import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import sokoban.SearchSolution;
import sokoban.SokobanMap;
import sokoban.SokobanSolver;
import sokoban.exception.BadMapException;
import sokoban.solvers.BlindSolver;
import sokoban.solvers.Heuristics;
import sokoban.solvers.InformedSolver;

@RunWith(Parameterized.class)
public class SokobanTest {
    private static final int TIME_LIMIT_PER_BOX = 30;
    
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { "1box_m1.txt", TIME_LIMIT_PER_BOX },
                { "1box_m2.txt", TIME_LIMIT_PER_BOX },
                { "1box_m3.txt", TIME_LIMIT_PER_BOX },
                { "2box_m1.txt", TIME_LIMIT_PER_BOX * 2 },
                { "2box_m2.txt", TIME_LIMIT_PER_BOX * 2 },
                { "2box_m3.txt", TIME_LIMIT_PER_BOX * 2 },
                { "3box_m1.txt", TIME_LIMIT_PER_BOX * 3 },
                { "3box_m2.txt", TIME_LIMIT_PER_BOX * 3 },
                { "4box_m1.txt", TIME_LIMIT_PER_BOX * 4 },
                { "4box_m2.txt", TIME_LIMIT_PER_BOX * 4 },
                { "4box_m3.txt", TIME_LIMIT_PER_BOX * 4 },
                { "6box_m2.txt", TIME_LIMIT_PER_BOX * 6 },
        });
    }
    
    @Parameter 
    public String filename;
    
    @Parameter(1) 
    public int timeLimit;
    
    @Test(timeout=210000)
    public void test() {
        /* Load file */
        String contents = "";
        try (BufferedReader br = new BufferedReader(new FileReader("testcases/" + filename))) {
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
        
        //SokobanSolver solver2 = new BlindSolver();
        
        SokobanSolver solver2 = new InformedSolver(Heuristics::boxesToClosestTarget);
        //SokobanSolver solver2 = new InformedSolver(Heuristics::freeBoxesToClosestFreeTargets);
        
        //solver = new InformedSolver(Heuristics::euclideanDistance);

        //SearchSolution solution = solver1.solve(map);
        SearchSolution solution2 = solver2.solve(map);

        //System.out.println(solution);
        System.out.println(solution2);
        
        assert(solution2.getTimeTaken() <= (timeLimit * 1000));
    }

}
