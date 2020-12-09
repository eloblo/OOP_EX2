package gameClient;

import api.*;

public class Agent implements Runnable {

    private CL_Agent _agent;
    private static game_service _game;
    private static Mover _mover;

    public Agent(CL_Agent agent, game_service game, Mover mover){
        _agent = agent;
        _game = game;
        _mover = mover;
    }

    @Override
    public void run() {
        long dt = 0;
        while(_game.isRunning()){
            dt = _mover.init(_agent);
            try {
                Thread.sleep(dt);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}