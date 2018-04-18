package ServiceRequestGUI;

import Database.SpillAPI.Spill;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class SpillRequestTable {

    public int ID;
    public SimpleStringProperty Description = new SimpleStringProperty("desc");
    public SimpleStringProperty Status = new SimpleStringProperty();
    public SimpleIntegerProperty Priority = new SimpleIntegerProperty();
    public SimpleStringProperty Assignee = new SimpleStringProperty();

    public Spill associatedSpill;

    public int getPriority(){
        return Priority.get();
    }

    public String getStatus() {
        return Status.get();
    }

    public void setID(int id){
        this.ID = id;
    }

    public int getID(){
        return this.ID;
    }

    public String getDescription(){
        return Description.get();
    }
    public void setSpill(Spill a){
        this.associatedSpill = a;
    }
    public Spill getAssociatedSpill(){
        return associatedSpill;
    }

}
