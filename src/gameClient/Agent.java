package gameClient;

import api.*;

import java.util.ArrayList;
import java.util.List;

public class Agent implements Runnable {

    private static Arena _ar;
    private CL_Agent _agent;
    private List<node_data> _path;
    private List<CL_Pokemon> _pokemons;
    private CL_Pokemon _pokemon;
    private static directed_weighted_graph _graph;
    private static DWGraphs_Algo _graphAlgo;
    private static game_service _game;
    private static Mover _mover;

    public Agent(Arena ar, CL_Agent agent, directed_weighted_graph graph, game_service game, Mover mover){
        _ar = ar;
        _agent = agent;
        _graph = graph;
        _game = game;
        _mover = mover;
        _graphAlgo = new DWGraphs_Algo(new DWGraph_DS());
        DWGraph_DS temp = _graphAlgo.caster(_graph);
        _graphAlgo.init(temp);
    }

    @Override
    public void run() {
        int dt = 10;
        while(_game.isRunning()){
            _path = _mover.init(_agent, _pokemon, _path);
            try {
                Thread.sleep(dt);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}