package algo.project;

import java.util.List;
import org.graphstream.ui.view.Viewer;

/**
 * Main class to demonstrate graph generation, visualization, and algorithm execution.
 */
public class Main {

    public static void main(String[] args) {
        // Configure the GraphStream to use Swing for rendering
        System.setProperty("org.graphstream.ui", "swing");

        // Initialize PositiveGraph and generate a positive graph from the CSV file
        PositiveGraph positiveGraphGenerator = new PositiveGraph();
        positiveGraphGenerator.loadFromCSV("PositiveGraphAddresses.csv");

        // Print the positive graph
        System.out.println("Positive Graph:");
        positiveGraphGenerator.printGraph();

        // Convert to GraphStream graph and display the positive graph
        org.graphstream.graph.Graph gsPositiveGraph = positiveGraphGenerator.toGraphStreamGraph();
        Viewer positiveViewer = gsPositiveGraph.display();
        positiveViewer.setCloseFramePolicy(Viewer.CloseFramePolicy.EXIT);

        // Specify the path to the CSV file for negative graph
        String negativeCsvFilePath = "negative_file.csv"; // Update with actual path

        // Create a NegativeGraph from the positive graph
        NegativeGraph negativeGraphGenerator = new NegativeGraph(negativeCsvFilePath);

        // Convert to GraphStream graph and display the negative graph
        org.graphstream.graph.Graph gsNegativeGraph = NegativeGraph.toGraphStreamGraph(negativeGraphGenerator);
        Viewer negativeViewer = gsNegativeGraph.display();
        negativeViewer.setCloseFramePolicy(Viewer.CloseFramePolicy.EXIT);

        // Run Bellman-Ford algorithm to find negative weight cycles and get the result vertices
        BellmanFord bellmanFord = new BellmanFord(negativeGraphGenerator);
        boolean hasNegativeCycle = bellmanFord.run(negativeGraphGenerator.getAllVertices().values().iterator().next()); // Assume the first vertex as source

        if (hasNegativeCycle) {
            System.out.println("Negative weight cycle detected:");

            bellmanFord.extractNegativeCycle(); // Extract the negative cycles

            // System.out.println("Stopped Here.");

            // Get the vertices involved in the most profitable paths
            List<Vertex> cycleVertices = bellmanFord.getNegativeCycle();
            for (Vertex v : cycleVertices) {
                System.out.println(v.getName());  // Print each vertex name in the result list
            }
        } else {
            System.out.println("No negative weight cycle detected.");
            return;
        }

        // Execute the Dijkstra algorithm to find paths from the cycle's Pickup to the nearest Garage and within the cycle
        Dijkstra dijkstra = new Dijkstra(positiveGraphGenerator);
        Vertex garage = positiveGraphGenerator.getVerticesByType(VertexType.GARAGE).get(0);
        dijkstra.executeNegativeCycleAndPrintPath(garage, bellmanFord.getNegativeCycle());
    }
}
