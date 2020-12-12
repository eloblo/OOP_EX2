package api;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

/** a node class that will be hold in the DWGraph_DS class.
 */
public class NodeData implements node_data{

    private int _key;               //node's id
    private double _weight = 0;     //mainly for algorithmic purposes
    private int _tag = 0;           //mainly for algorithmic purposes
    private String _info;           //mainly for algorithmic purposes
    private geo_location _pos = new GeoLocation(0,0,0);    //the node's position
    private HashMap<Integer, edge_data> _edgesDest = new HashMap<>(); //the edges the start from this node
    private HashMap<Integer, edge_data> _edgesSrc = new HashMap<>();  //the edges that end in this node
    private LinkedList<edge_data> _E = new LinkedList<>();    //list of the edges the start from this node
    private LinkedList<edge_data> _BE = new LinkedList<>();   //list of the edges that end in this node

    /** constructor of the node sets the node's id.
     *  the id cant be changed after.
     * @param key
     */
    public NodeData(int key){
        _key = key;
    }

    /** @return the node's key */
    @Override
    public int getKey() {
        return _key;
    }

    /**
     * @return return the position of the node.
     */
    @Override
    public geo_location getLocation() {
        return _pos;
    }

    /** set the node's location.
     * @param p - new new location  (position) of this node.
     */
    @Override
    public void setLocation(geo_location p) {
        _pos = p;
    }

    /** add an edge that start from this node.
     *  does nothing if the edge already exist.
     * @param e the edge that connects this node.
     */
    public void addEdge(edge_data e){
        int dest = e.getDest();
        if(!_edgesDest.containsKey(dest)){  //check if the edge exist
            _edgesDest.put(dest, e);        //add the edge
            _E.add(e);
        }
    }

    /** add an edge that ends in this node.
     *  does nothing if the edge already exist.
     * @param e the edge pointing to the node.
     */
    public void backEdge(edge_data e){
        int src = e.getSrc();
        if(!_edgesSrc.containsKey(src)){  //check if the node exist
            _edgesSrc.put(src,e);         //add the edge
            _BE.add(e);
        }
    }

    /** remove the edge pointing to this node.
     *  if the edge does not exist, does nothing.
     * @param src the source node of the edge.
     */
    public void removeBackEdge(int src){
        if(_edgesSrc.containsKey(src)){          //check if the edge exist
            edge_data edge = _edgesSrc.get(src); //remove the edge
            _edgesSrc.remove(src);
            _BE.remove(edge);
        }
    }

    /** remove the edge that start in this node.
     *  if the the node does not exist, does nothing
     * @param dest the destination node of the edge.
     * @return the remove edge. null if none.
     */
    public edge_data removeEdge(int dest){
        if(_edgesDest.containsKey(dest)){             //check if the edge exist
            edge_data edge = _edgesDest.get(dest);    //remove the node
            _edgesDest.remove(dest);
            _E.remove(edge);
            return edge;
        }
        return null;
    }

    /** does nothing if the edge does not exist.
     * @param dest the destination node of the edge.
     * @return the requested edge that ends in dest.
     * @return null if none.
     */
    public edge_data getEdge(int dest){
        if(_edgesDest.containsKey(dest)){  //if exist return edge
            return _edgesDest.get(dest);
        }
        return null;
    }

    /** @return if the edge exist. */
    public boolean hasEdge(int dest){
        return _edgesDest.containsKey(dest);
    }

    /** @return a collection of edges that start from this node. */
    public Collection<edge_data> getE(){
        return _E;
    }

    /** @return a collection of nodes that end in this node. */
    public Collection<edge_data> getBE(){
        return _BE;
    }

    /** @return theweight of the node */
    @Override
    public double getWeight() {
        return _weight;
    }

    /** set the node's weight.
     * @param w - the new weight
     */
    @Override
    public void setWeight(double w) {
        _weight = w;
    }

    /** @return the info of the node. */
    @Override
    public String getInfo() {
        return _info;
    }

    /** sets the node's info.
     * @param s wanted string info.
     */
    @Override
    public void setInfo(String s) {
        _info = s;
    }

    /** @return the tag of the node. */
    @Override
    public int getTag() {
        return _tag;
    }

    /** sets the node's tag.
     * @param t - the new value of the tag
     */
    @Override
    public void setTag(int t) {
        _tag = t;
    }
    /** a toString function.
     * prints the node's key and all his neighbors and edges
     * @return a string that hold all the information of the node
     */
    public String toString(){
        String info = "[" + _key + "]:";                     //set the string header with the key
        for(edge_data e : _E){                               //for every neighbor in the collection
            info += " ["+e.getDest()+","+e.getWeight()+"]";  //we add it to info string
        }
        info +=". tag = " + _tag + ", ";                     //adds the tag to close the string
        info +="weight = " + _weight + ". ";                 //adds the weight of the node
        info += _pos;                                        //adds the position of the node
        return info;
    }
}
