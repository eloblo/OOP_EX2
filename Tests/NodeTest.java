package api.Tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;

public class NodeTest {

    NodeData n1 = new NodeData(1);
    NodeData n2 = new NodeData(2);
    NodeData n3 = new NodeData(3);
    DWGraph_DS g = new DWGraph_DS();

    @BeforeEach
    void setUp(){
        g = new DWGraph_DS();
    }

    @Test
    void has_edge(){
        g.addNode(n1);
        g.addNode(n2);
        g.addNode(n3);
        g.connect(1,2,1);
        g.connect(2,3,1);
        g.connect(2,1,1);
        g.connect(3,1,1);
        Assertions.assertTrue(n3.hasEdge(1));
        Assertions.assertFalse(n2.hasEdge(2));
    }

    @Test
    void get_E(){
        g.addNode(n1);
        g.addNode(n2);
        g.addNode(n3);
        g.connect(1,2,1);
        g.connect(2,3,1);
        g.connect(2,1,1);
        g.connect(3,1,1);
        g.connect(1,3,1);
        Collection<edge_data> E = n1.getE();
        Assertions.assertEquals(2,E.size());
        int temp[] = new int[2];
        int i = 0;
        for(edge_data e : E){
            temp[i] = e.getDest();
            i++;
        }
        Arrays.sort(temp);
        for(i = 0; i < 2; i++){
            temp[i] = i+2;
        }
    }

    @Test
    void get_edge(){
        g.addNode(n1);
        g.addNode(n2);
        g.addNode(n3);
        g.connect(1,2,1);
        g.connect(2,3,1);
        g.connect(2,1,1);
        g.connect(3,1,1);
        Assertions.assertNull(n2.getEdge(5));
        Assertions.assertEquals(3,n2.getEdge(3).getDest());
    }

    @Test
    void remove_edge(){
        g.addNode(n1);
        g.addNode(n2);
        g.addNode(n3);
        g.connect(1,2,1);
        g.connect(2,3,1);
        g.connect(2,1,1);
        g.connect(3,1,1);
        Collection<edge_data> E = n2.getE();
        Assertions.assertEquals(2,E.size());
        Assertions.assertNull(n2.removeEdge(10));
        Assertions.assertNull(n2.removeEdge(2));
        n2.removeEdge(1);
        Assertions.assertNull(n2.removeEdge(1));
        Assertions.assertEquals(1,E.size());
    }

    @Test
    void add_edge(){
        g.addNode(n1);
        g.addNode(n2);
        g.addNode(n3);
        g.connect(1,2,1);
        g.connect(2,3,1);
        g.connect(2,1,1);
        g.connect(3,1,1);
        edge_data e = n1.removeEdge(2);
        Assertions.assertFalse(n1.hasEdge(2));
        n1.addEdge(e);
        Assertions.assertTrue(n1.hasEdge(2));
    }

    @Test
    void remove_back(){
        g.addNode(n1);
        g.addNode(n2);
        g.addNode(n3);
        g.connect(1,2,1);
        g.connect(3,2,1);
        Collection<edge_data> BE = n2.getBE();
        Assertions.assertEquals(2,BE.size());
        n2.removeBackEdge(10);
        n2.removeBackEdge(2);
        n2.removeBackEdge(1);
        n2.removeBackEdge(1);
        Assertions.assertEquals(1,BE.size());
    }

    @Test
    void nodePrint(){
        g.addNode(n1);
        g.addNode(n2);
        g.addNode(n3);
        g.connect(2,3,1);
        g.connect(2,1,2);
        System.out.println(n2);
    }
}