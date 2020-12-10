package api;

import com.google.gson.*;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/** a class of algorithms for the DWGraph_DS class.
 *  contains methods to check connectivity, distance & path,
 *  cast to DWGraph_DS, copy, save and load graphs from Json files.
 */
public class DWGraphs_Algo implements dw_graph_algorithms {

    private DWGraph_DS _graph;                   //the graph that we are working on
    NodeComparator _comp = new NodeComparator(); //a custom comparator for to compare between node in the algorithms

    /** empty constructor.
     *  methods will not work until inner graph is
     *  initialized, except init() and cast().
     */
    public DWGraphs_Algo(){}

    /** basic constructor, sets the graph as inner graph */
    public DWGraphs_Algo(directed_weighted_graph g){
        _graph = (DWGraph_DS) g;
    }

    /** set g as the new graph */
    @Override
    public void init(directed_weighted_graph g) {
        _graph = (DWGraph_DS) g;
    }

    /** return the current graph */
    @Override
    public directed_weighted_graph getGraph() {
        return _graph;
    }

    /** returns a copy of the graph as an object object,
     * instead of a shallow pointer.
     * @return copy of the graph as an object of directed_weighted_graph.
     */
    @Override
    public directed_weighted_graph copy() {
        DWGraph_DS newGr = new DWGraph_DS();
        Collection<node_data> nodes = _graph.getV();                       //get a list of all the nodes in the main graph
        for(node_data node : nodes){                                       //for every node in the graph, create a new node for newGr
            newGr.addNode(new NodeData(node.getKey()));
        }
        for(node_data node: nodes){                                        //for every node in the graph, copy its edges
            Collection<edge_data> edges = _graph.getE(node.getKey());
            for(edge_data e : edges){                                      //for every neighbors a node has, copy its edges
                newGr.connect(e.getSrc(), e.getDest(), e.getWeight());
            }
        }
        return newGr;
    }

    /** cast a directed_weighted_graph object to DWGraph_DS
     *  by making a copy of him of the DWGraph type.
     * @param oldG the graph to be copied
     * @return DWGraph object with equals elements as g
     */
    public DWGraph_DS caster(directed_weighted_graph oldG){
        DWGraph_DS newGr = new DWGraph_DS();
        Collection<node_data> nodes = oldG.getV();                      //get a list of all the nodes in the main graph
        for(node_data node : nodes){                                    //for every node in the graph, create a new node for newGr
            newGr.addNode(new NodeData(node.getKey()));
        }
        for(node_data node: nodes){                                     //for every node in the graph, copy its edges
            Collection<edge_data> edges = oldG.getE(node.getKey());
            for(edge_data e : edges){                                   //for every neighbors a node has, copy its edges
                newGr.connect(e.getSrc(), e.getDest(), e.getWeight());
            }
        }
        return newGr;
    }

    /** checks if every node is connected to every other node.
     * utilizes kosoraju algorithm.
     * @return if the graph is strongly connected
     */
    @Override
    public boolean isConnected() {
        clearTag();                                              //reset tags
        LinkedList<node_data> q = new LinkedList<>();            //queue for the nodes
        List<node_data> nodes = (List<node_data>) _graph.getV();
        if(nodes.size() == 0 || nodes.size() == 1){              //an empty graph or a node are connected
            return true;
        }
        node_data src = nodes.get(0);   //random starting node
        q.add(src);
        while(!q.isEmpty()){            //run until checked every node
            src = q.remove();
            Collection<edge_data> edges = _graph.getE(src.getKey());
            for(edge_data e : edges){
                node_data node = _graph.getNode(e.getDest());
                if(node.getTag() == 0){
                    q.add(node);
                }
                node.setTag(1);
            }
        }
        for(node_data node1 : nodes){
            if(node1.getTag() == 0){
                return false;
            }
        }
        clearTag();
        q = new LinkedList<node_data>();
        src = nodes.get(0);
        q.add(src);
        while(!q.isEmpty()){
            src = q.remove();
            Collection<edge_data> edges = _graph.getBE(src.getKey());
            for(edge_data e : edges){
                node_data node = _graph.getNode(e.getSrc());
                if(node.getTag() == 0){
                    q.add(node);
                }
                node.setTag(1);
            }
        }
        for(node_data node1 : nodes){
            if(node1.getTag() == 0){
                return false;
            }
        }
        return true;
    }

    @Override
    public double shortestPathDist(int src, int dest) {
        if(src == dest){
            return 0;
        }
        this.clearWeight();
        node_data node, nDest;
        node = _graph.getNode(src);
        nDest = _graph.getNode(dest);
        if(node != null && nDest != null){
            node.setInfo("");
            PriorityQueue<node_data> que = new PriorityQueue<>(_graph.nodeSize(), _comp);
            que.add(node);
            while(!que.isEmpty()){
                node = que.remove();
                if(node.getKey() == dest){
                    return node.getWeight();
                }
                Collection<edge_data> edges = _graph.getE(node.getKey());
                for(edge_data e : edges){
                    node_data temp = _graph.getNode(e.getDest());
                    double dist = node.getWeight() + e.getWeight();
                    if((dist < temp.getWeight() || temp.getWeight() == 0 )&& temp.getKey() != src){
                        if(dist < temp.getWeight()){
                            que.remove(temp);
                        }
                        temp.setInfo(node.getInfo() + temp.getKey() + ",");
                        temp.setWeight(dist);
                        que.add(temp);
                    }
                }
            }
        }
        return -1;
    }

    @Override
    public List<node_data> shortestPath(int src, int dest) {
        if(this.shortestPathDist(src, dest) != -1){
            LinkedList<node_data> path = new LinkedList<>();
            node_data node = _graph.getNode(src);
            path.add(node);
            if(src == dest){
                return path;
            }
            String info = _graph.getNode(dest).getInfo();
            while(!info.isEmpty()){
                int divider = info.indexOf(",");
                String key = info.substring(0,divider);
                node = _graph.getNode(Integer.parseInt(key));
                path.add(node);
                info = info.substring(divider+1);
            }
            return path;
        }
        return null;
    }

    @Override
    public boolean save(String file) {
        JsonObject Json_obj = new JsonObject();
        JsonObject Jedge;
        JsonObject Jnode;
        JsonArray edges = new JsonArray();
        JsonArray nodes = new JsonArray();
        Collection<node_data> V = _graph.getV();
        for(node_data node : V){
            Jnode = new JsonObject();
            geo_location GPos = node.getLocation();
            String pos = GPos.x()+","+ GPos.y()+","+ GPos.z();
            Jnode.addProperty("pos",pos);
            Jnode.addProperty("id",node.getKey());
            nodes.add(Jnode);
            Collection<edge_data> Es = _graph.getE(node.getKey());
            for(edge_data edge : Es){
                Jedge = new JsonObject();
                Jedge.addProperty("src", edge.getSrc());
                Jedge.addProperty("w", edge.getWeight());
                Jedge.addProperty("dest", edge.getDest());
                edges.add(Jedge);
            }
        }
        Json_obj.add("Edges",edges);
        Json_obj.add("Nodes",nodes);
        try{
            FileWriter writer = new FileWriter(file);
            writer.write(Json_obj.toString());
            writer.close();
            return true;
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean load(String file) {
        DWGraph_DS newGr = new DWGraph_DS();
        JsonObject json_obj;
        try{
            String Jstr = new String(Files.readAllBytes(Paths.get(file)));
            json_obj = JsonParser.parseString(Jstr).getAsJsonObject();
            JsonArray Jnodes = json_obj.getAsJsonArray("Nodes");
            for(JsonElement node : Jnodes){
                JsonObject temp = (JsonObject) node;
                int id = temp.get("id").getAsInt();
                NodeData newNode = new NodeData(id);
                String pos = temp.get("pos").getAsString();
                int firstComma = pos.indexOf(",");
                int lastComma = pos.lastIndexOf(",");
                double x = Double.parseDouble(pos.substring(0,firstComma));
                double y = Double.parseDouble(pos.substring(firstComma+1,lastComma));
                double z = Double.parseDouble(pos.substring(lastComma+1));
                GeoLocation GL = new GeoLocation(x,y,z);
                newNode.setLocation(GL);
                newGr.addNode(newNode);
            }
            JsonArray Jedges = json_obj.getAsJsonArray("Edges");
            for(JsonElement edge : Jedges){
                JsonObject temp = (JsonObject) edge;
                int src = temp.get("src").getAsInt();
                int dest = temp.get("dest").getAsInt();
                double weight = temp.get("w").getAsDouble();
                newGr.connect(src,dest,weight);
            }
            _graph = newGr;
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public directed_weighted_graph Json2Graph(String Jstr){
        DWGraph_DS newGr = new DWGraph_DS();
        JsonObject json_obj;
        json_obj = JsonParser.parseString(Jstr).getAsJsonObject();
        JsonArray Jnodes = json_obj.getAsJsonArray("Nodes");
        for(JsonElement node : Jnodes){
            JsonObject temp = (JsonObject) node;
            int id = temp.get("id").getAsInt();
            NodeData newNode = new NodeData(id);
            String pos = temp.get("pos").getAsString();
            int firstComma = pos.indexOf(",");
            int lastComma = pos.lastIndexOf(",");
            double x = Double.parseDouble(pos.substring(0,firstComma));
            double y = Double.parseDouble(pos.substring(firstComma+1,lastComma));
            double z = Double.parseDouble(pos.substring(lastComma+1));
            GeoLocation GL = new GeoLocation(x,y,z);
            newNode.setLocation(GL);
            newGr.addNode(newNode);
        }
        JsonArray Jedges = json_obj.getAsJsonArray("Edges");
        for(JsonElement edge : Jedges){
            JsonObject temp = (JsonObject) edge;
            int src = temp.get("src").getAsInt();
            int dest = temp.get("dest").getAsInt();
            double weight = temp.get("w").getAsDouble();
            newGr.connect(src,dest,weight);
        }
        return newGr;
    }

    //resets all the nodes' tag. mainly for algorithmic use
    private void clearTag(){
        Collection<node_data> nodes = _graph.getV();
        for(node_data node : nodes){
            node.setTag(0);
        }
    }

    //resets all the nodes' weight. mainly for algorithmic use
    private void clearWeight(){
        Collection<node_data> nodes = _graph.getV();
        for(node_data node : nodes){
            node.setWeight(0);
        }
    }
    /* a custom comparator for the queue, utilized in the main class
     * as to sort the nodes by their tag size.
     */
    private class NodeComparator implements Comparator<node_data> {

        @Override
        public int compare(node_data o1, node_data o2) {
            if((o1.getWeight() - o2.getWeight()) > 0){ return 1;}
            else if((o1.getWeight() - o2.getWeight()) < 0){ return -1;}
            else { return 0;}
        }
    }
}
