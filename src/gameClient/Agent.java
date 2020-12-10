package gameClient;

import api.*;

/** the class is Runnable object for CL_Agent class.
 *  calls it's specific agent to move according to it's
 *  position and sleep until the agent is calculated to
 *  reach is goal. runs until the game ends
 */
public class Agent implements Runnable {

    private CL_Agent _agent;           //game agent
    private static game_service _game; //game service
    private static Mover _mover;       //the mover object

    public Agent(CL_Agent agent, game_service game, Mover mover){
        _agent = agent;
        _game = game;
        _mover = mover;
    }

    @Override
    public void run() {
        long dt = 0;
        while(_game.isRunning()){
            dt = _mover.init(_agent);   //tell the agent to move and receives
            try {                       //sleeping time to reduce unnecessary movement calls
                Thread.sleep(dt);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}