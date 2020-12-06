package api;

import com.google.gson.*;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


public class DWGraphs_Algo implements dw_graph_algorithms {

    private DWGraph_DS _graph;
    NodeComparator _comp = new NodeComparator(); //a custom comparator for to compare between node in the algorithms

    public DWGraphs_Algo(directed_weighted_graph g){
        _graph = (DWGraph_DS) g;
    }

    @Override
    public void init(directed_weighted_graph g) {
        _graph = (DWGraph_DS) g;
    }

    @Override
    public directed_weighted_graph getGraph() {
        return _graph;
    }

    @Override
    public directed_weighted_graph copy() {
        DWGraph_DS newGr = new DWGraph_DS();                                             //initialize the new graph
        Collection<node_data> nodes = _graph.getV();                       //get a list of all the nodes in the main graph
        for(node_data node : nodes){                                                   //for every node in the graph, create a new node for newGr
            newGr.addNode(new NodeData(node.getKey()));                                              //create a copy of the nodes
        }
        for(node_data node: nodes){                                                    //for every node in the graph, copy its neighbors
            Collection<edge_data> edges = _graph.getE(node.getKey());  //get the neighbors of every node in the graph
            for(edge_data e : edges){                                                   //for every neighbors a node has, copy its edges
                newGr.connect(e.getSrc(), e.getDest(), e.getWeight());
            }
        }
        return newGr;
    }

    public DWGraph_DS caster(directed_weighted_graph temp){
        DWGraph_DS newGr = new DWGraph_DS();                                             //initialize the new graph
        Collection<node_data> nodes = temp.getV();                       //get a list of all the nodes in the main graph
        for(node_data node : nodes){                                                   //for every node in the graph, create a new node for newGr
            newGr.addNode(new NodeData(node.getKey()));                                              //create a copy of the nodes
        }
        for(node_data node: nodes){                                                    //for every node in the graph, copy its neighbors
            Collection<edge_data> edges = temp.getE(node.getKey());  //get the neighbors of every node in the graph
            for(edge_data e : edges){                                                   //for every neighbors a node has, copy its edges
                newGr.connect(e.getSrc(), e.getDest(), e.getWeight());
            }
        }
        return newGr;
    }

    @Override
    public boolean isConnected() {
        clearTag();                                                           //clear the tags from previous algorithms
        LinkedList<node_data> q = new LinkedList<>();                        //create a queue to hold the nodes that are checked
        List<node_data> nodes = (List<node_data>) _graph.getV();
        if(nodes.size() == 0 || nodes.size() == 1){                           //if the graph is empty or is just a node
            return true;                                                      //the graph is connected
        }
        node_data src = nodes.get(0);                                         //the starting node for the algorithm
        q.add(src);
        while(!q.isEmpty()){                                                  //if the queue is empty then we checked every connected node
            src = q.remove();                                                 //remove the node we checked
            Collection<edge_data> edges = _graph.getE(src.getKey());            //get the src neighbors
            for(edge_data e : edges){                                          //for every node in src's neighbors,
                node_data node = _graph.getNode(e.getDest());
                if(node.getTag() == 0){                                         //check if the node was visited (default 0 = false)
                    q.add(node);                                                //if not add it to the queue of unvisited nodes
                }
                node.setTag(1);                                                 //after the node was checked set the tag accordingly
            }
        }
        for(node_data node1 : nodes){                                         //check every node int the graph
            if(node1.getTag() == 0){                                          //if there is a node that wasn't visited
                return false;                                                 //then the algorithm couldn't reach the node because he wasn't connected
            }
        }
        clearTag();                                                           //clear the tags from previous algorithms
        q = new LinkedList<node_data>();                        //create a queue to hold the nodes that are checked
        src = nodes.get(0);                                         //the starting node for the algorithm
        q.add(src);
        while(!q.isEmpty()){                                                  //if the queue is empty then we checked every connected node
            src = q.remove();                                                 //remove the node we checked
            Collection<edge_data> edges = _graph.getBE(src.getKey());            //get the src neighbors
            for(edge_data e : edges){                                          //for every node in src's neighbors,
                node_data node = _graph.getNode(e.getSrc());
                if(node.getTag() == 0){                                         //check if the node was visited (default 0 = false)
                    q.add(node);                                                //if not add it to the queue of unvisited nodes
                }
                node.setTag(1);                                                 //after the node was checked set the tag accordingly
            }
        }
        for(node_data node1 : nodes){                                         //check every node int the graph
            if(node1.getTag() == 0){                                          //if there is a node that wasn't visited
                return false;                                                 //then the algorithm couldn't reach the node because he wasn't connected
            }
        }
        return true;
    }

    @Override
    public double shortestPathDist(int src, int dest) {
        if(src == dest){                                                                     //if the src is also the dest the distance is 0
            return 0;
        }
        this.clearWeight();                                                                     //clear the tags from previous algorithms
        node_data node, nDest;
        node = _graph.getNode(src);
        nDest = _graph.getNode(dest);
        if(node != null && nDest != null){                                                   //check that the nodes exist in the graph
            node.setInfo("");                                                                //empty the src node info, mainly to record the path of nodes
            PriorityQueue<node_data> que = new PriorityQueue<>(_graph.nodeSize(), _comp);    //priority queue for the algorithm with class' comp
            que.add(node);
            while(!que.isEmpty()){                                                           //check every node until queue is empty
                node = que.remove();                                                         //remove the node that is being checked
                if(node.getKey() == dest){                                                   //if reached the dest node
                    return node.getWeight();                                                    //return the distance calculated and stored in the tag
                }
                Collection<edge_data> edges = _graph.getE(node.getKey());
                for(edge_data e : edges){                                                     //for every neighbor of the checked node
                    node_data temp = _graph.getNode(e.getDest());
                    double dist = node.getWeight() + e.getWeight();                                    //set the distance as the current edge weight and node tag
                    if((dist < temp.getWeight() || temp.getWeight() == 0 )&& temp.getKey() != src){      //if encounter an unvisited node or a shorter path that isn't the src node
                        if(dist < temp.getWeight()){                                              //check if encountered a visited node with shorter path
                            que.remove(temp);                                                  //remove the visited node from the queue
                        }                                                                    //so he can be replaced with a the same node with a shorter path
                        temp.setInfo(node.getInfo() + temp.getKey() + ",");                      //record the node in the path, inside the next node
                        temp.setWeight(dist);                                                     //store distance that was passed in the tag
                        que.add(temp);                                                         //add the neighboring node to the the queue
                    }
                }
            }
        }
        return -1;
    }

    @Override
    public List<node_data> shortestPath(int src, int dest) {
        if(this.shortestPathDist(src, dest) != -1){              //checks if there is a path between the nodes
            LinkedList<node_data> path = new LinkedList<>();    //the list that will be returned
            node_data node = _graph.getNode(src);
            path.add(node);                                      //adds the first node in the path (src node)
            if(src == dest){                                     //if src is the only node in the path return the path
                return path;
            }
            String info = _graph.getNode(dest).getInfo();        //get the path that was stored inside the dest info
            while(!info.isEmpty()){                              //extract the nodes from info until there is none
                int divider = info.indexOf(",");                 //the divider between every node key in info
                String key = info.substring(0,divider);          //extract the node key from the path
                node = _graph.getNode(Integer.parseInt(key));
                path.add(node);                                  //add the extracted node
                info = info.substring(divider+1);                //remove the extracted node from info
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
        catch (FileNotFoundException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private void clearTag(){
        Collection<node_data> nodes = _graph.getV();
        for(node_data node : nodes){
            node.setTag(0);
        }
    }

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
