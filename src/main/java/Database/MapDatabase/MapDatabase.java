package Database.MapDatabase;

import Database.CSVReader;
import Database.CSVWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class MapDatabase {
    public Statement statement;
    private HashMap<String, MapNode> nodeList;

    private static final String MAPFILEDIRECTORY = "CSVs/mapFiles/";


    public HashMap<String, MapNode> getNodeList(){
        return this.nodeList;
    }

    /**
     * Creates a database with the given name. Adds edges and nodes tables.
     * Throws ClassNotFoundException if DB Driver not loaded
     * Throws SQLException if unable to connect to database
     *
     * @param name Name of database in filesystem
     */
    public MapDatabase(String name) {
        // Check for DB Driver
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Java DB Driver not found");
        }

        // Open connection
        try {
            Connection connection = DriverManager.getConnection("jdbc:derby:"+name+";create=true");
            statement = connection.createStatement();
        } catch (SQLException e) {
            System.out.println("Connection to database failed");
            e.printStackTrace();
            throw new IllegalStateException("Connection to database failed");
        }

        this.reset();

        // Create tables
        try {
            String sql1 = "CREATE TABLE EDGES" +
                    "(edgeID varchar(35) PRIMARY KEY, " +
                    "startNode varchar(25), " +
                    "endNode varchar(25))";
            statement.executeUpdate(sql1);

            sql1 = "CREATE TABLE Nodes" +
                    "(nodeID VARCHAR(12) PRIMARY KEY, " +
                    "xcoord FLOAT, " +
                    "ycoord FLOAT, " +
                    "floor VARCHAR(6), " +
                    "building VARCHAR(25), " +
                    "nodeType VARCHAR(6), " +
                    "longName VARCHAR(80), " +
                    "shortName VARCHAR(80), " +
                    "teamAssigned VARCHAR(20)," +
                    "newXcoord FLOAT, " +
                    "newYcoord FLOAT)";
            statement.executeUpdate(sql1);

        } catch  (SQLException e) {
            throw(new IllegalStateException("Unable to initialized database"));
        }

        //read in relevant files from csv
        readNodes("nodes.csv");
        readEdges("edges.csv");
    }

    public void reset() {
        try {
            statement.executeUpdate("DROP TABLE Nodes");
            statement.executeUpdate("DROP TABLE Edges");
        } catch (SQLException e) {
            System.err.println("Error with reset of database");
        }
    }

    /**
     * Reads file and returns an ArrayList with all the lines in the file
     * Handles all errors
     *
     * @param file CSV file to be read from
     * @return ArrayList with all lines in the csv file
     */
    private ArrayList<String[]> readCsvFile(String file) {
        // Create a Database.CSVReader to read from the map edges .csv file into csvData
        try {
            CSVReader csvReader = new CSVReader(new FileReader(MAPFILEDIRECTORY+file), ",");
            return csvReader.readCSV();
        } catch (FileNotFoundException e) {
            System.out.println("Unable to load file " + file);
            return null;
        } catch (IOException e) {
            System.out.println("Unable to read file " + file);
            return null;
        }
    }

    /**
     * Reads the edges from a csv file into the database
     *
     * @param file CSV file to be read from
     * @return true if successful, false if not
     */
    public boolean readEdges(String file) {
        ArrayList<String[]> csvData = readCsvFile(file);
        if (csvData == null)
            return false;

        //Insert each line of the CSV file into the database
        for (String[] line : csvData) {
            try {
                String sql = "INSERT INTO EDGES " +
                        "VALUES ('" + line[0] + "', '" + line[1] + "', '" + line[2] + "')";
                statement.executeUpdate(sql);
            } catch (SQLException e) {
                System.out.println("Unable to load an edge line into database");
            }
        }
        return true;
    }

    /**
     * Reads nodes from a CSV file into the database
     *
     * @param file CSV file to be read from
     * @return true if successful, false if not
     */
    public boolean readNodes(String file) {
        ArrayList<String[]> csvData = readCsvFile(file);
        if (csvData == null)
            return false;

        //Insert each line of the CSV file into the database
        for (String[] line : csvData) {
            insertNodeDB(line);
        }
        return true;
    }

    /**
     * Adds all edges to the given hashmap of nodes using addChild()
     *
     * @return HashMap of nodes with connected edges
     */
    public void getEdges(HashMap<String, MapNode> nodeList) {
        try {
            ResultSet rs = statement.executeQuery("SELECT * FROM EDGES");
            while (rs.next()) {
                MapNode first = nodeList.get(rs.getObject(2).toString());
                MapNode second = nodeList.get(rs.getObject(3).toString());

                if (first == null || second == null) {
                    if (first == null)
                        System.out.println("No such node " + rs.getObject(2).toString());
                    if (second == null)
                        System.out.println("No such node " + rs.getObject(3).toString());
                } else {
                    first.addConnection(second);
                    second.addConnection(first);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads all nodes from the database and returns a hashmap of MapNodes
     *
     * @return HashMap containing all edges in the database
     */
    public void getNodes(HashMap<String, MapNode> nodeList) {
        try {
            ResultSet rs = statement.executeQuery("SELECT * FROM NODES");
            while (rs.next()) {
                String nodeId = rs.getObject(1).toString();
                double xcoord2d = Double.parseDouble(rs.getObject(2).toString());
                double ycoord2d = Double.parseDouble(rs.getObject(3).toString());
                String floor = rs.getObject(4).toString();
                String building = rs.getObject(5).toString();
                String nodeType = rs.getObject(6).toString();
                String longName = rs.getObject(7).toString();
                String shortName = rs.getObject(8).toString();
                String teamAssigned = rs.getObject(9).toString();

                //get 3d coordinate info but do not add to node
                double xcoord3d = Double.valueOf(rs.getObject(10).toString());
                double ycoord3d = Double.valueOf(rs.getObject(11).toString());

                MapNode basicNode = new MapNode(nodeId, xcoord2d, ycoord2d, xcoord3d, ycoord3d, floor, building, nodeType, longName, shortName, teamAssigned);
                nodeList.put(nodeId, basicNode);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads all nodes from the database and returns a hashmap of the shortName to the nodeID
     * @return
     */

    public HashMap<String, String> getNodeNames() {
        try {
            ResultSet rs = statement.executeQuery("SELECT nodeID, shortName FROM NODES");
            HashMap<String, String> nameHash = new HashMap();
            while(rs.next()) {
                String nodeId = rs.getObject(1).toString();
                String shortName = rs.getObject(2).toString();

                nameHash.put(shortName, nodeId);
            }
            return nameHash;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Write new line to nodes file
     *
     * @param line String array full of information to add to db
     */
    public void insertNodeDB(String[] line){
        try {
            String sql = "INSERT INTO Nodes VALUES ('" +
                    line[0] + "', " +
                    line[1] + ", " +
                    line[2] + ", '" +
                    line[3] + "', '" +
                    line[4] + "', '" +
                    line[5] + "', '" +
                    line[6] + "', '" +
                    line[7] + "', '" +
                    line[8] + "', " +
                    line[9] + ", " +
                    line[10] + " )";
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println("Unable to load a line into node database");
        }
    }

    /**
     * Update existing node with information
     *
     * @param line String array full of information to add to db
     */
    public void updateNodeDB(String[] line){
        try {
            String sql = "UPDATE Nodes"+
                    "SET xcoord = " + line[1] + ", " +
                    "ycoord = " + line[2] + ", " +
                    "floor = '" + line[3] + "', " +
                    "building = '" + line[4] + "', " +
                    "nodeType = '" + line[5] + "', " +
                    "longName = '" + line[6] + "', " +
                    "shortName = '" + line[7] + "', " +
                    "teamAssigned = '" + line[8] + "', " +
                    "newXcoord = " + line[9] + ", " +
                    "newYcoord = " + line[10];
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println("Unable to load a line into node database");
        }
    }

    /**
     * Write new line to nodes file
     *
     * @param line String array full of information to add to db
     */
    public void insertEdgeDB(String[] line) {
        try {
            String sql = "INSERT INTO EDGES " +
                    "VALUES ('" + line[0] + "', '" + line[1] + "', '" + line[2] + "')";
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println("Unable to insert a line into edge database");
        }
    }

    /**
     *
     * Delete specified edges
     *
     * @param line String array full of information to add to db
     */
    public void deleteEdgeDB(String line) {
        try {
            String sql = "DELETE FROM EDGES WHERE edgeID = '" + line + "'";
            System.out.println(line);
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println("Unable to delete a line into edge database");
        }
    }

    /**
     *
     * Delete specified edges
     *
     * @param line String array full of information to add to db
     */
    public void deleteNodeDB(String line) {
        try {
            String sql = "DELETE FROM NODES WHERE edgeID = '" + line + "'";
            System.out.println(line);
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println("Unable to delete a line from node database");
        }
    }

    /**
     *
     * Delete specified edges
     *
     * @param id nodeID of node deleted
     */
    public void deleteEdgeDependenciesDB(String id) {
        String sql = "DELETE FROM NODES WHERE nodeId = " + id;
        try {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println("NodeId not present in database");
            e.printStackTrace();
        }

        //Remove edge connections to the node from the database
        String sql2 = "DELETE FROM EDGES WHERE startNode = " + id;
        String sql3 = "DELETE FROM EDGES WHERE endNode = " + id;
        try {
            statement.executeUpdate(sql2);
            statement.executeUpdate(sql3);
        } catch (SQLException e) {
            System.out.println("NodeId not present in database");
            e.printStackTrace();
        }
    }

    public void writeNodeDB() {
        System.out.println("Updating Node CSV");
        String sql = "SELECT * FROM NODES";
        try {
            ResultSet rs = statement.executeQuery(sql);
            CSVWriter writer = new CSVWriter("CSVs/mapFiles/nodes.csv");
            writer.writeRS(rs, 11);
        } catch (SQLException e) {
            System.out.println("Could not write to database");
            e.printStackTrace();
        }
    }

    public void writeEdgeDB() {
        System.out.println("Updating Edge CSV");
        String sql = "SELECT * FROM EDGES";
        try {
            ResultSet rs = statement.executeQuery(sql);
            CSVWriter writer = new CSVWriter("CSVs/mapFiles/edges.csv");
            writer.writeRS(rs, 3);
        } catch (SQLException e) {
            System.out.println("Could not write to database");
            e.printStackTrace();
        }
    }

}