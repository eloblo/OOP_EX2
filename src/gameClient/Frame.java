package gameClient;

import api.*;
import gameClient.util.Point3D;
import gameClient.util.Range;
import gameClient.util.Range2D;

import javax.swing.*;
import java.awt.*;
import java.util.Iterator;
import java.util.List;

/**
 * This class represents a very simple GUI class to present a
 * game on a graph. an improved MyFrame class.
 * shows the graph, agents, pokemons, score and a timer
 * to the end of the game.
 */
public class Frame extends JFrame{
    private Arena _ar;                          //game arena
    private gameClient.util.Range2Range _w2f;   //a util class for resizing the graph when resizing the window
    private game_service _game;                 //game service
    private Image graphImg;                     //the image of the graph and his components
    private int _scenario;                      //game level

    /** a constructor fpr initializing the frame variables.
     * @param a the name of the frame.
     * @param game the game service object.
     * @param level the current level.
     */
    Frame(String a, game_service game, int level) {
        super(a);            //using the the super's constructor
        _game = game;
        _scenario = level;
    }

    /** updates the frame..
     *  resizing the graph to the current size of the frame window.
     * @param ar the current game arena
     */
    public void update(Arena ar) {
        this._ar = ar;
        updateFrame();
    }


     //using the w2f to resize the graph. updates the range of the graph.
    private void updateFrame() {
        Range rx = new Range(20,this.getWidth()-20);     //calculate the new range for the graph.
        Range ry = new Range(this.getHeight()-10,150);
        Range2D frame = new Range2D(rx,ry);
        directed_weighted_graph g = _ar.getGraph();
        _w2f = Arena.w2f(g,frame);
    }

    /** paints the game on the frame.
     *  draws, redraws all the game elements in the current states.
     * @param g the main graphics object.
     */
    public void paint(Graphics g) {
        int w = this.getWidth();                     //scale the image to window size
        int h = this.getHeight();
        graphImg = this.createImage(w,h);
        Graphics graphics = graphImg.getGraphics(); //create an image of the graph that we will paint on
        paintComponents(graphics);
        g.drawImage(graphImg,0,0,this);
        updateFrame();                              //update the frame image in case of a resize
    }

    /** paints all the game components on the games GUI
     * @param g current graphics object.
     */
    @Override
    public void paintComponents(Graphics g){
        drawPokemons(g);
        drawGraph(g);
        drawAgents(g);
        drawInfo(g);
        drawTimer(g);
        drawScore(g);
    }

    // draws the game current level.
    private void drawInfo(Graphics g) {
        g.setFont(new Font("Arial",Font.BOLD,36));
        g.drawString("Level: "+_scenario,this.getWidth()/2 - 40, 70);
    }

    // draws the game's timer to the end of the game.
    private void drawTimer(Graphics g){
        g.setFont(new Font("Arial",Font.BOLD,36));
        int sec = (int) (_game.timeToEnd()/1000);   //calculate the amount of seconds
        int min = (int) (_game.timeToEnd()/60000);  //calculate the amount of minutes
        String time = min+":"+sec;                  //timer format
        g.drawString(time,20,70);
    }

    /* draws the agents' total score,
     * in addition of the score of each individual agent.
     */
    private void drawScore(Graphics g){
        List<CL_Agent> agents = _ar.getAgents();
        double totalScore = 0;
        double score;
        for(CL_Agent agent : agents){           //for each agent draw his score and add it to the total score
            totalScore += agent.getValue();
            score = agent.getValue();
            g.setFont(new Font("Arial",Font.BOLD,16));
            g.drawString("agent "+agent.getID()+": "+String.valueOf(score), this.getWidth()-125, 90+20*agent.getID());  //arrange the agents by their id
        }
        g.setFont(new Font("Arial",Font.BOLD,36));
        g.drawString(String.valueOf(totalScore), this.getWidth()-100, 70);
    }

    //draws the current graph and his components.
    private void drawGraph(Graphics g) {
        directed_weighted_graph gg = _ar.getGraph();
        Iterator<node_data> iter = gg.getV().iterator();
        while(iter.hasNext()) {           //draw every node
            node_data n = iter.next();
            g.setColor(Color.blue);
            drawNode(n,5,g);
            Iterator<edge_data> itr = gg.getE(n.getKey()).iterator();
            while(itr.hasNext()) {         //draw each node's edges
                edge_data e = itr.next();
                g.setColor(Color.gray);
                drawEdge(e, g);
            }
        }
    }

    //draws the pokemons on the graph.
    private void drawPokemons(Graphics g) {
        List<CL_Pokemon> fs = _ar.getPokemons();
        if(fs!=null) {                                      //check if we didn't run out of pokemons
            Iterator<CL_Pokemon> itr = fs.iterator();
            while(itr.hasNext()) {                          //draw each pokemon on his location
                CL_Pokemon f = itr.next();
                Point3D c = f.getLocation();
                int r=10;
                g.setColor(Color.green);
                if(f.getType()<0) {g.setColor(Color.orange);}
                if(c!=null) {
                    geo_location fp = this._w2f.world2frame(c);
                    g.fillOval((int)fp.x()-r, (int)fp.y()-r, 2*r, 2*r);
                }
            }
        }
    }

    //draws the game's agents.
    private void drawAgents(Graphics g) {
        List<CL_Agent> rs = _ar.getAgents();
        g.setColor(Color.red);
        int i=0;
        while(rs!=null && i<rs.size()) {                 //if there are agents to draw, draw them
            geo_location c = rs.get(i).getLocation();
            int r=8;
            i++;
            if(c!=null) {       //draw the agent on his current location
                geo_location fp = this._w2f.world2frame(c);
                g.fillOval((int)fp.x()-r, (int)fp.y()-r, 2*r, 2*r);
            }
        }
    }

    //draws the graph's nodes on their respective location.
    private void drawNode(node_data n, int r, Graphics g) {
        geo_location pos = n.getLocation();
        geo_location fp = this._w2f.world2frame(pos);        //check the frame's ratio to match the node's location to the frame's
        g.fillOval((int)fp.x()-r, (int)fp.y()-r, 2*r, 2*r);
        g.drawString(""+n.getKey(), (int)fp.x(), (int)fp.y()-4*r);
    }

    //draws the graph's edges in their respective location.
    private void drawEdge(edge_data e, Graphics g) {
        directed_weighted_graph gg = _ar.getGraph();
        geo_location s = gg.getNode(e.getSrc()).getLocation();
        geo_location d = gg.getNode(e.getDest()).getLocation();
        geo_location s0 = this._w2f.world2frame(s);                  //matches the edge's location to the frame's size
        geo_location d0 = this._w2f.world2frame(d);
        g.drawLine((int)s0.x(), (int)s0.y(), (int)d0.x(), (int)d0.y());
    }
}
