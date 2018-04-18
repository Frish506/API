package Database.MapDatabase;


import java.util.ArrayList;
import java.util.HashMap;

public class MapManager {
    public static HashMap<String, MapNode> map; //TODO: Make private with a public get. We don't want people directly changing this
    public static HashMap<String, MapNode> disabled;
    public static HashMap<String, String> nodeNames;
    private MapDatabase database;


    private static MapManager instance = null;
    /**
     * Constructor for the MapManager
     * Creates and populates MapDatabase
     * Creates and populates HashMap map
     */
    private MapManager(){
        //create and populate MapDatabase
        this.database = new MapDatabase("MapDB");
       /** database.readNodes("MapCnodes.csv"); //can make function
        database.readEdges("MapCedges.csv"); */

        //create and populate HashMap of nodes
        this.map = new HashMap<>();
        this.disabled = new HashMap<>();
        database.getNodes(map);
        database.getEdges(map);

        this.nodeNames = database.getNodeNames();
    }

    /**
     * public call to MapManager constructor
     *
     * @return an instance of a MapManager
     */

    public static MapManager getInstance(){

        if(instance == null) {
            instance = new MapManager();
        }
        return instance;
    }

    /**
     * Getter for the map attribute of MapManager class
     *
     * @return HashMap with all updated node information
     */
   /** public static HashMap<String, Database.MapDatabase.MapNode> getMap(){
        return this.map;
    } */

    /**
     * Updates a node in the database as well as the Hashmap of MapNodes
     *
     * @param nodeInfo csv style updated string for node
     *
     * @return HashMap containing MapNodes except the deleted
     */
    public void updateNode(String nodeInfo){
        //separate input string into individual entity values
        String [] nodeLine = nodeInfo.split(","); //splits string by comma

        //execute update statement for node db
        database.updateNodeDB(nodeLine);

        //replace with updated node information within HashMap
        MapNode currNode = map.get(nodeLine[0]); //find node based on the id
        currNode.setxCoord2d(Double.parseDouble(nodeLine[9]));
        currNode.setyCoord2d(Double.parseDouble(nodeLine[10]));
        currNode.setFloor(nodeLine[3]);
        currNode.setBuilding(nodeLine[4]);
        currNode.setNodeType(nodeLine[5]);
        currNode.setLongName(nodeLine[6]);
        currNode.setShortName(nodeLine[7]);
        currNode.setxCoord3d(Double.parseDouble(nodeLine[9]));
        currNode.setyCoord3d(Double.parseDouble(nodeLine[10]));

        this.nodeNames = database.getNodeNames();
    }

    /**
     * Adds a node to the databases as well as the Hashmap of MapNodes
     *
     * @param nodeInfo csv style string for node addition
     * @return HashMap containing MapNodes except the deleted
     */
    public void addNode(String nodeInfo) {
        //insert new node information into database
        String [] nodeLine = nodeInfo.split(","); //splits string by comma
        database.insertNodeDB(nodeLine); //execute insert statement for node db

        //insert new node to HashMap
        MapNode newNode = new MapNode(nodeLine[0], Double.parseDouble(nodeLine[1]), Double.parseDouble(nodeLine[2]), nodeLine[3], nodeLine[4], nodeLine[5], nodeLine[6], nodeLine[7], nodeLine[8]);
        map.put(newNode.getNodeID(), newNode);
        this.nodeNames = database.getNodeNames();
    }

    /**
     * Adds an edge that connects two existing nodes
     * Updates both database and Hashmap with information
     *
     * @param node1ID string ID for one of the nodes to connect
     * @param node2ID string ID for one of the nodes to connect
     * @return 0 if success, -1 if failure
     */
    public int addEdge(String node1ID, String node2ID){
        //check if both nodes exist

        System.out.println("test here");
        if(!(map.containsKey(node1ID) && map.containsKey(node2ID))){
            return -1; //indicate failure if either node does not exist
        }

        //populate array of information strings for new edge
        String [] edgeLine = new String[3];
        edgeLine[0] = node1ID + "_" + node2ID; //create edge ID
        edgeLine[1] = node1ID;
        edgeLine[2] = node2ID;

        //insert new edge into database
        database.insertEdgeDB(edgeLine);

        //update children of node
        map.get(node1ID).addConnection(map.get(node2ID));
        map.get(node2ID).addConnection(map.get(node1ID));

        return 0; //indicate success
    }

    /**
     * Deletes a node from the databases as well as the Hashmap of MapNodes
     *
     * @param ID of Database.MapDatabase.MapNode to be deleted
     * @return HashMap containing MapNodes except the deleted
     */
    public void deleteNode(String ID){
        //Remove the node from the HashMap of MapNodes if it exists
        if(map.containsKey(ID)){

            //find node to be deleted inside of the allNodes HashMap
            MapNode currNode = map.get(ID);

            //get children of the node to be deleted
            ArrayList<MapNode> currChildren = currNode.getConnections();
            for(MapNode child: currChildren){
                child.removeConnection(currNode);
            }

            map.remove(ID);
        }

        //delete dependent edges from database
        database.deleteNodeDB(ID);
        database.deleteEdgeDependenciesDB(ID);
    }

    /**
     * Deletes an edge from the database and updates Hashmap
     *
     * @param edgeID of edge to be deleted
     */
    public void deleteEdge(String edgeID){
        //Remove edge from the database
        database.deleteEdgeDB(edgeID);

        //Remove dependencies in child nodes
        String nodeInfo[] = edgeID.split("_"); //split id string into each node id
        map.get(nodeInfo[0]).removeConnection(map.get(nodeInfo[1]));
        map.get(nodeInfo[1]).removeConnection(map.get(nodeInfo[0]));
    }

    public void deleteEdge(String node1ID, String node2ID) {
        String edgeLine = "";
        edgeLine = node1ID + "_" + node2ID; //create edge ID

        //deletes edge in database
        database.deleteEdgeDB(edgeLine);

        //update children of node
        map.get(node1ID).removeConnection(map.get(node2ID));
        map.get(node2ID).removeConnection(map.get(node1ID));
    }

    public boolean checkEdge(String node1ID, String node2ID){
        //check if both nodes exist

        System.out.println("test here");
        if(!(map.containsKey(node1ID) && map.containsKey(node2ID))){
            return false; //indicate failure if either node does not exist
        }

        for(MapNode child :  map.get(node1ID).getConnections()) {
            if(child.getNodeID().equals(node2ID)) { return true; }
        }

        return false;
    }
    public void writeMapDB(){
        database.writeEdgeDB();
        database.writeNodeDB();
    }

    public void disableNode(String ID) {
        MapNode a = map.get(ID);
        disabled.put(ID, a);
        if(map.containsKey(ID)){
            //find node to be deleted inside of the allNodes HashMap
            MapNode currNode = map.get(ID);

            //get children of the node to be deleted
            ArrayList<MapNode> currChildren = currNode.getConnections();
            for(MapNode child: currChildren){
                child.removeConnection(currNode);
            }

            map.remove(ID);
        }
    }

    public void enableNode(MapNode node) {
        String s = node.getNodeID() + "," + node.getxCoord2d() + "," + node.getyCoord2d() + "," + node.getFloor() + "," + node.getBuilding() + "," + node.getNodeType() + "," + node.getLongName() + "," + node.getShortName() + ",C," + node.getxCoord3d() + "," + node.getyCoord3d();
        map.put(node.getNodeID(), node);
        for(MapNode child : node.getConnections()) {
            String [] edgeLine = new String[3];
            edgeLine[0] = node.getNodeID() + "_" + child.getNodeID(); //create edge ID
            edgeLine[1] = node.getNodeID();
            edgeLine[2] = child.getNodeID();

            //update children of node
            map.get(child.getNodeID()).addConnection(map.get(node.getNodeID()));
        }
        disabled.remove(node.getNodeID());
    }

}
