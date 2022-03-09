package billydrivingschoolfmis;

public class StudentCourse {
    private String course ;
    private int numberOfStudents;

    public StudentCourse(String course, int numberOfStudents) {
        this.course = course;
        this.numberOfStudents = numberOfStudents;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public int getNumberOfStudents() {
        return numberOfStudents;
    }

    public void setNumberOfStudents(int numberOfStudents) {
        this.numberOfStudents = numberOfStudents;
    }
}
