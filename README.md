# OOP_EX2 - README
this project contains an representation on a directed weighted graph, as the
DWGraph_DS class and his components under src\api folder.
along with a jar application Ex2.jar under out\artifacts folder.

## directed weighted graph
the data structer is made of several components to be used in the Ex2 application.

### GeoLocation
a class that holds a location of a point in 3D space.
is also able to claculate distance between two geo_location objects.
 
### NodeData
represent a node (vertix) in the graph.
the node contains:

key: the node's id (value). is uniqe to each node.
tag: an int meant for taging the node in algorithmic functions.
weight: a double that stores the weight of the node, 
but in this project is used as a container for algorithmic functions.
info: a string of information of the edge, but in this project is used as a container for algorithmic functions.
pos: a geo_location object that holds the node's location in 3D space.
E: a collection of edges that start in this node.
BE: a collection of "back edge", edges that end in this node.

### EdgeData
represent an edge in the graph. is a private class in DWGraph_DS.
contains the following:

weight: a double that stores the weight of the edge. 
src,dest: the source and destination nodes that make up the edge.
tag: an int meant for taging the node in algorithmic functions.
info: the meta info of the edge in a form of a string.

### DWGraph_DS
represent the main directed weighted graph.
his nodes and edges can be removed and added to the user's needs.
contains the following:

V: a collection of nodes that make up the graph. also contains the total number on nodes in the graph.
all the edges of the graph are stored in their respective nodes, but are accessible from the graph.
EC: an edge counter. stores the total number of edges in the graph.
MC: the total number of modification done to the graph. for testing purposes.

### DWGraph_Algo
is class containig algorithms to manipulate and calculate information in the graph.
the class contains a main graph that is worked on and a custom comparator for intarnal functions.
the class allows the following:

copy: create and return a copy of the current graph.
isConnected: checkes if the graph is strongly connected. utilizes the Korsaju algorithm.
shortestPathDist: calculate the shortest distance, if available, from point a to point b.
shortestPath: claculate the shortest path, if available, from point a to point b. 
the path is represented by a list of nodes starting from the source. utulizes the Dijkstra algorithm.
save: saves the current graph to a Json file.
load: load a graph from a Json file, and sets it as the current graph.
Json2Graph: create a graph from a Json string.
casr: create a copy of a directed_weighted_graph object to a DWGraph_DS object.

## Ex2.jar pokemon game

Ex2 is a game that utilizes the graphs as his arena, where a number of agents
try to catch pokemon on the graph while using min
