package Database.MapDatabase;

import java.util.ArrayList;

public class MapNode {

    private String nodeID;
    private double xCoord2d;
    private double yCoord2d;
    private double xCoord3d;
    private double yCoord3d;
    private String floor;
    private String building;
    private String nodeType;
    private String longName;
    private String shortName;
    private String teamAssigned;
    private ArrayList<MapNode> connections;

    //Database.MapDatabase.MapNode constructor for hardcoding most of the Database.MapDatabase.MapNode information including 3D coordinates- to generally only be used by the initial insertion of all the MapNodes from the DB
    public MapNode(String nodeID, double xCoord2D, double yCoord2D, double xCoord3d, double yCoord3d, String floor, String building, String nodeType, String longName, String shortName, String teamAssigned) {
        this.nodeID = nodeID;
        this.xCoord2d = xCoord2D;
        this.yCoord2d = yCoord2D;
        this.xCoord3d = xCoord3d;
        this.yCoord3d = yCoord3d;
        this.floor = floor;
        this.building = building;
        this.nodeType = nodeType;
        this.longName = longName;
        this.shortName = shortName;
        this.teamAssigned = teamAssigned;
        connections = new ArrayList<>();
    }

    //Database.MapDatabase.MapNode constructor called when the user manually inserts a new Database.MapDatabase.MapNode
    public MapNode(String nodeID, double xCoord2D, double yCoord2D, String floor, String building, String nodeType, String longName, String shortName, String teamAssigned) {
        this.nodeID = nodeID;
        this.xCoord2d = xCoord2D;
        this.yCoord2d = yCoord2D;
        this.floor = floor;
        this.building = building;
        this.nodeType = nodeType;
        this.longName = longName;
        this.shortName = shortName;
        this.teamAssigned = teamAssigned;
        connections = new ArrayList<>();
        get3D();
    }

    public void get3D() {
       /* MapTransformer tran = new MapTransformer();
        Coordinate coords = tran.calc3D(this);
        this.xCoord3d = coords.getX();
        this.yCoord3d = coords.getY(); */
    }


    public double getxCoord2d() {return xCoord2d;}
    public void setxCoord2d(double xCoord2d){this.xCoord2d = xCoord2d;}
    public double getyCoord2d() {return yCoord2d;}
    public void setyCoord2d(double yCoord2d){this.yCoord2d = yCoord2d;}
    public double getxCoord3d() {return xCoord3d;}
    public void setxCoord3d(double xCoord3d){this.xCoord3d = xCoord3d;}
    public double getyCoord3d() {return yCoord3d;}
    public void setyCoord3d(double yCoord3d){this.yCoord3d = yCoord3d;}
    public String getNodeID() {return nodeID;}
    public String getFloor(){return floor;}
    public void setFloor(String floor){this.floor = floor;}
    public String getBuilding(){return building;}
    public void setBuilding(String building){this.building = building;}
    public String getNodeType(){return nodeType;}
    public void setNodeType(String nodeType){this.nodeType = nodeType;}
    public String getLongName(){return longName;}
    public void setLongName(String floor){this.longName = longName;}
    public String getShortName(){return shortName;}
    public void setShortName(String shortName){this.shortName = shortName;}

    public ArrayList<MapNode> getConnections() {return connections;}

    public void addConnection(MapNode child) {
        connections.add(child);
    }

    public void removeConnection(MapNode child){
        connections.remove(child);
    }

    public void setConnections(ArrayList<MapNode> connect) {connections = connect;}

    public double distanceFrom(MapNode next) {
        final double FLOOR_SCALER = 1000; //TODO: Calc optimal value
        double zDiff = FLOOR_SCALER*(MapNode.StringToFloor(this.getFloor()) - MapNode.StringToFloor(this.getFloor()));
        return Math.sqrt(Math.pow(this.getxCoord2d()-next.getxCoord2d(),2)+Math.pow(this.getyCoord2d()-next.getyCoord2d(),2)+Math.pow(zDiff,2));
    }

    public static int StringToFloor(String s) {
        if(s.equals("G")) return -2;
        if(s.equals("L2")) return -1;
        if(s.equals("L1")) return 0;
        return Integer.parseInt(s);
    }
    public int StringToFloor(){
        if(floor.equals("G")) return -2;
        if(floor.equals("L2")) return -1;
        if(floor.equals("L1")) return 0;
        return Integer.parseInt(floor);
    }
    public static String FloorToString(int floor) {
        if(floor == 0) return "L1";
        if(floor == -1) return "L2";
        if(floor == -2) return "G";
        return (new Integer(floor)).toString();
    }

    @Override
    public String toString(){
        return this.getNodeID()+" " +this.getConnections().size();
    }

    public String updateString(double newX, double newY) {
        String returner = this.getNodeID()+"," + this.getxCoord2d()+","+this.getyCoord2d()+"," + this.getFloor() +"," + this.getBuilding() + ","+ this.getNodeType() + "," + this.getLongName() + "," + this.getShortName() + ",B," + newX + "," + newY;
        System.out.println(returner);
        return returner;
    }


    @Override
    public boolean equals(Object b) {
        MapNode a = (MapNode) b;
        if (this.nodeID == a.nodeID) {
            return true;
        }
        return false;

    }
}
