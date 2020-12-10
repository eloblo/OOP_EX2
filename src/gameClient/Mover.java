package gameClient;

import api.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/** a class to manage the movement of agents, in order
 * to keep agent movements to minimum while keeping the
 * score as high as possible. the class contains "libraries"
 * in form of Hash data structures for every agent data.
 */
public class Mover {

    private static Arena _ar;      //game arena
    private CL_Agent _agent;       //the agent being worked on
    private CL_Pokemon _pokemon;   //the targeted pokemon
    private CL_Pokemon _prevPok;   //previous targeted pokemon
    private edge_data _prevEdge;   //the previous edge the agent visited
    private static HashSet<String> _blackList;      //black list of pokemon the agents cant target
    private static HashMap<Integer,HashSet<String>> _whiteList;  //white list of what pokemon only this agent can target
    private static HashMap<Integer,edge_data> _prevEdges;        //map of prevEdge to each agent
    private static HashMap<Integer,CL_Pokemon> _prevPkList;      //map of prevPok to each agent
    private static directed_weighted_graph _graph;         //game graph
    private static DWGraphs_Algo _graphAlgo;      //graph algorithms to calculate paths
    private static game_service _game;        //game service
    private static int _reset = 0;     //reset counter to reset blackList in case of congestions

    /** constructor of mover. sets all the data from the agents */
    public Mover(Arena ar, directed_weighted_graph graph, game_service game, int numOfAgents){
        _ar = ar;
        _graph = graph;
        _game = game;
        _blackList = new HashSet<>();
        _whiteList = new HashMap<>();
        _graphAlgo = new DWGraphs_Algo(new DWGraph_DS());
        _prevEdges = new HashMap<>();
        _prevPkList = new HashMap<>();
        DWGraph_DS temp = _graphAlgo.caster(_graph);
        _graphAlgo.init(temp);
        for(int i = 0; i < numOfAgents; i++){
            _whiteList.put(i,new HashSet<String>());
        }
    }

    /** the central method of mover.
     *  moves the agents, check for congestions
     *  and check if an agent is stuck or without nodes.
     * @param agent the mover is currently working on.
     * @return the sleeping time of the agent.
     */
    public synchronized int init(CL_Agent agent){
        _agent = agent;                             //set the agent and pulls the relevant data
        _prevEdge = _prevEdges.get(_agent.getID());
        _prevPok = _prevPkList.get(_agent.getID());
        int nextNode = moveAgents();               //move the agent
        if(_reset == 100* _whiteList.size()){      //if reached congestion cap reset
            resetLists();
        }
        double slpTime = 100;    //default sleep time
        try{
            slpTime = getTime(nextNode);  //calculate slpTime
        }
       catch (NullPointerException ne){
            try{
                resetLists();    //reset if the error is from congestion
                slpTime = 0;     //set time to 0 so the agent could run right away
                if(nextNode(_agent.getSrcNode()) == -1){
                    wait();     //if the error is not from congestion wait
                }               //until the agent has a reason to move
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        return (int) slpTime;
    }

    /* calculate the sleeping time of the agents according to their targets.
     * @param nextNode the next node the agent is supposed to move to
     * @return the time the agent need to sleep
     */
    private long getTime(int nextNode){
        int currNode = _agent.getSrcNode();
        edge_data pokEdge = _pokemon.get_edge();
        edge_data currEdge = _graph.getEdge(currNode,nextNode);
        geo_location srcNodePos;
        geo_location destNodePos;
        double edgeDist;  //the edge distance
        double weight = currEdge.getWeight();
        double speed = _agent.getSpeed();  //edge weight per second
        //in case of facing a pokemon calculate how much time needed to reach its position
        if((pokEdge.getDest() == currEdge.getDest()) && (pokEdge.getSrc() == currEdge.getSrc())){
            double dist2Pok = _agent.getLocation().distance(_pokemon.getLocation());
            srcNodePos = _graph.getNode(pokEdge.getSrc()).getLocation();   //sets the variables according to the target
            destNodePos = _graph.getNode(pokEdge.getDest()).getLocation();
            edgeDist = srcNodePos.distance(destNodePos);
            double ratio = dist2Pok/edgeDist;   //calculate the ratio between the distance to the target compare to the edge
            weight = pokEdge.getWeight();
            weight *= ratio;                    //adjust the distance to the weight of the
            _prevEdges.put(_agent.getID(), currEdge);
            _prevPkList.put(_agent.getID(), _pokemon);
        }
        //after collecting all the pokemon on the edge and the agent is still on the edge calculate the time to reach the next node
        else if(_prevEdge != null && _prevEdge.getDest() == _prevPok.get_edge().getDest() && _prevEdge.getSrc() == _prevPok.get_edge().getSrc()){
            notifyAll();   //a new pokemon should appear notify waiting agents
            weight = _prevEdge.getWeight();                               //reset the function variable to match the previous edge
            currNode = _graph.getNode(_prevEdge.getSrc()).getKey();       //because if an agents is n a middle of en edge he forgets
            nextNode = _graph.getNode(_prevEdge.getDest()).getKey();      //his current edge
            srcNodePos = _graph.getNode(currNode).getLocation();
            destNodePos = _graph.getNode(nextNode).getLocation();
            edgeDist = srcNodePos.distance(destNodePos);
            double dist2Dest = destNodePos.distance(_agent.getLocation());//calculate the distance to the next node
            double ratio = dist2Dest/edgeDist;                            //ratio of the distance to the edge
            weight *= ratio;                                              //adjust the weight to the to the distance
            _prevEdges.put(_agent.getID(),_pokemon.get_edge());           //reset the prevEdge
        }
        weight *= 1000;                 //convert the weight and speed to milliseconds
        double slpTime = weight/speed;
        slpTime++;
        return (long) slpTime;
    }

    /* communicate with the server to move and get
     * current position of elements on the graph.
     * @return the next node in the path of the agent
     */
    private int moveAgents() {
        String lg = _game.move();                           // move and get current information of the game elements
        List<CL_Agent> log = Arena.getAgents(lg, _graph);   //set the arena according to the server
        _ar.setAgents(log);
        String fs = _game.getPokemons();
        List<CL_Pokemon> ffs = Arena.json2Pokemons(fs);
        _ar.setPokemons(ffs);
        _agent = log.get(_agent.getID());   //update the agent
        int id = _agent.getID();
        int src = _agent.getSrcNode();
        double v = _agent.getValue();
        int dest = this.nextNode(src);      //set the next node of the agent
        _game.chooseNextEdge(_agent.getID(), dest);
        System.out.println("Agent: " + id + ", val: " + v + "   turned to node: " + dest);  //print current game status
        return dest;
    }

    /* calculate the path of the agent.
     * @param src the starting node of the agent
     * @return the next node the agent's path
     */
    private int nextNode(int src) {
        int ans = -1;  //default answer
            List<CL_Pokemon> pokes = _ar.getPokemons();   //calculate what pokemon to pursue
            int finalDest = findPkm(pokes, src);
            List<node_data> path = _graphAlgo.shortestPath(src, finalDest);  //calculate movement path
            for(int i = 0; i < _whiteList.size()-1; i++){    //reserve pokemon so the agents would not
                reserveNextPok(pokes, finalDest);           //go after the same pokemon like its a competition
            }
            if(path != null){           //remove the source node from the path
                path.remove(0);
                ans = path.get(0).getKey();
            }
        return ans;
    }

    /* finds the closet pokemon with the highest value and reserve it.
     * @param pokes a list of all current pokemon on the graph.
     * @param src the node that the agent is currently on.
     * @return the node on which the pokemon is on
     */
    private int findPkm(List<CL_Pokemon> pokes, int src) {
        int ans = -1;  //default answer
        double minDist = Integer.MAX_VALUE;  //max value for comparison
        for (CL_Pokemon pok : pokes) {       //search for the closest pokemon
            String pos = pok.getLocation().toString();  //used to identify the pokemon as they don't have id
            if(!_blackList.contains(pos) || _whiteList.get(_agent.getID()).contains(pos)){  //check if the pokemon isn't targeted
                edge_data edge = pok.get_edge();
                int edgeSrc = edge.getSrc();
                int edgeDest = edge.getDest();
                double value = edge.getWeight();
                if (edgeDest == src) {  //in case of a U turn
                    return edge.getSrc();
                }
                double dist = _graphAlgo.shortestPathDist(src,edgeSrc);  //calculate the distance
                if(dist != -1){                //check if there is distance
                    dist += edge.getWeight();  //adds the last edge weight in case of a U turn
                    dist /= value;             //sets the distance by value
                }
                if (dist < minDist && dist > 0) {  //checks if the distance is the smallest
                    minDist = dist;
                    ans = edge.getDest();;
                    _pokemon = pok;               //sets the pokemon as the current target
                }
            }
        }
        if(_pokemon != null){  //if there is a relevant target, reserve it
            _blackList.add(_pokemon.getLocation().toString());
            _whiteList.get(_agent.getID()).add(_pokemon.getLocation().toString());
            _reset++;  //increased congestion
        }
        return ans;
    }

    /* reserve another target for the agent closest
     *  to it's current target.
     * @param pokes list of all the pokemon currently on the graph.
     * @param src the node of the agent.
     */
    private void reserveNextPok(List<CL_Pokemon> pokes, int src) {
        double minDist = Integer.MAX_VALUE;   //max value for comparison
        CL_Pokemon pokemon = null;            //a fictive replacement for _pokemon
        for (CL_Pokemon pok : pokes) {        //search for closest pokemon
            String pos = pok.getLocation().toString();  //used to identify the pokemon as they don't have id
            if(!_blackList.contains(pos) || _whiteList.get(_agent.getID()).contains(pos)){ //check if the pokemon isn't targeted
                edge_data edge = pok.get_edge();
                int edgeSrc = edge.getSrc();
                double value = edge.getWeight();
                double dist = _graphAlgo.shortestPathDist(src,edgeSrc); //calculate the distance
                if(dist != -1){                    //check if there is distance
                    dist += edge.getWeight();      //adds the last edge weight in case of a U turn
                    dist /= value;                 //sets the distance by value
                }
                if (dist < minDist && dist > 0) {  //checks if the distance is the smallest
                    minDist = dist;
                    pokemon = pok;
                }
            }
        }
        if(pokemon != null){    //if there is a relevant target reserve it
            _blackList.add(pokemon.getLocation().toString());
            _whiteList.get(_agent.getID()).add(pokemon.getLocation().toString());
            _reset++;   //increased congestion
        }
    }

    //resets all the list to reset pokemon congestion.
    private void resetLists(){
        _blackList.clear();
        for(int i = 0; i < _whiteList.size(); i++){
            _whiteList.get(i).clear();
        }
        _reset = 0;
    }
}
