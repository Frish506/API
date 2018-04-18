import ServiceRequestGUI.SpillTabController;
import Stages.StagesClass;
import javafx.application.Application;
import PhoneFeature.Server;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application{

    public static void main(String[] args) {

        Server s = new Server();
        s.runServer();

        launch(args);//launches JavaFX
    }

    @Override
    public void start(Stage stage) throws Exception{//This is run inside of launch to start the UI


        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        StagesClass.mainStage = stage;
        ///////////////////Set up Scenes Here////////////

        Parent ServiceRequestScreen = FXMLLoader.load(getClass().getResource("FXMLFiles/ServiceRequest/SpillTab.fxml"));//These files need to be in the resource folder!
        StagesClass.serviceScene = new Scene(ServiceRequestScreen, SpillRequest.windowWidth, SpillRequest.windowLength);
        StagesClass.serviceScene.getStylesheets().add(SpillRequest.cssPath);
        StagesClass.serviceStage.initModality(Modality.APPLICATION_MODAL);




        stage.setScene(StagesClass.serviceScene);
        stage.show();
    }
}
// Mark was here

