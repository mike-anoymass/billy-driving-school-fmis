package billydrivingschoolfmis;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FeesCalculationsClass {
    double totalPaid , balance, totalFees;
    ResultSet rs;
    ObservableList<Double> feesResults = FXCollections.observableArrayList();

    public ObservableList<Double> FeesCalculations(String studentID, String course, String duration){

        totalPaid = balance = totalFees= 0.0;

        try {
            rs = new CourseQueries().getCourseInfo(course, duration);
            while (rs.next()){
                totalFees = Double.parseDouble(rs.getString("fees"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        try{
            rs = ReceiptQueries.getReceiptsFor(studentID);

            while(rs.next()){
                totalPaid += Double.parseDouble(rs.getString("amount"));
            }

        }catch(SQLException ex){
            System.err.println(ex);
        }

        balance = totalFees - totalPaid;

        feesResults.addAll(totalPaid , balance, totalFees);

        return feesResults;
    }
}
