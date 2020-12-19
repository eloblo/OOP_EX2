package api;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

/** a class that represents a directional weighted graph.
 *  build from the NodeData and edge_data classes.
 */
public class DWGraph_DS implements directed_weighted_graph {

    private HashMap<Integer, node_data> _nodes = new HashMap<>(); //map of all the nodes
    private LinkedList<node_data> _V = new LinkedList<>();        //list of the nodes
    private int _EC = 0;   //edge counter
    private int _MC = 0;   //modification counter. for testing purposes

    /**
     * @param key - the node_id
     * @return the node by it's id, null if none.
     */
    @Override
    public node_data getNode(int key) {
        if (_nodes.containsKey(key)) {  //check if exist
            return _nodes.get(key);
        }
        return null;
    }

    /**
     * @param src the source node of the edge.
     * @param dest the destination of the edge
     * @return the requested edge, null if none.
     */
    @Override
    public edge_data getEdge(int src, int dest) {
        if (_nodes.containsKey(src) && _nodes.containsKey(dest) && src != dest) {  //check if exist
            NodeData node = (NodeData) _nodes.get(src);
            return node.getEdge(dest);
        }
        return null;
    }

    /** add a new node to the graph.
     * does nothing if the node already exist.
     * @param n the node object
     */
    @Override
    public void addNode(node_data n) {
        if (!_nodes.containsKey(n.getKey())) { //check if the node exist
            _nodes.put(n.getKey(), n);         //add the node
            _V.add(n);
            _MC++;
        }
    }

    /** connects 2 nodes by the weight and create an edge object.
     *  does nothing if the nodes or the weight are not valid.
     * @param src - the source of the edge.
     * @param dest - the destination of the edge.
     * @param w - positive weight representing the cost (aka time, price, etc) between src-->dest.
     */
    @Override
    public void connect(int src, int dest, double w) {
        if (_nodes.containsKey(src) && _nodes.containsKey(dest) && w > 0 && src != dest) { //check that the parameters are valid
            NodeData srcNode = (NodeData) _nodes.get(src);
            NodeData destNode = (NodeData) _nodes.get(dest);
            if (!srcNode.hasEdge(dest)) {  //check if the node exist
                EdgeData edge = new EdgeData(w, _nodes.get(src), _nodes.get(dest)); //connect the nodes
                srcNode.addEdge(edge);   //connect src
                destNode.backEdge(edge); //connect dest
                _EC++;
                _MC++;
            }
        }
    }

    /** @return a collection of all the nodes. */
    @Override
    public Collection<node_data> getV() {
        return _V;
    }

     /** @param node_id the id of the source node.
     * @return a collection of edges that start from the node.
      * @return an empty list if the node is not valid.
     */
    @Override
    public Collection<edge_data> getE(int node_id) {
        if (_nodes.containsKey(node_id)) {   //check if the node exist
            NodeData node = (NodeData) _nodes.get(node_id);
            return node.getE();  //get the collection from the node
        }
        LinkedList<edge_data> emptyList = new LinkedList<>();
        return emptyList;
    }

    /**
     * @param node_id the id of the destination node
     * @return a collection of all the edges that end in this node.
     * @return an empty list if the node is not valid.
     */
    public Collection<edge_data> getBE(int node_id) {
        if (_nodes.containsKey(node_id)) {   //check if the node exist
            NodeData node = (NodeData) _nodes.get(node_id);
            return node.getBE(); //get the collection from the node
        }
        LinkedList<edge_data> emptyList = new LinkedList<>();
        return emptyList;
    }

    /** remove the node from the graph and the edges
     * he is connected to. if the node is not valid
     * nothing changes.
     * @param key the key of the requested node.
     * @return the removed node. null if the node is not valid.
     */
    @Override
    public node_data removeNode(int key) {
        if (_nodes.containsKey(key)) {       //check if the node exist
            NodeData node = (NodeData) _nodes.get(key);
            Collection<edge_data> edges = node.getE();
            edge_data temp[] = edges.toArray(new edge_data[0]); //remove every edge starting in this node
            for(edge_data e : temp){
                this.removeEdge(e.getSrc(),e.getDest());
            }
            edges = node.getBE();
            temp = edges.toArray(new edge_data[0]);   //remove every node ending in this node
            for(edge_data e : temp){
                this.removeEdge(e.getSrc(),e.getDest());
            }
            _nodes.remove(key);
            _V.remove(node);
            _MC++;
            return node;
        }
        return null;
    }

    /** remove the requested edge from the graph.
     * @param src the source node of the edge.
     * @param dest the destination node of the edge.
     * @return the removed edge. null if the edge doesn't exist.
     */
    @Override
    public edge_data removeEdge(int src, int dest) {
        if (_nodes.containsKey(src) && _nodes.containsKey(dest)) { //check if the nodes are valid
            NodeData srcNode = (NodeData) _nodes.get(src);
            NodeData destNode = (NodeData) _nodes.get(dest);
            destNode.removeBackEdge(src);               //remove the edge from the dest node;
            edge_data edge = srcNode.removeEdge(dest);  //remove the edge from the src node
            if(edge != null) {
                _EC--;                     //if the nodes are connected by an edge
                _MC++;
            }
            return edge;
        }
        return null;
    }

    /** @return the number of nodes in the graph. */
    @Override
    public int nodeSize() {
        return _V.size();
    }

    /** @return the number of edges in the graph. */
    @Override
    public int edgeSize() {
        return _EC;
    }

    /** @return the number of modifications done to the graph. */
    @Override
    public int getMC() {
        return _MC;
    }

    /** a toString function for the graph.
     * prints the number of nodes edges in the graph,
     * along with a list of every node and his neighbors edges and tags.
     * @return
     */
    public String toString(){
        String sGraph = "nodes: "+_nodes.size()+", edges: "+_EC;     //the basic information of the graph
        Collection<node_data> graphList = this.getV();
        for(node_data node : graphList){                                 //for every node in the graph
            NodeData nodeI = (NodeData) node;                            //cast it to NodeInfo
            sGraph += "\n" + nodeI.toString();                           //add its information to the main info String
        }
        return sGraph;
    }

    ///////////////////////EdgeData_class////////////////////////////////

    /* a private implementation of edge_data.
     * is used to represent the edges of DWGraph_DS class.
     */
    private class EdgeData implements edge_data {

        private node_data _src, _dest;  //the source and destination nodes
        private double _weight;         //weight of the edge
        private int _tag = 0;           //tag for algorithmic purposes
        private String _info;          //edge's info for algorithmic purposes

        //a constructor to set edge.
        public EdgeData(double weight, node_data src, node_data dest) {
            _weight = weight;
            _src = src;
            _dest = dest;
        }

        //return the id of the src node
        @Override
        public int getSrc() {
            return _src.getKey();
        }

        //return the id of the dest node
        @Override
        public int getDest() {
            return _dest.getKey();
        }

        //return the weight of the edge
        @Override
        public double getWeight() {
            return _weight;
        }

        //return the info of the edge
        @Override
        public String getInfo() {
            return _info;
        }

        //set the info of the edge
        @Override
        public void setInfo(String s) {
            _info = s;
        }

        //return the tag of the edge
        @Override
        public int getTag() {
            return _tag;
        }

        //set the edge's tag
        @Override
        public void setTag(int t) {
            _tag = t;
        }
    }
}


