/*
 * Study example based on DesignPatterns.pdf, slides 94–100.
 * This demonstrates method flow using printed actions. Actual graph loading,
 * BFS/DFS path searching, and result collection are placeholders.
 * Run: java TemplateMethodExample.java
 */
public class TemplateMethodExample {
    public static void main(String[] args) {
        GraphPathFinder bfs = new BFSPathFinder();
        bfs.findPaths(1, 100);

        GraphPathFinder dfs = new DFSPathFinder();
        dfs.findPaths(1, 100);
    }
}

// BASE CLASS: provides the shared algorithm outline and common steps.
abstract class GraphPathFinder {
    protected int source;
    protected int destination;

    // TEMPLATE METHOD: subclasses inherit this sequence and cannot override it.
    public final void findPaths(int source, int destination) {
        this.source = source;
        this.destination = destination;

        initialize();
        traverseGraph(); // Runtime polymorphism selects the subclass's implementation.
        collectResults();
    }

    protected void initialize() {
        // In a full implementation, read the file and build the graph here.
        System.out.println("Initializing graph");
    }

    // REQUIRED STEP: every concrete subclass must supply its traversal behavior.
    protected abstract void traverseGraph();

    protected void collectResults() {
        // In a full implementation, print the discovered paths here.
        System.out.println("Collecting results");
    }
}

// CUSTOM VARIANT: supplies the traversal step and inherits the rest.
class BFSPathFinder extends GraphPathFinder {
    @Override
    protected void traverseGraph() {
        // The actual BFS-based path-search algorithm would go here.
        System.out.println("BFS traversal from " + source + " to " + destination);
    }
}

class DFSPathFinder extends GraphPathFinder {
    @Override
    protected void traverseGraph() {
        // The actual DFS-based path-search algorithm would go here.
        System.out.println("DFS traversal from " + source + " to " + destination);
    }
}

/*
 * bfs.findPaths(1, 100)
 *     -> GraphPathFinder.findPaths(1, 100), inherited by the BFS object
 *         -> GraphPathFinder.initialize()
 *         -> BFSPathFinder.traverseGraph()
 *         -> GraphPathFinder.collectResults()
 *
 * These inherited and overridden methods run on the SAME BFSPathFinder object.
 * There is no separate GraphPathFinder object or wrapper.
 * The DFS object uses the same sequence with DFSPathFinder.traverseGraph().
 */
