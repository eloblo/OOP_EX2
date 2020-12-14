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
     * utilizes Kosoraju algorithm.
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
            for(edge_data e : edges){                                   //check every edge pointing from the node
                node_data node = _graph.getNode(e.getDest());
                if(node.getTag() == 0){                                 //check if the new node is visited
                    q.add(node);
                }
                node.setTag(1);                                         //tag as visited
            }
        }
        for(node_data node1 : nodes){        //check if there was an unvisited node
            if(node1.getTag() == 0){         //if there is then we cant reach him
                return false;
            }
        }
        clearTag();                          //reset the tags and repeat the function only when the edges are reversed
        q = new LinkedList<node_data>();
        src = nodes.get(0);
        q.add(src);
        while(!q.isEmpty()){
            src = q.remove();
            Collection<edge_data> edges = _graph.getBE(src.getKey());   //check every edge pointing to node
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

    /** calculates the shortest distance from source node to
     *  the destination node. utilizes Dijkstra algorithm.
     * @param src - start node
     * @param dest - end (target) node
     * @return the shortest distance by the weight of the edges
     * @return -1 if there is no path.
     */
    @Override
    public double shortestPathDist(int src, int dest) {
        if(src == dest){
            return 0;
        }
        this.clearWeight();        //clear nodes weight
        node_data node, nDest;
        node = _graph.getNode(src);
        nDest = _graph.getNode(dest);
        if(node != null && nDest != null){   //check if src and dest exist
            node.setInfo("");                //node info will store the nodes in the path
            PriorityQueue<node_data> que = new PriorityQueue<>(_graph.nodeSize(), _comp);  //a priority queue that will hold the nodes
            que.add(node);
            while(!que.isEmpty()){            //if the queue then all the nodes were checked
                node = que.remove();
                if(node.getKey() == dest){    //check if we reached the destination
                    return node.getWeight();  //return the distance
                }
                Collection<edge_data> edges = _graph.getE(node.getKey());
                for(edge_data e : edges){                              //check every pointed neighboring node
                    node_data temp = _graph.getNode(e.getDest());
                    double dist = node.getWeight() + e.getWeight();    //sum the weight of the nodes
                    //check if the distance is shorter or if we are starting a new path and that the node we reached is not the src
                    if((dist < temp.getWeight() || temp.getWeight() == 0 )&& temp.getKey() != src){
                        if(dist < temp.getWeight()){
                            que.remove(temp);      //if we found a shorter path to node remove the old longer path
                        }
                        temp.setInfo(node.getInfo() + temp.getKey() + ","); //store the node path in the node info
                        temp.setWeight(dist);                               //store the distance in the node's weight
                        que.add(temp);                                      //add to the queue
                    }
                }
            }
        }
        return -1;
    }

    /** calculate the shortest path from src node to dest node.
     * @param src - start node
     * @param dest - end (target) node
     * @return a list of nodes in the path from src to dest.
     * @return null if there is bo path.
     */
    @Override
    public List<node_data> shortestPath(int src, int dest) {
        if(this.shortestPathDist(src, dest) != -1){            //check if there is a valid path
            LinkedList<node_data> path = new LinkedList<>();
            node_data node = _graph.getNode(src);              //add the src node to the head of the list
            path.add(node);
            if(src == dest){  //an empty path
                return path;
            }
            String info = _graph.getNode(dest).getInfo();      //in the shortestPathDist() also stores the path as a string
            while(!info.isEmpty()){                            //convert the string to node list
                int divider = info.indexOf(",");               //divider between the nodes in the string
                String key = info.substring(0,divider);        //extract node from the string
                node = _graph.getNode(Integer.parseInt(key));
                path.add(node);                                //add the node
                info = info.substring(divider+1);
            }
            return path;
        }
        return null;
    }

    /** save the graph to a Json file.
     * @param file - the file name (may include a relative path).
     * @return if the save was successful.
     */
    @Override
    public boolean save(String file) {
        JsonObject Json_obj = new JsonObject();
        JsonObject Jedge;
        JsonObject Jnode;
        JsonArray edges = new JsonArray();
        JsonArray nodes = new JsonArray();
        Collection<node_data> V = _graph.getV();
        for(node_data node : V){                  //add every node to the json array
            Jnode = new JsonObject();
            geo_location GPos = node.getLocation();             //add the position of the node to node json object
            String pos = GPos.x()+","+ GPos.y()+","+ GPos.z();
            Jnode.addProperty("pos",pos);
            Jnode.addProperty("id",node.getKey());      //add the id of the node
            nodes.add(Jnode);
            Collection<edge_data> Es = _graph.getE(node.getKey());
            for(edge_data edge : Es){                               //add all edges to the json array
                Jedge = new JsonObject();
                Jedge.addProperty("src", edge.getSrc());    //add all the data of edges to edge json object
                Jedge.addProperty("w", edge.getWeight());
                Jedge.addProperty("dest", edge.getDest());
                edges.add(Jedge);
            }
        }
        Json_obj.add("Edges",edges);    //add the arrays to main json object
        Json_obj.add("Nodes",nodes);
        try{
            FileWriter writer = new FileWriter(file);   //write the main json object to the file
            writer.write(Json_obj.toString());
            writer.close();
            return true;
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }

    /** load a graph from a json file.
     * @param file - file name of JSON file
     * @return if the load was successful.
     */
    @Override
    public boolean load(String file) {
        try{
            String jstr = new String(Files.readAllBytes(Paths.get(file)));   //read the file
            _graph = (DWGraph_DS) Json2Graph(jstr); //set the graph
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /** convert a json string to a graph.
     *  is based on the load() method.
     * @param Jstr
     * @return the graph made from the Json string
     */
    public directed_weighted_graph Json2Graph(String Jstr){
        DWGraph_DS newGr = new DWGraph_DS();
        JsonObject json_obj;
        json_obj = JsonParser.parseString(Jstr).getAsJsonObject();
        JsonArray Jnodes = json_obj.getAsJsonArray("Nodes");  //convert the json array to nodes
        for(JsonElement node : Jnodes){
            JsonObject temp = (JsonObject) node;
            int id = temp.get("id").getAsInt();
            NodeData newNode = new NodeData(id);        //create the ne node by the id
            String pos = temp.get("pos").getAsString(); //convert the position json string to a GeoLocation
            int firstComma = pos.indexOf(",");
            int lastComma = pos.lastIndexOf(",");
            double x = Double.parseDouble(pos.substring(0,firstComma));
            double y = Double.parseDouble(pos.substring(firstComma+1,lastComma));
            double z = Double.parseDouble(pos.substring(lastComma+1));
            GeoLocation GL = new GeoLocation(x,y,z);
            newNode.setLocation(GL);   //set the new position
            newGr.addNode(newNode);    //add the node
        }
        JsonArray Jedges = json_obj.getAsJsonArray("Edges");  //convert the edges json array to edges
        for(JsonElement edge : Jedges){
            JsonObject temp = (JsonObject) edge;         //extract the data of the edge json object
            int src = temp.get("src").getAsInt();
            int dest = temp.get("dest").getAsInt();
            double weight = temp.get("w").getAsDouble();
            newGr.connect(src,dest,weight);    //add the edges
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
