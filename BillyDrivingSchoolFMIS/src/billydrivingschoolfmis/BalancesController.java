package billydrivingschoolfmis;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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

public class BalancesController implements Initializable {

    @FXML
    private AnchorPane anchor;

    @FXML
    private Label loadingTxt;

    @FXML
    private TableView<Balance> receiptsTable;

    @FXML
    private Label receiptLbl;

    @FXML
    private JFXButton printBtn;

    @FXML
    private JFXButton exportBtn;

    @FXML
    private TextField filterTxt;

    @FXML
    private Label totalRecordsLbl;

    @FXML
    private Label totalFeesLbl;

    @FXML
    private JFXButton balancesBtn;

    private ObservableList<Balance> balances = FXCollections.observableArrayList();
    private ObservableList<Balance> zeroBalances = FXCollections.observableArrayList();
    private ObservableList<Student> students = FXCollections.observableArrayList();
    private ResultSet rs;
    private FilteredList<Balance> filteredData = new FilteredList<>(balances, e->true);

    @FXML
    void refreshAction(ActionEvent event) {
        loadBalancesData();
        setLabels(balances);
    }

    @FXML
    void exportAction(ActionEvent event) {
        loadingTxt.setVisible(true);
        loadingTxt.setText("Exporting Data...");

        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        XSSFWorkbook wb = new XSSFWorkbook();
                        XSSFSheet sheet = wb.createSheet("Payment Details");
                        XSSFRow header = sheet.createRow(0);
                        header.createCell(0).setCellValue("Student ID");
                        header.createCell(1).setCellValue("Student Name");
                        header.createCell(2).setCellValue("Course");
                        header.createCell(3).setCellValue("Course Type");
                        header.createCell(4).setCellValue("Fees (MK)");
                        header.createCell(5).setCellValue("Total Paid (MK)");
                        header.createCell(6).setCellValue("Balance (MK)");


                        sheet.setColumnWidth(0, 256 * 15);
                        sheet.autoSizeColumn(1);
                        sheet.autoSizeColumn(2);
                        sheet.setColumnWidth(3, 256 * 25);

                        sheet.setZoom(150);

                        int index = 1;

                        try{

                            for(Balance balance: balances){
                                XSSFRow row = sheet.createRow(index);
                                row.createCell(0).setCellValue(balance.getId());
                                row.createCell(1).setCellValue(balance.getFullname());
                                row.createCell(2).setCellValue(balance.getCourse());
                                row.createCell(3).setCellValue(balance.getDuration());
                                row.createCell(4).setCellValue(balance.getFees());
                                row.createCell(5).setCellValue(balance.getPaid());
                                row.createCell(6).setCellValue(balance.getBalance());

                                index++;
                            }

                            String fileName = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() +
                                    "\\Billy FMIS\\Fees Balances"+ System.currentTimeMillis() + ".xlsx";

                            FileOutputStream fileOut = new FileOutputStream(fileName);
                            wb.write(fileOut);
                            fileOut.close();

                            loadingTxt.setVisible(false);

                            showNotification("Data Exported Successfully\nFile Location is  " + fileName);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                return null;
            }
        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    void filterDataOnkeyReleased(KeyEvent event) {
        filterTxt.textProperty().addListener((observableValue, oldValue, newValue) -> {
            filteredData.setPredicate((Predicate<? super Balance>) receipt -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();
                if (receipt.getId().contains(newValue)) {
                    return true;
                } else if (receipt.getFullname().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }else if (receipt.getCourse().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }else if (receipt.getFees().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }else if (receipt.getPaid().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }else if (receipt.getBalance().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;

            });
        });

        SortedList<Balance> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(receiptsTable.comparatorProperty());
        receiptsTable.setItems(sortedData);
        setLabels(sortedData);
    }

    @FXML
    void printAction(ActionEvent event) {

       ObservableList<Balance> bal = FXCollections.observableArrayList();
       bal = receiptsTable.getItems();

        if(Integer.parseInt(totalRecordsLbl.getText()) > 0){
            try {
                Document doc = new Document();
                String fileName = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() +
                        "\\Billy FMIS\\Balances" + System.currentTimeMillis() + ".pdf";

                PdfWriter.getInstance(doc, new FileOutputStream(fileName));
                doc.open();
                com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance("src/img/driving_32.png");
                doc.add(image);
                doc.add(new Paragraph("Billy Driving School - Fees Balance Report",
                        FontFactory.getFont(FontFactory.TIMES_BOLD, 20, Font.BOLD, BaseColor.DARK_GRAY))
                );
                doc.add(new Paragraph("Printed by "
                        + LoginDocumentController.firstname + " " + LoginDocumentController.lastname +
                        " on "+ new Date().toString()
                ));
                doc.add(Chunk.NEWLINE);
                doc.add(new LineSeparator());
                doc.add(Chunk.NEWLINE);

                PdfPTable table0 = new PdfPTable(2);
                PdfPCell titleCell0 = new PdfPCell(new Paragraph("Balances Summary"));
                titleCell0.setColspan(4);
                titleCell0.setBorder(Rectangle.NO_BORDER);
                titleCell0.setHorizontalAlignment(Element.ALIGN_CENTER);
                titleCell0.setBackgroundColor(BaseColor.LIGHT_GRAY);

                table0.addCell(titleCell0);
                table0.addCell("Total Records:");
                table0.addCell(totalRecordsLbl.getText());
                table0.addCell("Sum of Balances:");
                table0.addCell(totalFeesLbl.getText());

                PdfPTable table1 = new PdfPTable(7);
                PdfPCell titleCell1 = new PdfPCell(new Paragraph("Balance Details"));
                titleCell1.setColspan(14);
                titleCell1.setBorder(Rectangle.NO_BORDER);
                titleCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                titleCell1.setBackgroundColor(BaseColor.LIGHT_GRAY);


                table1.addCell(titleCell1);
                table1.addCell("Student ID");
                table1.addCell("Student Name");
                table1.addCell("Course");
                table1.addCell("Course Type");
                table1.addCell("Course Fees (MK)");
                table1.addCell("Total Paid");
                table1.addCell("Balance (MK)");

                for(Balance r : bal){
                    table1.addCell(r.getId());
                    table1.addCell(r.getFullname());
                    table1.addCell(r.getCourse());
                    table1.addCell(r.getDuration());
                    table1.addCell(r.getFees());
                    table1.addCell(r.getPaid());
                    table1.addCell(r.getBalance());
                }

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
    void switchBalancesAction(ActionEvent event) {

        if(balancesBtn.getText().equals("With zero balances")){
            setBalancesTable("zeroBalances");
            balancesBtn.setText("With Balances");
            loadBalancesData();
        }else{
            setBalancesTable("balances");
            balancesBtn.setText("With zero balances");
            loadBalancesData();
        }

    }

    @FXML
    void loadHistoryActionTableOnMouseClicked(MouseEvent event) {
        Balance blc = receiptsTable.getSelectionModel().getSelectedItem();

        if(blc != null){
            try {
                FXMLLoader loader = new FXMLLoader();
                AnchorPane root = loader.load(getClass().getResource("FeesHistory.fxml").openStream());
                FeesHistoryController controller = (FeesHistoryController) loader.getController();
                controller.setStudentID(blc.getId());

                Scene scene = new Scene(root);
                StageManager.historyStage.setScene(scene);
                StageManager.historyStage.getIcons().add(new Image("file:src/img/history_16.png"));
                StageManager.historyStage.setTitle("Fees Payment History For " + blc.getFullname());

                StageManager.historyStage.show();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        anchor.getStylesheets().add(getClass().getResource("balance.css").toExternalForm());

        loadStudentData();
        loadBalancesData();
        setBalancesTable("balances");
        setTableColumns();
        setLabels(balances);

        loadingTxt.setVisible(false);
    }

    private void setBalancesTable(String balanceType) {

        if(balanceType == "balances"){
            receiptsTable.setItems(balances);
            setLabels(balances);
        }

        if(balanceType == "zeroBalances"){
            receiptsTable.setItems(zeroBalances);
            setLabels(zeroBalances);
        }



    }

    private void setTableColumns(){
        TableColumn col1 = new TableColumn("Student ID");
        TableColumn col2 = new TableColumn("Full Name");
        TableColumn col22= new TableColumn("Course");
        TableColumn col3 = new TableColumn("Course Type");
        TableColumn col4 = new TableColumn("Fees (MK)");
        TableColumn col5 = new TableColumn("Paid (MK)");
        TableColumn col6 = new TableColumn("Balance (MK)");

        col1.setMinWidth(100);
        col2.setMinWidth(200);
        col22.setMinWidth(100);
        col3.setMinWidth(200);
        col4.setMinWidth(150);
        col5.setMinWidth(150);
        col6.setMinWidth(150);

        receiptsTable.getColumns().addAll(col1, col2, col22, col3, col4, col5, col6);

        col1.setCellValueFactory(new PropertyValueFactory<>("id"));
        col22.setCellValueFactory(new PropertyValueFactory<>("course"));
        col2.setCellValueFactory(new PropertyValueFactory<>("fullname"));
        col3.setCellValueFactory(new PropertyValueFactory<>("duration"));
        col4.setCellValueFactory(new PropertyValueFactory<>("fees"));
        col5.setCellValueFactory(new PropertyValueFactory<>("paid"));
        col6.setCellValueFactory(new PropertyValueFactory<>("balance"));
    }

    private void loadStudentData() {
        students.clear();

        try {
            rs = StudentQueries.getStudents();

            while (rs.next()) {
                students.add(new Student(
                        rs.getString("studentID"),
                        rs.getString("fullname"),
                        rs.getString("postalAddress"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("qualification"),
                        rs.getString("course"),
                        rs.getString("duration"),
                        rs.getString("date")
                ));

            }
        } catch (SQLException ex) {
            Logger.getLogger(StudentsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadBalancesData() {
        balances.clear();
        zeroBalances.clear();

        for(Student student: students){
            ObservableList feesCalcResults = new FeesCalculationsClass().FeesCalculations(
                    student.getId(),
                    student.getCourse(),
                    student.getCourseType()
            );

            if(Double.parseDouble(feesCalcResults.get(1).toString()) > 0.0){
                balances.add(new Balance(
                        student.getId(),
                        student.getName(),
                        student.getCourse(),
                        student.getCourseType(),
                        ""+feesCalcResults.get(2),
                        ""+feesCalcResults.get(0),
                        ""+feesCalcResults.get(1)
                ));
            }else{
                zeroBalances.add(new Balance(
                        student.getId(),
                        student.getName(),
                        student.getCourse(),
                        student.getCourseType(),
                        ""+feesCalcResults.get(2),
                        ""+feesCalcResults.get(0),
                        ""+feesCalcResults.get(1)
                ));
            }

        }

    }

    private void setLabels(ObservableList<Balance> data) {
        totalRecordsLbl.setText(""+data.size());

        double total = 0.0;

        if(data.size() > 0){
            for(int i = 0; i < data.size() ; i++){
                total += Double.parseDouble(data.get(i).getBalance());
            }
        }

        totalFeesLbl.setText("K"+total);
    }
}
