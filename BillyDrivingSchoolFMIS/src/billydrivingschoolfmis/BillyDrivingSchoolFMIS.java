/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billydrivingschoolfmis;

import java.io.File;
import java.sql.Connection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author ANOYMASS
 */
public class BillyDrivingSchoolFMIS extends Application {

    public static String loginScreenID = "login";
    public static String loginScreenFile = "LoginDocument.fxml";
    public static String createAccountScreenID = "account";
    public static String createAccountScreenFile = "CreateAccountDocument.fxml";
    public static final Connection conn = SqlConnection.DbConnector();
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        createDirectory();
        StageManager.setStages();
        ScreensController mainController = new ScreensController();
        mainController.loadScreen(BillyDrivingSchoolFMIS.loginScreenID, BillyDrivingSchoolFMIS.loginScreenFile);
        mainController.loadScreen(BillyDrivingSchoolFMIS.createAccountScreenID, BillyDrivingSchoolFMIS.createAccountScreenFile);

        mainController.setScreen(BillyDrivingSchoolFMIS.loginScreenID);

        Group root = new Group();
        root.getChildren().addAll(mainController);

        Scene scene = new Scene(root,Color.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        //primaryStage.initModality(Modality.APPLICATION_MODAL);
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image("file:src/img/driving_16.png"));
        primaryStage.setTitle("Billy Driving School FMIS");
        primaryStage.show();
    }

    private void createDirectory() {
        String documents = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
        File file = new File(documents+"\\Billy FMIS");
        boolean bool = file.mkdir();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
