import java.util.ArrayDeque;
import java.util.Observable;
import java.util.Queue;

/**
 *  @author Josh Hug
 */

public class MazeBreadthFirstPaths extends MazeExplorer {
    /* Inherits public fields:
    public int[] distTo;
    public int[] edgeTo;
    public boolean[] marked;
    */
    private int s;
    private int t;
    private boolean targetFound = false;
    private Maze maze;
    private Queue<Integer> bq = new ArrayDeque<Integer>();

    public MazeBreadthFirstPaths(Maze m, int sourceX, int sourceY, int targetX, int targetY) {
        super(m);
        maze = m;
        s = maze.xyTo1D(sourceX, sourceY);
        t = maze.xyTo1D(targetX, targetY);
        distTo[s] = 0;
        edgeTo[s] = s;
    }

    /** Conducts a breadth first search of the maze starting at the source. */
    private void bfs() {
        while (!bq.isEmpty()) {
            int v = bq.poll();
            marked[v] = true;
            announce();

            if (v == t) {
                targetFound = true;
                return;
            }

            for (int w : maze.adj(v)) {
                if (!marked[w]) {
                    bq.add(w);
                    edgeTo[w] = v;
                    announce();
                    distTo[w] = distTo[v] + 1;
                }
            }
        }
    }


    @Override
    public void solve() {
        bq.add(s);
        bfs();
    }
}

