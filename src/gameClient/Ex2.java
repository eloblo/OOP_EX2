package gameClient;

import Server.Game_Server_Ex2;
import api.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class Ex2 implements Runnable{

    private static Frame _win;
    private static Arena _ar;
    private static int _id = -1;
    private static int _scenario = 0;
    private static int moves = 0;
    private static List<node_data> _path = null;
    private static directed_weighted_graph _graph;

    public static void main(String[] a) {
        LoginPanel log = new LoginPanel();
        log.loginPanel();
        Thread client = new Thread(new Ex2());
        while(log.isOpen()){
            try {
                client.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        _id = log.getID();
        _scenario = log.getScenario();
        log.dispose();
        client.start();
    }

    @Override
    public void run() {
        game_service game = Game_Server_Ex2.getServer(_scenario); // you have [0,23] games
        if(_id > -1){
            int id = _id;
            game.login(id);
        }
        init(game);

        game.startGame();
        _win.setTitle("Ex2 - OOP: (NONE trivial Solution) "+game.toString());
        int ind=0;
        long dt=100;

        while(game.isRunning()) {
            moveAgents(game);
            try {
                if(ind%1==0) {_win.repaint();}
                Thread.sleep(dt);
                ind++;
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
        String res = game.toString();

        System.out.println(res);
        System.exit(0);
    }
    /**
     * Moves each of the agents along the edge,
     * in case the agent is on a node the next destination (next edge).
     * @param game
     * @param gg
     * @param
     */
    private void moveAgents(game_service game) {
        String lg = game.move();
        moves++;
       // System.out.println(moves);                         //count the moves rate
        List<CL_Agent> log = Arena.getAgents(lg, _graph);
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
                dest = nextNode(src);
                game.chooseNextEdge(ag.getID(), dest);
                System.out.println("Agent: "+id+", val: "+v+"   turned to node: "+dest);
            }
        }
    }

    private static int nextNode(int src) {
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

    private void init(game_service game) {
        String g = game.getGraph();
        String fs = game.getPokemons();
        DWGraphs_Algo ga = new DWGraphs_Algo();
        _graph = ga.Json2Graph(g);
        _ar = new Arena();
        _ar.setGraph(_graph);
        _ar.setPokemons(Arena.json2Pokemons(fs));
        _win = new Frame("test Ex2");
        _win.setSize(1000, 700);
        _win.update(_ar);


        _win.show();
        String info = game.toString();
        JSONObject line;
        try {
            line = new JSONObject(info);
            JSONObject ttt = line.getJSONObject("GameServer");
            int rs = ttt.getInt("agents");
            System.out.println(info);
            System.out.println(game.getPokemons());
            int src_node = 0;  // arbitrary node, you should start at one of the pokemon
            ArrayList<CL_Pokemon> cl_fs = Arena.json2Pokemons(game.getPokemons());
            for(int a = 0;a<cl_fs.size();a++) { Arena.updateEdge(cl_fs.get(a),_graph);}
            for(int a = 0;a<rs;a++) {
                int ind = a%cl_fs.size();
                CL_Pokemon c = cl_fs.get(ind);
                int nn = c.get_edge().getDest();
                if(c.getType()<0 ) {nn = c.get_edge().getSrc();}

                game.addAgent(nn);
            }
        }
        catch (JSONException e) {e.printStackTrace();}
    }
}