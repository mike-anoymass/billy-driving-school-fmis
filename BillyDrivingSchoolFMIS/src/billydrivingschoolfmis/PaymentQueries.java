package billydrivingschoolfmis;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PaymentQueries {
    static ResultSet rs;
    static PreparedStatement pst;

    public static ResultSet getAllExpenses() {
        try {
            pst = BillyDrivingSchoolFMIS.conn.prepareStatement("SELECT * FROM expenses ORDER BY rowid DESC");
            rs = pst.executeQuery();

            return rs;
        } catch (SQLException ex) {
            Logger.getLogger(CourseQueries.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rs;
    }

    public static ResultSet getAllPayments() {
        try {
            pst = BillyDrivingSchoolFMIS.conn.prepareStatement(
                    "SELECT * FROM payments ORDER BY expense, date DESC");
            rs = pst.executeQuery();

            return rs;
        } catch (SQLException ex) {
            Logger.getLogger(CourseQueries.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rs;
    }

    public static ResultSet getPayment(String id) {
        try {
            pst = BillyDrivingSchoolFMIS.conn.prepareStatement("SELECT * FROM payments where id=?");
            pst.setString(1, id);
            rs = pst.executeQuery();

            return rs;
        } catch (SQLException ex) {
            Logger.getLogger(CourseQueries.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rs;
    }

    public static boolean addPayment(Payment p) {
        try {
            pst = BillyDrivingSchoolFMIS.conn.prepareStatement(
                    "INSERT INTO payments (id, expense, date, amount, mode," +
                            " user , ref) "
                            + "VALUES (?, ?, datetime('now'), ?, ?, ? ,?)");
            pst.setString(1, p.getId());
            pst.setString(2, p.getExpense());
            pst.setString(3, p.getAmount());
            pst.setString(4, p.getMode());
            pst.setString(5, p.getUser());
            pst.setString(6, p.getRef());

            return pst.execute();
        } catch (SQLException ex) {
            Logger.getLogger(CourseQueries.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    public static ResultSet getExpense(String id) {
        try {
            pst = BillyDrivingSchoolFMIS.conn.prepareStatement("SELECT * FROM Expenses where name=?");
            pst.setString(1, id);
            rs = pst.executeQuery();

            return rs;
        } catch (SQLException ex) {
            Logger.getLogger(CourseQueries.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rs;
    }

    public static boolean addExpense(Expense e) {
        try {
            pst = BillyDrivingSchoolFMIS.conn.prepareStatement(
                    "INSERT INTO Expenses (name, date) "
                            + "VALUES (?,  datetime('now'))");
            pst.setString(1, e.getName());

            return pst.execute();
        } catch (SQLException ex) {
            Logger.getLogger(CourseQueries.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    public static int deleteExpense(String name) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Please Confirm Deletion");
        //alert.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.REMOVE, "20px"));
        alert.setHeaderText(null);
        alert.setContentText("Are you sure; You wanna delete this fees Expense ?");
        Optional<ButtonType> action = alert.showAndWait();

        if (action.get() == ButtonType.OK) {
            try {
                pst = BillyDrivingSchoolFMIS.conn.prepareStatement("DELETE FROM Expenses WHERE name=?");
                pst.setString(1, name);
                return pst.executeUpdate();

            } catch (SQLException ex) {
                Logger.getLogger(CourseQueries.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 404;
    }

    public static int deletePayment(String id) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Please Confirm Deletion");
        //alert.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.REMOVE, "20px"));
        alert.setHeaderText(null);
        alert.setContentText("Are you sure; You wanna delete this fees Payment ?");
        Optional<ButtonType> action = alert.showAndWait();

        if (action.get() == ButtonType.OK) {
            try {
                pst = BillyDrivingSchoolFMIS.conn.prepareStatement("DELETE FROM payments WHERE id=?");
                pst.setString(1, id);
                return pst.executeUpdate();

            } catch (SQLException ex) {
                Logger.getLogger(CourseQueries.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 404;
    }

    public boolean editPayment(Payment p) {
        try {
            pst = BillyDrivingSchoolFMIS.conn.prepareStatement(
                    "UPDATE payments " +
                            "SET expense=?, amount=?, mode=?, ref=? " +
                    "WHERE id=?");
            pst.setString(1, p.getExpense());
            pst.setString(2, p.getAmount());
            pst.setString(3, p.getMode());
            pst.setString(4, p.getRef());
            pst.setString(5, p.getId());

            return pst.execute();
        } catch (SQLException ex) {
            Logger.getLogger(CourseQueries.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
}
