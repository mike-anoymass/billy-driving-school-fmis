/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billydrivingschoolfmis;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.util.Duration;

/**
 *
 * @author ANOYMASS
 */
public class CourseQueries {

    private PreparedStatement pst;
    private ResultSet rs;

    public ResultSet getAllCourses() {
        try {
            pst = BillyDrivingSchoolFMIS.conn.prepareStatement("SELECT * FROM courses ORDER BY rowid DESC");
            rs = pst.executeQuery();

            return rs;
        } catch (SQLException ex) {
            Logger.getLogger(CourseQueries.class.getName()).log(Level.SEVERE, null, ex);
        }

        return rs;
    }

    public ResultSet getCourse(String courseCode) {
        try {
            pst = BillyDrivingSchoolFMIS.conn.prepareStatement("SELECT * FROM courses where code=?");
            pst.setString(1, courseCode);
            rs = pst.executeQuery();

            return rs;
        } catch (SQLException ex) {
            Logger.getLogger(CourseQueries.class.getName()).log(Level.SEVERE, null, ex);
        }

        return rs;
    }

    boolean addCourse(Course course) {
        try {
            pst = BillyDrivingSchoolFMIS.conn.prepareStatement("INSERT INTO courses (code, name) VALUES (?, ?)");
            pst.setString(1, course.getCode());
            pst.setString(2, course.getName());

            return pst.execute();
        } catch (SQLException ex) {
            Logger.getLogger(CourseQueries.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    public int deleteCourse(String text) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Please Confirm Deletion");
       // alert.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.RemoVe, "20px"));
        alert.setHeaderText(null);
        alert.setContentText("Are you sure; wanna delete, " + text);
        Optional<ButtonType> action = alert.showAndWait();

        if (action.get() == ButtonType.OK) {
            try {
                pst = BillyDrivingSchoolFMIS.conn.prepareStatement("DELETE FROM courses WHERE code=?");
                pst.setString(1, text);

                return pst.executeUpdate();

            } catch (SQLException ex) {
                Logger.getLogger(CourseQueries.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 404;
    }

    boolean editCourse(Course course) {
        try {
            pst = BillyDrivingSchoolFMIS.conn.prepareStatement("UPDATE courses SET name=? WHERE code=?");
            pst.setString(1, course.getName());
            pst.setString(2, course.getCode());

            return pst.execute();
        } catch (SQLException ex) {
            Logger.getLogger(CourseQueries.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    ResultSet getAllCourseTypes() {
        try {
            pst = BillyDrivingSchoolFMIS.conn.prepareStatement("SELECT * FROM durations ORDER BY rowid DESC");
            rs = pst.executeQuery();

            return rs;
        } catch (SQLException ex) {
            Logger.getLogger(CourseQueries.class.getName()).log(Level.SEVERE, null, ex);
        }

        return rs;
    }

    ResultSet getCourseType(String name) {
        try {
            pst = BillyDrivingSchoolFMIS.conn.prepareStatement("SELECT * FROM durations where name=?");
            pst.setString(1, name);
            rs = pst.executeQuery();

            return rs;
        } catch (SQLException ex) {
            Logger.getLogger(CourseQueries.class.getName()).log(Level.SEVERE, null, ex);
        }

        return rs;
    }

    boolean addCourseType(CourseType cType) {
        try {
            pst = BillyDrivingSchoolFMIS.conn.prepareStatement("INSERT INTO durations (name, days) VALUES (?, ?)");
            pst.setString(1, cType.getName());
            pst.setString(2, cType.getDays());

            return pst.execute();
        } catch (SQLException ex) {
            Logger.getLogger(CourseQueries.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    boolean editCourseType(CourseType cType) {
        try {
            pst = BillyDrivingSchoolFMIS.conn.prepareStatement("UPDATE durations SET days=? WHERE name=?");
            pst.setString(1, cType.getDays());
            pst.setString(2, cType.getName());

            return pst.execute();
        } catch (SQLException ex) {
            Logger.getLogger(CourseQueries.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    int deleteCourseType(String text) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Please Confirm Deletion");
        //alert.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon., "20px"));
        alert.setHeaderText(null);
        alert.setContentText("Are you sure; wanna delete, " + text);
        Optional<ButtonType> action = alert.showAndWait();

        if (action.get() == ButtonType.OK) {
            try {
                pst = BillyDrivingSchoolFMIS.conn.prepareStatement("DELETE FROM durations WHERE name=?");
                pst.setString(1, text);

                return pst.executeUpdate();

            } catch (SQLException ex) {
                Logger.getLogger(CourseQueries.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 404;
    }

    ResultSet getAllCourseDurations() {
        try {
            pst = BillyDrivingSchoolFMIS.conn.prepareStatement("SELECT courseID , duration, "
                    + "name, fees, date "
                    + "FROM courseDuration INNER JOIN courses ON courseDuration.courseID=courses.code "
                    + "ORDER BY courseID, date");
            rs = pst.executeQuery();

            return rs;
        } catch (SQLException ex) {
            Logger.getLogger(CourseQueries.class.getName()).log(Level.SEVERE, null, ex);
        }

        return rs;
    }

    ResultSet getCourseInfo(String courseID, String durationName) {
        try {
            pst = BillyDrivingSchoolFMIS.conn.prepareStatement("SELECT * FROM courseDuration where courseID=? and duration=?");
            pst.setString(1, courseID);
            pst.setString(2, durationName);
            rs = pst.executeQuery();

            return rs;
        } catch (SQLException ex) {
            Logger.getLogger(CourseQueries.class.getName()).log(Level.SEVERE, null, ex);
        }

        return rs;
    }

    boolean addCourseInfo(CourseDuration info) {
        
        try {
            pst = BillyDrivingSchoolFMIS.conn.prepareStatement("INSERT INTO courseDuration (courseID, duration, fees, date) VALUES (?, ?, ?, datetime('now'))");
            pst.setString(1, info.getCode());
            pst.setString(2, info.getDuration());
            pst.setString(3, info.getAmount());

            return pst.execute();
        } catch (SQLException ex) {
            Logger.getLogger(CourseQueries.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    int deleteCourseInfo(String code, String duration) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Please Confirm Deletion");
        //alert.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.REMOVE, "20px"));
        alert.setHeaderText(null);
        alert.setContentText("Are you sure; wanna delete, " + code + " " + duration);
        Optional<ButtonType> action = alert.showAndWait();

        if (action.get() == ButtonType.OK) {
            try {
                pst = BillyDrivingSchoolFMIS.conn.prepareStatement("DELETE FROM courseDuration WHERE courseID=? and duration=?");
                pst.setString(1, code);
                pst.setString(2, duration);

                return pst.executeUpdate();

            } catch (SQLException ex) {
                Logger.getLogger(CourseQueries.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 404;
    }

    public int countCourses() {
        int count = 0;
        try {
            pst = BillyDrivingSchoolFMIS.conn.prepareStatement("Select count(*) FROM courses");

            rs = pst.executeQuery();
            rs.next();
            count = rs.getInt(1);
            return count;

        } catch (SQLException ex) {
            Logger.getLogger(CourseQueries.class.getName()).log(Level.SEVERE, null, ex);
        }

        return count;
    }

    public int countCourseTypes() {
        int count = 0;
        try {
            pst = BillyDrivingSchoolFMIS.conn.prepareStatement("Select count(*) FROM durations");

            rs = pst.executeQuery();
            rs.next();
            count = rs.getInt(1);
            return count;

        } catch (SQLException ex) {
            Logger.getLogger(CourseQueries.class.getName()).log(Level.SEVERE, null, ex);
        }

        return count;
    }

    public int countCourseInfo() {
        int count = 0;
        try {
            pst = BillyDrivingSchoolFMIS.conn.prepareStatement("Select count(*) FROM courseDuration");

            rs = pst.executeQuery();
            rs.next();
            count = rs.getInt(1);
            return count;

        } catch (SQLException ex) {
            Logger.getLogger(CourseQueries.class.getName()).log(Level.SEVERE, null, ex);
        }

        return count;
    }

    ResultSet getDurationsForThisCourse(String courseID) {
         
        try {
            pst = BillyDrivingSchoolFMIS.conn.prepareStatement("SELECT duration FROM courseDuration where courseID=?");
            pst.setString(1, courseID);
            rs = pst.executeQuery();

            return rs;
        } catch (SQLException ex) {
            Logger.getLogger(CourseQueries.class.getName()).log(Level.SEVERE, null, ex);
        }

        return rs;
    
    }

    public boolean editCourseInfo(CourseDuration cd) {
        try {
            pst = BillyDrivingSchoolFMIS.conn.prepareStatement("UPDATE courseDuration SET fees=? " +
                    "WHERE courseID=? and duration=?");
            pst.setString(1, cd.getAmount());
            pst.setString(2, cd.getCode());
            pst.setString(3, cd.getDuration());

            return pst.execute();
        } catch (SQLException ex) {
            Logger.getLogger(CourseQueries.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
}
