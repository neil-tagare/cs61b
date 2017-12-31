import java.util.Observable;
/**
 *  @author Josh Hug
 */

public class MazeCycles extends MazeExplorer {
    /* Inherits public fields:
    public int[] distTo;
    public int[] edgeTo;
    public boolean[] marked;
    */
    private int s;
    private int cycle = -1;
    private static boolean detect;

    public MazeCycles(Maze m) {
        super(m);
        s = maze.xyTo1D(1, 1);
        distTo[s] = 0;
        edgeTo[s] = s;
    }

    @Override
    public void solve() {
        cycle(s);
        for (int i =0; i <maze.V(); i++) {
            marked[i] = false;
        }
        for (int i = edgeTo[cycle]; i != cycle; i = edgeTo[i]) {
            marked[i]=true;
        }
        marked[cycle]=true;
        announce();
    }

    private void cycle(int v) {
        marked[v] = true;

        for (int w : maze.adj(v)) {

            if (!marked[w]) {
                edgeTo[w] = v;
                cycle(w);
                if(detect) {
                    break;
                }
            }

            if (!detect && marked[w] && edgeTo[v] != w) {
                edgeTo[w]=v;
                cycle = w;
                detect = true;
                return;
            }
        }
    }
}

