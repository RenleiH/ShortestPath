package algo.project;

import java.util.*;

public class Dijkstra {

    private PositiveGraph positiveGraph; // The positive graph containing all vertices

    /**
     * Constructor for Dijkstra's algorithm.
     *
     * @param positiveGraph The graph on which Dijkstra's algorithm will be run.
     */
    public Dijkstra(PositiveGraph positiveGraph) {
        this.positiveGraph = positiveGraph;
    }

    /**
     * Runs Dijkstra's algorithm from a given source vertex.
     *
     * @param source The source vertex from which to calculate shortest paths.
     */
    public void runDijkstra(Vertex source) {
        PriorityQueue<Vertex> priorityQueue = new PriorityQueue<>(Comparator.comparingDouble(Vertex::getDistance));

        // Initialize distances and status for all vertices in the positive graph
        for (Vertex vertex : positiveGraph.getAllVertices().values()) {
            vertex.setDistance(Double.MAX_VALUE); // Set initial distance to infinity
            vertex.setStatus(0); // Set vertex status to unvisited
            vertex.setParent(null); // No parent initially
        }
        source.setDistance(0); // Set distance for the source vertex to 0
        priorityQueue.add(source); // Add the source vertex to the priority queue

        while (!priorityQueue.isEmpty()) {
            Vertex u = priorityQueue.poll(); // Get the vertex with the smallest distance
            u.setStatus(1); // Mark the vertex as visited

            for (Edge edge : u.getNeighbors()) {
                Vertex v = edge.getVertexT(); // Get the target vertex of the edge
                if (v == u) {
                    v = edge.getVertexF();
                }
                if (v.getStatus() == 1) continue; // Skip already visited vertices

                double newDist = u.getDistance() + edge.getWeight(); // Calculate the new distance
                if (newDist < v.getDistance()) {
                    priorityQueue.remove(v); // Remove the vertex from the queue to update its distance
                    v.setDistance(newDist); // Update the distance
                    v.setParent(u); // Set the parent to the current vertex
                    priorityQueue.add(v); // Re-add the vertex to the queue with the updated distance
                }
            }
        }
    }

    /**
     * Retrieves the shortest path from the source to the target vertex after running Dijkstra's algorithm.
     *
     * @param target The target vertex for which to retrieve the shortest path.
     * @return A list of vertices representing the shortest path from source to target.
     */
    public List<Vertex> getShortestPath(Vertex target) {
        List<Vertex> path = new ArrayList<>();
        for (Vertex at = target; at != null; at = at.getParent()) {
            path.add(at); // Add each vertex to the path list
        }
        Collections.reverse(path); // Reverse the list to get the path from source to target
        return path;
    }

    /**
     * Prints the shortest path from the source to the target vertex.
     *
     * @param source The source vertex.
     * @param target The target vertex.
     */
    public void printShortestPath(Vertex source, Vertex target) {
        runDijkstra(source); // Run Dijkstra from the source to calculate all shortest paths
        List<Vertex> path = getShortestPath(target); // Get the shortest path to the target
        if (path.isEmpty() || !path.get(0).equals(source)) {
            System.out.println("No path from " + source.getName() + " to " + target.getName()); // If no path is found
        } else {
            String pathString = String.join(", ", path.stream()
                    .map(Vertex::getName)
                    .toArray(String[]::new));
            System.out.println("Shortest path from " + source.getName() + " to " + target.getName() + ": " + pathString); // Print the path
        }
    }

    /**
     * Executes Dijkstra's algorithm for a negative cycle and prints the paths for each step.
     *
     * @param garage The starting vertex (garage) from which the cycle begins.
     * @param cycle  The list of vertices representing the cycle to follow.
     */
    public void executeNegativeCycleAndPrintPath(Vertex garage, List<Vertex> cycle) {
        System.out.println("Shortest path:");
        // Find the corresponding vertices in the positive graph
        List<Vertex> positiveCycle = new ArrayList<>();
        for (Vertex vertex : cycle) {
            positiveCycle.add(positiveGraph.getVertexByName(vertex.getName()));
        }

        // Iterate over the cycle in the positive graph and print the shortest paths
        for (int i = 0; i < positiveCycle.size(); i++) {
            if (i == 0) { // From the garage to the first pickup vertex
                runDijkstra(garage);
                printShortestPath(garage, positiveCycle.get(i));
            }
            if (i == positiveCycle.size() - 1) { // Back to the garage
                runDijkstra(positiveCycle.get(i));
                printShortestPath(positiveCycle.get(i), garage);
                continue;
            }
            // Run Dijkstra again from the new current vertex to find the next shortest path
            runDijkstra(positiveCycle.get(i));
            printShortestPath(positiveCycle.get(i), positiveCycle.get(i + 1));
        }
    }
}
