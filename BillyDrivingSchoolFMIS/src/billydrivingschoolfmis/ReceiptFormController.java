package billydrivingschoolfmis;

import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static billydrivingschoolfmis.AlertClass.makeAlert;
import static billydrivingschoolfmis.NotificationClass.showNotification;
import static billydrivingschoolfmis.RandomNumbers.randomNumber;


public class ReceiptFormController implements Initializable {

    @FXML
    AnchorPane downPane;

    @FXML
    private JFXButton historyBtn;

    @FXML
    private Label courseLbl;

    @FXML
    private Label typeLbl;

    @FXML
    private Label totalLbl;

    @FXML
    private Label paidLbl;

    @FXML
    private Label balanceLbl;

    @FXML
    private Label quaLbl;

    @FXML
    private Label idLbl;

    @FXML
    private Label phoneLbl;

    @FXML
    private Label emailLbl;

    @FXML
    private Label dateLbl;

    @FXML
    private Label fullNameLbl;

    @FXML
    private JFXButton saveBtn;

    @FXML
    private TextField amountTxt;

    @FXML
    private TextField bpoTxt;

    @FXML
    private ComboBox<String> pmCombo;

    @FXML
    private TextField receiptNoTxt;

    @FXML
    private TextField referenceNoTxt;

    private String studentID;
    private ResultSet rs;
    private ObservableList<String> pm = FXCollections.observableArrayList();
    private ObservableList<Double> feesResults = FXCollections.observableArrayList();
    double totalFees = 0.0;
    double balance = 0.0;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //getStudentDetails(studentID);
        setPmCombo();
        setReferenceTxt();

    }

    private void setReferenceTxt() {
        referenceNoTxt.setVisible(false);
    }

    private void setPmCombo() {
        pm.clear();

        pm.add("Cash");
        pm.add("Cheque");
        pm.add("Airtel Money");
        pm.add("TNM Mpamba");
        pm.add("Mo626");

        pmCombo.setItems(pm);
    }

    private void getStudentDetails(String studentID) {
        try {
            rs = StudentQueries.getStudent(studentID);
            while (rs.next()){
                idLbl.setText(rs.getString("studentID"));
                phoneLbl.setText(rs.getString("phone"));
                quaLbl.setText(rs.getString("qualification"));
                emailLbl.setText(rs.getString("email"));
                dateLbl.setText(rs.getString("date"));
                courseLbl.setText(rs.getString("course"));
                typeLbl.setText(rs.getString("duration"));
                fullNameLbl.setText(rs.getString("fullname"));

                feesResults = new FeesCalculationsClass().FeesCalculations(studentID,rs.getString("course"), rs.getString("duration") );
                totalLbl.setText("(MK)" + feesResults.get(2));
                setStudentFeesHistory();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void setStudentFeesHistory(){
        double totalPaid = feesResults.get(0);

        if(totalPaid > 0){
            historyBtn.setVisible(true);
        }else historyBtn.setVisible(false);

        balance = feesResults.get(1);

        if(balance == 0.0){
            downPane.setVisible(false);
        }else{
            downPane.setVisible(true);
        }

        paidLbl.setText("(MK)" + totalPaid);
        balanceLbl.setText("(MK)" + balance);
    }

    public void setStudentID(String text) {
        getStudentDetails(text);
        studentID = text;
    }

    @FXML
    void generateReceiptNo(ActionEvent event) {
        int randomID = randomNumber();

        while (!checkReceipt(Integer.toString(randomID))) {
            randomID = randomNumber();
        }

        receiptNoTxt.setText(Integer.toString(randomID));
    }



    @FXML
    void saveAction(ActionEvent event) {
        Receipt receipt = new Receipt(receiptNoTxt.getText(), idLbl.getText(), "date", amountTxt.getText(),
                pmCombo.getSelectionModel().getSelectedItem(), bpoTxt.getText(),
                LoginDocumentController.userName, referenceNoTxt.getText());

        if (validateFields(receipt)) {
            if (checkReceipt(receipt.getId())) {
                boolean added = ReceiptQueries.addReceipt(receipt);

                if (added == false) {
                    showNotification("Receipt Has been added Succesfully");
                    clearFields();
                    getStudentDetails(studentID);
                }
            } else {
                makeAlert("warning", "The Receipt you entered Exist");
            }
        }
    }

    private void clearFields() {
        receiptNoTxt.setText("");
        amountTxt.setText("");
        bpoTxt.setText("");
        referenceNoTxt.setText("");
        pmCombo.getSelectionModel().select(null);
    }

    private boolean validateFields(Receipt receipt) {
        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

        if (!receipt.getId().isEmpty() & !receipt.getAmount().isEmpty() & pmCombo.getSelectionModel().getSelectedItem() != null  ) {

            if (pattern.matcher(receipt.getAmount()).matches() ) {
                if(Double.parseDouble(receipt.getAmount()) > 0){
                    if(Double.parseDouble(receipt.getAmount()) <= balance){
                        return true;
                    }else {
                        makeAlert("warning", "The amount you entered (K" + amountTxt.getText() +
                                ") is greater than balance (K"+ balance +")\n" +
                                "Please enter amount less or equal to the balance");
                    }
                }else{
                    makeAlert("warning", "Amount should not be less than zero");
                }

            } else {
                makeAlert("warning", "Amount should be numeric and not less than zero");
            }
        } else {
            makeAlert("warning", "Please complete the fields");
        }

        return false;
    }

    private boolean checkReceipt(String id) {
        try {
            rs = ReceiptQueries.getReceipt(id);
            while (rs.next()) {
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(CoursesController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    @FXML
    void mpmOnAction(ActionEvent event) {
        String selectedItem = pmCombo.getSelectionModel().getSelectedItem();
        
        if(selectedItem != "Cash" & selectedItem != null){
            referenceNoTxt.setVisible(true);
        }else{
            referenceNoTxt.setVisible(false);
        }
    }

    @FXML
    void paymentHistoryAction(ActionEvent event) {

        ((Node)event.getSource()).getScene().getWindow().hide();

        try {
            FXMLLoader loader = new FXMLLoader();
            AnchorPane root = loader.load(getClass().getResource("FeesHistory.fxml").openStream());
            FeesHistoryController controller = (FeesHistoryController) loader.getController();
            controller.setStudentID(studentID);

            Scene scene = new Scene(root);
            StageManager.historyStage.setScene(scene);
            StageManager.historyStage.getIcons().add(new Image("file:src/img/history_16.png"));
            StageManager.historyStage.setTitle("Fees Payment History For " + fullNameLbl.getText());

            StageManager.historyStage.show();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

}
