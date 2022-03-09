package billydrivingschoolfmis;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
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
import java.util.regex.Pattern;
import static billydrivingschoolfmis.AlertClass.makeAlert;
import static billydrivingschoolfmis.NotificationClass.showNotification;
import static billydrivingschoolfmis.RandomNumbers.randomNumber;

public class PaymentController implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Label expenseLbl;

    @FXML
    private JFXTextField filterExpenseTxt;

    @FXML
    private JFXTextField expenseNameTxt;

    @FXML
    private JFXButton saveBtn;

    @FXML
    private TableView<Expense> expenseTable;

    @FXML
    private MenuItem refreshMenuItem;

    @FXML
    private MenuItem deleteMenuItem;

    @FXML
    private Label paymentLbl;

    @FXML
    private JFXButton generateBtn;

    @FXML
    private JFXTextField idTxt;

    @FXML
    private JFXComboBox<String> expenseCombo;

    @FXML
    private JFXTextField amountTxt;

    @FXML
    private JFXComboBox<String> modeCombo;

    @FXML
    private JFXTextField refTxt;

    @FXML
    private TableView<Payment> paymentsTable;

    @FXML
    private MenuItem refreshMenuItem11;

    @FXML
    private MenuItem deleteMenuItem11;

    @FXML
    private JFXButton printBtn;

    @FXML
    private JFXButton exportBtn;

    @FXML
    private Label totalRecordsLbl;

    @FXML
    private Label totalPaymentsLbl;

    @FXML
    private JFXTextField filterPaymentTxt;

    @FXML
    private JFXButton searchBtns;

    @FXML
    private Label loadingTxt;

    private ResultSet rs;
    private final ObservableList<Expense> expenses = FXCollections.observableArrayList();
    private final ObservableList<Payment> payments = FXCollections.observableArrayList();
    private ObservableList<Payment> pdata = FXCollections.observableArrayList();
    private ObservableList<Payment> ppdata = FXCollections.observableArrayList();
    private final ObservableList<String> expensesForCombo = FXCollections.observableArrayList();
    private final ObservableList<String> pm = FXCollections.observableArrayList();
    private FilteredList<Expense> filteredExpenseData;
    private FilteredList<Payment> filteredPaymentData;

    @FXML
    void printAction(ActionEvent event) {
        if(pdata.size() > 0){
            ppdata = pdata;
        }else{
            ppdata = payments;
        }

        if(Integer.parseInt(totalRecordsLbl.getText()) > 0){
            try {
                Document doc = new Document();
                String fileName = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() +
                        "\\Billy FMIS\\Payments" + System.currentTimeMillis() + ".pdf";

                PdfWriter.getInstance(doc, new FileOutputStream(fileName));
                doc.open();
                com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance("src/img/driving_32.png");
                doc.add(image);
                doc.add(new Paragraph("Billy Driving School - Payments Report",
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
                PdfPCell titleCell0 = new PdfPCell(new Paragraph("Payments Summary"));
                titleCell0.setColspan(4);
                titleCell0.setBorder(Rectangle.NO_BORDER);
                titleCell0.setHorizontalAlignment(Element.ALIGN_CENTER);
                titleCell0.setBackgroundColor(BaseColor.LIGHT_GRAY);

                table0.addCell(titleCell0);
                table0.addCell("Total Records:");
                table0.addCell(totalRecordsLbl.getText());
                table0.addCell("Sum of Payments:");
                table0.addCell(totalPaymentsLbl.getText());

                PdfPTable table1 = new PdfPTable(6);
                PdfPCell titleCell1 = new PdfPCell(new Paragraph("Payments Details"));
                titleCell1.setColspan(12);
                titleCell1.setBorder(Rectangle.NO_BORDER);
                titleCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                titleCell1.setBackgroundColor(BaseColor.LIGHT_GRAY);


                table1.addCell(titleCell1);
                table1.addCell("Payment ID");
                table1.addCell("Payment For");
                table1.addCell("Date of Payment");
                table1.addCell("Mode of Payment");
                table1.addCell("Reference");
                table1.addCell("Amount (MK)");

                for(Payment r : ppdata){
                    table1.addCell(r.getId());
                    table1.addCell(r.getExpense());
                    table1.addCell(r.getDate());
                    table1.addCell(r.getMode());
                    table1.addCell(r.getRef());
                    table1.addCell(r.getAmount());
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
    void exportAction(ActionEvent event) {
        loadingTxt.setVisible(true);
        loadingTxt.setText("Exporting Data...");

        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        rs = PaymentQueries.getAllPayments();

                        XSSFWorkbook wb = new XSSFWorkbook();
                        XSSFSheet sheet = wb.createSheet("Payment Details");
                        XSSFRow header = sheet.createRow(0);
                        header.createCell(0).setCellValue("Payment ID");
                        header.createCell(1).setCellValue("Payment For");
                        header.createCell(2).setCellValue("Cost (MK)");
                        header.createCell(3).setCellValue("Mode of Payment");
                        header.createCell(4).setCellValue("Reference");
                        header.createCell(5).setCellValue("Date Of Payment");
                        header.createCell(6).setCellValue("Recorded By");


                        sheet.setColumnWidth(0, 256 * 15);
                        sheet.autoSizeColumn(1);
                        sheet.autoSizeColumn(2);
                        sheet.setColumnWidth(3, 256 * 25);

                        sheet.setZoom(150);

                        int index = 1;

                        try{
                            while (rs.next()) {
                                XSSFRow row = sheet.createRow(index);
                                row.createCell(0).setCellValue(rs.getString("id"));
                                row.createCell(1).setCellValue(rs.getString("expense"));
                                row.createCell(2).setCellValue(rs.getString("amount"));
                                row.createCell(3).setCellValue(rs.getString("mode"));
                                row.createCell(4).setCellValue(rs.getString("ref"));
                                row.createCell(5).setCellValue(rs.getString("date"));
                                row.createCell(6).setCellValue( rs.getString("user"));

                                index++;

                            }

                            String fileName = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() +
                                    "\\Billy FMIS\\Payments"+ System.currentTimeMillis() + ".xlsx";

                            FileOutputStream fileOut = new FileOutputStream(fileName);
                            wb.write(fileOut);
                            fileOut.close();

                            loadingTxt.setVisible(false);

                            showNotification("Data Exported Successfully\nFile Location is  " + fileName);

                        }catch(SQLException ex){

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
    void ccOnKeyReleasedAction(KeyEvent event) {
        filterPaymentTxt.textProperty().addListener((observableValue, oldValue, newValue) -> {
            filteredPaymentData.setPredicate((Predicate<? super Payment>) p -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();
                if (p.getId().contains(newValue)) {
                    return true;
                } else if (p.getExpense().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }else if (p.getAmount().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }else if (p.getDate().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }else if (p.getRef().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }else if (p.getUser().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }else if (p.getMode().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;

            });
        });
        SortedList<Payment> sortedData = new SortedList<>(filteredPaymentData);
        sortedData.comparatorProperty().bind(paymentsTable.comparatorProperty());
        paymentsTable.setItems(sortedData);
        setLabels(sortedData);
        pdata = sortedData;
    }

    @FXML
    void coursesOnKeyReleasedAction(KeyEvent event) {
        filterExpenseTxt.textProperty().addListener((observableValue, oldValue, newValue) -> {
            filteredExpenseData.setPredicate((Predicate<? super Expense>) e -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();
                if (e.getName().contains(newValue)) {
                    return true;
                } else if (e.getDate().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;

            });
        });
        SortedList<Expense> sortedData = new SortedList<>(filteredExpenseData);
        sortedData.comparatorProperty().bind(expenseTable.comparatorProperty());
        expenseTable.setItems(sortedData);
    }

    @FXML
    void deleteExpenseAction(ActionEvent event) {
        Expense e = expenseTable.getSelectionModel().getSelectedItem();
        if(e != null){
            int response = PaymentQueries.deleteExpense(e.getName());

            if (response != 404) {
                loadExpenseTableData();
                setExpenseCombo();
                loadPaymentTableData();
                showNotification("Expense Deleted Successfully");
            }
        }else{
            makeAlert("warning" , "Please Select an Expense From the table");
        }
    }

    @FXML
    void deletePaymentInfoAction(ActionEvent event) {
        Payment p = paymentsTable.getSelectionModel().getSelectedItem();
        if(p != null){
            int response = PaymentQueries.deletePayment(p.getId());

            if (response != 404) {
                loadPaymentTableData();
                clearFields();
                showNotification("Payment Deleted Successfully");
            }
        }else{
            makeAlert("warning" , "Please Select a Payment from the table");
        }
    }

    @FXML
    void expenseTableClickedAction(MouseEvent event) {

    }

    @FXML
    void expenseTableOnKeyReleasedAction(KeyEvent event) {

    }

    @FXML
    void paymentsOnKeyRelAction(KeyEvent event) {
        if (event.getCode() == KeyCode.UP | event.getCode() == KeyCode.DOWN) {
            autoFillPaymentFields();
        }
    }

    @FXML
    void paymentsTableMouseClicked(MouseEvent event) {
        autoFillPaymentFields();
    }

    @FXML
    void refreshCourseAction(ActionEvent event) {
        loadPaymentTableData();
        setLabels(payments);
    }

    @FXML
    void refreshCourseInfoAction(ActionEvent event) {

    }

    @FXML
    void saveExpenseAction(ActionEvent event) {
        Expense e = new Expense(expenseNameTxt.getText(), "date");

        if (validateExpenseFields(e)) {
            if (checkExpense(e.getName())) {
                boolean added = PaymentQueries.addExpense(e);

                if (added == false) {
                    showNotification("Expense Has been added Succesfully");
                    loadExpenseTableData();
                    setExpenseCombo();
                    clearField();
                }
            } else {
                makeAlert("warning", "The Expense you entered Exist");
            }
        }

    }

    private void clearField() {
        expenseNameTxt.setText("");
    }

    private boolean validateExpenseFields(Expense e) {
        if (!e.getName().isEmpty()) {
            return true;
        } else {
            makeAlert("warning", "Please complete the field");
        }

        return false;
    }

    private boolean checkExpense(String id){
        try {
            rs = PaymentQueries.getExpense(id);
            while (rs.next()) {
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(CoursesController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    @FXML
    void generateID(ActionEvent event) {
        int randomID = randomNumber();

        while (!checkPayment(Integer.toString(randomID))) {
            randomID = randomNumber();
        }

        idTxt.setText(Integer.toString(randomID));
    }


    @FXML
    void savePaymentAction(ActionEvent event) {
        Payment p = new Payment(idTxt.getText(), expenseCombo.getSelectionModel().getSelectedItem(), "date", amountTxt.getText(),
                modeCombo.getSelectionModel().getSelectedItem(), refTxt.getText(), LoginDocumentController.userName);

        if (validatePaymentFields(p)) {
            if (checkPayment(p.getId())) {
                boolean added = PaymentQueries.addPayment(p);

                if (added == false) {
                    showNotification("Payment Has been added Succesfully");
                    loadPaymentTableData();
                    clearFields();
                }
            } else {
                makeAlert("warning", "The Payment you entered Exist");
            }
        }
    }

    private void clearFields() {
        idTxt.setText("");
        expenseCombo.getSelectionModel().select(null);
        amountTxt.setText("");
        modeCombo.getSelectionModel().select(null);
        refTxt.setText("");
    }

    private boolean validatePaymentFields(Payment p) {
        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

        if (!p.getId().isEmpty() & !p.getAmount().isEmpty() &
                modeCombo.getSelectionModel().getSelectedItem() != null &   expenseCombo.getSelectionModel().getSelectedItem() != null ) {

            if (pattern.matcher(p.getAmount()).matches()) {
                if(Double.parseDouble(p.getAmount()) > 0){
                    return true;
                }else{
                    makeAlert("warning", "Amount should not be less than zero");
                }
            } else {
                makeAlert("warning", "Amount should be numeric");
            }
        } else {
            makeAlert("warning", "Please complete the fields");
        }

        return false;
    }

    private boolean checkPayment(String id){
        try {
            rs = PaymentQueries.getPayment(id);
            while (rs.next()) {
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(CoursesController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    @FXML
    void searchBtnsAction(ActionEvent event) {
        if (filterExpenseTxt.isVisible()) {
            searchBtns.setText("Search");
            hideSearchTxts();
        } else {
            searchBtns.setText("Hide Search Fields");
            showSearchTxts();
        }
    }

    public void showSearchTxts() {
        filterExpenseTxt.setVisible(true);
        filterPaymentTxt.setVisible(true);
    }


    public void hideSearchTxts() {
        filterExpenseTxt.setVisible(false);
        filterPaymentTxt.setVisible(false);
    }

    @FXML
    void updatePaymentInfoAction(ActionEvent event) {
        Payment p = new Payment(idTxt.getText(), expenseCombo.getSelectionModel().getSelectedItem(), "date", amountTxt.getText(),
                modeCombo.getSelectionModel().getSelectedItem(), refTxt.getText(), LoginDocumentController.userName);

        if (validatePaymentFields(p) & !checkPayment(p.getId())) {
            boolean edited = new PaymentQueries().editPayment(p);

            if (edited == false) {
                showNotification("Payment Has been Edited Succesfully");
                clearFields();
                loadPaymentTableData();
            }
        } else {
            makeAlert("error", "Course not Found");
        }
    }

    @FXML
    void modeOnAction(ActionEvent event) {
        String selectedItem = modeCombo.getSelectionModel().getSelectedItem();

        if(selectedItem != "Cash" & selectedItem != null){
            refTxt.setVisible(true);
        }else{
            refTxt.setVisible(false);
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        anchorPane.getStylesheets().add(getClass().getResource("course.css").toExternalForm());

        loadExpenseTableData();
        setExpenseTable();

        loadPaymentTableData();
        setPaymentTable();

        setExpenseCombo();
        setModeCombo();

        setReferenceTxt();

        hideSearchTxts();
        initSearchFilterVars();

        setLabels(payments);

        loadingTxt.setVisible(false);

    }

    public void initSearchFilterVars() {
        filteredExpenseData = new FilteredList<>(expenses, e -> true);
        filteredPaymentData = new FilteredList<>(payments, e -> true);
    }

    private void setReferenceTxt() {
        refTxt.setVisible(false);
    }

    private void setExpenseCombo() {
        expensesForCombo.clear();
        for(Expense data: expenses){
            expensesForCombo.add(data.getName());
        }

        expenseCombo.setItems(expensesForCombo);
    }

    private void setModeCombo(){
        pm.add("Cash");

        pm.add("Cheque");
        pm.add("Airtel Money");
        pm.add("TNM Mpamba");
        pm.add("Mo626");

        modeCombo.setItems(pm);
    }

    private void loadExpenseTableData() {
        expenses.clear();

        try {
            rs = PaymentQueries.getAllExpenses();
            while (rs.next()) {
                expenses.add(new Expense(rs.getString("name"), rs.getString("date")));
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(CoursesController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void   setExpenseTable(){
        expenseTable.setItems(expenses);
        expenseTable.setEditable(true);

        TableColumn col1 = new TableColumn("Description");
        TableColumn col2 = new TableColumn("Date Added");

        col1.setMinWidth(250);
        col2.setMinWidth(200);

        col1.setCellValueFactory(new PropertyValueFactory<>("name"));
        col2.setCellValueFactory(new PropertyValueFactory<>("date"));

        expenseTable.getColumns().addAll(col1, col2);
    }

    private void loadPaymentTableData(){
        payments.clear();

        try {
            rs = PaymentQueries.getAllPayments();
            while (rs.next()) {
                payments.add(new Payment(
                        rs.getString("id"),
                        rs.getString("expense"),
                        rs.getString("date"),
                        rs.getString("amount"),
                        rs.getString("mode"),
                        rs.getString("ref"),
                        rs.getString("user")
                        )
                );
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(CoursesController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setPaymentTable(){
        paymentsTable.setItems(payments);
        paymentsTable.setEditable(true);

        TableColumn col1 = new TableColumn("Payment ID");
        TableColumn col2 = new TableColumn("Payment For");
        TableColumn col3 = new TableColumn("Cost/Amount (MK)");
        TableColumn col4 = new TableColumn("Mode Of Payment");
        TableColumn col5 = new TableColumn("Reference");
        TableColumn col6 = new TableColumn("Date of Payment");
        TableColumn col7 = new TableColumn("Recorded By");

        col1.setMinWidth(70);
        col2.setMinWidth(150);
        col3.setMinWidth(130);
        col4.setMinWidth(130);
        col5.setMinWidth(70);
        col6.setMinWidth(150);
        col7.setMinWidth(100);

        col1.setCellValueFactory(new PropertyValueFactory<>("id"));
        col2.setCellValueFactory(new PropertyValueFactory<>("expense"));
        col3.setCellValueFactory(new PropertyValueFactory<>("amount"));
        col4.setCellValueFactory(new PropertyValueFactory<>("mode"));
        col5.setCellValueFactory(new PropertyValueFactory<>("ref"));
        col6.setCellValueFactory(new PropertyValueFactory<>("date"));
        col7.setCellValueFactory(new PropertyValueFactory<>("user"));

        paymentsTable.getColumns().addAll(col1, col2, col3, col4, col5, col6, col7);
    }

    private void autoFillPaymentFields() {
        Payment p = paymentsTable.getSelectionModel().getSelectedItem();

        if(p != null){
            idTxt.setText(p.getId());
            expenseCombo.getSelectionModel().select(p.getExpense());
            amountTxt.setText(p.getAmount());
            modeCombo.getSelectionModel().select(p.getMode());
            refTxt.setText(p.getRef());
        }
    }

    private void setLabels(ObservableList<Payment> data) {
        totalRecordsLbl.setText(""+data.size());

        double total = 0.0;

        if(data.size() > 0){
            for(int i = 0; i < data.size() ; i++){
                total += Double.parseDouble(data.get(i).getAmount());
            }
        }

        totalPaymentsLbl.setText("K"+total);
    }

}
