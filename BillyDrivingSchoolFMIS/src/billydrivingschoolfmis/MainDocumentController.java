/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billydrivingschoolfmis;

import com.jfoenix.controls.JFXButton;

import java.io.*;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import static billydrivingschoolfmis.AlertClass.makeAlert;

/**
 * FXML Controller class
 *
 * @author ANOYMASS
 */
public class MainDocumentController implements Initializable {

    @FXML
    private Label userFullNameLbl;

    @FXML
    private Label userTypeLbl;

    @FXML
    private Label nameLbl;

    private Window loginWindow;

    String username;
    @FXML
    private BorderPane borderPane;
    @FXML
    private AnchorPane anchorPane;
    private Window alertWindow;
    @FXML
    private JFXButton loginOutBtn;
    @FXML
    private ImageView logoutImg;

    @FXML
    private Circle imageCircle;

    private final Image defaultImg = new Image("file:src/img/defaultImg3.png");
    private ResultSet rs;
    private Image image;
    private File prevImgFile;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

        setInitialAppScreenSize();
        
        setApplicationCloseConfirmation();

        setLabels();

        logoutImg.setImage(new Image("file:src/img/back_32.png"));

        loadHome();
        
    }

    private void setLabels() {
        userFullNameLbl.setText(LoginDocumentController.firstname + " " + LoginDocumentController.lastname);
        userTypeLbl.setText(LoginDocumentController.userType);

        try {
            rs = UserQueries.getUser(LoginDocumentController.userName);
            while (rs.next()) {

                InputStream is = rs.getBinaryStream("image");

                prevImgFile = new File("photo.jpg");
                OutputStream os = new FileOutputStream(prevImgFile);
                byte[] content = new byte[1024];
                int size = 0;
                while ((size = is.read(content)) != -1) {
                    os.write(content, 0, size);

                }

                os.close();
                is.close();

                image = new Image("file:photo.jpg", 150, 200, true, true);

                imageCircle.setFill(new ImagePattern(image));
                imageCircle.setEffect(new DropShadow(+25d, 0d, +2d, Color.DARKSEAGREEN));

            }


        } catch (SQLException ex) {
            Logger.getLogger(CoursesController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }catch (NullPointerException ex){
            seDefaulttImg();
        }
    }

    private void seDefaulttImg() {
        imageCircle.setFill(new ImagePattern(defaultImg));
        imageCircle.setEffect(new DropShadow(+25d, 0d  , +2d , Color.DARKSEAGREEN));
    }

    public void setNameLbl(String name) {
        //nameLbl.setText("Welcome " + name);
        username = name;
    }

    @FXML
    private void openChildWindow(ActionEvent event) {
        StageManager.announcementStage.show();
    }

    @FXML
    private void goToLoginPage(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Please Confirm Login out");
        //alert.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.REMOVE, "20px"));
        alert.setHeaderText(null);
        alert.setContentText("Are you sure; You want to log out ?");
        Optional<ButtonType> action = alert.showAndWait();

        if (action.get() == ButtonType.OK) {
            Window mainWindow = ((Node) event.getSource()).getScene().getWindow();
            mainWindow.hide();

            Stage stage = (Stage) loginWindow;
            stage.show();
        }


        
    }

    public void setLoginWindow(Window loginWindow) {
        this.loginWindow = loginWindow;
    }

    private void loadHome(){
        FxmlLoader loader = new FxmlLoader();
        Pane view = loader.getDocument("Home");
        borderPane.setCenter(view);
    }

    @FXML
    private void gotoReceipts(ActionEvent event) {
        FxmlLoader loader = new FxmlLoader();
        Pane view = loader.getDocument("Receipts");
        borderPane.setCenter(view);
    }

    @FXML
    private void goToBalances(ActionEvent event) {
        FxmlLoader loader = new FxmlLoader();
        Pane view = loader.getDocument("Balances");
        borderPane.setCenter(view);
    }   

    private void setInitialAppScreenSize() {
        javafx.geometry.Rectangle2D bounds = Screen.getPrimary().getVisualBounds();

        anchorPane.setMinHeight(bounds.getHeight() - 100);
        anchorPane.setMinWidth(bounds.getWidth() - 60);
    }

    private void setApplicationCloseConfirmation() {
        StageManager.ParentStage.getIcons().add(new Image("file:src/img/driving_16.png"));
        ///confirm closing the App
        StageManager.ParentStage.setOnCloseRequest(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    username + "! Are you sure to exit the FMIS");

            Button exitButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
            exitButton.setText("Exit");

            alert.setTitle("Application Close Confirmation");
            alert.setHeaderText("Confirm Exit");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.initOwner(StageManager.ParentStage);
            //alert.setX(StageManager.ParentStage.getX());
            //alert.setY(StageManager.ParentStage.getY() + StageManager.ParentStage.getHeight());

            //setting an icon to alert Dialog- first thing, get he window of the alert dialog
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image("file:src/img/warning_32.png"));

            Optional<ButtonType> closeOptional = alert.showAndWait();
            if (!ButtonType.OK.equals(closeOptional.get())) {
                event.consume();
            }

        });
    }

    @FXML
    private void goToCourses(ActionEvent event) {
        FxmlLoader loader = new FxmlLoader();
        Pane view = loader.getDocument("Courses");
        borderPane.setCenter(view);
    }

    @FXML
    private void goToStudents(ActionEvent event) {
        FxmlLoader loader = new FxmlLoader();
        Pane view = loader.getDocument("Students");
        borderPane.setCenter(view);
    }

    @FXML
    private void goToPayments(ActionEvent event) {
        FxmlLoader loader = new FxmlLoader();
        Pane view = loader.getDocument("Payments");
        borderPane.setCenter(view);
    }

    @FXML
    private void goToReport(ActionEvent event) {
        FxmlLoader loader = new FxmlLoader();
        Pane view = loader.getDocument("FinancialReport");
        borderPane.setCenter(view);
    }

    @FXML
    private void goToHome(ActionEvent event) {
        loadHome();
    }

    public void goToUsers(ActionEvent actionEvent) {
        if(LoginDocumentController.userType.equals("admin")){
            FxmlLoader loader = new FxmlLoader();
            Pane view = loader.getDocument("Users");
            borderPane.setCenter(view);
        }else{
            makeAlert("error", "Access denied\nOnly Admin user is granted access to this module");
        }
    }


}
