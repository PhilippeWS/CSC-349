import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;


public class TopSorter {
    static ArrayList<Integer> topSort;
    public static ArrayList<Integer> dfsTopSorter(String fileName) {
        Graph graph = new Graph();
        try {
            graph.readfile_graph(fileName);
        }catch (FileNotFoundException e){}


        topSort = new ArrayList<>();
        //Standard dfs, for every unvisited node, call recursive dfs.
        for(Graph.Vertex vertex : graph.vertices) {
            if (!vertex.discovered)
                dfs(vertex);
        }

        //If all nodes are not a cycleComponent, then we have a DAG, thus, construct the topological sort using the postOrder.
            //Otherwise, it is not a DAG, return -1 list.
        if(graph.vertices.stream().noneMatch(v -> v.cycleComponent)){
            Collections.reverse(topSort);
            return topSort;
        }else {
            return new ArrayList<>(Collections.nCopies(graph.nvertices, -1));
        }
    }

    private static void dfs(Graph.Vertex v){
        v.discovered = true;
        Graph.process_vertex_early(v);
        for (Graph.Vertex subVertex : v.edges) {
            if (!subVertex.discovered) {
                dfs(subVertex);
                //Utilizing Graph's classification, checking if the vertex has a back-edge.
                subVertex.cycleComponent = isCycleComponent(subVertex);
            }
        }
        Graph.process_vertex_late(v);
        topSort.add(v.key);
        v.processed = true;
    }

    private static boolean isCycleComponent(Graph.Vertex v){
        //Checks every edge of the vertex. If it is a back-edge, then it is part of a cycle, so return false.
        for (Graph.Vertex u : v.edges)
            if (Graph.edge_classification(v, u) == Graph.BACK)
                return true;
        return false;
    }

    public static ArrayList<Integer> srcRemTopSorter(String fileName){
        Graph graph = new Graph();
        try{
            graph.readfile_graph(fileName);
        }catch (FileNotFoundException e){}

        //Initialize all inDegrees
        for(Graph.Vertex vertex : graph.vertices) {
            for (Graph.Vertex subVertex : vertex.edges) {
                subVertex.inDegree++;
            }
        }

        //Reinitialize topSort
        topSort = new ArrayList<>();


        //Find the first vertex with in degree 0;
        Graph.Vertex source = graph.vertices.stream().filter(v -> v.inDegree == 0).findFirst().orElse(null );

        while(source != null) { //While another source node exists
            //For every edge that the source node contains, remove that edge and decrement the inDegree
            while(source.edges.size() > 0){
                source.edges.get(0).inDegree--;
                graph.remove_edge(source, source.edges.get(0));
            }

            topSort.add(source.key);
            graph.vertices.remove(source);
            //Find next source
            source = graph.vertices.stream().filter(v -> v.inDegree == 0).findFirst().orElse(null);
        }


        //The graph is a DAG only if source removal could remove every vertex, so check that the graph size is 0.
        //Otherwise, return -1 list.
        if (graph.vertices.size() == 0)
            return topSort;
        else
            return new ArrayList<>(Collections.nCopies(graph.nvertices, -1));
    }
}
//GRAPH 3 CLass --------------------------------------------------------------------------------------------------------

class Graph{
    ArrayList<Vertex> vertices = new ArrayList<Vertex>();
    boolean directed;
    int nvertices;
    int nedges;

    public Graph() {
    }

    void readfile_graph(String filename) throws FileNotFoundException {
        int x, y;
        //read the input
        FileInputStream in = new FileInputStream(filename);
        Scanner sc = new Scanner(in);
        int temp = sc.nextInt(); // if 1 directed; 0 undirected
        directed = (temp == 1);
        nvertices = sc.nextInt();
        for (int i = 0; i <= nvertices - 1; i++) {
            Vertex tempv = new Vertex(i + 1);   // kludge to store proper key starting at 1
            vertices.add(tempv);
        }

        nedges = sc.nextInt();   // m is the number of edges in the file
        int nedgesFile = nedges;
        for (int i = 1; i <= nedgesFile; i++) {
            // System.out.println(i + " compare " + (i<=nedges) + " nedges " + nedges);
            x = sc.nextInt();
            y = sc.nextInt();
            //  System.out.println("x  " + x + "  y:  " + y  + " i " + i);
            insert_edge(x, y, directed);
        }
        // order edges to make it easier to see what is going on
        for (int i = 0; i <= nvertices - 1; i++) {
            Collections.sort(vertices.get(i).edges);
        }
    }

    static void process_vertex_early(Vertex v) {
        timer++;
        v.entry_time = timer;
        //     System.out.printf("entered vertex %d at time %d\n",v.key, v.entry_time);
    }

    static void process_vertex_late(Vertex v) {
        timer++;
        v.exit_time = timer;
        //     System.out.printf("exit vertex %d at time %d\n",v.key , v.exit_time);
    }

    static final int TREE = 1, BACK = 2, FORWARD = 3, CROSS = 4;
    static int timer = 0;

    static int edge_classification(Vertex x, Vertex y) {
        if (y.parent == x) return (TREE);
        if (y.discovered && !y.processed) return (BACK);
        if (y.processed && (y.entry_time > x.entry_time)) return (FORWARD);
        if (y.processed && (y.entry_time < x.entry_time)) return (CROSS);
        System.out.printf("Warning: self loop (%d,%d)\n", x, y);
        return -1;
    }

    void insert_edge(int x, int y, boolean directed) {
        Vertex one = vertices.get(x - 1);
        Vertex two = vertices.get(y - 1);
        one.edges.add(two);
        vertices.get(x - 1).degree++;
        if (!directed)
            insert_edge(y, x, true);
        else
            nedges++;
    }

    void remove_edge(Vertex x, Vertex y) {
        if (x.degree < 0)
            System.out.println("Warning: no edge --" + x + ", " + y);
        x.edges.remove(y);
        x.degree--;
    }

    class Vertex implements Comparable<Vertex> {
        int key;
        int degree;
        boolean discovered = false;
        boolean processed = false;
        int entry_time = 0;
        int exit_time = 0;

        //Additional Field----------------------
        int inDegree;
        boolean cycleComponent;

        Vertex parent = null;
        ArrayList<Vertex> edges = new ArrayList<Vertex>();

        public Vertex(int tkey) {
            key = tkey;
        }

        public int compareTo(Vertex other) {
            if (this.key > other.key) {
                return 1;
            } else if (this.key < other.key) {
                return -1;
            } else
                return 0;
        }
    }
}

