package gameClient;

import api.*;

import java.util.HashSet;

/** the class is Runnable object for CL_Agent class.
 *  calls it's specific agent to move according to it's
 *  position and sleep until the agent is calculated to
 *  reach is goal. runs until the game ends
 */
public class Agent implements Runnable {

    private CL_Agent _agent;           //game agent
    private CL_Pokemon _prevPok;       //previous targeted pokemon
    private edge_data _prevEdge;       //the previous edge the agent visited
    private HashSet<String> whiteList; //list of pokemon that are allowed to be targeted
    private static game_service _game; //game service
    private static Mover _mover;       //the mover object

    public Agent(CL_Agent agent, game_service game, Mover mover){
        _agent = agent;
        _game = game;
        _mover = mover;
        whiteList = new HashSet<>();
    }

    @Override
    public void run() {
        long dt = 0;
        while(_game.isRunning()){
            dt = _mover.init(this, _agent);   //tell the agent to move and receives
            try {                       //sleeping time to reduce unnecessary movement calls
                Thread.sleep(dt);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
    public CL_Pokemon getPrevPok() {
        return _prevPok;
    }

    public void setPrevPok(CL_Pokemon _prevPok) {
        this._prevPok = _prevPok;
    }

    public edge_data getPrevEdge() {
        return _prevEdge;
    }

    public void setPrevEdge(edge_data _prevEdge) {
        this._prevEdge = _prevEdge;
    }

    public HashSet<String> getWhiteList() {
        return whiteList;
    }

    public void setAgent(CL_Agent agent) {
        _agent = agent;
    }
}