package ServiceRequestGUI;

import Database.SpillAPI.SpillManager;
import Database.SpillAPI.SpillStaff;
import com.jfoenix.controls.JFXComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;

import java.util.ArrayList;
import java.util.Arrays;

public class ReportController {
    ///PROGRESS RANGE 0.0 -> 1.0, indeterminate is -1.0
    private double progress = 0;
    private double unresolved = 0;

    @FXML
    private Button update = new Button();
    @FXML
    private Button overview = new Button();
    @FXML
    private ProgressIndicator resolvedReqs = new ProgressIndicator();

    @FXML
    private Label t = new Label();
    @FXML
    private PieChart pieChart = new PieChart();
    @FXML
    private JFXComboBox<SpillStaff> empList = new JFXComboBox<>();
    @FXML
    private CategoryAxis x = new CategoryAxis();
    @FXML
    private NumberAxis y = new NumberAxis();
    @FXML
    private LineChart<String, Number> weekly = new LineChart<>(x,y);

    //grabs SpillManager database singleton
    private SpillManager db = SpillManager.getSpillMangager();

    public void initialize(){
        reset();
        populateTabReport();
        updateProgTotal();
    }

    //resets employee list
    public void reset(){

        //clears the chart
        XYChart.Series<String, Number> empty = new XYChart.Series();
        weekly.getData().addAll(empty);

        //clears the combobox
        empList.getItems().clear();

        //re-populates the list
        populateTabReport();
    }

    /**
     * Populates the employee tab so
     * admin can select employee to search for
     */
    public void populateTabReport() {
        ArrayList<SpillStaff> possibleStaff = db.filterStaff(null, null, null, null);

        empList.getItems().clear();
        empList.getItems().addAll(possibleStaff);
    }

    /**
     * Method that computes the total
     * percent of completed spill requests.
     * @return int
     */
    public double getTotalStats(){
        return db.getPercentCompleted();
    }

    /**
     * Method that computes the given employee's
     * percent of completed spill requests.
     * @param id
     * @return int
     */
    public double getIndivStats(int id){

        //searches the db for the staff with the corresponding ID
        SpillStaff e = db.filterStaff(id, null, null, null).get(0);

        //return the employee
        return e.getCompleted();
    }

    //fires on action of update button
    //gets the stats then resets the combobox
    public void updateProgIndividual(){
        //resets progress bar
        progress = 0;

        //gets the staffID from the selected staff in combo box
        int staffID = empList.getValue().getID();

        //gets the statistics of that individual employee
        progress = getIndivStats(staffID);
        unresolved = 1-progress;

        //populates the pie chart
        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(
                        new PieChart.Data("Resolved Requests", progress),
                        new PieChart.Data("Unresolved Requests", unresolved));
        pieChart.setData(pieChartData);

        //updates the chart to display their daily stats
        updateChart(staffID);
        updateTime(staffID);

        //updates the average time for the employee
        String time = String.valueOf(updateTime(staffID));
        t.setText(time);
    }

    //updates the total progress of hospital staff
    //default screen
    public void updateProgTotal(){
        progress = getTotalStats();
        resolvedReqs.setProgress(progress);
    }

    //updates the linechart with the daily request completions
    public void updateChart(int id){

        //reset chart
        weekly.getData().clear();

        //initializing series for linechart
        XYChart.Series<String, Number> completions = new XYChart.Series();

        //getting the employee we selected
        SpillStaff e  = db.filterStaff(id, null, null, null).get(0);

        //returns a list of tasks assigned last week,
        // where list[i] = # of tasks assigned i days ago
        Integer[] weeklyTasks = e.getLastWeek();

        //enters the # of days ago (i) and # of requests assigned (index of i) into the series
        for (int i = Arrays.asList(weeklyTasks).size()-1; i >=0; i--){
            if (i == 0) {
                completions.getData().add(new XYChart.Data("Today", weeklyTasks[i]));
            }
            else if (i == 1){
                completions.getData().add(new XYChart.Data("Yesterday", weeklyTasks[i]));
            }
            else {
                completions.getData().add(new XYChart.Data(i + " Days Ago", weeklyTasks[i]));
            }
        }

        //show data
        weekly.getData().addAll(completions);

    }

    public long updateTime(int id){
        SpillStaff e = db.filterStaff(id, null, null, null).get(0);

        long time = e.getCompleteTime();

        long seconds = time/1000;

        long rem = seconds%60;

        long min = seconds / 60;

        return e.getCompleteTime();
    }

    public void showOverview(){

        //reset chart
        weekly.getData().clear();

        //initializing series for linechart
        XYChart.Series<String, Number> completions = new XYChart.Series();

        //returns a list of tasks assigned last week,
        // where list[i] = # of tasks assigned i days ago
        Integer[] weeklyTasks = db.getLastWeek();

        //enters the # of days ago (i) and # of requests assigned (index of i) into the series
        for (int i = Arrays.asList(weeklyTasks).size()-1; i >=0; i--){
            completions.getData().add(new XYChart.Data(i+" Days Ago", weeklyTasks[i]));
        }

        //show data
        weekly.getData().addAll(completions);

        progress = getTotalStats();
        unresolved = 1-progress;

        //populates the pie chart
        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(
                        new PieChart.Data("Total Resolved Requests", progress),
                        new PieChart.Data("Total Unresolved Requests", unresolved));
        pieChart.setData(pieChartData);

    }
}
