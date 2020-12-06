package api;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

class NodeData implements node_data{

    private int _key;
    private double _weight = 0;
    private int _tag = 0;
    private String _info;
    private geo_location _pos = new GeoLocation(0,0,0);
    private HashMap<Integer, edge_data> _edgesDest = new HashMap<>();
    private HashMap<Integer, edge_data> _edgesSrc = new HashMap<>();
    private LinkedList<edge_data> _E = new LinkedList<>();
    private LinkedList<edge_data> _BE = new LinkedList<>();

    public NodeData(int key){
        _key = key;
    }

    @Override
    public int getKey() {
        return _key;
    }

    @Override
    public geo_location getLocation() {
        return _pos;
    }

    @Override
    public void setLocation(geo_location p) {
        _pos = p;
    }

    public void addEdge(edge_data e){
        int dest = e.getDest();
        if(!_edgesDest.containsKey(dest)){
            _edgesDest.put(dest, e);
            _E.add(e);
        }
    }

    public void backEdge(edge_data e){
        int src = e.getSrc();
        if(!_edgesSrc.containsKey(src)){
            _edgesSrc.put(src,e);
            _BE.add(e);
        }
    }

    public void removeBackEdge(int src){
        if(_edgesSrc.containsKey(src)){
            edge_data edge = _edgesSrc.get(src);
            _edgesSrc.remove(src);
            _BE.remove(edge);
        }
    }

    public edge_data removeEdge(int dest){
        if(_edgesDest.containsKey(dest)){
            edge_data edge = _edgesDest.get(dest);
            _edgesDest.remove(dest);
            _E.remove(edge);
            return edge;
        }
        return null;
    }

    public edge_data getEdge(int dest){
        if(_edgesDest.containsKey(dest)){
            return _edgesDest.get(dest);
        }
        return null;
    }

    public boolean hasEdge(int dest){
        return _edgesDest.containsKey(dest);
    }

    public Collection<edge_data> getE(){
        return _E;
    }

    public Collection<edge_data> getBE(){
        return _BE;
    }

    @Override
    public double getWeight() {
        return _weight;
    }

    @Override
    public void setWeight(double w) {
        _weight = w;
    }

    @Override
    public String getInfo() {
        return _info;
    }

    @Override
    public void setInfo(String s) {
        _info = s;
    }

    @Override
    public int getTag() {
        return _tag;
    }

    @Override
    public void setTag(int t) {
        _tag = t;
    }
    /* a toString function.
     * return a string that hold all the information of the node
     * prints the node's key and all his neighbors and edges
     */
    public String toString(){
        String info = "[" + _key + "]:";                        //set the string header with the key
        for(edge_data e : _E){                         //for every neighbor in the collection
            info += " ["+e.getDest()+","+e.getWeight()+"]";  //we add it to info string
        }
        info +=". tag = " + _tag + ", ";                         //adds the tag to close the string
        info +="weight = " + _weight + ". ";
        info += _pos;
        return info;
    }
}
