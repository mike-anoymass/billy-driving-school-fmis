/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billydrivingschoolfmis;

import java.io.IOException;

import java.util.HashMap;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

/**
 *
 * @author ANOYMASS
 */
public class ScreensController extends StackPane {

    //hold screens to be displayed
    private HashMap<String, Node> screens = new HashMap<>();

    public ScreensController() {
        super();
    }

    public void addScreen(String name, Node screen) {
        screens.put(name, screen);
    }

    //return screen node with appropriate name
    public Node getScreen(String name) {
        return screens.get(name);
    }

    //load fxml file add screen to the screens collection
    //injects screenpane to the controller
    public boolean loadScreen(String name, String resource) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
            Parent loadScreen = (Parent) loader.load();
            ControlledScreen controlledScreenController = ((ControlledScreen) loader.getController());
            controlledScreenController.setScreenParent1(this);
            addScreen(name, loadScreen);
            
            return true;

        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            return false;
        }

    }

    /*displays screen with a predefined name
      makes sure the screen has been loaded
      if there is more than one screen the new screen will be added second
      current screen is removed 
      if there isnt any screen being displayed, then new screen is just added to the parent/root
     */
    public boolean setScreen(final String name) {
        if (screens.get(name) != null) {
            //load screen 
            final DoubleProperty opacity = opacityProperty();
            
            if (!getChildren().isEmpty()) {
                //if there is more than one screen 
                Timeline fade = new Timeline(
                        new KeyFrame(javafx.util.Duration.ZERO, new KeyValue(opacity, 1.0)),
                        new KeyFrame(new javafx.util.Duration(1000), new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                getChildren().remove(0);
                                getChildren().add(0, screens.get(name));
                                Timeline fadeIn = new Timeline(
                                        new KeyFrame(javafx.util.Duration.ZERO, new KeyValue(opacity, 0.0)),
                                        new KeyFrame(new javafx.util.Duration(800), new KeyValue(opacity, 1.0)));
                                fadeIn.play();
                            }
                        }, new KeyValue(opacity, 0.0)));
                fade.play();
            } else {
                setOpacity(0.0);
                getChildren().add(screens.get(name));
                Timeline fadeIn = new Timeline(
                        new KeyFrame(javafx.util.Duration.ZERO, new KeyValue(opacity, 0.0)),
                        new KeyFrame(new javafx.util.Duration(2500), new KeyValue(opacity, 1.0)));
                fadeIn.play();
            }
            return true;
        } else {
            System.err.println("Screen hasnt been loaded");
            return false;
        }
    }

    public boolean removeScreen(String name) {
        if (screens.remove(name) == null) {
            System.err.println("Screen didnt exist");
            return false;
        } else {
            return true;
        }
    }

}
