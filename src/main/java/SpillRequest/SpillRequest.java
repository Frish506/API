package SpillRequest;

import PhoneFeature.Server;
import Stages.StagesClass;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class SpillRequest {

    public SpillRequest() {

    }

    public void run(int xcoord, int ycoord, int windowWidth, int windowLength, String cssPath, String destNodeID, String originNodeID) throws Exception {

        Server s = new Server();
        s.runServer();

        StagesClass.serviceStage = new Stage();
        ///////////////////Set up Scenes Here////////////

        Parent ServiceRequestScreen = FXMLLoader.load(getClass().getResource("/FXMLFiles/ServiceRequest/SpillTab.fxml"));//These files need to be in the resource folder!
        StagesClass.serviceScene = new Scene(ServiceRequestScreen, windowWidth, windowLength);
        StagesClass.serviceScene.getStylesheets().add(cssPath);

        StagesClass.reportStage = new Stage();
        Parent ReportScreen = FXMLLoader.load(getClass().getResource("/FXMLFiles/ServiceRequest/Report.fxml")); //change
        StagesClass.reportScene = new Scene(ReportScreen, 1080, 700); //change
        StagesClass.reportStage.initModality(Modality.APPLICATION_MODAL);
        StagesClass.reportStage.setTitle("Report Window");
        StagesClass.reportStage.setScene(StagesClass.reportScene);

        StagesClass.serviceStage.setX(xcoord);
        StagesClass.serviceStage.setY(ycoord);

        StagesClass.reportStage.setX(xcoord);
        StagesClass.reportStage.setY(ycoord);


        StagesClass.serviceStage.setScene(StagesClass.serviceScene);
        StagesClass.serviceStage.show();
    }
}
