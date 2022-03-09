package billydrivingschoolfmis;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.interfaces.IAccessibleElement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;

import javax.swing.filechooser.FileSystemView;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import static billydrivingschoolfmis.AlertClass.makeAlert;
import static billydrivingschoolfmis.NotificationClass.showNotification;

public class FeesHistoryController implements Initializable {

    @FXML
    private TableView<Receipt> receiptTable;

    @FXML
    private Label typeLbl;

    @FXML
    private Label totalFeesLbl;

    @FXML
    private Label totalPaidLbl;

    @FXML
    private Label balanceLbl;

    @FXML
    private Label nameLbl;

    @FXML
    private Label courseLbl;

    @FXML
    private TextField filterTxt;

    String studentID;

    ResultSet rs;
    ObservableList<Double> feesResults = FXCollections.observableArrayList();
    private ObservableList<Receipt> data = FXCollections.observableArrayList();
    private FilteredList<Receipt> filteredData = new FilteredList<>(data, e->true);
    String studentName;
    String courseType;
    double balance;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setReceiptTable();
    }

    private void loadData() {
        data.clear();

        try {
            rs =  ReceiptQueries.getReceipts(studentID);

            while (rs.next()) {
                data.add(new Receipt(
                        rs.getString("receiptNo"),
                        rs.getString("studentID"),
                        rs.getString("date"),
                        rs.getString("amount"),
                        rs.getString("modeOfPayment"),
                        rs.getString("paymentOf"),
                        rs.getString("user"),
                        rs.getString("reference")
                ));
            }
        } catch (SQLException ex) {
            Logger.getLogger(StudentsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setReceiptTable() {
            receiptTable.setItems(data);

            TableColumn col1 = new TableColumn("Receipt No.");
            TableColumn col2 = new TableColumn("Amount (MK)");
            TableColumn col3 = new TableColumn("Payment Date");
            TableColumn col4 = new TableColumn("Payment Of");
            TableColumn col5 = new TableColumn("Payment Mode");
            TableColumn col6 = new TableColumn("Reference");
            TableColumn col7 = new TableColumn("Received By");

            col1.setMinWidth(30);
            col2.setMinWidth(60);
            col3.setMinWidth(150);
            col4.setMinWidth(70);
            col5.setMinWidth(70);
            col6.setMinWidth(70);
            col7.setMinWidth(50);

            receiptTable.getColumns().addAll(col1, col2, col3, col4, col5, col6, col7);

            col1.setCellValueFactory(new PropertyValueFactory<>("id"));
            col2.setCellValueFactory(new PropertyValueFactory<>("amount"));
            col3.setCellValueFactory(new PropertyValueFactory<>("date"));
            col4.setCellValueFactory(new PropertyValueFactory<>("bpo"));
            col5.setCellValueFactory(new PropertyValueFactory<>("mop"));
            col6.setCellValueFactory(new PropertyValueFactory<>("ref"));
            col7.setCellValueFactory(new PropertyValueFactory<>("user"));

    }

    public void setStudentID(String text) {
        getStudentDetails(text);

        studentID = text;

        loadData();
    }

    private void getStudentDetails(String studentID) {
        try {
            rs = StudentQueries.getStudent(studentID);
            while (rs.next()){
                studentName = rs.getString("fullname");
                nameLbl.setText(rs.getString("studentID") + "-> " + rs.getString("fullname"));
                courseLbl.setText(rs.getString("course"));
                courseType = rs.getString("duration");

                feesResults = new FeesCalculationsClass().FeesCalculations(studentID,rs.getString("course"), rs.getString("duration") );
                courseLbl.setText(rs.getString("course"));
                typeLbl.setText(rs.getString("duration"));
                totalFeesLbl.setText("K"+ feesResults.get(2));
                totalPaidLbl.setText("K"+ feesResults.get(0));
                balanceLbl.setText("K"+ feesResults.get(1));
                balance = feesResults.get(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @FXML
    void filterTxtOnKeyReleased(KeyEvent event) {
        filterTxt.textProperty().addListener((observableValue, oldValue, newValue) -> {
            filteredData.setPredicate((Predicate<? super Receipt>) receipt -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();
                if (receipt.getId().contains(newValue)) {
                    return true;
                } else if (receipt.getMop().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }else if (receipt.getRef().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }else if (receipt.getUser().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }else if (receipt.getDate().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }else if (receipt.getBpo().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }else if (receipt.getAmount().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }

                return false;

            });
        });
        SortedList<Receipt> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(receiptTable.comparatorProperty());
        receiptTable.setItems(sortedData);
    }

    @FXML
    void deleteAction(ActionEvent event) {
        Receipt receipt = receiptTable.getSelectionModel().getSelectedItem();

        if(receipt != null){
            int deleted = ReceiptQueries.deleteReceipt(receipt.getId());

            if (deleted != 404) {
                loadData();
                showNotification("Payment Deleted Successfully");
                getStudentDetails(studentID);
            }

        }else{
            makeAlert("error", "No student Selected");
        }
    }

    @FXML
    void printAction(ActionEvent event) {
        if(data.size() > 0){
            try {
                Document doc = new Document();
                String fileName = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() +
                        "\\Billy FMIS\\"+ studentName +" Fees Report" + System.currentTimeMillis() + ".pdf";

                PdfWriter.getInstance(doc, new FileOutputStream(fileName));
                doc.open();
                com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance("src/img/driving_32.png");
                doc.add(image);
                doc.add(new Paragraph("Billy Driving School - Fees Payment Report",
                        FontFactory.getFont(FontFactory.TIMES_BOLD, 20, Font.BOLD, BaseColor.DARK_GRAY))
                );
                doc.add(new Paragraph("Printed by "
                        + LoginDocumentController.firstname + " " + LoginDocumentController.lastname +
                        " on "+ new Date().toString()
                ));
                doc.add(Chunk.NEWLINE);
                doc.add(new LineSeparator());
                doc.add(Chunk.NEWLINE);

                PdfPTable table = new PdfPTable(2);
                PdfPCell titleCell = new PdfPCell(new Paragraph("Student Details"));
                titleCell.setColspan(4);
                titleCell.setBorder(Rectangle.NO_BORDER);
                titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                titleCell.setBackgroundColor(BaseColor.LIGHT_GRAY);

                table.addCell(titleCell);
                table.addCell("Student ID:");
                table.addCell(studentID);
                table.addCell("Student Name:");
                table.addCell(studentName);
                table.addCell("Course:");
                table.addCell(courseLbl.getText());
                table.addCell("Course Type:");
                table.addCell(courseType);

                PdfPTable table0 = new PdfPTable(2);
                PdfPCell titleCell0 = new PdfPCell(new Paragraph("Fees Summary"));
                titleCell0.setColspan(4);
                titleCell0.setBorder(Rectangle.NO_BORDER);
                titleCell0.setHorizontalAlignment(Element.ALIGN_CENTER);
                titleCell0.setBackgroundColor(BaseColor.LIGHT_GRAY);

                table0.addCell(titleCell0);
                table0.addCell("Total Fees for this course:");
                table0.addCell(totalFeesLbl.getText());
                table0.addCell("Total Paid:");
                table0.addCell(totalPaidLbl.getText());
                table0.addCell("Your Balance:");
                table0.addCell(balanceLbl.getText());
                table0.addCell("Remarks:");
                if(balance > 0){
                    table0.addCell("Please settle the balance");
                }else{
                    table0.addCell("Thank you for completing your fees");
                }

                PdfPTable table1 = new PdfPTable(6);
                PdfPCell titleCell1 = new PdfPCell(new Paragraph("Payment History"));
                titleCell1.setColspan(12);
                titleCell1.setBorder(Rectangle.NO_BORDER);
                titleCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                titleCell1.setBackgroundColor(BaseColor.LIGHT_GRAY);


                table1.addCell(titleCell1);
                table1.addCell("Receipt No");
                table1.addCell("Date of Payment");
                table1.addCell("Fees for");
                table1.addCell("Mode of Payment");
                table1.addCell("Reference");
                table1.addCell("Amount (MK)");

                for(Receipt r : data){
                    table1.addCell(r.getId());
                    table1.addCell(r.getDate());
                    table1.addCell(r.getBpo());
                    table1.addCell(r.getMop());
                    table1.addCell(r.getRef());
                    table1.addCell(r.getAmount());
                }

                doc.add(table);
                doc.add(Chunk.NEWLINE);
                doc.add(table0);
                doc.add(Chunk.NEWLINE);
                doc.add(table1);
                doc.add(Chunk.NEWLINE);

                doc.add(new Paragraph("Billy Driving School Financial Management " +
                        "Information System @ " + LocalDate.now().getYear(),
                        FontFactory.getFont(FontFactory.TIMES_ITALIC, 12, Font.NORMAL, BaseColor.DARK_GRAY))
                );

                doc.add(new Paragraph("*Anoymass Programs",
                        FontFactory.getFont(FontFactory.TIMES_ITALIC, 9, Font.NORMAL, BaseColor.DARK_GRAY))
                );

                doc.close();

                makeAlert("information","Report Saved Successfully !\n\n" +
                        "Location of the report: *"+fileName+"*");
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            makeAlert("warning" , "Nothing to report");
        }

    }

    @FXML
    void tableOnMouseClickedAction(MouseEvent event) {
        Receipt receipt = receiptTable.getSelectionModel().getSelectedItem();

        if(receipt != null){

        }
    }

    @FXML
    void recordPaymentAction(ActionEvent event) {

        ((Node)event.getSource()).getScene().getWindow().hide();
        try {
            FXMLLoader loader = new FXMLLoader();
            AnchorPane root = loader.load(getClass().getResource("ReceiptForm.fxml").openStream());
            ReceiptFormController controller = (ReceiptFormController) loader.getController();
            controller.setStudentID(studentID);

            Scene scene = new Scene(root);
            StageManager.receiptStage.setScene(scene);
            StageManager.receiptStage.getIcons().add(new Image("file:src/img/receipt_16.png"));
            StageManager.receiptStage.setTitle("Record Fees Receipt for " + nameLbl.getText());

            StageManager.receiptStage.show();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

}
