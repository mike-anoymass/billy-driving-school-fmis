/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billydrivingschoolfmis;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.sun.org.apache.xerces.internal.impl.dv.ValidatedInfo;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

/**
 * FXML Controller class
 *
 * @author ANOYMASS
 */
public class CoursesController implements Initializable {
    
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private TableView<Course> courseTable;
    @FXML
    private TableView<CourseType> courseTypeTable;
    @FXML
    private TableView<CourseDuration> ccDurationTable;
    @FXML
    private JFXTextField courseCodeTxt;
    @FXML
    private JFXTextField courseNameTxt;
    @FXML
    private JFXButton saveBtn;
    @FXML
    private JFXButton updateBtn;
    @FXML
    private JFXButton deleteBtn;
    @FXML
    private MenuItem refreshMenuItem;
    @FXML
    private MenuItem deleteMenuItem;
    @FXML
    private JFXTextField durationNameTxt;
    @FXML
    private JFXTextField daysTxt;
    @FXML
    private MenuItem refreshMenuItem1;
    @FXML
    private MenuItem deleteMenuItem1;
    @FXML
    private MenuItem refreshMenuItem11;
    @FXML
    private MenuItem deleteMenuItem11;
    @FXML
    private JFXComboBox<String> courseCombo;
    @FXML
    private JFXComboBox<String> durationCombo;
    @FXML
    private JFXTextField amountTxt;
    @FXML
    private JFXTextField filterCoursesTxt;
    @FXML
    private JFXTextField filterCoursesTypeTxt;
    @FXML
    private JFXTextField filterCcTxt;
    @FXML
    private Button searchBtns;
    @FXML
    private Label courseLbl;
    @FXML
    private Label courseTypeLbl;
    @FXML
    private Label courseInfoLbl;
    
    private ResultSet rs;
    private final ObservableList<Course> courseData = FXCollections.observableArrayList();
    private final ObservableList<CourseType> courseTypeData = FXCollections.observableArrayList();
    private final ObservableList<CourseDuration> ccData = FXCollections.observableArrayList();
    private final ObservableList<String> coursesForCombo = FXCollections.observableArrayList();
    private final ObservableList<String> courseTypesForCombo = FXCollections.observableArrayList();
    private FilteredList<Course> filteredCourseData;
    private FilteredList<CourseType> filteredCourseTypeData;
    private FilteredList<CourseDuration> filteredCcData;
    private String[] courseArray, durationArray;
    private String courseID, durationName;
    private CourseDuration cd;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        anchorPane.getStylesheets().add(getClass().getResource("course.css").toExternalForm());
        
        loadCourseTableData();
        setCourseTable();
        
        loadCourseTypeTableData();
        setCourseTypeTable();
        
        loadCcTableData();
        setCcTable();
        
        setCourseComboBoxData();
        setCourseTypeComboBoxData();
        
        setShortcuts();
        
        initSearchFilterVars();
        
        hideSearchTxts();
        
        setLabels();
        
    }
    
    public void initSearchFilterVars() {
        filteredCourseData = new FilteredList<>(courseData, e -> true);
        filteredCourseTypeData = new FilteredList<>(courseTypeData, e -> true);
        filteredCcData = new FilteredList<>(ccData, e -> true);
    }
    
    public void hideSearchTxts() {
        filterCoursesTxt.setVisible(false);
        filterCcTxt.setVisible(false);
        filterCoursesTypeTxt.setVisible(false);
    }
    
    public void showSearchTxts() {
        filterCoursesTxt.setVisible(true);
        filterCcTxt.setVisible(true);
        filterCoursesTypeTxt.setVisible(true);
    }
    
    private void loadCourseTableData() {
        courseData.clear();

        try {
            rs = new CourseQueries().getAllCourses();
            while (rs.next()) {
                courseData.add(new Course(rs.getString("code"), rs.getString("name")));
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(CoursesController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    public void setCourseTable() {
        courseTable.setItems(courseData);
        courseTable.setEditable(true);
        
        TableColumn col1 = new TableColumn("Course Code");
        TableColumn col2 = new TableColumn("Course Name");
        
        col1.setMinWidth(100);
        col2.setMinWidth(300);
        
        col1.setCellValueFactory(new PropertyValueFactory<>("code"));
        col2.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        courseTable.getColumns().addAll(col1, col2);
    }
    
    @FXML
    private void saveCourseAction(ActionEvent event) {
        if (checkCompletedFields() ) {
            if(checkIfCourseExist()){
                Course course = new Course(courseCodeTxt.getText(), courseNameTxt.getText());
                boolean added = new CourseQueries().addCourse(course);

                if (added == false) {
                    showNotification(courseNameTxt.getText() + " Has been added Succesfully");

                    clearTextFieldsFor("course");
                    loadCourseTableData();
                    setCourseComboBoxData();
                    setLabels();

                }
            }else {
                makeAlert("warning", "The course you entered Exist");
            }
        }
        
    }
    
    @FXML
    private void updateCourseAction(ActionEvent event) {
        if (checkCompletedFields() & !checkIfCourseExist()) {
            Course course = new Course(courseCodeTxt.getText(), courseNameTxt.getText());
            boolean edited = new CourseQueries().editCourse(course);
            
            if (edited == false) {
                showNotification(courseNameTxt.getText() + " Has been Edited Succesfully");
                
                clearTextFieldsFor("course");
                loadCourseTableData();
                setCourseComboBoxData();
            }
        } else {
            makeAlert("error", "Course not Found");
        }
        
    }
    
    @FXML
    private void deleteCourseAction(ActionEvent event) {
        if (checkCompletedFields()) {
            int response = new CourseQueries().deleteCourse(courseCodeTxt.getText());
            
            if (response != 404) {
                clearTextFieldsFor("course");
                loadCourseTableData();
                setCourseComboBoxData();
                loadCcTableData();
                setLabels();
                showNotification("Course Deleted Successfully");
            }
            
        }
    }
    
    @FXML
    private void courseTableClickedAction(MouseEvent event) {
        autoFillCourseTxtFields();
    }
    
    @FXML
    private void courseTableOnKeyReleasedAction(KeyEvent event) {
        if (event.getCode() == KeyCode.UP | event.getCode() == KeyCode.DOWN) {
            autoFillCourseTxtFields();
        }
    }
    
    private void autoFillCourseTxtFields() {
        Course course = courseTable.getSelectionModel().getSelectedItem();
        
        if (course != null) {
            try {
                rs = new CourseQueries().getCourse(course.getCode());
                while (rs.next()) {
                    courseCodeTxt.setText(rs.getString("code"));
                    courseNameTxt.setText(rs.getString("name"));
                }
                rs.close();
            } catch (SQLException ex) {
                Logger.getLogger(CoursesController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            makeAlert("warning", "Course not Selected");
        }
        
    }
    
    private boolean checkCompletedFields() {
        if (courseNameTxt.getText().isEmpty() | courseCodeTxt.getText().isEmpty()) {
            makeAlert("warning", "Please Complete the fields");
            return false;
        }
        return true;
    }
    
    private boolean checkIfCourseExist() {
        
        try {
            rs = new CourseQueries().getCourse(courseCodeTxt.getText());
            while (rs.next()) {
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(CoursesController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
    
    private void clearTextFieldsFor(String tag) {
        switch (tag) {
            case "course":
                courseCodeTxt.setText("");
                courseNameTxt.setText("");
                break;
            
            case "courseType":
                durationNameTxt.setText("");
                daysTxt.setText("");
                break;
            
            case "courseInfo":
                amountTxt.setText("");
                courseCombo.getSelectionModel().select(null);
                durationCombo.getSelectionModel().select(null);
                break;
        }
    }
    
    private void showNotification(String txt) {
        TrayNotification tray = new TrayNotification("Billy FMIS",
                txt,
                NotificationType.SUCCESS);
        
        tray.setAnimationType(AnimationType.SLIDE);
        tray.showAndDismiss(Duration.seconds(3));
    }
    
    @FXML
    private void refreshCourseAction(ActionEvent event) {
        loadCourseTableData();
    }
    
    private void setShortcuts() {
        refreshMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+R"));
        deleteMenuItem.setAccelerator(KeyCombination.keyCombination("Ctrl+D"));
        refreshMenuItem1.setAccelerator(KeyCombination.keyCombination("Ctrl+R"));
        deleteMenuItem1.setAccelerator(KeyCombination.keyCombination("Ctrl+D"));
    }
    
    private void loadCourseTypeTableData() {
        courseTypeData.clear();
        
        try {
            rs = new CourseQueries().getAllCourseTypes();
            while (rs.next()) {
                courseTypeData.add(new CourseType(rs.getString("days"), rs.getString("name")));
            }
            
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(CoursesController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void setCourseTypeTable() {
        courseTypeTable.setItems(courseTypeData);
        courseTypeTable.setEditable(true);
        
        TableColumn col1 = new TableColumn("Course Type Name");
        TableColumn col2 = new TableColumn("Days");
        
        col1.setMinWidth(250);
        col2.setMinWidth(150);
        
        col1.setCellValueFactory(new PropertyValueFactory<>("name"));
        col2.setCellValueFactory(new PropertyValueFactory<>("days"));
        
        courseTypeTable.getColumns().addAll(col1, col2);
    }
    
    @FXML
    private void saveDurationAction(ActionEvent event) {
        CourseType cType = new CourseType(daysTxt.getText(), durationNameTxt.getText());
        
        if (validateDuration(cType)) {
            if (checkIfCourseTypeExist()) {
                boolean added = new CourseQueries().addCourseType(cType);
                
                if (added == false) {
                    showNotification(durationNameTxt.getText() + " Has been added Succesfully");
                    
                    clearTextFieldsFor("courseType");
                    loadCourseTypeTableData();
                    setCourseTypeComboBoxData();
                    setLabels();
                    
                }
            } else {
                makeAlert("warning", "The duration you entered Exist");
            }
        }
        
    }
    
    @FXML
    private void updateDurationAction(ActionEvent event) {
        CourseType cType = new CourseType(daysTxt.getText(), durationNameTxt.getText());
        
        if (validateDuration(cType)) {
            if (!checkIfCourseTypeExist()) {
                boolean edited = new CourseQueries().editCourseType(cType);
                
                if (edited == false) {
                    showNotification(durationNameTxt.getText() + " Has been Edited Succesfully");
                    
                    clearTextFieldsFor("courseType");
                    loadCourseTypeTableData();
                    setCourseTypeComboBoxData();
                    
                }
            } else {
                makeAlert("warning", "The duration you entered does not Exist");
            }
        }
    }
    
    @FXML
    private void deleteCourseTypeAction(ActionEvent event) {
        CourseType cType = new CourseType(daysTxt.getText(), durationNameTxt.getText());
        
        if (validateDuration(cType)) {
            if (!checkIfCourseTypeExist()) {
                int response = new CourseQueries().deleteCourseType(durationNameTxt.getText());
                
                if (response != 404) {
                    clearTextFieldsFor("courseType");
                    loadCourseTypeTableData();
                    setCourseTypeComboBoxData();
                    loadCcTableData();
                    setLabels();
                    showNotification("Course Type Deleted Successfully");
                }
            } else {
                makeAlert("warning", "The duration you entered does not Exist");
            }
            
        }
    }
    
    @FXML
    private void courseTypeTableOnMouseClickedAction(MouseEvent event) {
        autoFillCourseTypeTxtFiels();
    }
    
    public void autoFillCourseTypeTxtFiels() {
        CourseType courseType = courseTypeTable.getSelectionModel().getSelectedItem();
        
        if (courseType != null) {
            try {
                rs = new CourseQueries().getCourseType(courseType.getName());
                while (rs.next()) {
                    durationNameTxt.setText(rs.getString("name"));
                    daysTxt.setText(rs.getString("days"));
                }
                rs.close();
            } catch (SQLException ex) {
                Logger.getLogger(CoursesController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            makeAlert("warning", "Course Type not Selected");
        }
        
    }
    
    @FXML
    private void courseDurationOnKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.UP | event.getCode() == KeyCode.DOWN) {
            autoFillCourseTypeTxtFiels();
        }
    }
    
    
    private boolean checkIfCourseTypeExist() {
        
        try {
            rs = new CourseQueries().getCourseType(durationNameTxt.getText());
            while (rs.next()) {
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(CoursesController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
    
    private boolean validateDuration(CourseType cType) {
        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
        if (!cType.getName().isEmpty() & !cType.getDays().isEmpty()) {
            if (pattern.matcher(cType.getDays()).matches()) {
                return true;
            } else {
                makeAlert("warning", "Days should be numeric");
            }
        } else {
            makeAlert("warning", "Please complete the fields");
        }
        
        return false;
    }
    
    public void makeAlert(String title, String txt) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        switch (title) {
            case "error":
                alert = new Alert(Alert.AlertType.ERROR);
                break;
            
            case "information":
                alert = new Alert(Alert.AlertType.INFORMATION);
                break;
        }
        
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(txt);
        alert.showAndWait();
        
    }
    
    @FXML
    private void refreshCourseTypeAction(ActionEvent event) {
        loadCourseTypeTableData();
    }
    
    @FXML
    private void refreshCourseInfoAction(ActionEvent event) {
        loadCcTableData();
    }
    
    @FXML
    private void deleteCourseInfoAction(ActionEvent event) {
        
        if (cd != null) {
            int response = new CourseQueries().deleteCourseInfo(cd.getCode(), cd.getDuration());
            
            if (response != 404) {
                clearTextFieldsFor("courseInfo");
                loadCcTableData();
                setLabels();
                showNotification("Course Info Deleted Successfully");
            }
        } else {
            makeAlert("warning", "Nothing To Delete");
        }
        
    }

    @FXML
    void ccOnKeyRelAction(KeyEvent event) {
        if (event.getCode() == KeyCode.UP | event.getCode() == KeyCode.DOWN) {
            initializeDeleteVarsForCourseInfo();
            autoFillCourseInfoFields();
        }
    }
    
    public void initializeDeleteVarsForCourseInfo() {
        cd = ccDurationTable.getSelectionModel().getSelectedItem();
    }
    
    private void setCourseComboBoxData() {
        coursesForCombo.clear();
        for (int i = 0; i < courseData.size(); i++) {
            coursesForCombo.add(courseData.get(i).getCode() + "~" + courseData.get(i).getName());
        }
        
        courseCombo.setItems(coursesForCombo);
       // courseCombo.setEditable(true);
    }
    
    private void setCourseTypeComboBoxData() {
        courseTypesForCombo.clear();
        for (int i = 0; i < courseTypeData.size(); i++) {
            courseTypesForCombo.add(courseTypeData.get(i).getName() + "~" + courseTypeData.get(i).getDays() + " days");
        }
        
        durationCombo.setItems(courseTypesForCombo);
        //durationCombo.setEditable(true);
    }
    
    private void loadCcTableData() {
        ccData.clear();
        
        try {
            rs = new CourseQueries().getAllCourseDurations();
            while (rs.next()) {
                ccData.add(new CourseDuration(rs.getString("courseID"), rs.getString("duration"), rs.getString("fees"), rs.getString("date")));
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(CoursesController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void setCcTable() {
        ccDurationTable.setItems(ccData);
        ccDurationTable.setEditable(true);
        
        TableColumn col1 = new TableColumn("Course");
        TableColumn col2 = new TableColumn("Course Type");
        TableColumn col3 = new TableColumn("Total Fees (MK)");
        TableColumn col4 = new TableColumn("Date Added");
        
        
        col1.setMinWidth(100);
        col2.setMinWidth(150);
        col3.setMinWidth(100);
        col4.setMinWidth(150);
        
        col1.setCellValueFactory(new PropertyValueFactory<>("code"));
        col2.setCellValueFactory(new PropertyValueFactory<>("duration"));
        col3.setCellValueFactory(new PropertyValueFactory<>("amount"));
        col4.setCellValueFactory(new PropertyValueFactory<>("date"));
        
        
        ccDurationTable.getColumns().addAll(col1, col2, col3, col4);
        
    }
    
    @FXML
    private void coursesOnKeyReleasedAction(KeyEvent event) {
        filterCoursesTxt.textProperty().addListener((observableValue, oldValue, newValue) -> {
            filteredCourseData.setPredicate((Predicate<? super Course>) course -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                
                String lowerCaseFilter = newValue.toLowerCase();
                if (course.getCode().contains(newValue)) {
                    return true;
                } else if (course.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
                
            });
        });
        SortedList<Course> sortedData = new SortedList<>(filteredCourseData);
        sortedData.comparatorProperty().bind(courseTable.comparatorProperty());
        courseTable.setItems(sortedData);
    }
    
    @FXML
    private void coursesTypeOnKeyReleasedAction(KeyEvent event) {
        filterCoursesTypeTxt.textProperty().addListener((observableValue, oldValue, newValue) -> {
            filteredCourseTypeData.setPredicate((Predicate<? super CourseType>) course -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                
                String lowerCaseFilter = newValue.toLowerCase();
                if (course.getDays().contains(newValue)) {
                    return true;
                } else if (course.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
                
            });
        });
        SortedList<CourseType> sortedData = new SortedList<>(filteredCourseTypeData);
        sortedData.comparatorProperty().bind(courseTypeTable.comparatorProperty());
        courseTypeTable.setItems(sortedData);
    }
    
    @FXML
    private void ccOnKeyReleasedAction(KeyEvent event) {
        filterCcTxt.textProperty().addListener((observableValue, oldValue, newValue) -> {
            filteredCcData.setPredicate((Predicate<? super CourseDuration>) course -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                
                String lowerCaseFilter = newValue.toLowerCase();
                if (course.getCode().contains(newValue)) {
                    return true;
                } else if (course.getDuration().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (course.getAmount().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });
        SortedList<CourseDuration> sortedData = new SortedList<>(filteredCcData);
        sortedData.comparatorProperty().bind(ccDurationTable.comparatorProperty());
        ccDurationTable.setItems(sortedData);
    }
    
    @FXML
    private void searchBtnsAction(ActionEvent event) {
        if (filterCoursesTxt.isVisible()) {
            searchBtns.setText("Search Courses");
            hideSearchTxts();
        } else {
            searchBtns.setText("Hide Search Fields");
            showSearchTxts();
        }
    }
    
    @FXML
    private void saveCcAction(ActionEvent event) {
        initializeCourseAndDurationVars();
        
        if (validateCourseInfo()) {
            if (checkIfCourseInfoExist()) {
                CourseDuration info = new CourseDuration(courseID, durationName, amountTxt.getText(), "date");
                
                boolean added = new CourseQueries().addCourseInfo(info);
                
                if (added == false) {
                    showNotification("Course Details Have been added Succesfully");
                    
                    clearTextFieldsFor("courseInfo");
                    loadCcTableData();
                    setLabels();
                    
                }
            } else {
                makeAlert("warning", "The duration you entered Exist");
            }
        }
    }
    
    public void initializeCourseAndDurationVars() {
        
        String selectedCourse = courseCombo.getSelectionModel().getSelectedItem();
        String selectedDuration = durationCombo.getSelectionModel().getSelectedItem();
        
        if (selectedCourse != null & selectedDuration != null) {
            courseArray = selectedCourse.split("~");
            durationArray = selectedDuration.split("~");
            
            courseID = courseArray[0];
            durationName = durationArray[0];
        } else {
            courseID = null;
            durationName = null;
        }
        
    }
    
    private boolean checkIfCourseInfoExist() {
        
        try {
            rs = new CourseQueries().getCourseInfo(courseID, durationName);
            while (rs.next()) {
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(CoursesController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
    
    private boolean validateCourseInfo() {
        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
        if (courseID != null & durationName != null & !amountTxt.getText().isEmpty()) {
            if (pattern.matcher(amountTxt.getText()).matches() ) {
                if(Double.parseDouble(amountTxt.getText()) > 0){
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
    
    @FXML
    private void ccTableMouseClicked(MouseEvent event) {
        initializeDeleteVarsForCourseInfo();
        autoFillCourseInfoFields();
    }

    private void autoFillCourseInfoFields() {
        if(cd != null){
            amountTxt.setText(cd.getAmount());
        }

    }

    public void setLabels(){
        courseLbl.setText("Courses -> " + new CourseQueries().countCourses());
        courseTypeLbl.setText("Course Type -> " + new CourseQueries().countCourseTypes());
        courseInfoLbl.setText("Course and Course Duration -> " + new CourseQueries().countCourseInfo());   
    }

    @FXML
    void updateCourseInfoAction(ActionEvent event) {
        if (validateCourseInfo() & !checkIfCourseInfoExist()) {
            CourseDuration cs = new CourseDuration(courseID, durationName, amountTxt.getText(), "date");
            boolean edited = new CourseQueries().editCourseInfo(cs);

            if (edited == false) {
                showNotification("Course Information has been Edited Succesfully");

                clearTextFieldsFor("courseInfo");
                loadCcTableData();
            }
        } else {
            makeAlert("error", "Course Information not Found");
        }
    }
    
}
