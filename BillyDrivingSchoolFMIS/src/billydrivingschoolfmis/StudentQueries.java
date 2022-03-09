/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billydrivingschoolfmis;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import static billydrivingschoolfmis.AlertClass.makeAlert;

/**
 *
 * @author ANOYMASS
 */
public class StudentQueries {

    private static PreparedStatement pst;
    private static ResultSet rs;

    public static ResultSet getStudentsWithFees() {
        try {
            pst = BillyDrivingSchoolFMIS.conn.prepareStatement(
                    "SELECT studentID, fullname, postalAddress, phone, "
                    + "email, course, qualification, students.date AS dateAdded, students.duration AS courseType, fees  "
                    + "FROM students "
                    + "INNER JOIN courseDuration ON students.course=courseDuration.courseID and students.duration=courseDuration.duration");
            rs = pst.executeQuery();
            return rs;

        } catch (SQLException ex) {
            Logger.getLogger(StudentQueries.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rs;
    }

    public static ResultSet getStudents() {
        try {
            pst = BillyDrivingSchoolFMIS.conn.prepareStatement("Select * from students order by rowid DESC");
            rs = pst.executeQuery();
            return rs;

        } catch (SQLException ex) {
            Logger.getLogger(StudentQueries.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rs;
    }

    public static ResultSet getStudent(String id) {
        try {
            pst = BillyDrivingSchoolFMIS.conn.prepareStatement("SELECT * FROM students where studentID=?");
            pst.setString(1, id);
            rs = pst.executeQuery();
            return rs;

        } catch (SQLException ex) {
            Logger.getLogger(StudentQueries.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rs;
    }

    static boolean addStudent(Student student) {
        try {
            pst = BillyDrivingSchoolFMIS.conn.prepareStatement(
                    "INSERT INTO students (studentID, fullname, phone, email, postalAddress," +
                            " qualification, course , duration, date) "
                    + "VALUES (?, ?, ?, ?, ?, ? ,? , ? , datetime('now'))");
            pst.setString(1, student.getId());
            pst.setString(2, student.getName());
            pst.setString(3, student.getPhone());
            pst.setString(4, student.getEmail());
            pst.setString(5, student.getAddress());
            pst.setString(6, student.getQua());
            pst.setString(7, student.getCourse());
            pst.setString(8, student.getCourseType());

            return pst.execute();
        } catch (SQLException ex) {
            Logger.getLogger(CourseQueries.class.getName()).log(Level.SEVERE, null, ex);
            makeAlert("error", "Imported data has an error \n" +
                    "Please make sure that course and course type matches the ones in the database");
        }
        return true;
    }

    static int deleteStudents(ObservableList<Student> selectedStudents) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Please Confirm Deletion");
        //alert.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon., "20px"));
        alert.setHeaderText(null);
        alert.setContentText("Are you sure; wanna delete these Students ? ");
        Optional<ButtonType> action = alert.showAndWait();
       
        if (action.get() == ButtonType.OK) {
                
            
            for (int i = 0; i < selectedStudents.size(); i++) {
                
                try {
                    pst = BillyDrivingSchoolFMIS.conn.prepareStatement("DELETE FROM students WHERE studentID=?");
                    pst.setString(1, selectedStudents.get(i).getId());

                    pst.executeUpdate();

                } catch (SQLException ex) {
                    Logger.getLogger(CourseQueries.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
          return 101;
        }
        return 404;
    }

    public static boolean editStudent(Student student){
        try {
            pst = BillyDrivingSchoolFMIS.conn.prepareStatement("UPDATE students " +
                    "SET fullname=?, phone=?, email=?, postalAddress=?, qualification=?, course=? , duration=? " +
                    " WHERE studentID=?");
            pst.setString(1, student.getName());
            pst.setString(2, student.getPhone());
            pst.setString(3, student.getEmail());
            pst.setString(4, student.getAddress());
            pst.setString(5, student.getQua());
            pst.setString(6, student.getCourse());
            pst.setString(7, student.getCourseType());
            pst.setString(8, student.getId());

            return pst.execute();
        } catch (SQLException ex) {
            Logger.getLogger(CourseQueries.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    public static int countStudents() {
        int count = 0;
        try {
            pst = BillyDrivingSchoolFMIS.conn.prepareStatement("Select count(*) FROM students");

            rs = pst.executeQuery();
            rs.next();
            count = rs.getInt(1);
            return count;

        } catch (SQLException ex) {
            Logger.getLogger(CourseQueries.class.getName()).log(Level.SEVERE, null, ex);
        }

        return count;
    }

    public static int countStudentsInThisCourse(String code) {
        int count = 0;
        try {
            pst = BillyDrivingSchoolFMIS.conn.prepareStatement("Select count(*) FROM students " +
                    "where course=?");

            pst.setString(1, code);
            rs = pst.executeQuery();
            rs.next();
            count = rs.getInt(1);
            return count;

        } catch (SQLException ex) {
            Logger.getLogger(CourseQueries.class.getName()).log(Level.SEVERE, null, ex);
        }

        return count;
    }
}
