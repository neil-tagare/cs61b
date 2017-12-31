import java.util.*;
public class Graph {

    private LinkedList<Edge>[] adjLists;
    private int vertexCount;
    private int length;

    // Initialize a graph with the given number of vertices and no edges.
    @SuppressWarnings("unchecked")
    public Graph(int numVertices) {
        adjLists = (LinkedList<Edge>[]) new LinkedList[numVertices];
        for (int k = 0; k < numVertices; k++) {
            adjLists[k] = new LinkedList<Edge>();
        }
        vertexCount = numVertices;
    }

    // Add to the graph a directed edge from vertex v1 to vertex v2,
    // with the given edge information. If the edge already exists,
    // replaces the current edge with a new edge with edgeInfo.
    public void addEdge(int v1, int v2, int edgeWeight) {
        if (!isAdjacent(v1, v2)) {
            LinkedList<Edge> v1Neighbors = adjLists[v1];
            v1Neighbors.add(new Edge(v1, v2, edgeWeight));
        } else {
            LinkedList<Edge> v1Neighbors = adjLists[v1];
            for (Edge e : v1Neighbors) {
                if (e.to() == v2) {
                    e.edgeWeight = edgeWeight;
                }
            }
        }
    }

    // Add to the graph an undirected edge from vertex v1 to vertex v2,
    // with the given edge information. If the edge already exists,
    // replaces the current edge with a new edge with edgeInfo.
    public void addUndirectedEdge(int v1, int v2, int edgeWeight) {
        addEdge(v1, v2, edgeWeight);
        addEdge(v2, v1, edgeWeight);
    }

    // Return true if there is an edge from vertex "from" to vertex "to";
    // return false otherwise.
    public boolean isAdjacent(int from, int to) {
        for (Edge e : adjLists[from]) {
            if (e.to() == to) {
                return true;
            }
        }
        return false;
    }

    // Returns a list of all the neighboring  vertices 'u'
    // such that the edge (VERTEX, 'u') exists in this graph.
    public List<Integer> neighbors(int vertex) {
        ArrayList<Integer> neighbors = new ArrayList<>();
        for (Edge e : adjLists[vertex]) {
            neighbors.add(e.to());
        }
        return neighbors;
    }

    // Runs Dijkstra's algorithm starting from vertex 'startVertex' and returns
    // an integer array consisting of the shortest distances from 'startVertex'
    // to all other vertices.
    public int[] dijkstras(int startVertex) {
        int[] dist = new int[vertexCount];
        int[] back = new int[vertexCount];
        PriorityQueue<Integer> fringe = new PriorityQueue<Integer>(vertexCount, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                if (dist[o1] <= dist[o2]) {
                    return o1;
                }
                return o2;
            }
        });

        for (int k = 0; k < vertexCount; k++) {
            dist[k] = Integer.MAX_VALUE;
            fringe.add(k);
        }
        dist[startVertex]=0;

        while (!fringe.isEmpty()) {
            length = dist.length - fringe.size();
            int v = fringe.poll();
            for (int vertex : neighbors(v)) {
                if (dist[v] + getEdge(v, vertex).edgeWeight < dist[vertex]) {
                    dist[vertex] = dist[v] + getEdge(v, vertex).edgeWeight;
                    back[vertex] = v;
                    fringe.add(vertex);
                }
            }
        }
        return dist;
    }

    // Returns the Edge object corresponding to the listed vertices, v1 and v2.
    // You may find this helpful to implement!
    private Edge getEdge(int v1, int v2) {
        LinkedList<Edge> v1Neighbors = adjLists[v1];
        for (Edge e : v1Neighbors) {
            if (e.to() == v2) {
                return e;
            }
        }
        return null;
    }

    private class Edge {

        private int from;
        private int to;
        private int edgeWeight;

        public Edge(int from, int to, int weight) {
            this.from = from;
            this.to = to;
            this.edgeWeight = weight;
        }

        public int to() {
            return to;
        }

        public int info() {
            return edgeWeight;
        }

        public String toString() {
            return "(" + from + "," + to + ",dist=" + edgeWeight + ")";
        }

    }

    public static void main(String[] args) {
        // Put some tests here!

        Graph g1 = new Graph(5);
        g1.addEdge(0, 1, 1);
        g1.addEdge(0, 2, 1);
        g1.addEdge(0, 4, 1);
        g1.addEdge(1, 2, 1);
        g1.addEdge(2, 0, 1);
        g1.addEdge(2, 3, 1);
        g1.addEdge(4, 3, 1);

        Graph g2 = new Graph(5);
        g2.addEdge(0, 1, 1);
        g2.addEdge(0, 2, 1);
        g2.addEdge(0, 4, 1);
        g2.addEdge(1, 2, 1);
        g2.addEdge(2, 3, 1);
        g2.addEdge(4, 3, 1);

        int[] dist1 = g1.dijkstras(0);
        System.out.println(Arrays.toString(dist1));

        int[] dist2 = g2.dijkstras(0);
        System.out.println(Arrays.toString(dist2));

        Graph g3 = new Graph(8);
        g3.addEdge(0, 1, 2);
        g3.addEdge(0, 2, 5);
        g3.addEdge(0, 3, 3);
        g3.addEdge(0, 6, 7);
        g3.addEdge(1, 2, 4);
        g3.addEdge(1, 3, 5);
        g3.addEdge(3, 6, 3);
        g3.addEdge(2, 5, 2);
        g3.addEdge(2, 4, 2);
        g3.addEdge(1, 4, 3);
        g3.addEdge(3, 4, 4);
        g3.addEdge(3, 7, 6);
        g3.addEdge(4, 5, 1);
        g3.addEdge(4, 7, 2);
        g3.addEdge(6, 7, 1);


        g3.addEdge(1, 0, 2);
        g3.addEdge(2, 0, 5);
        g3.addEdge(3, 0, 3);
        g3.addEdge(6, 0, 7);
        g3.addEdge(2, 1, 4);
        g3.addEdge(3, 1, 5);
        g3.addEdge(6, 3, 3);
        g3.addEdge(5, 2, 2);
        g3.addEdge(4, 2, 2);
        g3.addEdge(4, 1, 3);
        g3.addEdge(4, 3, 4);
        g3.addEdge(7, 3, 6);
        g3.addEdge(7, 4, 2);
        g3.addEdge(5, 4, 1);
        g3.addEdge(7, 6, 1);

        int[] dist3 = g3.dijkstras(0);
        System.out.println(Arrays.toString(dist3));

    }
}
