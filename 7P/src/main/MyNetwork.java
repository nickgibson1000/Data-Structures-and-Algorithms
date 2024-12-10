/** CS515 Assignment 7P

Name: Nick Gibson

Section: 1

Date: 11/11/24

Collaboration Declaration:

Collaboration: None

*/
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;
import java.util.Arrays;
import java.util.Deque;

public class MyNetwork {

    private Graph<String> g;
    private Graph<String> mst;
    LinkedList<String> sshStack = new LinkedList<>();

    // First vertex encountered from readGraph
    // Used for currentNode and ping 
    private String currentNode;

    // Looks Good
    public String currentNode() {

        return this.currentNode;
    }
    
    // Looks Good
    public void readGraph(BufferedReader file) {

        /**
         * Store the first vertex of graph intp firstVertex
         * we are just reading in the full graph here not worrying about MST
         * Remember: we will want to split by space tokenizeing vertices (verticies may be more than 1 letter)
         */

        // Try accessing file
        try(Scanner scan = new Scanner(file)) {


            //Graph<String> graph = new Graph<>();
            g = new Graph<>();

            // Holds content of our line
            String line;

            int lineCount = 0;

            while(scan.hasNextLine()) {

                // Store line contents
                line = scan.nextLine().trim();

                // Tokenize the long by white space in the string
                String[] tokens = line.split("\\s+");

                // Establish our tokens value
                String v1 = tokens[0];
                String v2 = tokens[1];
                int weight = Integer.parseInt(tokens[2]);

                // Add contents too graph
                g.addVertex(v1);
                g.addVertex(v2);

                /**
                 * Even though we are working with undirected graph
                 * addEdge makes 2 edges the second is a reciprocal
                 * of the first. So we don't need to add opposite edge here
                 */
                g.addEdge(v1, v2, weight);
                

                // Set our first vertex of the graph
                if(lineCount == 0) {
                    currentNode = v1;
                }

                // Increment our line count
                lineCount++;
            }

            scan.close();
        } catch (Exception e) {
            //System.out.println("Cannot Open File.");
            //e.printStackTrace();
        }


    }

    // Looks Good
    public Graph<String> getFullNetwork() {

        // Graph we created in read graph method
        return g;
    }

    // Looks Good
    public Graph<String> getMST() {
        
        // Generate MST 
        mst = g.generateMST();

        // Return new MST graph
        return mst;
    }

    // Looks Good
    public String ping(String target) {

    
        // Get our path of vertices
        List<String> path = g.findPath(currentNode(), target);

        // StringBuilder to hold our results
        StringBuilder sb = new StringBuilder();


        // Start off our string builder with root vertex
        sb.append("From: " + currentNode() + "\n");

        // If root and the target are the same vertex
        if(currentNode().equals(target)) {
            sb.append("  To: " + currentNode());
            return sb.toString();
        }

        if (path != null) {

            // Go through the list and append to sb 
            for (int i = 0; i < path.size(); i++) {
                String vertex = path.get(i);
    
                // Prevent a double print of the root vertex
                if (!vertex.equals(currentNode())) {
                    sb.append("  To: " + vertex);

                    // Append a newline character only if it's not the last vertex
                    if (i < path.size() - 1) {
                        sb.append("\n");
                    }
                }
            }
        } else {

            // Debugging stmt to make sure path intialized
            System.out.println("Path is null");
        }

        // Return string path
        return sb.toString();
    }

    // Looks Good
    public void ssh(String target) {

        /**
         * We have our sshStack which we will use to hold our past nodes( We dont push onto stack our current node)
         * We will need to update graph.java becasue we created an undirected graph
         * (We need to re add all reciprocal edges) I Actually don't think i need too but we will see
         * 
         */

        // If current node and target are the same don't do anything
        if(currentNode().equals(target)) {
            return;
        }

        // Find the path from our current node to our new target node
        List<String> list = g.findPath(currentNode(), target);

        //for(String vertex : list) {
        //    System.out.println(vertex);
        //}

         // Check if the path is valid
        if (list.isEmpty()) {
            System.out.println("No path found to target: " + target);
            return; // Exit if no path exists
        }

        sshStack.push(currentNode);

        // Change our current node to our target node
        currentNode = target;

        //// Iterate over the list of vertices and push them in order onto the stack 
        //// (Exclude last vertex because that is our target)
        //for(int i = 0; i < list.size() - 1; i++) {

        //    // Push previous vertex onto the stack
        //    // Because we have size - 1 we dont have to worry about target node
        //    sshStack.push(list.get(i));
        //}
    }

    // Looks Good
    public void exit() {

        currentNode = sshStack.pop();
        
        //// Pop last node from stack and set it as our new root vertex
        //if(!sshStack.isEmpty()) {
        //    this.currentNode = sshStack.pop();
        //} else {  
        //    // If we get here there is issues
        //    System.out.println("Nothing too pop from sshStack");
        //}
    }

    public static void main(String[] args) {
        if( args.length != 1) {
            System.out.println("usage: java MyNetwork <filename>");
            return;
        }

        BufferedReader file;
        try {
            Path filePath = Paths.get(args[0]);
            file = new BufferedReader(new FileReader(filePath.toFile()));
        } catch (IOException e) {
            System.out.println("error opening " + args[0] + " for input.");
            //e.printStackTrace();
            return;
        }

        MyNetwork n = new MyNetwork();
        n.readGraph(file);

        String commandLine, currentNode, target;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        
        while((currentNode = n.currentNode()) != null) {
            System.out.println("<" + currentNode + ">#");

    
            try {
               commandLine = in.readLine();
            } catch (IOException e) {
               System.out.println("Error reading command");
               e.printStackTrace();
               continue;
            }
        
            // Seperate entered command line arguments into two sections
            // tokens[0] will be ping, ssh, or exit
            // tokens[1] will be the node 
            String[] tokens = commandLine.split("\\s+");
            String command = tokens[0];
            //String target; = tokens[1];

            // We don't provide a node when using exit so we want to prevent accessing out of array bounds
            if(!command.equals("exit")) {
                
                if (tokens.length > 1) {
                    target = tokens[1];
                } else {
                    target = null; // or handle the case where no vertex is provided
                }
            } else {
                target = null;
            }
            

            switch(command) {

                case "ping":

                    // Error issued if user tries to access invalid vertex
                    if(!n.g.containsVertex(target)) {
                        System.out.println("Cannot find node.  Available nodes are: ");
                        // Print all graph vertice so user can fix mistake
                        for(String v : n.g.getVertices()) {
                            System.out.println(v);
                        }
                        // We break and allow user to re-issue proper command
                        break;
                    }

                    String pingResult = n.ping(target);
                    System.out.println(pingResult);
                    break;

                case "ssh":
                    // Error issued if user tries to access invalid vertex
                    if(!n.g.containsVertex(target)) {
                        System.out.println("Cannot find node.  Available nodes are: ");
                        // Print all graph vertice so user can fix mistake
                        for(String v : n.g.getVertices()) {
                            System.out.println(v);
                        }
                        // We break and allow user to re-issue proper command
                        break;
                    }
                
                    n.ssh(target);
                    break;
                case "exit":
                    //exitCount++;
                    //System.err.println(exitCount);
                    // Set current node to null (We exit program if this occurs)
                    if(n.sshStack.isEmpty()) {
                        n.currentNode = null;
                        break;
                    }
                    n.exit();
                    break;
                
                    default:
                        System.out.println("Unknown Command");
                    
            }
        }
    }
}
