package Database.SpillAPI;

import java.sql.Timestamp;

public class Spill {
    private int id;
    private String description;
    private int priority;
    private String status;
    private String location, resolvedBy, name;
    private boolean isHazardous;
    private Integer staff, timeTaken;
    private Timestamp start, end;


    public Spill(int id, String description, int priority, String status,
          String location, boolean isHazardous, Integer staff, Timestamp start) {
        this.id = id;
        this.description = description;
        this.priority = priority;
        this.status = status; //received, in progress, done
        this.location = location;
        this.isHazardous = isHazardous;
        this.staff = staff;
        this.resolvedBy = null;
        this.name = null;
        this.start = start;
        this.timeTaken = null;
        this.end = null;
    }

    /**
     * Gets the SQL statement needed to insert this spill into the database
     * @return SQL statement to insert this spill into the database
     */
    String getSQLInsert() {
        String middle = "id,description,priority,status,location,isHazardous,start";
        String end = id+",'"+description+"',"+priority+",'"+status+"','"+location+"',"+isHazardous+",'"+start+"'";

        if (staff != null) {
            middle += ",staff";
            end += ","+staff+"";
        }
        if (resolvedBy != null) {
            middle += ",resolvedBy";
            end += ",'"+resolvedBy+"'";
        }
        if (this.end != null) {
            middle += ",endtime";
            end += ",'"+this.end+"'";
        }
        if (timeTaken != null) {
            middle += ",timetaken";
            end += ","+timeTaken+"";
        }

        return "INSERT INTO SPILLS ("+middle+") VALUES ("+end+")";
    }

    //
    // Getters and Setters
    //

    public Integer getID() {
        return this.id;
    }

    public String getDescription() {
        return this.description;
    }

    public int getPriority() {
        return priority;
    }

    public String getStatus() {
        return status;
    }

    public String getLocation() {
        return location;
    }

    public boolean getIsHazardous(){
        return this.isHazardous;
    }

    public Integer getStaff() {
        return this.staff;
    }

    public String getResolvedBy() {
        return this.resolvedBy;
    }

    public String getName() {
        return this.name;
    }

    public Timestamp getStart() { return this.start; }

    public Timestamp getEnd() { return this.end; }

    public int getTimeTaken() { return this.timeTaken; }

    void setStaff(Integer staff) {
        this.staff = staff;
    }

    void setResolvedBy(String by) {
        this.resolvedBy = by;
    }

    void setName(String name) {
        this.name = name;
    }

    void setTimeTaken(int timeTaken) {
        this.timeTaken = timeTaken;
    }

    void setEnd(Timestamp end) {
        this.end = end;
    }
}
