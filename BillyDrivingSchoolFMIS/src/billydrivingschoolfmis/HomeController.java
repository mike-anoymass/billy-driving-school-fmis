package billydrivingschoolfmis;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.TickLabelOrientation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HomeController implements Initializable {

    @FXML
    private ImageView pieChartImg;

    @FXML
    private VBox pieChartBox;

    @FXML
    private HBox gaugeHBox;

    @FXML
    private ImageView barchartImg;

    @FXML
    private HBox studentBox;

    @FXML
    private HBox coursesBox;

    @FXML
    private HBox courseTypeBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadGauges();
        createBarChart();
        loadPieChart();
    }

    private void loadPieChart() {
        DefaultPieDataset pieDataset = new DefaultPieDataset();
        pieDataset.setValue("Payments", getPayments());
        pieDataset.setValue("Receipts", getReceipts());

        JFreeChart pieChart = ChartFactory.createPieChart3D("Comparing Payments and Receipts for this month ",
                pieDataset, true, true, false);

        try {
            ChartUtilities.saveChartAsJPEG(new File(
                            FileSystemView.getFileSystemView().getDefaultDirectory().getPath() +"\\pieChart.jpg"),
                    pieChart, 700, 350
            );
            pieChartImg.setImage(new Image("file:"+
                    FileSystemView.getFileSystemView().getDefaultDirectory().getPath() +"\\pieChart.jpg")
            );

        } catch (IOException ex) {
            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private double getReceipts() {
        double totalReceipts = 0.0;
        LocalDate curr = LocalDate.now();
        ObservableList<Receipts> receiptData = new LoadData().loadReceipts();
        ObservableList<Receipts> receiptsForThisMonth = FXCollections.observableArrayList();

        for(Receipts receipt: receiptData){

            if(receipt.getDate().startsWith(curr.toString().split("-")[0]) &
                    receipt.getDate().split("-")[1].equals(curr.toString().split("-")[1]) ){

                receiptsForThisMonth.add(receipt);
            }
        }

        if(receiptsForThisMonth.size() > 0){
            totalReceipts = 0.0;
            for(int i = 0; i < receiptsForThisMonth.size(); i++){
                totalReceipts += Double.parseDouble(receiptsForThisMonth.get(i).getAmount());
            }

            return totalReceipts;
        }

        return 0.0;
    }


    private double getPayments() {
        double totalPayments = 0.0;
        LocalDate curr = LocalDate.now();
        ObservableList<Payment> paymentData = new LoadData().loadPayments();
        ObservableList<Payment> paymentsForThisMonth = FXCollections.observableArrayList();

        for(Payment payment: paymentData){

            if(payment.getDate().startsWith(curr.toString().split("-")[0]) &
                    payment.getDate().split("-")[1].equals(curr.toString().split("-")[1]) ){

                paymentsForThisMonth.add(payment);
            }
        }

        if(paymentsForThisMonth.size() > 0){
            for(int i = 0; i < paymentsForThisMonth.size(); i++){
                totalPayments += Double.parseDouble(paymentsForThisMonth.get(i).getAmount());
            }

            return totalPayments;
        }

        return 0.0;
    }

    private void createBarChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        ObservableList<StudentCourse> sc = loadCourseStudentsData();

        if(sc.size() > 0){
            for(StudentCourse s : sc){
                dataset.setValue(s.getNumberOfStudents(), "Students", s.getCourse());
            }
        }

        JFreeChart chart = ChartFactory.createBarChart3D("Courses and Number of Students",
                "Course", "Students", dataset, PlotOrientation.VERTICAL,
                true, true, false);
        try {
            ChartUtilities.saveChartAsJPEG(new File(
                            FileSystemView.getFileSystemView().getDefaultDirectory().getPath() +"\\barchart.jpg"),
                    chart, 900, 300
            );

            barchartImg.setImage(new Image("file:"+
                    FileSystemView.getFileSystemView().getDefaultDirectory().getPath() +"\\barchart.jpg")
            );


        } catch (IOException e) {
            System.err.println("Problem occurred creating chart." + e);

        }
    }

    private ObservableList<StudentCourse> loadCourseStudentsData() {
        ObservableList<StudentCourse> sc = FXCollections.observableArrayList();
        ObservableList<Course> courses = FXCollections.observableArrayList();
        courses = new LoadData().loadCourseTableData();
        int studentCount = 0;

        if(courses.size() > 0){
            for(Course c : courses){
                studentCount = StudentQueries.countStudentsInThisCourse(c.getCode());
                sc.add(new StudentCourse(c.getCode(), studentCount));

                studentCount = 0;
            }
        }

        return sc;
    }

    private void loadGauges() {
        studentBox.getChildren().add(makeGauge("Students", StudentQueries.countStudents()));
        coursesBox.getChildren().add(makeGauge("Courses" , new CourseQueries().countCourses()));
        courseTypeBox.getChildren().add(makeGauge("Course Types", new CourseQueries().countCourseTypes()));

    }

    private Gauge makeGauge(String title, int value){
        Gauge gauge = new Gauge();
        gauge.setSkinType(Gauge.SkinType.SIMPLE);
        gauge.setTitle(title);  //title
        gauge.setTitleColor(Color.WHITE);
        gauge.setUnit("");  //unit
        gauge.setUnitColor(Color.GREY);
        gauge.setDecimals(0);
        gauge.setValue(0); //deafult position of needle on gauage
        gauge.setAnimated(true);
        gauge.setAnimationDuration(5000);
        gauge.setValueColor(Color.WHITE);
        gauge.setSubTitleColor(Color.WHITE);
        gauge.setBarColor(Color.BLACK);
        gauge.setPrefSize(200, 150);
        gauge.setNeedleColor(Color.GREY);
        gauge.setNeedleBorderColor(Color.WHITE);
        gauge.setNeedleShape(Gauge.NeedleShape.ROUND);
        gauge.setThresholdColor(Color.RED);
        //gauge.setThreshold(thr);
        gauge.setThresholdVisible(true);
        gauge.setTickLabelColor(Color.rgb(151, 151, 151));
        gauge.setTickMarkColor(Color.WHITE);
        gauge.setTickLabelOrientation(TickLabelOrientation.TANGENT);
        gauge.setBorderPaint(Paint.valueOf("grey"));
        gauge.setForegroundPaint(Paint.valueOf("white"));
        gauge.setForegroundBaseColor(Color.DARKGRAY);
        gauge.setBarBackgroundColor(Color.WHITE);
        gauge.setBackgroundPaint(Paint.valueOf("white"));
        gauge.setSmoothing(true);
        gauge.setLedColor(Color.WHITE);
        gauge.setValue(value);
        gauge.setValueColor(Color.WHITE);

        return gauge;
    }
}
