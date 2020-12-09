package gameClient;

import Server.Game_Server_Ex2;
import api.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Ex2 implements Runnable{

    private static Frame _win;
    private static Arena _ar;
    private static int _id = -1;
    private static int _scenario = 0;
    private static Mover _mover;
    private static ArrayList<Thread> _agents = new ArrayList<>();
    private static directed_weighted_graph _graph;

    public static void main(String[] a) {
        LoginPanel log = new LoginPanel();
        log.loginPanel();
        Thread client = new Thread(new Ex2());
        while(log.isOpen()){
            try {
                client.sleep(10);
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
        long dt=1000/60;
        moveAgents(game);
        for(Thread thread : _agents){
            thread.start();
        }
        System.out.println(game.getGraph());
        while(game.isRunning()) {
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

    private void moveAgents(game_service game) {
        String lg = game.move();
        List<CL_Agent> log = Arena.getAgents(lg, _graph);
        _ar.setAgents(log);
        String fs =  game.getPokemons();
        List<CL_Pokemon> ffs = Arena.json2Pokemons(fs);
        _ar.setPokemons(ffs);
        Mover mover = new Mover(_ar,_graph,game,log.size());
        _mover = mover;
        for(int i=0;i<log.size();i++) {
            CL_Agent ag = log.get(i);
            Agent agent = new Agent(_ar,ag,_graph,game,mover);
            Thread thread = new Thread(agent);
            _agents.add(thread);
        }
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