package billydrivingschoolfmis;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static billydrivingschoolfmis.AlertClass.makeAlert;
import static billydrivingschoolfmis.NotificationClass.showNotification;

public class UsersController implements Initializable {

    @FXML
    private Label userLbl;

    @FXML
    private AnchorPane ancho;

    @FXML
    private Label excelFileTxt;

    @FXML
    private JFXTextField fnameTxt;

    @FXML
    private JFXTextField lnameTxt;

    @FXML
    private JFXTextField usernameTxt;

    @FXML
    private JFXTextField phoneTxt;

    @FXML
    private JFXTextField emailTxt;

    @FXML
    private JFXComboBox<String> userTypeCombo;

    @FXML
    private JFXPasswordField passwordTxt;

    @FXML
    private JFXPasswordField passwdTxt;

    @FXML
    private Label imageLbl;

    @FXML
    private Circle imageCircle;

    @FXML
    private JFXButton saveBtn;

    @FXML
    private JFXButton updateBtn;

    @FXML
    private JFXButton deleteBtn;

    @FXML
    private TextField filtersTxt;

    @FXML
    private TableView<User> userTable;

    @FXML
    private MenuItem refreshBtn;

    private FileInputStream fis;
    private FileChooser fileChooser;
    private File file;
    private Image image;
    private File prevImgFile;
    private final Image defaultImg = new Image("file:src/img/defaultImg3.png");
    private ObservableList<User> data = FXCollections.observableArrayList();
    private ObservableList<String> types = FXCollections.observableArrayList();
    private ResultSet rs;


    @FXML
    void browseFileAction(MouseEvent event) {
        browserImage();
    }

    @FXML
    void deleteAction(ActionEvent event) {

        User user = userTable.getSelectionModel().getSelectedItem();
        if(user != null){

            if(LoginDocumentController.userType.equals(user.getUsertype())){
                makeAlert("warning" , "You cannot delete this user!\nThis User is an administrator");
            }else{
                int response = UserQueries.deleteUser(user.getUsername());

                if (response != 404) {
                    loadData();
                    clearTextFields();
                    setLabels();
                    showNotification("User Deleted Successfully");
                }
            }

        }else{
            makeAlert("warning" , "Please Select a user from the table");
        }
    }

    @FXML
    void excelFileAction(MouseEvent event) {

    }

    @FXML
    void filterAction(KeyEvent event) {

    }

    @FXML
    void refreshAction(ActionEvent event) {
        loadData();
        clearTextFields();
    }

    @FXML
    void saveAction(ActionEvent event) {
        User user = new User(
                fnameTxt.getText(), lnameTxt.getText(), usernameTxt.getText(),
                phoneTxt.getText(), emailTxt.getText(), passwordTxt.getText(),
                userTypeCombo.getSelectionModel().getSelectedItem(), ""
        );


        if (validateUser(user)) {
            if (checkUser(user.getUsername())) {
                boolean added = UserQueries.addUser(user, file);

                if (added == false) {
                    showNotification(user.getFirstName() + " Has been added Succesfully");

                    clearTextFields();
                    loadData();
                    setLabels();
                }
            } else {
                makeAlert("warning", "The User you entered Exist");
            }
        }

    }

    private void setLabels() {
        userLbl.setText("Users -> " + UserQueries.countUsers());
        imageLbl.setText("Click Here to Attach Image");
    }

    private void clearTextFields() {
        prevImgFile = null;
        file = null;
        usernameTxt.setText("");
        fnameTxt.setText("");
        lnameTxt.setText("");
        phoneTxt.setText("");
        emailTxt.setText("");
        userTypeCombo.getSelectionModel().select(null);
        passwordTxt.setText("");
        passwdTxt.setText("");
        setDefaultImage();
    }

    private boolean checkUser(String username) {
        try {
            rs = UserQueries.getUser(username);
            while (rs.next()) {
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(CoursesController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    private boolean validateUser(User user) {
        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
        Pattern emailP = Pattern.compile("[a-zA-Z0-9][a-zA-Z0-9._]*@[a-zA-Z0-9]+([.][a-zA-Z]+)+");

        if (!user.getUsername().isEmpty() & !user.getFirstName().isEmpty() & !user.getLastName().isEmpty()
                & !user.getPhone().isEmpty() & !user.getEmail().isEmpty()
                & user.getUsertype() != null & !user.getPassword().isEmpty()) {

            if (pattern.matcher(user.getPhone()).matches()) {
                if (emailP.matcher(user.getEmail()).matches()) {
                    if(user.getPassword().equals(passwdTxt.getText())){
                        return true;
                    }else{
                        makeAlert("warning", "Passwords dont match");
                    }
                } else {
                    makeAlert("warning", "Invalid Email Address");
                }

            } else {
                makeAlert("warning", "Phone number should be numeric");
            }
        } else {
            makeAlert("warning", "Please complete the fields");
        }

        return false;
    }

    private void autoFillFields(){
        User user = userTable.getSelectionModel().getSelectedItem();
        prevImgFile = null;

        if(user != null){
            try {
                rs = UserQueries.getUser(user.getUsername());
                while (rs.next()) {
                    usernameTxt.setText(rs.getString("username"));
                    fnameTxt.setText(rs.getString("firstname"));
                    lnameTxt.setText(rs.getString("lastname"));
                    phoneTxt.setText(rs.getString("phone"));
                    emailTxt.setText(rs.getString("email"));
                    userTypeCombo.getSelectionModel().select(rs.getString("type"));
                    passwordTxt.setText(rs.getString("password"));
                    passwdTxt.setText(rs.getString("password"));

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
                prevImgFile = null;
                setDefaultImage();
            }
        }
    }

    @FXML
    void studentTableOnMouseClickedAction(MouseEvent event) {
        autoFillFields();
    }

    @FXML
    void studentsTableOnKeyReleasedAction(KeyEvent event) {
        if (event.getCode() == KeyCode.UP | event.getCode() == KeyCode.DOWN) {
            autoFillFields();
        }
    }

    @FXML
    void updateAction(ActionEvent event) {
        User user = new User(
                fnameTxt.getText(), lnameTxt.getText(), usernameTxt.getText(),
                phoneTxt.getText(), emailTxt.getText(), passwordTxt.getText(),
                userTypeCombo.getSelectionModel().getSelectedItem(), ""
        );


        if(LoginDocumentController.userType.equals(user.getUsertype())){
            makeAlert("warning" , "You cannot edit this user!\nThis User is an administrator");
        }else{
            if (validateUser(user) & !checkUser(user.getUsername())) {

                if(file == null){
                    file = prevImgFile;
                }

                boolean edited = UserQueries.editUser(user, file);

                if (edited == false) {
                    showNotification(user.getFirstName() + " Has been Edited Succesfully");
                    loadData();
                    clearTextFields();
                    setLabels();
                }
            } else {
                makeAlert("error", "User not Found");
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ancho.getStylesheets().add(getClass().getResource("students.css").toExternalForm());
        setUserTypes();
        setDefaultImage();
        loadData();
        setUserTable();
        setLabels();

    }

    private void setDefaultImage() {
        imageCircle.setFill(new ImagePattern(defaultImg));
        imageCircle.setEffect(new DropShadow(+25d, 0d  , +2d , Color.DARKSEAGREEN));
    }

    private void setUserTypes() {
        types.clear();
        types.addAll("Director" , "Secretary");
        userTypeCombo.setItems(types);
    }

    private void setUserTable() {
        userTable.setItems(data);

        TableColumn col1 = new TableColumn("Username");
        TableColumn col2 = new TableColumn("First Name");
        TableColumn col3 = new TableColumn("Last Name");
        TableColumn col4 = new TableColumn("Phone");
        TableColumn col5 = new TableColumn("Email");
        TableColumn col6 = new TableColumn("User Type");
        TableColumn col7 = new TableColumn("Password");
        TableColumn col8 = new TableColumn("Date Added");

        col1.setMinWidth(70);
        col2.setMinWidth(70);
        col3.setMinWidth(70);
        col4.setMinWidth(80);
        col5.setMinWidth(150);
        col6.setMinWidth(70);
        col7.setMinWidth(70);
        col8.setMinWidth(150);


        userTable.getColumns().addAll(col1, col2, col3, col4, col5 , col6, col7, col8);
        userTable.setEditable(true);

        col1.setCellValueFactory(new PropertyValueFactory<>("username"));
        col2.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        col3.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        col4.setCellValueFactory(new PropertyValueFactory<>("phone"));
        col5.setCellValueFactory(new PropertyValueFactory<>("email"));
        col6.setCellValueFactory(new PropertyValueFactory<>("usertype"));
        col7.setCellValueFactory(new PropertyValueFactory<>("password"));
        col8.setCellValueFactory(new PropertyValueFactory<>("date"));
    }

    private void loadData() {
        data.clear();
        
        rs = UserQueries.getUsers();
        
        try{
            while(rs.next()){
                data.add(new User(
                        rs.getString("firstname"),
                        rs.getString("lastname"),
                        rs.getString("username"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("type"),
                        rs.getString("date")
                ));
            }
        }catch(SQLException ex){
            
        }
        
    }

    private void browserImage(){
        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif")
        );

        file = fileChooser.showOpenDialog(StageManager.browsingStage);

        if (file != null) {
            imageLbl.setText(file.getAbsolutePath());
            image = new Image(file.toURI().toString(), 170,
                    200, true, true);
            imageCircle.setFill(new ImagePattern(image));
            imageCircle.setEffect(new DropShadow(+25d, 0d, +2d, Color.DARKSEAGREEN));
        }

    }

}

