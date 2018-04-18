package Database.SpillAPI;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class SpillRequestTable {

    public int ID;
    public SimpleBooleanProperty checked;



    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public SpillRequestTable(int id, Boolean checked, String desc, String time, String status, int Priority, String assignee, Spill spill){
        this.ID = id;

        this.checked = new SimpleBooleanProperty(checked);
        this.Description = new SimpleStringProperty(desc);
        this.Time = new SimpleStringProperty(time);
        this.Status = new SimpleStringProperty(status);
        this.Priority = new SimpleIntegerProperty(Priority);
        this.Assignee = new SimpleStringProperty(assignee);
        this.associatedSpill = spill;
    }

    public SimpleStringProperty Description;
    public void setDescription(String value) { descriptionProperty().set(value); }
    public String getDescription() { return descriptionProperty().get(); }
    public SimpleStringProperty descriptionProperty() {
        if (Description == null) {
            Description = new SimpleStringProperty(this, "Description");
        }
        return Description;
    }

    public SimpleStringProperty Time;
    public void setTime(String value){
        timeProperty().set(value);
    }
    public String getTime(){
        return timeProperty().get();
    }
    public SimpleStringProperty timeProperty() {
        if (Time == null) {
            Time = new SimpleStringProperty(this, "Time");
        }
        return Time;
    }


    public SimpleStringProperty Status;
    public void setStatus(String status){
        statusProperty().set(status);
    }
    public String getStatus(){
        return statusProperty().get();
    }
    public SimpleStringProperty statusProperty() {
        if (Status == null) {
            Status = new SimpleStringProperty(this, "Status");
        }
        return Status;
    }

    public SimpleIntegerProperty Priority;
    public void setPriority(int priority){
        priorityProperty().set(priority);
    }
    public int getPriority(){
        return priorityProperty().get();
    }
    public SimpleIntegerProperty priorityProperty() {
        if (Priority == null) {
            Priority = new SimpleIntegerProperty(this, "Priority");
        }
        return Priority;
    }

    public SimpleStringProperty Assignee;
    public void setAssignee(String assignee){ //WHY UNUSED???
        assigneeProperty().set(assignee);
    }
    public String getAssignee(){
        return assigneeProperty().get();
    }
    public SimpleStringProperty assigneeProperty() {
        if (Assignee == null) {
            Assignee = new SimpleStringProperty(this, "Assignee");
        }
        return Assignee;
    }

    public Spill associatedSpill;
    public Spill getAssociatedSpill(){
        return associatedSpill;
    }

    public Boolean getChecked(){
        return checked.get();
    }


    public void setSpill(Spill spill){
        this.associatedSpill = spill;
    }





}
