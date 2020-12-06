package gameClient;

import api.*;

import java.util.ArrayList;
import java.util.List;

public class Agent implements Runnable {

    private static Arena _ar;
    private static CL_Agent _agent;
    private int moves = 0;
    private static List<node_data> _path = null;

    @Override
    public void run() {

    }

    private void moveAgents(game_service game, directed_weighted_graph gg) {
        String lg = game.move();
        moves++;
      //  System.out.println(moves);                         //count the moves rate
        List<CL_Agent> log = Arena.getAgents(lg, gg);
        _ar.setAgents(log);
        String fs =  game.getPokemons();
        List<CL_Pokemon> ffs = Arena.json2Pokemons(fs);
        _ar.setPokemons(ffs);
        for(int i=0;i<log.size();i++) {
            CL_Agent ag = log.get(i);
            int id = ag.getID();
            int dest = ag.getNextNode();
            int src = ag.getSrcNode();
            double v = ag.getValue();
            if(dest==-1) {
                dest = nextNode(gg, src);
                game.chooseNextEdge(ag.getID(), dest);
                System.out.println("Agent: "+id+", val: "+v+"   turned to node: "+dest);
            }
        }
    }
    /**
     * a very simple random walk implementation!
     * @param g
     * @param src
     * @return
     */
    private static int nextNode(directed_weighted_graph g, int src) {
        int ans = -1;
        if(_path == null || _path.isEmpty()){
            List<CL_Pokemon> pokes = _ar.getPokemons();
            DWGraphs_Algo ga = new DWGraphs_Algo(new DWGraph_DS());
            DWGraph_DS newGr = ga.caster(_ar.getGraph());
            ga.init(newGr);
            ans = findPkm(pokes, src, ga);
            _path = ga.shortestPath(src,ans);
            _path.remove(0);
        }
        ans = _path.get(0).getKey();
        _path.remove(0);
        return ans;
    }

    private static int findPkm(List<CL_Pokemon> pokes, int src, DWGraphs_Algo ga){
        ArrayList<Integer> destinations = new ArrayList<>();
        for(CL_Pokemon pok : pokes){
            edge_data edge = pok.get_edge();
            int dest = edge.getDest();
            if(dest == src){
                return edge.getSrc();
            }
            destinations.add(dest);
        }
        int ans = -1;
        double minDist = Integer.MAX_VALUE;
        for(int dest : destinations){
            double dist = ga.shortestPathDist(src, dest);
            if(dist < minDist && dist != -1){
                minDist = dist;
                ans = dest;
            }
        }
        return ans;
    }
}
