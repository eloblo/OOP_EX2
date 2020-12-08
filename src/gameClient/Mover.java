package gameClient;

import api.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Mover {

    private static Arena _ar;
    private CL_Agent _agent;
    private List<node_data> _path;
    private List<CL_Pokemon> _pokemons;
    private CL_Pokemon _pokemon;
    private static HashSet<String> _blackList;
    private static HashMap<Integer,HashSet<String>> _whiteList;
    private static HashMap<Integer,List<node_data>> _paths;
    private static directed_weighted_graph _graph;
    private static DWGraphs_Algo _graphAlgo;
    private static game_service _game;
    private int _reset = 0;

    public Mover(Arena ar, directed_weighted_graph graph, game_service game, int numOfAgents){
        _ar = ar;
        _graph = graph;
        _game = game;
        _blackList = new HashSet<>();
        _whiteList = new HashMap<>();
        _paths = new HashMap<>();
        _graphAlgo = new DWGraphs_Algo(new DWGraph_DS());
        DWGraph_DS temp = _graphAlgo.caster(_graph);
        _graphAlgo.init(temp);
        for(int i = 0; i < numOfAgents; i++){
            _whiteList.put(i,new HashSet<String>());
            _paths.put(i,null);
        }
    }

    public synchronized List<node_data> init(CL_Agent agent, CL_Pokemon pokemon, List<node_data> path){
        _agent = agent;
        _pokemon = pokemon;
        _path = path;
        moveAgents();
        if(_reset == 300){
            _blackList.clear();
            for(int i = 0; i < _whiteList.size(); i++){
                _whiteList.get(i).clear();
            }
        }
        return _path;
    }

    public synchronized void moveAgents() {
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
            dest = this.nextNode(src);
            _game.chooseNextEdge(_agent.getID(), dest);
             System.out.println("Agent: " + id + ", val: " + v + "   turned to node: " + dest);
        }
    }

    private synchronized int nextNode(int src) {
        int ans = -1;
        if (_path == null || _path.isEmpty()) {
            List<CL_Pokemon> pokes = _ar.getPokemons();
            int finalDest = findPkm(pokes, src);
            _path = _graphAlgo.shortestPath(src, finalDest);
            findPkm(pokes, finalDest);
            if(_path != null){
                _path.remove(0);
                ans = _path.get(0).getKey();
                _path.remove(0);
            }
        }
        else{
            ans = _path.get(0).getKey();
            _path.remove(0);
        }
        return ans;
    }

    private synchronized int findPkm(List<CL_Pokemon> pokes, int src) {
        int ans = -1;
        double minDist = Integer.MAX_VALUE;
        for (CL_Pokemon pok : pokes) {
            String pos = pok.getLocation().toString();
            if(!_blackList.contains(pos) || _whiteList.get(_agent.getID()).contains(pos)){
                edge_data edge = pok.get_edge();
                int edgeDest = edge.getDest();
                if (edgeDest == src) {
                  //  _blackList.add(pos);
                    return edge.getSrc();
                }
                double dist = _graphAlgo.shortestPathDist(src,edgeDest);
                if (dist < minDist && dist != -1) {
                    minDist = dist;
                    ans = edge.getDest();;
                    _pokemon = pok;
                }
            }
        }
        if(_pokemon != null){
            _blackList.add(_pokemon.getLocation().toString());
            _whiteList.get(_agent.getID()).add(_pokemon.getLocation().toString());
            _reset++;
        }
    //    System.out.println(_agent.getID()+", "+ans);
        return ans;
    }
}
