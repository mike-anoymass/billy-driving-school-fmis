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
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
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

import static billydrivingschoolfmis.AlertClass.makeAlert;
import static billydrivingschoolfmis.NotificationClass.showNotification;

public class ReceiptsController implements Initializable {
    @FXML
    private AnchorPane anchor;

    @FXML
    private Label loadingTxt;

    @FXML
    private TableView<Receipts> receiptsTable;

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

    private ObservableList<Receipts> data = FXCollections.observableArrayList();
    private ObservableList<Receipts> pdata = FXCollections.observableArrayList();
    private ObservableList<Receipts> ppdata = FXCollections.observableArrayList();
    private ResultSet rs;
    private FilteredList<Receipts> filteredData = new FilteredList<>(data, e->true);

    @FXML
    void deleteReceipt(ActionEvent event) {
        ObservableList<Receipts> selected = FXCollections.observableArrayList();

        for(Receipts data  : data){
            if(data.getCheckBox().isSelected()){
                selected.add(data);
            }
        }

        if(selected.size() > 0){
            int deleted = ReceiptQueries.deleteReceipt(selected);

            if (deleted != 404) {
                loadData();
                setLabels(data);
                showNotification("Receipt(s) Deleted Successfully");
            }

        }else{
            makeAlert("warning", "No Students Selected");
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
                        rs = ReceiptQueries.getNReceipts();

                        XSSFWorkbook wb = new XSSFWorkbook();
                        XSSFSheet sheet = wb.createSheet("Receipts Details");
                        XSSFRow header = sheet.createRow(0);
                        header.createCell(0).setCellValue("Receipt No");
                        header.createCell(1).setCellValue("Student Name");
                        header.createCell(2).setCellValue("Amount (MK)");
                        header.createCell(3).setCellValue("Date of Receipt");
                        header.createCell(4).setCellValue("Payment of");
                        header.createCell(5).setCellValue("Payment Mode");
                        header.createCell(6).setCellValue("Reference");
                        header.createCell(7).setCellValue("Received By");


                        sheet.setColumnWidth(0, 256 * 15);
                        sheet.autoSizeColumn(1);
                        sheet.autoSizeColumn(2);
                        sheet.setColumnWidth(3, 256 * 25);

                        sheet.setZoom(150);

                        int index = 1;

                        try{
                            while (rs.next()) {
                                XSSFRow row = sheet.createRow(index);
                                row.createCell(0).setCellValue(rs.getString("receiptNo"));
                                row.createCell(1).setCellValue(rs.getString("fullname"));
                                row.createCell(2).setCellValue(rs.getString("amount"));
                                row.createCell(3).setCellValue(rs.getString("aDate"));
                                row.createCell(4).setCellValue(rs.getString("paymentOf"));
                                row.createCell(5).setCellValue( rs.getString("modeOfPayment"));
                                row.createCell(6).setCellValue( rs.getString("reference"));
                                row.createCell(7).setCellValue(rs.getString("user"));

                                index++;

                            }

                            String fileName = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() +
                                    "\\Billy FMIS\\Receipts"+ System.currentTimeMillis() + ".xlsx";

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
    void printAction(ActionEvent event) {
        if(pdata.size() > 0){
            ppdata = pdata;
        }else{
            ppdata = data;
        }

        if(Integer.parseInt(totalRecordsLbl.getText()) > 0){
            try {
                Document doc = new Document();
                String fileName = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() +
                        "\\Billy FMIS\\Receipts" + System.currentTimeMillis() + ".pdf";

                PdfWriter.getInstance(doc, new FileOutputStream(fileName));
                doc.open();
                com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance("src/img/driving_32.png");
                doc.add(image);
                doc.add(new Paragraph("Billy Driving School - Receipts Report",
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
                PdfPCell titleCell0 = new PdfPCell(new Paragraph("Receipts Summary"));
                titleCell0.setColspan(4);
                titleCell0.setBorder(Rectangle.NO_BORDER);
                titleCell0.setHorizontalAlignment(Element.ALIGN_CENTER);
                titleCell0.setBackgroundColor(BaseColor.LIGHT_GRAY);

                table0.addCell(titleCell0);
                table0.addCell("Total Records:");
                table0.addCell(totalRecordsLbl.getText());
                table0.addCell("Sum of Receipts:");
                table0.addCell(totalFeesLbl.getText());

                PdfPTable table1 = new PdfPTable(7);
                PdfPCell titleCell1 = new PdfPCell(new Paragraph("Receipts Details"));
                titleCell1.setColspan(14);
                titleCell1.setBorder(Rectangle.NO_BORDER);
                titleCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                titleCell1.setBackgroundColor(BaseColor.LIGHT_GRAY);


                table1.addCell(titleCell1);
                table1.addCell("Receipt No");
                table1.addCell("Received From");
                table1.addCell("Receipt Date");
                table1.addCell("Receipt For");
                table1.addCell("Payment Mode");
                table1.addCell("Reference");
                table1.addCell("Amount (MK)");

                for(Receipts r : ppdata){
                    table1.addCell(r.getId());
                    table1.addCell(r.getFullname());
                    table1.addCell(r.getDate());
                    table1.addCell(r.getBpo());
                    table1.addCell(r.getMop());
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
    void filterDataOnkeyReleased(KeyEvent event) {
        filterTxt.textProperty().addListener((observableValue, oldValue, newValue) -> {
            filteredData.setPredicate((Predicate<? super Receipts>) receipt -> {
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
                }else if (receipt.getFullname().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }

                return false;

            });
        });

        SortedList<Receipts> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(receiptsTable.comparatorProperty());
        receiptsTable.setItems(sortedData);
        setLabels(sortedData);
        pdata = sortedData;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        anchor.getStylesheets().add(getClass().getResource("receipt.css").toExternalForm());
        loadData();
        setTableView();
        setLabels(data);

        loadingTxt.setVisible(false);
    }

    private void setLabels(ObservableList<Receipts> data) {
        totalRecordsLbl.setText(""+data.size());

        double total = 0.0;

        if(data.size() > 0){
            for(int i = 0; i < data.size() ; i++){
                total += Double.parseDouble(data.get(i).getAmount());
            }
        }

        totalFeesLbl.setText("K"+total);
    }

    private void setTableView() {
        receiptsTable.setItems(data);

        TableColumn col00 = new TableColumn("Tick");
        TableColumn col1 = new TableColumn("Receipt No.");
        TableColumn col2 = new TableColumn("Student Name");
        TableColumn col22= new TableColumn("Amount (MK)");
        TableColumn col3 = new TableColumn("Payment Date");
        TableColumn col4 = new TableColumn("Payment Of");
        TableColumn col5 = new TableColumn("Payment Mode");
        TableColumn col6 = new TableColumn("Reference");
        TableColumn col7 = new TableColumn("Received By");

        col00.setMinWidth(50);
        col1.setMinWidth(100);
        col2.setMinWidth(200);
        col22.setMinWidth(100);
        col3.setMinWidth(200);
        col4.setMinWidth(120);
        col5.setMinWidth(120);
        col6.setMinWidth(120);
        col7.setMinWidth(100);

        receiptsTable.getColumns().addAll(col00, col1, col2, col22, col3, col4, col5, col6, col7);

        col1.setCellValueFactory(new PropertyValueFactory<>("id"));
        col22.setCellValueFactory(new PropertyValueFactory<>("amount"));
        col2.setCellValueFactory(new PropertyValueFactory<>("fullname"));
        col3.setCellValueFactory(new PropertyValueFactory<>("date"));
        col4.setCellValueFactory(new PropertyValueFactory<>("bpo"));
        col5.setCellValueFactory(new PropertyValueFactory<>("mop"));
        col6.setCellValueFactory(new PropertyValueFactory<>("ref"));
        col7.setCellValueFactory(new PropertyValueFactory<>("user"));
        col00.setCellValueFactory(new PropertyValueFactory<Receipts, String>("checkBox"));
    }

    private void loadData() {
        data.clear();

        try{
            rs = ReceiptQueries.getNReceipts();
            while(rs.next()){
                data.add(new Receipts(
                        rs.getString("receiptNo"),
                        rs.getString("aDate"),
                        rs.getString("amount"),
                        rs.getString("modeOfPayment"),
                        rs.getString("paymentOf"),
                        rs.getString("user"),
                        rs.getString("reference"),
                        rs.getString("fullname")
                ));
            }

        }catch(SQLException ex){
            System.err.println(ex);
        }

    }
}
