/** CS515 Assignment 7P

Name: Nick Gibson

Section: 1

Date: 11/10/24

Collaboration Declaration:

Collaboration: None
*/

import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import set.DisjointSet;

public class Graph<T extends Comparable<T>> {

    class Edge implements Comparable<Edge> {
        public final T first;
        public final T second;
        public final int weight;

        public Edge( T v1, T v2, int w) {
            first = v1;
            second = v2;
            weight = w;
        }

        // Looks Good
        @Override
        public int compareTo(Edge rhs) {

            // Compare the weights of edges
            return Integer.compare(this.weight, rhs.weight);
        }

        // Looks Good
        @Override
        public boolean equals(Object any) {
            
            // Compare two objects for equality
            return this.equals(any);
        }

        // For debugging purposes
        @Override
        public String toString() {
            return "Edge of " + first + " and " + second + " weight: " + weight;
        }

    }

    /** Ceates empty graph
     *  T - Vertex
     *  List<Edge> - List of edges connected to vertex
     **/
    HashMap<T, List<Edge>> graph;

    /** Ceates empty graph to hold MST
     *  T - Vertex
     *  List<Edge> - List of edges connected to vertex
     **/
    //HashMap<T, List<Edge>> graphMST;

    // First vertex inserted into graph
    private T firstVertex;

    // Maybe we need more?

    // Constructor to intialize graph attibutes
    public Graph() {

        graph = new HashMap<>();
        //graphMST = new HashMap<>();
        firstVertex = null;
    }

    // Looks Good
    public T getRoot() {

        return firstVertex;
    }

    // Looks Good
    public Collection<T> getVertices() {

        /**
         * Iterate over each vertice in the hashmap of graph
         * We only care a bout the keys not the value in this case
         * Dont have to worry about duplciate vertices
         */

        // Create a list to hold all keys
        List<T> verticeList = new ArrayList<>();

        // Create iterator
        Iterator<T> it = iterator(); 

        // Loop through key set and add vertices to list
        while(it.hasNext()) {
            verticeList.add(it.next());
        }

        //Return list of vertices
        return verticeList;
    }

    // Looks Good
    public Collection<Edge> getEdges() { 

        /**
         * Iterate over each edge list in the hashmap of the graph
         * We only care about the values in this case not the key
         * We have to make sure we arent adding duplicate edges 
         * Could use set instead of list for this
         * 
         */

        // List to hold all edges
        ArrayList<Edge> edgeList = new ArrayList<>();

        // Create iterator
        Iterator<T> it = iterator();

        // Loop through entire key set
        while(it.hasNext()) {
            
            T vertex = it.next();

            // Store edges of each vertex in list
            List<Edge> edges = graph.get(vertex);

            // Make sure edge list is not empty
            if(edges != null) {

                // Go through list of edges
                for(Edge edge : edges) {

                    // Add edges too 
                    edgeList.add(edge);
                }
            }
        }

        // Check for duplicate (reciprocal) edges and remove from edge list
        // This is incredibly inefficient but its the only way I could think of how to do it
        for (int i = 0; i < edgeList.size(); i++) {
            Edge edge1 = edgeList.get(i);
        
            for (int j = i + 1; j < edgeList.size(); j++) {
                Edge edge2 = edgeList.get(j);
        
                // Check if edge1 and edge2 are reciprocal duplicates
                if ((edge1.first.equals(edge2.first) && edge1.second.equals(edge2.second)) ||
                    (edge1.first.equals(edge2.second) && edge1.second.equals(edge2.first))) {
                    
                    // Remove the duplicate reciprocal edge
                    edgeList.remove(j);
                    j--; // Adjust the index to account for the removed item
                }
            }
        }

        // List of all edges
        return edgeList;
    }

    // Looks Good
    Iterator<T> iterator() {

        // Return a keySet containing all vertices in the graph
        return this.graph.keySet().iterator();
    }

    // Looks Good
    // does nothing if vertex already in graph
    void addVertex( T v) {
       

        // If the graph contains vertex exit function
        if(this.containsVertex(v)) {
            return;
        }

        // If adding to empty graph - make first vertex
        if(graph.size() == 0) {
            firstVertex = v;
        } 

        // Add the vertex like normal to the graph
        // Create empty list of edges to go with
        graph.put(v, new ArrayList<Edge>());
        
    }

    // Looks Good
    boolean containsVertex(T v) { 

        // Create a new iterator 
        Iterator<T> it = iterator(); 

        // Loop through using iterator to see if graphs contains vertex
        while (it.hasNext()) {
            if(it.next().equals(v)) {
                // Vertex found 
                return true;
            }
        }
        // Vertex not found
        return false;
    }

    // Looks Good
    void addEdge( T v1, T v2, int weight) {
        
        // Create new Edge with parameter specifications
        // Create edges going both ways to support undirected graph reqs.
        Edge newEdge = new Edge(v1, v2, weight);
        Edge newEdge2 = new Edge(v2, v1, weight);

        graph.get(v1).add(newEdge);
        graph.get(v2).add(newEdge2);
    }

    // Looks Good
    // creates a minimum spanning tree from the graph
    // use graph = graph.generateMST(); for easy conversion
    Graph<T> generateMST( ) {
	    

        /**
         * Create our MST graph
         * We wanna make a disjoint set and seperately insert all vertices into the set to create our "forest of singletons"
         * Then enter every edge into a min priority queue which will sort it based on there weight 
         * Then continue with kruskals algorithm
         * 
         */

        // Create our MST graph
        Graph<T> MST = new Graph<>();

        // Disjoint set to hold our vertices
        DisjointSet<T> djs = new DisjointSet<>();

        // Create our iterator to loop over all keys
        Iterator<T> it = iterator();

        // Loop through iterators key set and insert each vertex into djs
        // Create our "forest"
        while (it.hasNext()) {

            T vertex = it.next();
            djs.createSet(vertex);

            // Add our verticies to the MST graph
            MST.addVertex(vertex);
        }

        // Min PQueue to hold all edges
        PriorityQueue<Edge> edgeQueue = new PriorityQueue<>();

        
        // Create new iterator
        Iterator<T> it2 = iterator();


        // Loop through and add all edges too PQueue
        while(it2.hasNext()) {

            T vertex = it2.next();

            // Store edges of each vertex in list
            List<Edge> edges = graph.get(vertex);

            //// Debugging: Check what edges are being retrieved
            //if (edges == null) {
            //    System.out.println("No edges for vertex: " + vertex);
            //} else {
            //    System.out.println("Edges for vertex " + vertex + ": " + edges);
            //}

            //System.out.println(vertex + " " + edgeQueue);

            // Make sure edge list is not empty
            if(edges != null) {

                // Go through list of edges
                for(Edge edge : edges) {

                    // Add edge too PQueue
                    edgeQueue.add(edge);
                }
            }
        }
        

        // Now we loop over dsj until only one tree remains meaning we found our MST
        while(djs.size() > 1) {

            // Remove minimum edge in PQueue
            Edge edge = edgeQueue.poll();
            
            // Store vertices attached to the removed edge
            T v1 = edge.first;
            T v2 = edge.second;

            
            // If sets have different representatives then they are in different sets, so union them
            if(djs.findSet(v1) != djs.findSet(v2)) {
                djs.unionSets(v1, v2);

                // Add our edges to MST graph
                MST.addEdge(v1, v2, edge.weight);
                
                //Collection<Edge> edg = MST.getEdges();
                
                //// Print each edge in the MST
                //for (Edge e : edg) {
                //    System.out.println(e);
                //}
            }
            
        }

        //// Assuming getEdges() gives you a list of all edges in the MST
        //Collection<Edge> mstEdges = MST.getEdges(); 
        //// Print each edge in the MST
        //for (Edge edge : mstEdges) {
        //    System.out.println(edge);
        //}

        return MST;
    }   

     // Retrieves edges for a specific vertex
     public List<Edge> getEdgesForVertex(T vertex) {
        
        return this.graph.getOrDefault(vertex, Collections.emptyList());
    }

    public void printMST(Graph<T> MST) {
        System.out.println("MST Vertices and Edges:");
    
        for (T vertex : MST.graph.keySet()) {
            System.out.println("Vertex: " + vertex);
            List<Edge> edges = MST.getEdgesForVertex(vertex); // Ensure this references the MST
        
        for (Edge edge : edges) {
            System.out.println("  Edge: " + edge.first + " - " + edge.second + " with weight " + edge.weight);
        }
    }
    }
    
    // Looks Good
    // returned lists, including start and target, the vertices along path
    // breadth-first traversal; best if called on MST
    List<T> findPath( T start, T target) {
        

        // Get our MST graph
        Graph<T> MST = this.generateMST();

        // Create a queue to hold our vertices
        Queue<T> queue = new LinkedList<T>();

        // Holds our visited vertices
        Set<T> visited = new HashSet<>();

        // Track preddecessors of each vertex
        Map<T,T> predecessors = new HashMap<>();

        // Add our starting vertex to queue and visited set
        queue.add(start);
        visited.add(start);


        // Loop through MST until we reach target vertex
        while(!queue.isEmpty()) {

            // Remove vertex
            T vertex = queue.poll();

            // If vertex = target we will build our path
            if(vertex.equals(target)) {
                
                // List to hold our path
                List<T> path = new ArrayList<>();
                for(T v = target; v != null; v = predecessors.get(v)) {
                    path.add(v);
                }

                // Reverse the list because it was created in reverse order
                Collections.reverse(path);
                return path;
            }

            //Iterator it = iterator()
            // Get the edge associated with the given vertex
            Collection<Edge> edges = MST.getEdgesForVertex(vertex);


            if(edges != null) {

                // Traverse the edge 
                for(Edge edge : edges) {

                    // Store connecting vertex
                    T v2 = edge.second;

                    // If other vertex is not in visited set, add it
                    if(!visited.contains(v2)) {
                        
                        visited.add(v2);
                        queue.add(v2);
                        predecessors.put(v2,vertex);
                    }


                    T v1 = edge.first;

                    if (!visited.contains(v1)) {

                        visited.add(v1);
                        queue.add(v1);
                        predecessors.put(v1, vertex); 
                    }
                }
            }
        }

        // Something bad happened
        return null;
    }
}
