/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billydrivingschoolfmis;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author ANOYMASS
 */
public class CreateAccountDocumentController implements Initializable, ControlledScreen {
    private ScreensController controller;
    private int count;
    @FXML
    private AnchorPane anchorpane;
    @FXML
    private ImageView imageview;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
         slideShow();
        
        anchorpane.getStylesheets().add(getClass().getResource("loginCss.css").toExternalForm());
    }    

    @FXML
    private void goToLoginPage(ActionEvent event) {
        controller.setScreen(BillyDrivingSchoolFMIS.loginScreenID);
    }

    @Override
    public void setScreenParent1(ScreensController screen) {
        controller = screen;
    }
    
     public void slideShow(){
        ArrayList<Image> images = new ArrayList<Image>();
        images.add(new Image("img/logo.JPG"));
        images.add(new Image("img/d1.jpeg"));
        images.add(new Image("img/d2.jpeg"));
        images.add(new Image("img/d3.jpeg"));
        images.add(new Image("img/d4.jpeg"));
        images.add(new Image("img/d5.jpeg"));
        images.add(new Image("img/d6.jpeg"));
        images.add(new Image("img/d7.jpeg"));
        images.add(new Image("img/d8.jpeg"));
        
        final DoubleProperty op = imageview.opacityProperty();
        
        
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5.0), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                imageview.setImage(images.get(count));
                count++;
                
                if(count == 9){
                    count = 0;
                }
            }
        }, new KeyValue(op, 0.0)));
        
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        
    }

    @FXML
    private void quit(ActionEvent event) {
        System.exit(0);
    }
    
    
}
