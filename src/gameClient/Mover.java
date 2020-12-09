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
    private CL_Pokemon _pokemon;
    private static HashSet<String> _blackList;
    private static HashMap<Integer,HashSet<String>> _whiteList;
    private static HashMap<Integer,List<node_data>> _pathSet;
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
        _pathSet = new HashMap<>();
        _graphAlgo = new DWGraphs_Algo(new DWGraph_DS());
        DWGraph_DS temp = _graphAlgo.caster(_graph);
        _graphAlgo.init(temp);
        for(int i = 0; i < numOfAgents; i++){
            _whiteList.put(i,new HashSet<String>());
            _pathSet.put(i,null);
        }
    }

    public synchronized int init(CL_Agent agent){
        _agent = agent;
        _path = _pathSet.get(_agent.getID());
        int nextNode = moveAgents();
        if(_reset == 100* _whiteList.size()){
            resetLists();
        }
        double slpTime = 100;
        if(_pokemon != null){
            slpTime = getTime(nextNode);
        }
        return (int) slpTime;
    }

    private long getTime(int nextNode){

        double slpTime;
        double speed = _agent.getSpeed();
        edge_data pokEdge = _pokemon.get_edge();
        int currNode = _agent.getSrcNode();
        edge_data currEdge = _graph.getEdge(currNode,nextNode);
        geo_location srcNodePos = _graph.getNode(currNode).getLocation();
        geo_location destNodePos = _graph.getNode(nextNode).getLocation();
        double weight = currEdge.getWeight();
        if((pokEdge.getDest() == currEdge.getDest()) && (pokEdge.getSrc() == currEdge.getSrc())){
            double dist2Pok = _agent.getLocation().distance(_pokemon.getLocation());
            double edgeDist = srcNodePos.distance(destNodePos);
            double ratio = dist2Pok/edgeDist;
            weight *= ratio;
        }
        else{
            double dist2Dest = _agent.getLocation().distance(destNodePos);
            double edgeDist = srcNodePos.distance(destNodePos);
            double ratio = (dist2Dest/edgeDist);
            weight *= ratio;
        }
        weight *= 1000;
        slpTime = weight/speed;
        System.out.println("w:"+weight+", s:"+speed+", t:"+slpTime);
        slpTime++;
        return (long) slpTime;
    }

    private int moveAgents() {
        String lg = _game.move();
        List<CL_Agent> log = Arena.getAgents(lg, _graph);
        _ar.setAgents(log);
        String fs = _game.getPokemons();
        List<CL_Pokemon> ffs = Arena.json2Pokemons(fs);
        _ar.setPokemons(ffs);
        _agent = log.get(_agent.getID());
        int id = _agent.getID();
        int src = _agent.getSrcNode();
        double v = _agent.getValue();
        int dest = this.nextNode(src);
        _game.chooseNextEdge(_agent.getID(), dest);
        System.out.println("Agent: " + id + ", val: " + v + "   turned to node: " + dest);
        return dest;
    }

    private int nextNode(int src) {
        int ans = -1;
        if (_path == null || _path.isEmpty()) {
            List<CL_Pokemon> pokes = _ar.getPokemons();
            int finalDest = findPkm(pokes, src);
            _path = _graphAlgo.shortestPath(src, finalDest);
            for(int i = 0; i < _whiteList.size()-1; i++){
                reserveNextPok(pokes, finalDest);
            }
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

    private int findPkm(List<CL_Pokemon> pokes, int src) {
        int ans = -1;
        double minDist = Integer.MAX_VALUE;
        CL_Pokemon pokemon = null;
        for (CL_Pokemon pok : pokes) {
            String pos = pok.getLocation().toString();
            if(!_blackList.contains(pos) || _whiteList.get(_agent.getID()).contains(pos)){
                edge_data edge = pok.get_edge();
                int edgeDest = edge.getDest();
                if (edgeDest == src) {
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
        if(pokemon != null){
            _blackList.add(_pokemon.getLocation().toString());
            _whiteList.get(_agent.getID()).add(_pokemon.getLocation().toString());
            _reset++;
        }
        return ans;
    }

    private void reserveNextPok(List<CL_Pokemon> pokes, int src) {
        double minDist = Integer.MAX_VALUE;
        CL_Pokemon pokemon = null;
        for (CL_Pokemon pok : pokes) {
            String pos = pok.getLocation().toString();
            if(!_blackList.contains(pos) || _whiteList.get(_agent.getID()).contains(pos)){
                edge_data edge = pok.get_edge();
                int edgeDest = edge.getDest();
                double dist = _graphAlgo.shortestPathDist(src,edgeDest);
                if (dist < minDist && dist != -1) {
                    minDist = dist;
                    pokemon = pok;
                }
            }
        }
        if(pokemon != null){
            _blackList.add(pokemon.getLocation().toString());
            _whiteList.get(_agent.getID()).add(pokemon.getLocation().toString());
            _reset++;
        }
    }

    private void resetLists(){
        _blackList.clear();
        for(int i = 0; i < _whiteList.size(); i++){
            _whiteList.get(i).clear();
        }
        _reset = 0;
    }
}
