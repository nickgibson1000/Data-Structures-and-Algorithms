import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;



/**
 * New tests for Java set using Java iterator and set methods.
 */

public class MyNetworkTest {

    BufferedReader openInputFile(String filename) {
        BufferedReader file;
        try {
            URI uri = MyNetworkTest.class.getResource(filename).toURI();
            String filePath = Paths.get(uri).toString();
            file = new BufferedReader(new FileReader(filePath));
        } catch (Exception e) {
            System.out.println("error opening " + filename + " for input.");
            return null;
        }
        return file;
    }

    @Test
    void readGraphReadsCorrectNumberOfVerticesEdgesAndHasRightRoot() {
        MyNetwork n = new MyNetwork();
        BufferedReader file = openInputFile("data1");
        assertNotNull(file);
        n.readGraph(file);
        Graph<String> g = n.getFullNetwork();
        assertEquals(7, g.getVertices().size());
        assertEquals(10, g.getEdges().size());
        assertEquals("A", n.currentNode());
    }

    @Test

    void pingToStartingNodeReturnsCorrectPath() {
        MyNetwork n = new MyNetwork();
        BufferedReader file = openInputFile("data1");
        assertNotNull(file);
        n.readGraph(file);
        String result = n.ping(n.currentNode());
        assertEquals("From: A\n  To: A", result);
    }

    @Test
    void pingToAdjacentNodeReturnsCorrectPath() {
        MyNetwork n = new MyNetwork();
        BufferedReader file = openInputFile("data1");
        assertNotNull(file);
        n.readGraph(file);
        String result = n.ping("B");
        assertEquals("From: A\n  To: B", result);
    }

    @Test
    void pingToOtherNodesReturnCorrectPaths() {
        MyNetwork n = new MyNetwork();
        BufferedReader file = openInputFile("data1");
        assertNotNull(file);
        n.readGraph(file);
        String result;
        result = n.ping("C");
        assertEquals("From: A\n  To: C", result);
        result = n.ping("D");
        assertEquals("From: A\n  To: C\n  To: F\n  To: D", result);
        result = n.ping("E");
        assertEquals("From: A\n  To: C\n  To: F\n  To: G\n  To: E", result);
        result = n.ping("F");
        assertEquals("From: A\n  To: C\n  To: F", result);
        result = n.ping("G");
        assertEquals("From: A\n  To: C\n  To: F\n  To: G", result);
    }

    //*****************************************TESTS FOR graph.java******************************************************** */

    @Test
    void graphTest1() {

        Graph<String> graph = new Graph<>();
        graph.addVertex("a");
        graph.addVertex("b");
        graph.addEdge("a", "b", 3);
        assertTrue(graph.containsVertex("a"));
        assertTrue(graph.containsVertex("b"));
        assertFalse(graph.containsVertex("c"));
    }

    @Test
    void graphTestMST() {

        MyNetwork n = new MyNetwork();
        BufferedReader file = openInputFile("data1");
        assertNotNull(file);
        n.readGraph(file);
        Graph<String> g = n.getMST();
        //g.printMST(g);
        assertEquals(7,g.getVertices().size());
        assertEquals(6,g.getEdges().size());

    }
}
