package api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

public class GraphTest {

    DWGraph_DS g = new DWGraph_DS();
    NodeData n1 = new NodeData(1);
    NodeData n2 = new NodeData(2);
    NodeData n3 = new NodeData(3);

    @BeforeEach
    void reInitialize(){
        g = new DWGraph_DS();
    }

    @Test
    void add_node(){
        g.addNode(n1);
        g.addNode(n1);
        g.addNode(n2);
        g.addNode(n3);
        g.addNode(n3);
        Assertions.assertEquals(3, g.nodeSize());
    }

    @Test
    void edge_connect(){
        g.addNode(n1);
        g.addNode(n2);
        g.addNode(n3);
        g.connect(1,2,1);//+
        g.connect(2,3,1);//+
        g.connect(2,1,1);//+
        g.connect(3,1,1);//+
        g.connect(5,10,1);
        g.connect(10,3,1);
        g.connect(2,2,1);
        g.connect(3,1,1);
        Assertions.assertEquals(4,g.edgeSize());
    }

    @Test
    void get_edge(){
        g.addNode(n1);
        g.addNode(n2);
        g.connect(1,2,1);
        edge_data e12 = g.getEdge(1,2);
        Assertions.assertEquals(2, e12.getDest());
        Assertions.assertEquals(1, e12.getSrc());
        edge_data eNull = g.getEdge(0,5);
        Assertions.assertNull(eNull);
        eNull = g.getEdge(1,5);
        Assertions.assertNull(eNull);
        eNull = g.getEdge(2,1);
        Assertions.assertNull(eNull);
    }

    @Test
    void get_V(){
        g.addNode(n1);
        g.addNode(n2);
        g.addNode(n3);
        int i = 1;
        Collection<node_data> V = g.getV();
        for(node_data n : V){
            Assertions.assertEquals(i, n.getKey());
            i++;
        }
    }

    @Test
    void get_E(){
        g.addNode(n1);
        g.addNode(n2);
        g.addNode(n3);
        g.connect(1,2,1);
        g.connect(1,3,1);
        Collection<edge_data> edges =  g.getE(2);
        int i = 2;
        for(edge_data e : edges){
            Assertions.assertEquals(1,e.getDest());
            Assertions.assertEquals(i, e.getDest());
            i++;
        }
    }

    @Test
    void remove_node(){
        g.addNode(n1);
        g.addNode(n2);
        g.addNode(n3);
        int i;
        for(i = g.nodeSize(); i > 0; i--){
            Assertions.assertEquals(i, g.nodeSize());
            Assertions.assertEquals(i,g.removeNode(i).getKey());
        }
        Assertions.assertNull(g.removeNode(-5));
        Assertions.assertNull(g.removeNode(1));
    }

    @Test
    void remove_edge(){
        g.addNode(n1);
        g.addNode(n2);
        g.addNode(n3);
        g.connect(2,1,1);
        g.connect(3,1,1);
        int i;
        for(i = g.edgeSize(); i > 1; i--){
            Assertions.assertEquals(i, g.edgeSize());
            Assertions.assertEquals(i, g.removeEdge(i,1).getSrc());
        }
        Assertions.assertNull(g.removeEdge(1,3));
        Assertions.assertNull(g.removeEdge(-1,30));
    }

    @Test
    void remove_node_edge(){
        g.addNode(n1);
        g.addNode(n2);
        g.addNode(n3);
        g.connect(1,3,1);
        g.connect(2,1,1);
        g.connect(3,1,1);
        g.connect(2,3,1);
        g.removeNode(1);
        Assertions.assertEquals(1,g.edgeSize());
    }

    @Test
    void graphPrint(){
        g.addNode(n1);
        g.addNode(n2);
        g.addNode(n3);
        g.connect(1,3,1);
        g.connect(2,1,2);
        g.connect(3,1,3);
        g.connect(2,3,4);
        System.out.println(g);
    }
}
