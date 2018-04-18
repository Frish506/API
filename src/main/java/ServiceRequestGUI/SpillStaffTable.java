package ServiceRequestGUI;

import Database.SpillAPI.SpillStaff;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public class SpillStaffTable {

    public int ID;
    public SimpleBooleanProperty isChecked;
    public SimpleStringProperty firstName;
    public SimpleStringProperty lastName;
    public SimpleBooleanProperty cleanHazard;

    public SpillStaff getAssociatedStaff() {
        return associatedStaff;
    }

    public void setAssociatedStaff(SpillStaff associatedStaff) {
        this.associatedStaff = associatedStaff;
    }

    public SpillStaff associatedStaff;

    public SpillStaffTable(int id, Boolean isChecked, String firstName, String lastName, boolean cleanHazard, SpillStaff staff){
        this.ID = id;
        this.isChecked = new SimpleBooleanProperty(isChecked);
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.cleanHazard = new SimpleBooleanProperty(cleanHazard);
        this.associatedStaff = staff;

    }

    public SpillStaff getStaff() {
        return associatedStaff;
    }

    public void setStaff(SpillStaff staff) {
        this.associatedStaff = staff;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public boolean isIsChecked() {
        return isChecked.get();
    }

    public SimpleBooleanProperty isCheckedProperty() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked.set(isChecked);
    }

    public void setFirstName(String value) { firstNameProperty().set(value); }
    public String getFirstName() { return firstNameProperty().get(); }
    public SimpleStringProperty firstNameProperty() {
        if (firstName == null) {
            firstName = new SimpleStringProperty(this, "First Name");
        }
        return firstName;
    }

    public String getLastName() {
        return lastName.get();
    }

    public SimpleStringProperty lastNameProperty() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    public boolean isCleanHazard() {
        return cleanHazard.get();
    }

    public SimpleBooleanProperty cleanHazardProperty() {
        return cleanHazard;
    }

    public void setCleanHazard(boolean cleanHazard) {
        this.cleanHazard.set(cleanHazard);
    }


}
