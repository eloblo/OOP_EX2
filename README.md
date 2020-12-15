# OOP_EX2 - README
this project contains a representation on a directed weighted graph, as the
DWGraph_DS class and his components under src\api folder.
along with a jar application Ex2.jar under out\artifacts folder.

## directed weighted graph
the data structure is made of several components to be used in the Ex2 application.

### GeoLocation
a class that holds a location of a point in 3D space.
is also able to calculate distance between two geo_location objects.
 
### NodeData
represent a node (vertx) in the graph.
the node contains:

* key: the node's id (value). is unique to each node.
* tag: an int meant for tagging the node in algorithmic functions.
* weight: a double that stores the weight of the node, 
but in this project it is used as a container for algorithmic functions.
* info: a string of information of the edge, but in this project is used as a container for algorithmic functions.
* pos: a geo_location object that holds the node's location in 3D space.
* E: a collection of edges that start in this node.
* BE: a collection of "back edge", edges that end in this node.

### EdgeData
represent an edge in the graph. is a private class in DWGraph_DS.
contains the following:

* weight: a double that stores the weight of the edge. 
* src,dest: the source and destination nodes that make up the edge.
* tag: an int meant for tagging the node in algorithmic functions.
* info: the meta info of the edge in a form of a string.

### DWGraph_DS
represent the main directed weighted graph.
his nodes and edges can be removed and added to the user's needs.
contains the following:

* V: a collection of nodes that make up the graph. also, contains the total number on nodes in the graph.
all the edges of the graph, are stored in their respective nodes, but are accessible from the graph.
* EC: an edge counter. stores the total number of edges in the graph.
* MC: the total number of modification done to the graph. for testing purposes.

### DWGraph_Algo
is class containing algorithms to manipulate and calculate information in the graph.
the class contains a main graph that is worked on, and a custom comparator for internal functions.
the class allows the following:

* copy: create and return a copy of the current graph.
* isConnected: checks if the graph is strongly connected. utilizes the Korsaju algorithm.
* shortestPathDist: calculate the shortest distance, if available, from point a to point b.
* shortestPath: calculate the shortest path, if available, from point a to point b. 
the path is represented by a list of nodes starting from the source. utilizes the Dijkstra algorithm.
* save: saves the current graph to a Json file.
* load: load a graph from a Json file, and sets it as the current graph.
* Json2Graph: create a graph from a Json string.
* cast: create a copy of a directed_weighted_graph object to a DWGraph_DS object.

the api folder contains tests for the classes in the tests' folder.

## Ex2.jar pokemon game

Ex2 is a game, that utilizes the graphs as his arena, where a number of agents
try to catch pokemon on the graph while using minimal movements.
there are 24 (0 to 23) levels to choose from, with a different graph, agents and pokemons.
the game is communicating with the game_service server to track the changes in the game.
the api folder hold an interface of the game_service class.

the game is built on from several already made classes and libraries(mainly libs folder, Arena, CL_Agent, CL_Pokemon).
all the game elements are found in src\gameClient folder. the application is made from:

### Ex2.jar

the java application. can be run from desktop UI and command line >java -jar Ex2.jar [id] [level].
int the command line the file can run in two ways.
* 1) without parameters, in which a login window will pop up. requiring to choose a level, and the option to log on
the game server in order to upload the score. the window has a "Free play" option if the user doesn't want to play without logging in.
* 2) when entering parameters, if the parameters are valid, the game will run without the login window as being logged in. 

### Ex2

the main java class (not to be confused with the jar file) launches the game and his associate GUI.
the class is responsible to initialize the game and to maintain the GUI.
the class contains:

* win: the main GUI frame, that present the game to the user.
* ar: the game arena. holds the game's information since the last move.
* id: the login id.
* scenario: the chosen game level.
* agents: a list of threads that run the agent's movement.
* graph: a directional_weighted_graph, graph that the game is set on.

### Frame

the main GUI frame class. is responsible to draw the game. utilizes the classes from the util folder.
the frame will present the graph, pokemons, the moving agents, level, timer to the end of the game and the total score.
the frame is resizeable.

### Login Panel

a GUI window that allows the user to choose a game level and to login if he desires.
the panel is made up of a text box for entering the id, a popup menu to choose the level (default 0)
and option to login or "Free Play" if the user wants to play without logging in.
closing the panel will terminate the program.

### Agent

a class, that extends runnable and tells the agent that its managing to move in the appropriate time.
the class holds:

* agent: the Agent's current CL_Agent objects that moves in the game.
* prevPok: previously targeted pokemon object. mainly for algorithmic purposes.
* prevEdge: the last edge the agent passed mainly for algorithmic purposes.
* white List: a hashSet of pokemons' position that are exclusive to the agent.
* game: the main game service object.
* mover: an object responsible for the movement algorithms.
* inSight: a boolean flag, mainly for algorithmic purposes.

### Mover

the object responsible to move the agents on the graph as efficiently as possible.
the class searches for pokemons, manages and update the different agents, calculate paths and time for the agent 
to reach the pokemon with minimal movement and as fast as possible.
the class contains:

* ar: the current arena of the game.
* agT: the current Agent object he is managing, along of with variables to store all the agent's data.
* pokemon: the current target of the current agent.
* black List: a list of pokemons' position that are not allowed to be targeted, except their allocated agents.
* graph: the graph that the level is built on.
* graphAlgo: a DWGraph_Algo objects that calculate paths and distances for the agents.
* AC: total number of agents in the game.
* reset: a counter when reached the limit, will trigger a reset to avoid congestions.

### Arena

a pre built class, that utilizes the util folder in order to build the game's level.
the class is responsible to hold and track the game's elements since the last move.
the class also reads JString in order to process the data of the game server.
was modified to reduce bugs. the project cannot run with the original class.

### CL_Pokemon

a pre built class, that represent the pokemons in game. each hold a specific edge, location type and value.
the pokemon type and location determine on what edge he is located. the type is also represented by pokemon color in the GUI.
the pokemons value determine the score of the capturing agent.
the game is remaking the pokemons from json string from the server after every move.

### CL_Agent

a pre built class, that represent the games agents. 
the agent is remade with each move of the game, and is made from the Json String of the game server.
the agent contains:

* speed: the speed is calculated as edge-weight per second, and is increased after he reaches a certain amount of score.
* curr_edge: the edge the agent is currently located.
* curr_node: the node the agent is currently located, or the source of the edge he is currently located.
* pos: the current location of the agent.

### util folder

hold number of pre built classes. they are responsible to calculate and maintain position of the games' elements.
like the location of agents and to calculate how to resize the arena after resizing the game's UI window.


the gameClient folder also contains MyFrame, Ex2_client and simpleGameClient, which are pre built classes
that the main classes are built on. they are not to be used, only for testing purposes as they contain flawed
and primitive solutions and algorithms.
