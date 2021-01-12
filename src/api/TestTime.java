package api;

import org.junit.jupiter.api.Test;

public class TestTime {

    DWGraphs_Algo ga = new DWGraphs_Algo(new DWGraph_DS());

    @Test
    long testPath(){
        long start = System.currentTimeMillis();
        ga.shortestPath(1,8502);
        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
        System.out.println(timeElapsed);
        return timeElapsed;
    }

    @Test
    long testCC(){
        long start = System.currentTimeMillis();
        ga.connected_component(456);
        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
        System.out.println(timeElapsed);
        return timeElapsed;
    }

    @Test
    long testCCS(){
        long start = System.currentTimeMillis();
        ga.connected_components();
        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
        System.out.println(timeElapsed);
        return timeElapsed;
    }

    @Test
    void avrg(){
        ga.load("data\\G_10000_80000_1.json");
        double avr = 0;
        for(int i = 0; i <10; i++){
            avr += testPath();
        }
        avr /= 10;
        System.out.println(avr);
    }
}
