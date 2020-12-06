package api;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

public class DWGraph_DS implements directed_weighted_graph {

    private HashMap<Integer, node_data> _nodes = new HashMap<>();
    private LinkedList<node_data> _V = new LinkedList<>();
    private int _EC = 0;
    private int _MC = 0;

    @Override
    public node_data getNode(int key) {
        if (_nodes.containsKey(key)) {
            return _nodes.get(key);
        }
        return null;
    }

    @Override
    public edge_data getEdge(int src, int dest) {
        if (_nodes.containsKey(src) && _nodes.containsKey(dest) && src != dest) {
            NodeData node = (NodeData) _nodes.get(src);
            return node.getEdge(dest);
        }
        return null;
    }

    @Override
    public void addNode(node_data n) {
        if (!_nodes.containsKey(n.getKey())) {
            _nodes.put(n.getKey(), n);
            _V.add(n);
            _MC++;
        }
    }

    @Override
    public void connect(int src, int dest, double w) {
        if (_nodes.containsKey(src) && _nodes.containsKey(dest) && w > 0 && src != dest) {
            NodeData srcNode = (NodeData) _nodes.get(src);
            NodeData destNode = (NodeData) _nodes.get(dest);
            if (!srcNode.hasEdge(dest)) {
                EdgeData edge = new EdgeData(w, _nodes.get(src), _nodes.get(dest));
                srcNode.addEdge(edge);
                destNode.backEdge(edge);
                _EC++;
            }
        }
    }

    @Override
    public Collection<node_data> getV() {
        return _V;
    }

    @Override
    public Collection<edge_data> getE(int node_id) {
        if (_nodes.containsKey(node_id)) {
            NodeData node = (NodeData) _nodes.get(node_id);
            return node.getE();
        }
        LinkedList<edge_data> emptyList = new LinkedList<>();
        return emptyList;
    }

    public Collection<edge_data> getBE(int node_id) {
        if (_nodes.containsKey(node_id)) {
            NodeData node = (NodeData) _nodes.get(node_id);
            return node.getBE();
        }
        LinkedList<edge_data> emptyList = new LinkedList<>();
        return emptyList;
    }

    @Override
    public node_data removeNode(int key) {
        if (_nodes.containsKey(key)) {
            NodeData node = (NodeData) _nodes.get(key);
            Collection<edge_data> edges = node.getE();
            edge_data temp[] = edges.toArray(new edge_data[0]);
            for(edge_data e : temp){
                this.removeEdge(e.getSrc(),e.getDest());
            }
            edges = node.getBE();
            temp = edges.toArray(new edge_data[0]);
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

    @Override
    public edge_data removeEdge(int src, int dest) {
        if (_nodes.containsKey(src) && _nodes.containsKey(dest)) {
            NodeData srcNode = (NodeData) _nodes.get(src);
            NodeData destNode = (NodeData) _nodes.get(dest);
            destNode.removeBackEdge(src);
            _EC--;
            return srcNode.removeEdge(dest);
        }
        return null;
    }

    @Override
    public int nodeSize() {
        return _V.size();
    }

    @Override
    public int edgeSize() {
        return _EC;
    }

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

    private class EdgeData implements edge_data {

        private node_data _src, _dest;
        private double _weight;
        private int _tag = 0;
        private String _info;

        public EdgeData(double weight, node_data src, node_data dest) {
            _weight = weight;
            _src = src;
            _dest = dest;
        }

        @Override
        public int getSrc() {
            return _src.getKey();
        }

        @Override
        public int getDest() {
            return _dest.getKey();
        }

        @Override
        public double getWeight() {
            return _weight;
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
    }
}


