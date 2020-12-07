package gameClient;

import api.*;

import java.util.ArrayList;
import java.util.List;

public class Agent implements Runnable {

    private static Arena _ar;
    private CL_Agent _agent;
    private List<node_data> _path = null;
    private List<CL_Pokemon> _pokemons = null;
    private static directed_weighted_graph _graph;
    private static game_service _game;

    public Agent(Arena ar, CL_Agent agent, directed_weighted_graph graph, game_service game){
        _ar = ar;
        _agent = agent;
        _graph = graph;
        _game = game;
    }

    @Override
    public void run() {
        int dt = 100;
        while(_game.isRunning()){
            moveAgents();
            try {
                Thread.sleep(dt);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    synchronized private void moveAgents() {
        String lg = _game.move();
        List<CL_Agent> log = Arena.getAgents(lg, _graph);
        _ar.setAgents(log);
        String fs = _game.getPokemons();
        List<CL_Pokemon> ffs = Arena.json2Pokemons(fs);
        _ar.setPokemons(ffs);
        _agent = log.get(_agent.getID());
        int id = _agent.getID();
        int dest = _agent.getNextNode();
        int src = _agent.getSrcNode();
        double v = _agent.getValue();
        if (dest == -1) {
            dest = nextNode(src);
            _game.chooseNextEdge(_agent.getID(), dest);
            System.out.println("Agent: " + id + ", val: " + v + "   turned to node: " + dest);
        }
    }

    private int nextNode(int src) {
        int ans = -1;
        if (_path == null || _path.isEmpty()) {
            List<CL_Pokemon> pokes = _ar.getPokemons();
            DWGraphs_Algo ga = new DWGraphs_Algo(new DWGraph_DS());
            DWGraph_DS newGr = ga.caster(_ar.getGraph());
            ga.init(newGr);
            ans = findPkm(pokes, src, ga);
            _path = ga.shortestPath(src, ans);
            _path.remove(0);
        }
        ans = _path.get(0).getKey();
        _path.remove(0);
        return ans;
    }

    private static int findPkm(List<CL_Pokemon> pokes, int src, DWGraphs_Algo ga) {
        ArrayList<Integer> destinations = new ArrayList<>();
        for (CL_Pokemon pok : pokes) {
            edge_data edge = pok.get_edge();
            int dest = edge.getDest();
            if (dest == src) {
                return edge.getSrc();
            }
            destinations.add(dest);
        }
        int ans = -1;
        double minDist = Integer.MAX_VALUE;
        for (int dest : destinations) {
            double dist = ga.shortestPathDist(src, dest);
            if (dist < minDist && dist != -1) {
                minDist = dist;
                ans = dest;
            }
        }
        return ans;
    }
}