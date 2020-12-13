package gameClient;

import Server.Game_Server_Ex2;
import api.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/** main class. launches the game by setting the
 *  graph agents and pokemons, closes when the
 *  time is up and game finishes. is also responsible
 *  for opening the login GUI and game GUI, in addition
 *  to repaint and update the GUI.
 */
public class Ex2 implements Runnable{

    private static Frame _win;                                     //game window
    private static Arena _ar;                                      //game arena
    private static long _id = -1;                                  //login id
    private static int _scenario = 0;                              //game level [0,23]
    private static ArrayList<Thread> _agents = new ArrayList<>();  //list of agent threads
    private static directed_weighted_graph _graph;                 //the graph shown

    /** main method, launches the game and the GUIs.
     * @param a a[0] = id, a[1] = level.
     * if there are no parameter launches login window.
     */
    public static void main(String[] a) {
        Thread client = new Thread(new Ex2());
        if(a.length == 0){                         //check if there are given arguments
            LoginPanel log = new LoginPanel();
            log.loginPanel();                      //launches the login GUI
            while(log.isOpen()){                   //wait for the user to choose his options
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            _id = log.getID();
            _scenario = log.getScenario();
            log.dispose();                        //set the id and level and closes the login GUI
        }
        else{
            _id = Long.parseLong(a[0]);          //in case of command arguments just set them
            _scenario = Integer.parseInt(a[1]);  //without a UI
        }
        client.start();
    }

    /** run method: sets the level and logs in.
     *  initialize the game and repaint the UI.
     */
    @Override
    public void run() {
        game_service game = Game_Server_Ex2.getServer(_scenario);  //set level and login if there is id
        if(_id > -1){
            long id = _id;
            game.login(id);
        }
        init(game);

        game.startGame();
        _win.setTitle("Ex2 - OOP: Gotta catch them all! "+game.toString());
        int ind=0;
        long dt=1000/60;  //frame rates 60FPS
        moveAgents(game); //after the game was set, initialize the agents and start them
        for(Thread thread : _agents){
            thread.start();
        }
        while(game.isRunning()) {
            try {
                if(ind%1==0) {_win.repaint();}  //repaint the window in the wanted rate
                Thread.sleep(dt);
                ind++;
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
        String res = game.toString();    //print result and exit
        System.out.println(res);
        System.exit(0);
    }

    // first move, sets the game arena and agents.
    private void moveAgents(game_service game) {
        String lg = game.move();                             //get game data from the server
        List<CL_Agent> log = Arena.getAgents(lg, _graph);    //set agents
        _ar.setAgents(log);
        String fs =  game.getPokemons();                     //set pokemons
        List<CL_Pokemon> ffs = Arena.json2Pokemons(fs);
        _ar.setPokemons(ffs);
        Mover mover = new Mover(_ar,_graph,game,log.size()); //the object responsible to move the agents
        for(int i=0;i<log.size();i++) {                      //initialize the agents' threads
            CL_Agent ag = log.get(i);
            Agent agent = new Agent(ag,game,mover);
            Thread thread = new Thread(agent);
            _agents.add(thread);
        }
    }

    //initialize the game's elements and GUI.
    private void init(game_service game) {
        String g = game.getGraph();
        String fs = game.getPokemons();
        DWGraphs_Algo ga = new DWGraphs_Algo();
        _graph = ga.Json2Graph(g);               //set game graph
        _ar = new Arena();                       //set game arena
        _ar.setGraph(_graph);
        _ar.setPokemons(Arena.json2Pokemons(fs));
        _win = new Frame("Ex2", game);        //set game GUI
        _win.setSize(1000, 700);
        _win.update(_ar);
        _win.show();
        String info = game.toString();   //build the game from the server
        JSONObject line;
        try {
            line = new JSONObject(info);
            JSONObject ttt = line.getJSONObject("GameServer");
            int rs = ttt.getInt("agents");
            System.out.println(info);
            System.out.println(game.getPokemons());
            ArrayList<CL_Pokemon> cl_fs = Arena.json2Pokemons(game.getPokemons());
            for(int a = 0;a<cl_fs.size();a++) { Arena.updateEdge(cl_fs.get(a),_graph);}
            for(int a = 0;a<rs;a++) {    //default setting of the agents on the graph
                int ind = a%cl_fs.size();
                CL_Pokemon c = cl_fs.get(ind);
                int nn = c.get_edge().getSrc(); //mainly close to pokemons and far apart
                game.addAgent(nn);
            }
        }
        catch (JSONException e) {e.printStackTrace();}
    }
}