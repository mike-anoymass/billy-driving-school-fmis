/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billydrivingschoolfmis;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static billydrivingschoolfmis.AlertClass.makeAlert;

/**
 *
 * @author ANOYMASS
 */
public class UserQueries {

    private static PreparedStatement pst;
    private static ResultSet rs;

    public static ResultSet getUsers(){
        try {
            pst = BillyDrivingSchoolFMIS.conn.prepareStatement("Select * from users order by rowid DESC");
            rs = pst.executeQuery();
            return rs;

        } catch (SQLException ex) {
            Logger.getLogger(UserQueries.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rs;
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

    public static ResultSet getUser(String username) {
        try {
            pst = BillyDrivingSchoolFMIS.conn.prepareStatement("SELECT * FROM users where username=?");
            pst.setString(1, username);
            rs = pst.executeQuery();
            return rs;

        } catch (SQLException ex) {
            Logger.getLogger(UserQueries.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rs;
    }

    public static boolean addUser(User user, File file) {
        try {
            pst = BillyDrivingSchoolFMIS.conn.prepareStatement(
                    "INSERT INTO users (username, firstname , lastname, phone, email, type, password, image ,date) "
                            + "VALUES (?, ?, ?, ?, ?, ? ,? , ? , datetime('now'))"
            );
            pst.setString(1, user.getUsername());
            pst.setString(2, user.getFirstName());
            pst.setString(3, user.getLastName());
            pst.setString(4, user.getPhone());
            pst.setString(5, user.getEmail());
            pst.setString(6, user.getUsertype());
            pst.setString(7, user.getPassword());

            if (file != null) {
                try {
                    FileInputStream fis;
                    fis = new FileInputStream(file);
                    pst.setBinaryStream(8, fis, (int) file.length());

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
            return pst.execute();
        } catch (SQLException ex) {
            Logger.getLogger(CourseQueries.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    public static int countUsers() {
        int count = 0;
        try {
            pst = BillyDrivingSchoolFMIS.conn.prepareStatement("Select count(*) FROM users");

            rs = pst.executeQuery();
            rs.next();
            count = rs.getInt(1);
            return count;

        } catch (SQLException ex) {
            Logger.getLogger(CourseQueries.class.getName()).log(Level.SEVERE, null, ex);
        }

        return count;
    }

    public static boolean editUser(User user, File file) {
        try {
            pst = BillyDrivingSchoolFMIS.conn.prepareStatement("UPDATE users " +
                    "SET firstname=?, lastname=?, email=?, phone=?, type=?, password=?, image=? " +
                    " WHERE username=?");

            pst.setString(8, user.getUsername());
            pst.setString(1, user.getFirstName());
            pst.setString(2, user.getLastName());
            pst.setString(4, user.getPhone());
            pst.setString(3, user.getEmail());
            pst.setString(5, user.getUsertype());
            pst.setString(6, user.getPassword());

            if (file != null) {
                try {
                    FileInputStream fis;
                    fis = new FileInputStream(file);
                    pst.setBinaryStream(7, fis, (int) file.length());

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }

            return pst.execute();
        } catch (SQLException ex) {
            Logger.getLogger(CourseQueries.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    public static int deleteUser(String username) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Please Confirm Deletion");
        //alert.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.REMOVE, "20px"));
        alert.setHeaderText(null);
        alert.setContentText("Are you sure; You wanna delete this User ?");
        Optional<ButtonType> action = alert.showAndWait();

        if (action.get() == ButtonType.OK) {
            try {
                pst = BillyDrivingSchoolFMIS.conn.prepareStatement("DELETE FROM users WHERE username=?");
                pst.setString(1, username);
                return pst.executeUpdate();

            } catch (SQLException ex) {
                Logger.getLogger(CourseQueries.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 404;
    }
}
