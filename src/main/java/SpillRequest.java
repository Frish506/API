

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

        StagesClass.mainStage = new Stage();
        ///////////////////Set up Scenes Here////////////

        Parent ServiceRequestScreen = FXMLLoader.load(getClass().getResource("FXMLFiles/ServiceRequest/SpillTab.fxml"));//These files need to be in the resource folder!
        StagesClass.serviceScene = new Scene(ServiceRequestScreen, windowWidth, windowLength);
        StagesClass.serviceScene.getStylesheets().add(cssPath);
        StagesClass.serviceStage.initModality(Modality.APPLICATION_MODAL);

        StagesClass.mainStage.setX(xcoord);

        StagesClass.mainStage.setY(ycoord);


        StagesClass.mainStage.setScene(StagesClass.serviceScene);
        StagesClass.mainStage.show();
    }
}
