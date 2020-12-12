package api.Tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import api.*;
import java.io.File;
import java.util.LinkedList;
import java.util.Scanner;

public class GraphAlgoTest {

    DWGraph_DS g;
    DWGraphs_Algo ga;

    @BeforeEach
    void Initialize(){
        g = new DWGraph_DS();
        ga = new DWGraphs_Algo(g);
    }

    @Test
    void connected(){
        Assertions.assertTrue(ga.isConnected());
        g.addNode(new NodeData(0));
        Assertions.assertTrue(ga.isConnected());
        for(int i = 1; i < 10; i++){
            g.addNode(new NodeData(i));
            g.connect(i-1,i,i*2+1);
        }
        Assertions.assertFalse(ga.isConnected());
        g.connect(9,0,9);
        Assertions.assertTrue(ga.isConnected());
        g.removeEdge(5,6);
        Assertions.assertFalse(ga.isConnected());
        g.connect(5,6,11);
        g.removeNode(3);
        Assertions.assertFalse(ga.isConnected());
    }

    @Test
    void shortest_dist(){
        g.addNode(new NodeData(0));
        for(int i = 1; i < 10; i++){
            g.addNode(new NodeData(i));
            g.connect(i-1,i,i);
        }
        Assertions.assertEquals(11,ga.shortestPathDist(4,6));
        Assertions.assertEquals(-1,ga.shortestPathDist(6,4));
        g.connect(4,6,100);
        g.connect(6,4,1);
        g.connect(4,8,1);
        g.connect(8,0,1);
        g.connect(0,6,1);
        Assertions.assertEquals(3,ga.shortestPathDist(4,6));
    }

    @Test
    void shortest_path(){
        g.addNode(new NodeData(0));
        for(int i = 1; i < 10; i++){
            g.addNode(new NodeData(i));
            g.connect(i-1,i,i);
        }
        LinkedList<node_data> list = (LinkedList<node_data>) ga.shortestPath(4,6);
        for(int i = 4; i < 7; i++){
            Assertions.assertEquals(i,list.get(i-4).getKey());
        }
        g.connect(4,6,100);
        g.connect(6,4,1);
        g.connect(4,8,1);
        g.connect(8,0,1);
        g.connect(0,6,1);
        list = (LinkedList<node_data>) ga.shortestPath(4,6);
        int arr[] = {4,8,0,6};
        for(int i : arr){
            Assertions.assertEquals(i,list.remove().getKey());
        }
    }

    @Test
    void copy_graph(){
        g.addNode(new NodeData(0));
        for(int i = 1; i < 10; i++){
            g.addNode(new NodeData(i));
            g.connect(i-1,i,i);
        }
        g.connect(4,6,100);
        g.connect(6,4,1);
        g.connect(4,8,1);
        g.connect(0,9,9);
        g.connect(8,0,1);
        g.connect(0,6,1);
        DWGraph_DS gc = (DWGraph_DS) ga.copy();
        Assertions.assertTrue(g.toString().equals(gc.toString()));
    }

    @Test
    void load_graph(){
        Assertions.assertTrue(ga.load("data/A0"));
        Assertions.assertTrue(ga.load("data/A5"));
        Assertions.assertTrue(ga.load("data/empty"));
        Assertions.assertEquals(0,ga.getGraph().edgeSize());
        Assertions.assertEquals(0,ga.getGraph().nodeSize());
    }

    @Test
    void save_graph(){
        ga.load("data/A0");
        Assertions.assertTrue(ga.save("data/A"));
        try{
            File A0 = new File("data/A");
            File A = new File("data/A0");
            Scanner reader = new Scanner(A);
            String SA = "";
            while(reader.hasNext()){
                SA += reader.nextLine();
            }
            reader = new Scanner(A0);
            String SA0 = "";
            while(reader.hasNext()){
                SA0 += reader.nextLine();
            }
            Assertions.assertEquals(SA,SA0);
        }
        catch (Exception e){
            Assertions.fail("Failed to load file");
        }
        DWGraph_DS emptyG = new DWGraph_DS();
        ga.init(emptyG);
        Assertions.assertTrue(ga.save("data/empty"));
    }
}