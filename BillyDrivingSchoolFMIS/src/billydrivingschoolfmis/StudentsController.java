/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billydrivingschoolfmis;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;

import java.io.*;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

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
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javafx.scene.image.Image;

import javax.swing.filechooser.FileSystemView;

import static billydrivingschoolfmis.AlertClass.makeAlert;
import static billydrivingschoolfmis.NotificationClass.showNotification;
import static billydrivingschoolfmis.RandomNumbers.randomNumber;

/**
 * FXML Controller class
 *
 * @author ANOYMASS
 */
public class StudentsController implements Initializable {

    @FXML
    private AnchorPane ancho;

    @FXML
    private GridPane grid;
    @FXML
    private JFXTextField filtersTxt;
    @FXML
    private JFXComboBox<String> courseCombo;

    @FXML
    private Label loadingTxt;
    @FXML
    private JFXComboBox<String> durationCombo;
    @FXML
    private JFXButton saveBtn;
    @FXML
    private JFXButton updateBtn;
    @FXML
    private TableView<Student> studentTable;
    @FXML
    private JFXButton searchBtns;
    @FXML
    private JFXButton generateBtn;
    @FXML
    private JFXTextField studentIDTxt;
    @FXML
    private JFXTextField fullnameTxt;
    @FXML
    private JFXTextField pAddressTxt;
    @FXML
    private JFXTextField phoneTxt;
    @FXML
    private JFXTextField emailTxt;
    @FXML
    private JFXComboBox<String> quaCombo;
    @FXML
    private Label studentLbl;
    @FXML
    private Label excelFileTxt;

    @FXML
    private MenuItem refreshBtn;

    private ObservableList<Student> data = FXCollections.observableArrayList();
    private ResultSet rs;
    private final ObservableList<String> coursesForCombo = FXCollections.observableArrayList();
    private final ObservableList<String> courseTypesForCombo = FXCollections.observableArrayList();
    private final ObservableList<String> courseData = FXCollections.observableArrayList();
    private final ObservableList<String> qualification = FXCollections.observableArrayList();
    private FilteredList<Student> filteredStudentsData;
    private FileChooser fileChooser;


    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ancho.getStylesheets().add(getClass().getResource("students.css").toExternalForm());
        //deleteImg.setImage(new Image("file:src/img/upArr_16.png"));

        loadStudentData();
        setStudentTable();

        loadCourseTableData();
        setCourseComboBoxData();

        setQualificationComboBox();
        hideSearchTxt();
        initSearchFilterVars();
        setLabels();

        loadingTxt.setVisible(false);
    }

    private void initSearchFilterVars() {
        filteredStudentsData = new FilteredList<>(data, e->true);
    }


    @FXML
    private void saveAction(ActionEvent event) {
        Student student = new Student(studentIDTxt.getText(), fullnameTxt.getText(), pAddressTxt.getText(),
                phoneTxt.getText(), emailTxt.getText(), quaCombo.getSelectionModel().getSelectedItem(),
                courseCombo.getSelectionModel().getSelectedItem(), durationCombo.getSelectionModel().getSelectedItem(), "");

        if (validateStudent(student)) {
            if (checkStudentExist(student.getId())) {
                boolean added = StudentQueries.addStudent(student);

                if (added == false) {
                    showNotification(student.getName() + " Has been added Succesfully");

                    clearTextFields();
                    loadStudentData();
                    setLabels();
                }
            } else {
                makeAlert("warning", "The Student you entered Exist");
            }
        }
    }

    @FXML
    private void updateAction(ActionEvent event) {
        Student student = new Student(studentIDTxt.getText(), fullnameTxt.getText(), pAddressTxt.getText(),
                phoneTxt.getText(), emailTxt.getText(), quaCombo.getSelectionModel().getSelectedItem(),
                courseCombo.getSelectionModel().getSelectedItem(), durationCombo.getSelectionModel().getSelectedItem(), "");

        if (validateStudent(student) & !checkStudentExist(student.getId())) {

            boolean edited = StudentQueries.editStudent(student);

            if (edited == false) {
                showNotification(student.getName() + " Has been Edited Succesfully");

                clearTextFields();
                loadStudentData();
            }
        } else {
            makeAlert("error", "Student not Found");
        }
    }

    @FXML
    private void searchBtnsAction(ActionEvent event) {
        if (filtersTxt.isVisible()) {
            searchBtns.setText("Search Students");
            hideSearchTxt();
        } else {
            searchBtns.setText("Hide Search Students");
            showSearchTxt();
        }
    }

    private void hideSearchTxt() {
        filtersTxt.setVisible(false);
    }

    private void showSearchTxt() {
        filtersTxt.setVisible(true);
    }

    @FXML
    private void generateID(ActionEvent event) {
        int randomID = randomNumber();

        while (!checkStudentExist(Integer.toString(randomID))) {
            randomID = randomNumber();
        }

        studentIDTxt.setText(Integer.toString(randomID));

    }

    private boolean checkStudentExist(String id) {

        try {
            rs = StudentQueries.getStudent(id);
            while (rs.next()) {
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(CoursesController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    private void loadStudentData() {
        data.clear();

        try {
            rs = StudentQueries.getStudents();

            while (rs.next()) {
                data.add(new Student(
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

    private void setStudentTable() {
        studentTable.setItems(data);

        TableColumn col00 = new TableColumn("Tick");
        TableColumn col1 = new TableColumn("Student ID");
        TableColumn col2 = new TableColumn("Full Name");
        TableColumn col3 = new TableColumn("Phone");
        TableColumn col4 = new TableColumn("Postal Address");
        TableColumn col5 = new TableColumn("Email");
        TableColumn col6 = new TableColumn("Qualification");
        TableColumn col7 = new TableColumn("Course");
        TableColumn col8 = new TableColumn("Course Type");
        TableColumn col9 = new TableColumn("Date Registered");
        //TableColumn col0 = new TableColumn("Balance");

        col1.setMinWidth(50);
        col2.setMinWidth(150);
        col3.setMinWidth(70);
        col4.setMinWidth(150);
        col5.setMinWidth(150);
        col6.setMinWidth(50);
        col7.setMinWidth(20);
        col8.setMinWidth(70);
        col9.setMinWidth(70);
        col00.setMinWidth(20);

        //col0.setMinWidth(70);

        studentTable.getColumns().addAll(col00, col1, col2, col7, col8, col3 , col4, col5, col6,  col9/*, col0*/);
        studentTable.setEditable(true);

        col1.setCellValueFactory(new PropertyValueFactory<>("id"));
        col2.setCellValueFactory(new PropertyValueFactory<>("name"));
        col3.setCellValueFactory(new PropertyValueFactory<>("phone"));
        col4.setCellValueFactory(new PropertyValueFactory<>("address"));
        col5.setCellValueFactory(new PropertyValueFactory<>("email"));
        col6.setCellValueFactory(new PropertyValueFactory<>("qua"));
        col7.setCellValueFactory(new PropertyValueFactory<>("course"));
        col8.setCellValueFactory(new PropertyValueFactory<>("courseType"));
        col9.setCellValueFactory(new PropertyValueFactory<>("date"));
        // col0.setCellValueFactory(new PropertyValueFactory<>("amount"));
        col00.setCellValueFactory(new PropertyValueFactory<Student, String>("checkBox"));


    }




    public void autoFillCourseTypeTxtFiels() {
        Student student = studentTable.getSelectionModel().getSelectedItem();

        if (student != null) {
            try {
                rs = StudentQueries.getStudent(student.getId());
                while (rs.next()) {
                    studentIDTxt.setText(rs.getString("studentID"));
                    fullnameTxt.setText(rs.getString("fullname"));
                    pAddressTxt.setText(rs.getString("postalAddress"));
                    phoneTxt.setText(rs.getString("phone"));
                    emailTxt.setText(rs.getString("email"));
                    quaCombo.getSelectionModel().select(rs.getString("qualification"));
                    courseCombo.getSelectionModel().select(rs.getString("course"));
                    durationCombo.getSelectionModel().select(null);
                }


            } catch (SQLException ex) {
                Logger.getLogger(CoursesController.class.getName()).log(Level.SEVERE, null, ex);
            }


        } else {
            makeAlert("warning", "No Student Selected");
        }

    }



    @FXML
    private void studentTableOnMouseClickedAction(MouseEvent event) {
        autoFillCourseTypeTxtFiels();
    }

    @FXML
    private void studentsTableOnKeyReleasedAction(KeyEvent event) {
        if (event.getCode() == KeyCode.UP | event.getCode() == KeyCode.DOWN) {
            autoFillCourseTypeTxtFiels();
        }
    }

    private boolean validateStudent(Student student) {
        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
        Pattern emailP = Pattern.compile("[a-zA-Z0-9][a-zA-Z0-9._]*@[a-zA-Z0-9]+([.][a-zA-Z]+)+");

        if (!student.getId().isEmpty() & !student.getName().isEmpty() & !student.getPhone().isEmpty()
                & student.getCourse() != null & student.getCourseType() != null & student.getQua() != null) {

            if (pattern.matcher(student.getPhone()).matches()) {

                if (emailP.matcher(student.getEmail()).matches()) {
                    return true;
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

    private void clearTextFields() {
        studentIDTxt.setText("");
        fullnameTxt.setText("");
        pAddressTxt.setText("");
        phoneTxt.setText("");
        emailTxt.setText("");

        quaCombo.getSelectionModel().select(null);
        courseCombo.getSelectionModel().select(null);
        durationCombo.getSelectionModel().select(null);
    }


    private void setCourseComboBoxData() {
        coursesForCombo.clear();
        courseCombo.setItems(courseData);
        //courseCombo.setEditable(true);
    }

    private void setCourseTypeComboBoxData() {
        durationCombo.setItems(courseTypesForCombo);
        //durationCombo.setEditable(true);
    }

    private void loadCourseTableData() {
        courseData.clear();

        try {
            rs = new CourseQueries().getAllCourses();
            while (rs.next()) {
                courseData.add(rs.getString("code"));
            }

        } catch (SQLException ex) {
            Logger.getLogger(CoursesController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void setQualificationComboBox() {
       qualification.add("PLSCE");
       qualification.add("JCE");
       qualification.add("MSCE");
       qualification.add("Diploma");
       qualification.add("Advanced Diploma");
       qualification.add("Degree");
       qualification.add("Master's Degree");
       qualification.add("PhD");
       qualification.add("Elite");
       qualification.add("Other");

       quaCombo.setItems(qualification);
    }

    @FXML
    private void courseOnAction(ActionEvent event) {
        String courseID = courseCombo.getSelectionModel().getSelectedItem();
        setDurationForThisCourse(courseID);

    }

    public void setDurationForThisCourse(String courseID){
        rs = new CourseQueries().getDurationsForThisCourse(courseID);

        courseTypesForCombo.clear();
        try {
            while(rs.next()){
               courseTypesForCombo.add(rs.getString("duration"));
            }
            setCourseTypeComboBoxData();
        } catch (SQLException ex) {
            Logger.getLogger(StudentsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void deleteStudent(ActionEvent event) {

        ObservableList<Student> selectedStudents = FXCollections.observableArrayList();

        for(Student data  : data){
            if(data.getCheckBox().isSelected()){
                selectedStudents.add(data);
            }
        }

        if(selectedStudents.size() > 0){
            loadingTxt.setVisible(true);
            loadingTxt.setText("Deleting Student(s)...");

            Task task = new Task(){

                @Override
                protected Object call() throws Exception {
                    Platform.runLater(new Runnable(){

                        @Override
                        public void run() {
                            int deleted = StudentQueries.deleteStudents(selectedStudents);

                            if (deleted != 404) {
                                clearTextFields();
                                loadStudentData();
                                showNotification("Student(s) Deleted Successfully");
                                setLabels();
                                loadingTxt.setVisible(false);
                            }else{
                                loadingTxt.setVisible(false);
                            }

                        }
                    });

                    return null;
                }
            };

            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();

        }else{
            makeAlert("warning", "No Students Selected");

        }


    }

    @FXML
    private void studentsFilterOnKeyReleased(KeyEvent e){
        filtersTxt.textProperty().addListener((observableValue, oldValue, newValue) -> {
            filteredStudentsData.setPredicate((Predicate<? super Student>) student -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();
                if (student.getId().contains(newValue)) {
                    return true;
                } else if (student.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }else if (student.getCourse().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }else if (student.getCourseType().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }else if (student.getDate().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;

            });
        });
        SortedList<Student> sortedData = new SortedList<>(filteredStudentsData);
        sortedData.comparatorProperty().bind(studentTable.comparatorProperty());
        studentTable.setItems(sortedData);
    }

    public void setLabels(){
        studentLbl.setText("Students -> " + StudentQueries.countStudents());
    }

    @FXML
    public void viewFeesAction(ActionEvent e){
        try {
            FXMLLoader loader = new FXMLLoader();
            AnchorPane root = loader.load(getClass().getResource("FeesHistory.fxml").openStream());
            FeesHistoryController controller = (FeesHistoryController) loader.getController();
            controller.setStudentID(studentIDTxt.getText());

            Scene scene = new Scene(root);
            StageManager.historyStage.setScene(scene);
            StageManager.historyStage.getIcons().add(new Image("file:src/img/history_16.png"));
            StageManager.historyStage.setTitle("Fees Payment History For " + fullnameTxt.getText());

            StageManager.historyStage.show();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    @FXML
    public void recordFeesAction(ActionEvent e){

        try {
            FXMLLoader loader = new FXMLLoader();
            AnchorPane root = loader.load(getClass().getResource("ReceiptForm.fxml").openStream());
            ReceiptFormController controller = loader.getController();
            controller.setStudentID(studentIDTxt.getText());

            Scene scene = new Scene(root);
            StageManager.receiptStage.setScene(scene);
            StageManager.receiptStage.getIcons().add(new Image("file:src/img/receipt_16.png"));
            StageManager.receiptStage.setTitle("Record Fees Receipt for " + fullnameTxt.getText());

            StageManager.receiptStage.show();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }

    @FXML
    public void refreshAction(ActionEvent e){

        
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
                        rs = StudentQueries.getStudents();

                        XSSFWorkbook wb = new XSSFWorkbook();
                        XSSFSheet sheet = wb.createSheet("Students Details");
                        XSSFRow header = sheet.createRow(0);
                        header.createCell(0).setCellValue("Student ID");
                        header.createCell(1).setCellValue("Full Name");
                        header.createCell(6).setCellValue("Course");
                        header.createCell(7).setCellValue("Course Type");
                        header.createCell(2).setCellValue("Phone Number");
                        header.createCell(3).setCellValue("Postal Address");
                        header.createCell(4).setCellValue("Email Address");
                        header.createCell(5).setCellValue("Qualification");
                        header.createCell(8).setCellValue("Date Registered");


                        sheet.setColumnWidth(0, 256 * 15);
                        sheet.autoSizeColumn(1);
                        sheet.autoSizeColumn(2);
                        sheet.setColumnWidth(3, 256 * 25);

                        sheet.setZoom(150);

                        int index = 1;

                        try{

                            while (rs.next()) {
                                XSSFRow row = sheet.createRow(index);
                                row.createCell(0).setCellValue(rs.getString("studentID"));
                                row.createCell(1).setCellValue(rs.getString("fullname"));
                                row.createCell(6).setCellValue(rs.getString("course"));
                                row.createCell(7).setCellValue(rs.getString("duration"));
                                row.createCell(2).setCellValue(rs.getString("phone"));
                                row.createCell(3).setCellValue(rs.getString("postalAddress"));
                                row.createCell(4).setCellValue(rs.getString("email"));
                                row.createCell(5).setCellValue(rs.getString("qualification"));
                                row.createCell(8).setCellValue(rs.getString("date"));

                                index++;

                            }

                            String fileName = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() +
                                    "\\Billy FMIS\\Students"+ System.currentTimeMillis() + ".xlsx";

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
    void importAction(ActionEvent event) {

        if(excelFileTxt.getText().equals("Click Here to Browse students excel file")){
            makeAlert("warning", "Please Select an excel file");
        }else{

            loadingTxt.setVisible(true);
            loadingTxt.setText("Importing Data... Please Wait!");

            Task task = new Task(){

                @Override
                protected Object call() throws Exception {
                    Platform.runLater(new Runnable(){

                        @Override
                        public void run() {
                            try {
                                FileInputStream fileIn = new FileInputStream(new File(excelFileTxt.getText()));

                                XSSFWorkbook wb = new XSSFWorkbook(fileIn);
                                XSSFSheet sheet = wb.getSheetAt(0);
                                Row row;

                                Student student = new Student();
                                DataFormatter formatter = new DataFormatter(Locale.US);
                                ObservableList<Student> importedStudents = FXCollections.observableArrayList();

                                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                                    row = sheet.getRow(i);

                                    importedStudents.add(
                                            new Student(
                                                    formatter.formatCellValue(row.getCell(0)),
                                                    formatter.formatCellValue(row.getCell(1)),
                                                    formatter.formatCellValue(row.getCell(3)),
                                                    formatter.formatCellValue(row.getCell(2)),
                                                    formatter.formatCellValue(row.getCell(4)),
                                                    formatter.formatCellValue(row.getCell(5)),
                                                    formatter.formatCellValue(row.getCell(6)),
                                                    formatter.formatCellValue(row.getCell(7)),
                                                    ""
                                            )
                                    );
                                }

                                for(Student students : importedStudents){

                                    if(checkStudentExist(students.getId())){
                                        if(validateStudent(students) & validate(students)){
                                            StudentQueries.addStudent(students);
                                        }

                                    }else{
                                        showNotification(students.getId() + " " + students.getName() + " Exists in the database");
                                    }
                                }

                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            loadStudentData();
                            setLabels();
                            loadingTxt.setVisible(false);
                        }
                    });
                    return null;
                }
            };

            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
        }

    }

    private boolean validate(Student student) {
        if (!student.getCourse().isEmpty() & !student.getQua().isEmpty() & !student.getCourseType().isEmpty()) {

            return true;
        } else {
            makeAlert("warning", "Please complete the fields\nMake Sure that course and course type you are importing should" +
                    "exist in the database");
        }

        return false;
    }

    private String getFilePath(){
        fileChooser = new FileChooser();
        fileChooser.setTitle("Select Students Excel File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("xlsx files", "*.xlsx"),
                new FileChooser.ExtensionFilter("xls files", "*.xls")
        );

        File file = fileChooser.showOpenDialog(StageManager.browsingStage);
        String path = file.getPath();
        return path;
    }


    @FXML
    void excelFileAction(MouseEvent event) {
        String path = getFilePath();
        excelFileTxt.setText(path);
    }
}
