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
    private boolean _inSight = false;  //a flag for if there is a pokemon in front of the agent
    private int reset = 0;             //a reset for managing congestion

    /** agent's constructor. initialize all variables
     * @param agent the CL_Agent that the Agent is managing.
     * @param game the main game service.
     * @param mover the agent moving algorithm.
     */
    public Agent(CL_Agent agent, game_service game, Mover mover){
        _agent = agent;
        _game = game;
        _mover = mover;
        whiteList = new HashSet<>();
    }

    /** get the sleeping time from mover and wait
     *  to move the agent again.
     */
    @Override
    public void run() {
        long dt;
        while(_game.isRunning()){
            dt = _mover.init(this, _agent);   //tell the agent to move and receives
            reset++;
            if(reset == 100){     //reached cap reset the whiteList
                whiteList.clear();
            }
            try {                       //sleeping time to reduce unnecessary movement calls
                Thread.sleep(dt);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    /** @return the previous pokemon targeted. */
    public CL_Pokemon getPrevPok() {
        return _prevPok;
    }

    /** sets the new previous pokemon that was targeted.
     * @param _prevPok the previous pokemon that was targeted.
     */
    public void setPrevPok(CL_Pokemon _prevPok) {
        this._prevPok = _prevPok;
    }

    /**
     * @return the previous edge the agent passed.
     */
    public edge_data getPrevEdge() {
        return _prevEdge;
    }

    /** sets the previous edge visited.
     * @param _prevEdge the previous edge that was passed.
     */
    public void setPrevEdge(edge_data _prevEdge) {
        this._prevEdge = _prevEdge;
    }

    /**
     * @return a white list of exclusive pokemons to be targeted.
     */
    public HashSet<String> getWhiteList() {
        return whiteList;
    }

    /** sets a new agent to mange.
     *  mainly for updating the agent after the game's move().
     * @param agent the agent to manage
     */
    public void setAgent(CL_Agent agent) {
        _agent = agent;
    }

    /** mainly for mover algorithms.
      * @return if there is a pokemon in sight of the agent.
     */
    public boolean getFlag() {
        return _inSight;
    }

    /** sets if the agent reached a pokemon.
     *  mainly for mover algorithms.
     * @param flag if we reached the pokemon
     */
    public void setFlag(boolean flag) {
        this._inSight = flag;
    }
}