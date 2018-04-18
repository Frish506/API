package Database.SpillAPI;

import java.util.ArrayList;

public class SpillStaff {
    private int id;
    private String first, last;
    private ArrayList<Integer> task;
    private int cleaned;
    private boolean hazardous;
    private double completed;
    private Integer[] lastWeek;
    private long completeTime;

    SpillStaff(int id, String first, String last, boolean hazardous) {
        this.id = id;
        this.first = first;
        this.last = last;
        this.hazardous = hazardous;
        this.task = new ArrayList<Integer>();
    }

    /**
     * Gets the SQL statement needed to insert this object into the database
     * @return SQL statement to insert into database
     */
    String getSQLInsert() {
        String middle = "id,firstname,lastname,hazardous";
        String end = id+",'"+first+"','"+last+"',"+hazardous;

        return "INSERT INTO STAFF ("+middle+") VALUES ("+end+")";
    }

    public String toString() {
        return first + " " + last;
    }

    void addTask(int task) {
        this.task.add(task);
    }

    public boolean isHazardous() {
        return hazardous;
    }

    public void setHazardous(boolean hazardous) {
        this.hazardous = hazardous;
    }

    public Integer getID() {
        return id;
    }

    public String getFirst() {
        return first;
    }

    public String getLast() {
        return last;
    }

    public ArrayList<Integer> getTask() {
        return task;
    }

    public int getCleaned() {
        return cleaned;
    }

    public String getFullName(){
        return first + " " + last;
    }

    // gets the decimal proportion of completed tasks
    public double getCompleted() { return completed; }

    // gets a list of tasks assigned last week, where list[i] = # of tasks assigned i days ago
    public Integer[] getLastWeek() { return lastWeek; }

    // gets the average time in ms the staff takes to complete their tasks
    public long getCompleteTime() { return completeTime; }

    void setCleaned(int cleaned) {
        this.cleaned = cleaned;
    }
    void setCompleted(double completed) { this.completed = completed; }
    void setLastWeek(Integer[] lastWeek) { this.lastWeek = lastWeek; }
    void setCompleteTime(long time) { this.completeTime = time; }
}
