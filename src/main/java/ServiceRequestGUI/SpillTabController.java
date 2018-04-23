package ServiceRequestGUI;

import Database.MapDatabase.MapManager;
import Database.SpillAPI.*;
import Database.SpillAPI.SpillRequestTable;

import Database.UserLoginDatabase.UserDatabase;
import PhoneFeature.Phone;


import Stages.StagesClass;
import com.jfoenix.controls.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;

public class SpillTabController implements Initializable{

    //Toggle views
    @FXML private JFXButton manageRequests;
    @FXML private JFXButton manageStaff;

    //FORM
    @FXML private JFXTextField description;
    @FXML private JFXToggleButton toggleHazardous;
    @FXML private JFXComboBox<Integer> priorityAdd;
    @FXML private JFXComboBox<SpillStaff> assigneeAdd = new JFXComboBox();
    @FXML private JFXComboBox<String> locationAdd = new JFXComboBox();
    @FXML private Label errorMessage;
    //submit button

    //Filter
    @FXML private JFXComboBox<Integer> priorityFilter = new JFXComboBox();
    @FXML private JFXComboBox<String> statusFilter = new JFXComboBox();
    @FXML private JFXComboBox<SpillStaff> assigneeFilter = new JFXComboBox();


    //REQUEST MANAGER
    @FXML private AnchorPane requestPane;
    @FXML TableView<SpillRequestTable> spillTable =  new TableView<SpillRequestTable>();
    @FXML private JFXButton deleteButton;
    @FXML private JFXComboBox<Integer> priorityUpdate = new JFXComboBox<>();
    @FXML private JFXComboBox<String> statusUpdate = new JFXComboBox<>();
    @FXML private JFXComboBox<SpillStaff> assigneeUpdate = new JFXComboBox<>();

    //STAFF MANAGER
    @FXML private AnchorPane staffPane;
    @FXML private JFXTextField addFirstName;
    @FXML private JFXTextField addLastName;
    @FXML private JFXToggleButton hazardToggle;
    @FXML private JFXTextField updateId;
    @FXML private JFXTextField updateFirst;
    @FXML private JFXTextField UpdateLast;
    @FXML TableView<SpillStaffTable> staffTable =  new TableView<SpillStaffTable>();



    //Database connections
    SpillManager _spillDB = SpillManager.getSpillMangager();
    UserDatabase adminDB = UserDatabase.getUserDatabase();
    String adminName = adminDB.getCurrentUsername();
    public final static MapManager mapManager = MapManager.getInstance();


    //set up Spill table
    //private boolean spillBool = _spillDB.addSpill("First Spill", 2, "received", "Kiosk1", false, 1);
    ObservableList<SpillRequestTable> spillData = FXCollections.observableArrayList(); // create the spill data

    //set up Staff table
    private boolean staffBool = _spillDB.addStaff("Randy", "Smith", false);
    ObservableList<SpillStaffTable> staffData = FXCollections.observableArrayList(); // create the staff data


    //For Location dropdown
    Iterator<String> iterKey = mapManager.nodeNames.keySet().iterator(); //creates iterator of all the keys in the hashmap
    //ObservableList<MaintenanceTable> data = FXCollections.observableArrayList(); // create the spill data


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        errorMessage.setVisible(false);

        iterKey = mapManager.nodeNames.keySet().iterator(); //creates iterator of all the keys in the hashmap
        while (iterKey.hasNext()) { //iterates through each key in the hashmap storing the MapNodes and adds each string to each dropdown bar
            String temp = iterKey.next();
            String addee = temp;
            locationAdd.getItems().addAll(addee);//add that key to the list
        }

        priorityAdd.getItems().addAll(1, 2, 3, 4, 5);

        priorityUpdate.getItems().addAll(1, 2, 3, 4,5);
        statusUpdate.getItems().addAll("received", "in progress", "done");

        priorityFilter.getItems().addAll(1,2, 3, 4, 5);
        statusFilter.getItems().addAll("received", "in progress", "done");

        populateTab(); //populate all the dropdowns

        //columns in request table
        TableColumn<SpillRequestTable, Boolean> checkbox = new TableColumn<>();
        checkbox.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<SpillRequestTable,Boolean>,ObservableValue<Boolean>>()
        {
            //This callback tell the cell how to bind the data model 'Checked' property to
            //the cell, itself.
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<SpillRequestTable, Boolean> param)
            {
                return param.getValue().checked;
            }
        });
        checkbox.setCellFactory(CheckBoxTableCell.forTableColumn(checkbox));

        TableColumn<SpillRequestTable,String> descriptionCol = new TableColumn<SpillRequestTable,String>("Description");
        descriptionCol.setCellValueFactory(new PropertyValueFactory("Description"));

        TableColumn<SpillRequestTable,String> timeCol = new TableColumn<SpillRequestTable,String>("Time");
        timeCol.setCellValueFactory(new PropertyValueFactory("Time"));

        TableColumn<SpillRequestTable,String> statusCol = new TableColumn<SpillRequestTable,String>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory("Status"));

        statusCol.setEditable(true);

        /*ObservableList<String> statusDropdown = FXCollections.observableArrayList();
        statusDropdown.add("Received");
        statusDropdown.add("In Progress");
        statusDropdown.add("Done");

        statusCol.setCellFactory(ComboBoxTableCell.forTableColumn(new DefaultStringConverter(), statusDropdown));


        statusCol.setOnEditCommit(event ->{
            int id = staffTable.getSelectionModel().getSelectedItem().getID();

            SpillRequestTable spillSelected = spillTable.getSelectionModel().getSelectedItem();
            String newStatus = event.getNewValue();
            spillSelected.setStatus(newStatus);

            //    public boolean updateSpill(int id, String description, Integer priority, String status, String location, Boolean isHazard, String resolvedBy) {

            boolean updateSpill =  _spillDB.updateSpill(id, null, null, newStatus, null, null, null);
        });*/

        //statusCol.setCellFactory(ComboBoxTableCell.forTableColumn(statusFilter));

        TableColumn<SpillRequestTable,Integer> priorityCol = new TableColumn<SpillRequestTable,Integer>("Priority");
        priorityCol.setCellValueFactory(new PropertyValueFactory("Priority"));

        /*ObservableList<Integer> priorityDropdown = FXCollections.observableArrayList();
        priorityDropdown.addAll(1, 2, 3, 4, 5);

        priorityCol.setCellFactory(ComboBoxTableCell.forTableColumn(priorityDropdown));

        priorityCol.setOnEditCommit(event ->{
            int id = staffTable.getSelectionModel().getSelectedItem().getID();

            SpillRequestTable spillSelected = spillTable.getSelectionModel().getSelectedItem();
            int newPriority = event.getNewValue();
            spillSelected.setPriority(newPriority);

            boolean updateSpill =  _spillDB.updateSpill(id, null, newPriority, null, null, null, null);
        });*/

        TableColumn<SpillRequestTable,String> assigneeCol = new TableColumn<SpillRequestTable,String>("Assignee");
        assigneeCol.setCellValueFactory(new PropertyValueFactory("Assignee"));

        spillTable.getColumns().setAll(checkbox, descriptionCol, timeCol, statusCol, priorityCol, assigneeCol);

        spillTable.setItems(spillData);

        //set editable
        spillTable.setEditable(true);
        checkbox.setEditable(true);
        statusCol.setEditable(true);
        priorityCol.setEditable(true);
        assigneeCol.setEditable(true);

        //set widths
        descriptionCol.setPrefWidth(131.6712417602539);
        timeCol.setPrefWidth(178.32875061035156);
        statusCol.setPrefWidth(111.8355712890625);
        priorityCol.setPrefWidth(82.876708984375);
        assigneeCol.setPrefWidth(140.12322998046875);

        generateSpillTable(false, null, null, null);


        //******************************************* SET UP STAFF TABLE ***********************************************

        TableColumn<SpillStaffTable, Boolean> staffCheck = new TableColumn<>();
        staffCheck.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<SpillStaffTable,Boolean>,ObservableValue<Boolean>>()
        {

            //This callback tell the cell how to bind the data model 'Checked' property to
            //the cell, itself.
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<SpillStaffTable, Boolean> param)
            {
                return param.getValue().isChecked;
            }
        });
        staffCheck.setCellFactory(CheckBoxTableCell.forTableColumn(staffCheck));

        TableColumn<SpillStaffTable,String> firstCol = new TableColumn<SpillStaffTable,String>("First Name");
        firstCol.setCellValueFactory(new PropertyValueFactory("FirstName"));

        firstCol.setCellFactory(TextFieldTableCell.forTableColumn());

        firstCol.setOnEditCommit(event ->  {
                int id = staffTable.getSelectionModel().getSelectedItem().getID();

                SpillStaffTable staffSelected = staffTable.getSelectionModel().getSelectedItem();
                String newName = event.getNewValue().toString();
                staffSelected.setFirstName(newName);

                boolean updateFirst =  _spillDB.updateStaff(id, newName, null, null);
        });



        TableColumn<SpillStaffTable,String> lastCol = new TableColumn<SpillStaffTable,String>("Last Name");
        lastCol.setCellValueFactory(new PropertyValueFactory("LastName"));

        lastCol.setCellFactory(TextFieldTableCell.forTableColumn());

        lastCol.setOnEditCommit(event ->  {
            int id = staffTable.getSelectionModel().getSelectedItem().getID();

            SpillStaffTable staffSelected = staffTable.getSelectionModel().getSelectedItem();
            String newName = event.getNewValue().toString();
            staffSelected.setLastName(newName);

            boolean updateFirst =  _spillDB.updateStaff(id, null, newName, null);
        });


        TableColumn<SpillStaffTable,Boolean> hazardCol = new TableColumn<SpillStaffTable,Boolean>("Clean Hazardous");
        hazardCol.setCellValueFactory(new PropertyValueFactory("CleanHazard"));

        ObservableList<Boolean> trueFalseDropdown = FXCollections.observableArrayList();
        trueFalseDropdown.add(true);
        trueFalseDropdown.add(false);

        hazardCol.setCellFactory(ComboBoxTableCell.forTableColumn(trueFalseDropdown));

        hazardCol.setOnEditCommit(event ->{
            int id = staffTable.getSelectionModel().getSelectedItem().getID();

            SpillStaffTable staffSelected = staffTable.getSelectionModel().getSelectedItem();
            boolean newHazard = event.getNewValue();
            staffSelected.setCleanHazard(newHazard);

            boolean updateHazard =  _spillDB.updateStaff(id, null, null, newHazard);
        });

        staffTable.getColumns().setAll(staffCheck, firstCol, lastCol, hazardCol);

        staffTable.setItems(staffData);

        staffTable.setEditable(true);
        staffTable.setVisible(true);
        firstCol.setEditable(true);
        lastCol.setEditable(true);
        hazardCol.setEditable(true);

        staffTable.setPrefWidth(797.0);
        staffCheck.setPrefWidth(60.249305725097656);
        firstCol.setPrefWidth(278.16162872314453);
        lastCol.setPrefWidth(254.49313354492188);
        hazardCol.setPrefWidth(201.5068359375);

        generateStaffTable();
    }

    public void populateTab() {
        assigneeAdd.getItems().clear();
        ArrayList<SpillStaff> possibleStaff = _spillDB.filterStaff(null, null, null, null);
        assigneeAdd.getItems().addAll(possibleStaff);

        assigneeFilter.getItems().clear();
        assigneeFilter.getItems().addAll(possibleStaff);

        assigneeUpdate.getItems().clear();
        assigneeUpdate.getItems().addAll(possibleStaff);

    }

    /**
     * invoke on click of Manage Requests button
     */
    public void requestManager(){
        //staffPane invisible
        populateTab();
        staffPane.setVisible(false);
        requestPane.setVisible(true);

    }

    public void staffManager(){
        //staffPane invisible
        requestPane.setVisible(false);
        staffPane.setVisible(true);

    }

    /**
     * invoked on submit button
     */
    public void submitARequest(){
        //default to false
        boolean hazardFlag = false; //true if is hazardous (isHazardous is selected)

        if (toggleHazardous.isSelected()) {
            hazardFlag = true; //yes hazardous
        }

        //null checks for text fields
        if (description.getText() == null || description.getText().trim().isEmpty()){
            errorMessage.setVisible(true);
            return; //exit
        }
        //add and check if it was added successfully
        //status is 0 on initialization meaning the request is UNRESOLVED
        boolean serviceAddedSuccess = _spillDB.addSpill(description.getText(), priorityAdd.getSelectionModel().getSelectedItem(), "Received", "Kiosk 1", hazardFlag,
                assigneeAdd.getSelectionModel().getSelectedItem().getID());

        if(serviceAddedSuccess){
            errorMessage.setVisible(false);

            if (priorityAdd.getSelectionModel().getSelectedItem() > 3){
                //CALL and TEXT
                Phone aPhone = new Phone("+17812547256");
                //return the id of the spill being used
                int currID = _spillDB.getSpillID();
                String desc = description.getText();

                aPhone.sendCall("New Urgent spill request: " + desc + " Priority: " + priorityAdd.getSelectionModel().getSelectedItem());
                aPhone.sendMessage( "New spill request, ID: " +currID+ ". Description: " + desc + ". Priority: " + priorityAdd.getSelectionModel().getSelectedItem() +
                                    ". To update request status, reply with the spill ID (contained in this message), a single space, and the updated status of INPROGRESS, or DONE.");
            }
            else{
                //TEXT
                int currID = _spillDB.getSpillID();
                Phone aPhone = new Phone("+17812547256");
                aPhone.sendMessage( "New spill request, ID: " +currID+ ". Description: " + description.getText() + ". Priority: " + priorityAdd.getSelectionModel().getSelectedItem() +
                        ". To update request status, reply with the spill ID (contained in this message), a single space, and the updated status of INPROGRESS, or DONE.");            }
        }
        else {
            errorMessage.setVisible(true);
        }

        //private method that refreshes the table
        generateSpillTable(false, null, null, null);
        System.out.println("generated Table");

    }

    public void clearFilter(){

        //only impact Filter dropdowns
        priorityFilter.getItems().clear();
        statusFilter.getItems().clear();
        assigneeFilter.getItems().clear();

        ArrayList<SpillStaff> possibleStaff = _spillDB.filterStaff(null, null, null, null);

        priorityFilter.getItems().addAll(1,2, 3, 4, 5);
        statusFilter.getItems().addAll("received", "in progress", "done");
        assigneeFilter.getItems().addAll(possibleStaff);

        //reset the table
        generateSpillTable(false, null, null, null);

    }


    /**
     * invoke on click of Filter Button
     */
    public void filterSpills(){
        Integer assignee = null;
        Integer priority = null;
        String status = null;

        if(priorityFilter.getSelectionModel().getSelectedItem() != null){
            priority = priorityFilter.getSelectionModel().getSelectedItem();
        }

        if(statusFilter.getSelectionModel().getSelectedItem() != null){
            status = statusFilter.getSelectionModel().getSelectedItem();
        }

        if (assigneeFilter.getSelectionModel().getSelectedItem() != null){
            assignee = assigneeFilter.getSelectionModel().getSelectedItem().getID();
        }

        generateSpillTable(true, priority, status, assignee);

    }

    public void refreshUpdateDropdowns(){
        priorityUpdate.getItems().clear();
        statusUpdate.getItems().clear();
        assigneeUpdate.getItems().clear();

        ArrayList<SpillStaff> possibleStaff = _spillDB.filterStaff(null, null, null, null);

        priorityUpdate.getItems().setAll(1, 2, 3, 4, 5);
        statusUpdate.getItems().setAll("received", "in progress", "done");
        assigneeUpdate.getItems().setAll(possibleStaff);

    }

   /*
    public void populateFields(){
        SpillRequestTable selectedSpill = spillTable.getSelectionModel().getSelectedItem();

        if(selectedSpill == null){
            return;
        }

        String status = selectedSpill.getStatus();

        //statusUpdate.set



    }

    /**
     * invoked on edit commit of Status
     * when changed to DONE - fill resolve field
     * Handling is done here
     */
    public void updateSpill(){
        //assigneeUpdate.setAccessibleText();
        //get info from selected Spill and put in ComboBox
        SpillRequestTable selectedSpill = spillTable.getSelectionModel().getSelectedItem();

        if(selectedSpill == null){
            return;
        }

        int id = selectedSpill.getID();

        Integer assignee = null;
        Integer priority = null;
        String status = null;

        //int newPriority = ;

        boolean isMyComboBoxEmpty = priorityUpdate.getSelectionModel().isEmpty();

        if(!isMyComboBoxEmpty){
            priority = priorityUpdate.getSelectionModel().getSelectedItem();
        }

        //String newStatus = statusUpdate.getSelectionModel().getSelectedItem();
        if(statusUpdate.getSelectionModel().getSelectedItem() != null){
            status = statusUpdate.getSelectionModel().getSelectedItem();
        }

        //int updateID = assigneeUpdate.getSelectionModel().getSelectedItem().getID();

        if (assigneeUpdate.getSelectionModel().getSelectedItem() != null){
            assignee = assigneeUpdate.getSelectionModel().getSelectedItem().getID();
        }
        //update
        _spillDB.updateSpill(id, null, priority, status, null, null, null, assignee);

        //regenerate the whole table
        generateSpillTable(false, null, null, null);

        //call??

    }

    /**
     * invoke on Delete Button
     */
    public void deleteSpills(){
        //get selections
        //delete from DB
        //reload the whole table

        boolean delete = false;

        for (int i = 0; i < spillData.size(); i++) {
            System.out.println("trying to remove");
            if (spillData.get(i).checked.getValue()) {
                //delete
                delete = _spillDB.deleteSpill(spillData.get(i).getAssociatedSpill().getID());
            }
        }
        //reload the whole table - even if there is a filter on
        if (delete){
            generateSpillTable(false, null, null, null);
        }

    }
    /**
     * Map Spill object to SpillRequestTable
     */
    public void generateSpillTable(boolean filter, Integer priority, String status, Integer assignee){
        spillData.clear();
        ArrayList<Spill> ret = new ArrayList<>();

        //get ALL the service requests
        if (filter = false){
            //get ALL Spills
            ret =  _spillDB.filterSpills(null, null, null, null, null, null, null);
        }
        else{
            //filter the spills
            ret = _spillDB.filterSpills(null, priority, status, null, null, assignee, null);
        }

        if(ret != null){
            for (int i = 0; i<ret.size(); i++){
                Spill curspill = ret.get(i);
                System.out.print(curspill.getID());
                spillData.add(generateSpillEntry(curspill.getID(), curspill.getDescription(), curspill.getStart().toString(), curspill.getStatus(), curspill.getPriority(), curspill.getName(), curspill));
            }
        }
    }

    private SpillRequestTable generateSpillEntry(int id, String desc, String time, String status, int priority, String assignee, Spill aSpill) {
        // Add to the data any time, and the table will be updated
        SpillRequestTable entry = new SpillRequestTable(id, false, desc, time, status, priority, assignee, aSpill);
        return entry;
    }

    //*************************************************************** STAFF **************************************************************************

    public void addStaff(){

        boolean hazard = false; //true if is hazardous (isHazardous is selected)

        if (hazardToggle.isSelected()) {
            hazard = true; //yes hazardous
        }

        //null checks for text fields
        if (addFirstName.getText() == null || addFirstName.getText().trim().isEmpty()
                || addLastName.getText() == null || addLastName.getText().trim().isEmpty() ){
            return; //exit
        }

        boolean didItWork = _spillDB.addStaff(addFirstName.getText(), addLastName.getText(), hazard);

        if(didItWork){
            generateStaffTable(); //populate the table
        }
    }

    public void deleteStaff(){

        boolean delete = false;

        for (int i = 0; i < staffData.size(); i++) {
            System.out.println("trying to remove");
            if (staffData.get(i).isChecked.getValue()) {
                //delete
                delete = _spillDB.removeStaff(staffData.get(i).getAssociatedStaff().getID());
            }
        }
        //reload the whole table - even if there is a filter on
        if (delete){
            generateStaffTable();
        }
    }

    public void updateStaff(){

        if (updateId.getText() == null || updateId.getText().trim().isEmpty()
                || updateFirst.getText() == null || updateFirst.getText().trim().isEmpty()
                || UpdateLast.getText() == null || UpdateLast.getText().trim().isEmpty()){
            return; //exit
        }

        _spillDB.updateStaff(Integer.parseInt(updateId.getText()), updateFirst.getText(), UpdateLast.getText(), null);
    }

    private void generateStaffTable(){
        staffData.clear();

        ArrayList<SpillStaff> retStaff = _spillDB.filterStaff(null, null, null, null);

        if (retStaff != null){
            for(int i = 0; i < retStaff.size(); i++){
                SpillStaff currStaff = retStaff.get(i);
                staffData.add(mapStaffEntry(currStaff.getID(), currStaff.getFirst(), currStaff.getLast(), currStaff.isHazardous(), currStaff));
            }
        }

    }

    private SpillStaffTable mapStaffEntry(int id, String first, String last, boolean hazard, SpillStaff staff){
        SpillStaffTable entry = new SpillStaffTable(id, false, first, last, hazard, staff);
        return entry;
    }

    public void generateReportPressed(){
        StagesClass.reportStage.showAndWait();
    }



}
