package Database.SpillAPI;

import Database.CSVReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

//ToDo: make consistent string for done

public class SpillManager {
    private static int idTracker;

    Connection connection;
    Statement statement;

    private static class Helper {
        private static final SpillManager manager = new SpillManager();
    }
    public static SpillManager getSpillMangager() {
        return Helper.manager;
    }

    private SpillManager() {
        try {
            idTracker = 1000;
            connection = DriverManager.getConnection("jdbc:derby:SpillDatabase;create=true");
            connection.setAutoCommit(false);
            statement = connection.createStatement();
        } catch (SQLException e) {
            throw new IllegalStateException("Connection to database failed");
        }

        reset();
    }

    public void reset() {
        try {
            statement.executeUpdate("DROP TABLE SPILLS");
        } catch (SQLException e) {}
        try {
            statement.executeUpdate("DROP TABLE STAFF");
        } catch (SQLException e) {}

        try {
            statement.executeUpdate("CREATE TABLE SPILLS" +
                    " (ID INTEGER PRIMARY KEY, " +
                    "description VARCHAR(140), " +
                    "priority INTEGER," +
                    "status VARCHAR(20)," +
                    "location VARCHAR(20)," +
                    "isHazardous BOOLEAN," +
                    "staff INTEGER," +
                    "resolvedBy VARCHAR(20)," +
                    "timetaken INTEGER," +
                    "start TIMESTAMP," +
                    "endtime TIMESTAMP)");
            statement.executeUpdate("CREATE TABLE STAFF (" +
                    "ID INTEGER PRIMARY KEY, firstname varchar(10), lastname varchar(10), hazardous BOOLEAN)");
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to initialize spill database");
        }

        try {
            CSVReader reader = new CSVReader(new FileReader("CSVs/SpillStaff.csv"), ",");
            ArrayList<String[]> lines = reader.readCSV();
            for (String[] s : lines) {
                statement.execute("INSERT INTO STAFF VALUES ("+s[0]+",'"+s[1]+"','"+s[2]+"',"+s[3]+")");
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Could not read in csv");
        } catch (SQLException e) {
            System.out.println("Could not put data into database");
        }

        try {
            CSVReader reader = new CSVReader(new FileReader("CSVs/Spills.csv"), ",");
            ArrayList<String[]> lines = reader.readCSV();
            for (String[] s : lines) {
                Spill spill = new Spill(Integer.parseInt(s[0]), s[1], Integer.parseInt(s[2]), s[3], s[4], s[5].equals("true"), null, new Timestamp(Long.parseLong(s[9])));
                if (!s[6].equals("NULL")) {
                    spill.setStaff(Integer.parseInt(s[6]));
                }
                if (!s[7].equals("NULL")) {
                    spill.setResolvedBy(s[7]);
                }
                if (!s[8].equals("NULL")) {
                    spill.setTimeTaken(Integer.parseInt(s[8]));
                }
                if (!s[10].equals("NULL")) {
                    spill.setEnd(new Timestamp(Long.parseLong(s[10])));
                }
                statement.execute(spill.getSQLInsert());
            }
        } catch (Exception e) {
            System.out.println("Problem reading in spills");
            e.printStackTrace();
        }
    }

    /**
     * Adds a spill to the database
     * @param description Description of the spill
     * @param priority Priority of the spill
     * @param status Status of the spill
     * @param location Location of the spill
     * @param isHazardous Whether or not the spill is hazardous
     * @return True if successful, false if not
     */
    public boolean addSpill(String description, int priority, String status, String location,
                         boolean isHazardous, Integer staff) {
        Spill spill = new Spill(idTracker++, description, priority, status, location, isHazardous, staff, new Timestamp(System.currentTimeMillis()));
        try {
            statement.executeUpdate(spill.getSQLInsert());
            return true;
        } catch (SQLException e) {
            System.out.println("Unable to add spill to database");
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteSpill(int id){
        try {
            statement.executeUpdate("DELETE FROM SPILLS WHERE ID = " + id);
            return true;
        } catch (SQLException e ) {
            System.out.println("Could not remove spill");
            return false;
        }
    }

    /**
     * Adds a staff member to the database
     * @param first First name of the staff member
     * @param last Last name of the staff member
     * @param hazardous True if the staff member can clean up hazardous spills
     * @return True if successful, false if not
     */
    public boolean addStaff(String first, String last, boolean hazardous) {
        SpillStaff staff = new SpillStaff(idTracker++, first, last, hazardous);
        try {
            statement.executeUpdate(staff.getSQLInsert());
            return true;
        } catch (SQLException e) {
            System.out.println("Unable to add staff to database");
            return false;
        }
    }

    public boolean removeStaff(int id) {
        try {
            statement.executeUpdate("DELETE FROM STAFF WHERE ID = " + id);
            return true;
        } catch (SQLException e ) {
            System.out.println("Could not remove staff");
            return false;
        }
    }

    /**
     * Assigns a staff member to a spill
     * Each spill may have only one staff assigned to it, but a staff may be assigned to many spills
     * @param staffID ID of the staff member being assigned
     * @param spillID ID of the spill the staff is being assigned to
     * @return True if successful, false if not
     */
    public boolean assignStaff(int staffID, int spillID) {
        if (!hasSpill(spillID)) {
            System.out.println("Spill does not exist");
            return false;
        }

        if (!hasStaff(staffID)) {
            System.out.println("Staff does not exist");
            return false;
        }

        try {
            statement.execute("UPDATE SPILLS SET staff = "+staffID+" WHERE ID = "+spillID);
            return true;
        } catch (SQLException e) {
            System.out.println("Unable to update database");
            return false;
        }
    }

    /**
     * Unassigns the staff from the given spill
     * @param spillID ID of the spill to have its staff unassigned
     * @return True if successful, false if not
     */
    public boolean unassignStaff(int spillID) {
        if (!hasSpill(spillID)) {
            System.out.println("Spill does not exist");
            return false;
        }


        try {
            statement.execute("UPDATE SPILLS SET staff = NULL WHERE ID = "+spillID);
            return true;
        } catch (SQLException e) {
            System.out.println("Unable to update database");
            return false;
        }
    }

    /**
     * Updates a spill with the given id to have the given information
     * If any parameter is null, that aspect of the spill is not updated. At least one parameter must be not null.
     * @param id ID of the spill to be updated
     * @param description Updated description of the spill
     * @param priority Updated priority of the spill
     * @param status Updated status of the spill
     * @param location Updated location of the spill
     * @param isHazard Updated indication of whether the spill is hazardous
     * @return True if successful, false if not
     */
    public boolean updateSpill(int id, String description, Integer priority, String status, String location, Boolean isHazard, String resolvedBy, Integer assignee) {
        try {
            ResultSet rs = statement.executeQuery("SELECT * FROM SPILLS"+
                    " WHERE ID = "+id);
            if (rs.next()) {
                String middle = "";
                if (description != null)
                    middle += "description = '"+description+"',";
                if (priority != null)
                    middle += "priority = "+priority+",";
                if (status != null)
                    middle += "status = '"+status+"',";
                if (location != null)
                    middle += "location = '"+location+"',";
                if (isHazard != null)
                    middle += "isHazard = "+isHazard+",";
                if (resolvedBy != null)
                    middle += "resolvedBy = '"+resolvedBy+"',";
                if (assignee != null)
                    middle += "staff = "+assignee+",";
                if (middle.length() == 0) {
                    System.out.println("Nothing to update");
                    return false;
                }
                middle = middle.substring(0, middle.length()-1);

                statement.execute("UPDATE SPILLS SET "+
                        middle+" WHERE ID = "+id);
                return true;
            }
            System.out.println("Service request did not exist");
            return false;
        } catch (SQLException e) {
            System.out.println("Could not update status");
            e.printStackTrace();
            return false;
        }
    }

    public boolean generateReport(int id, int timeTaken) {
        try {
            statement.execute("UPDATE SPILLS SET timetaken = "+timeTaken+",endtime = '"+new Timestamp(System.currentTimeMillis())+"' WHERE ID = "+id);
            return true;
        } catch (SQLException e) {
            System.out.println("Unable to generate report");
            return false;
        }
    }

    /**
     * Returns the proportion of service requests which have been completed
     * @return proportion of service requests which have been completed
     */
    public double getPercentCompleted() {
        try {
            ResultSet rs = statement.executeQuery("SELECT * FROM SPILLS WHERE STATUS = 'done'");
            int done = 0;
            while (rs.next()) {
                done++;
            }

            rs = statement.executeQuery("SELECT * FROM SPILLS");
            int total = 0;
            while (rs.next()) {
                total++;
            }
            return (double) done/total;
        } catch (SQLException e) {
            System.out.println("Unable to query database");
            return 0;
        }
    }

    /**
     * Gets completion statistics for the whole hospital for the past week
     * @return List where list[i] is the number of services completed i days ago
     */
    public Integer[] getLastWeek() {
        try {
            Timestamp current = new Timestamp(System.currentTimeMillis());
            Integer[] lastWeek = new Integer[] {0, 0, 0, 0, 0, 0, 0};
            int count = 0;
            ResultSet rs = statement.executeQuery("SELECT * FROM SPILLS WHERE STATUS = 'done'");
            while (rs.next()) {
                if (rs.getObject(11)!=null) {
                    Timestamp stamp = rs.getTimestamp(11);
                    long diff = current.getTime() - stamp.getTime();
                    diff /= 1000*60*60*24;
                    if (diff <= 6 && diff >= 0) {
                        lastWeek[(int) diff]++;
                    }
                }
            }
            return lastWeek;
        } catch (SQLException e) {
            System.out.println("Unable to get last week");
            return new Integer[] {0, 0, 0, 0, 0, 0, 0};
        }
    }

    public boolean updateStaff(int id, String first, String last, Boolean hazardous) {
        try {
            ResultSet rs = statement.executeQuery("SELECT * FROM STAFF WHERE ID = "+id);
            if (rs.next()) {
                String where = "";
                if (first != null)
                    where += "firstname = '" + first + "',";
                if (last != null)
                    where += "lastname = '" + last + "',";
                if (hazardous != null)
                    where += "hazardous = " + hazardous + ",";
                if (where.length() == 0) {
                    System.out.println("Nothing to update");
                    return false;
                }
                where = where.substring(0, where.length() - 1);
                statement.executeUpdate("UPDATE STAFF SET " + where + " WHERE ID = " + id);
                return true;
            } return false;
        } catch (SQLException e) {
            System.out.println("Unable to update staff");
            return false;
        }

    }

    /**
     * Filters spills based on the given parameters
     * If any parameter is null, spills will not be filtered based on that parameter.
     * If all parameters are null, all spills will be returned.
     * @param id ID to filter spills by
     * @param priority Priority to filter spills by
     * @param status Status to filter spills by
     * @param location Location to filter spills by
     * @param isHazardous Whether filtered spills should be hazardous
     * @param staff Staff ID to filter spills by
     * @return ArrayList of all spills matching the given parameters
     */
    public ArrayList<Spill> filterSpills(Integer id, Integer priority, String status, String location, Boolean isHazardous, Integer staff, String resolvedBy) {
        String where = "";
        if (id != null || priority != null || status != null || location != null || isHazardous != null || staff != null) {
            where = " WHERE ";
            if (id != null)
                where += "ID = "+id+" AND ";
            if (priority != null)
                where += "priority = "+priority+" AND ";
            if (status != null)
                where += "status = '"+status+"' AND ";
            if (location != null)
                where += "location = '"+location+"' AND ";
            if (isHazardous != null)
                where += "isHazardous = "+isHazardous+" AND ";
            if (staff != null)
                where += "staff = "+staff+" AND ";
            if (resolvedBy != null)
                where += "resolvedBy = '"+resolvedBy+"' AND ";
            where = where.substring(0, where.length()-4);
        }

        try {
            ArrayList<Spill> spills = new ArrayList<>();
            ResultSet rs = statement.executeQuery("SELECT * FROM SPILLS"+where);
            while (rs.next()) {
                Spill newSpill = new Spill(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getInt(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getBoolean(6),
                        null,
                        rs.getTimestamp(10));
                if (rs.getObject(7) != null) {
                    newSpill.setStaff(rs.getInt(7));
                }
                if (rs.getObject(8) != null) {
                    newSpill.setResolvedBy(rs.getString(8));
                }
                if (rs.getObject(9) != null) {
                    newSpill.setTimeTaken(rs.getInt(9));
                }
                if (rs.getObject(11) != null) {
                    newSpill.setEnd(rs.getTimestamp(11));
                }
                spills.add(newSpill);
            }

            for (Spill s : spills) {
                if (s.getStaff() != null) {
                    rs = statement.executeQuery("SELECT * FROM STAFF WHERE ID = "+s.getStaff());
                    rs.next();
                    s.setName(rs.getString(2) + " " + rs.getString(3));
                }
            }

            return spills;
        } catch (SQLException e) {
            System.out.println("Unable filter spills");
            return null;
        }
    }

    /**
     * Filters staff based on the given parameters
     * If any parameter is null, staff will not be filtered based on that parameter.
     * If all parameters are null, all staff will be returned
     * @param id ID to filter by
     * @param first First name to filter by
     * @param last name to filter by
     * @param hazardous Whether filtered staff should be able to clean hazardous spills
     * @return ArrayList of staff filtered based on the given parameters
     */
    public ArrayList<SpillStaff> filterStaff(Integer id, String first, String last, Boolean hazardous) {
        String where = "";
        if (id != null || first != null || last != null || hazardous != null) {
            where = " WHERE ";
            if (id != null)
                where += "id = "+id+" AND ";
            if (first != null)
                where += "firstname = '"+first+"' AND ";
            if (last != null)
                where += "lastname = '"+last+"' AND ";
            if (hazardous != null)
                where += "hazardous = "+hazardous+" AND ";
            where = where.substring(0, where.length()-4);
        }

        try {
            ArrayList<SpillStaff> staff = new ArrayList<>();
            ResultSet rs = statement.executeQuery("SELECT * FROM STAFF"+where);
            while (rs.next()) {
                SpillStaff newStaff = new SpillStaff(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getBoolean(4));
                staff.add(newStaff);
            }

            for (SpillStaff s : staff) {
                rs = statement.executeQuery("SELECT * FROM SPILLS WHERE staff = "+s.getID());
                while (rs.next()) {
                    s.addTask(rs.getInt(1));
                }

                int x = 0;
                rs = statement.executeQuery("SELECT * FROM SPILLS WHERE staff = "+s.getID()+" AND STATUS = 'done'");
                while (rs.next()) {
                    x++;
                }
                s.setCleaned(x);
                if (s.getTask().size() == 0)
                    s.setCompleted(0);
                else
                    s.setCompleted((double) x/s.getTask().size());

                Timestamp current = new Timestamp(System.currentTimeMillis());
                Integer[] lastWeek = new Integer[] {0, 0, 0, 0, 0, 0, 0};
                long totalTime = 0;
                int count = 0;
                for (Integer i : s.getTask()) {
                    rs = statement.executeQuery("SELECT * FROM SPILLS WHERE ID = "+i + " AND STATUS = 'done'");
                    if (rs.next()) {
                        Timestamp stamp = rs.getTimestamp(10);
                        long diff = current.getTime() - stamp.getTime();
                        diff /= 1000*60*60*24;
                        if (diff <= 6 && diff >= 0) {
                            lastWeek[(int) diff]++;
                        }
                        if (rs.getObject(11)!=null) {
                            count++;
                            totalTime += rs.getTimestamp(11).getTime()-stamp.getTime();
                        }
                    }
                }
                s.setLastWeek(lastWeek);
                if (count != 0) {
                    s.setCompleteTime(totalTime / count);
                }
            }

            return staff;
        } catch (SQLException e) {
            System.out.println("Unable filter spills");
            e.printStackTrace();
            return null;
        }
    }

    public boolean hasSpill(int spillID) {
        try {
            ResultSet rs = statement.executeQuery("SELECT * FROM SPILLS WHERE ID = "+spillID);
            if (!rs.next()) {
                return false;
            }
            return true;
        } catch (SQLException e) {
            System.out.println("Unable to query spill database");
            return false;
        }
    }

    private boolean hasStaff(int staffID) {
        try {
            ResultSet rs = statement.executeQuery("SELECT * FROM STAFF WHERE ID = "+staffID);
            if (!rs.next()) {
                return false;
            }
            return true;
        } catch (SQLException e) {
            System.out.println("Unable to query staff database");
            return false;
        }
    }

    public int getSpillID(){
        //since it was the most recently created spill, it can return the current spill tracker number minus its incremented value
        return idTracker - 1;
    }
}
