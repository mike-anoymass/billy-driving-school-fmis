/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billydrivingschoolfmis;

import javafx.scene.control.CheckBox;

/**
 *
 * @author ANOYMASS
 */
public class Student {
    private String id;
    private String name;
    private String address;
    private String phone;
    private String email;
    private String qua;
    private String course;
    private String courseType;
    private String date;
    private boolean selected;
    private CheckBox checkBox = new CheckBox();

    public Student(String id, String name, String address, String phone, String email, String qua, String course, String courseType, String date) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.qua = qua;
        this.course = course;
        this.courseType = courseType;
        this.date = date;
        this.address = address;
    }

    public Student() {
    }


    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

        public void setQua(String qua) {
        this.qua = qua;
    }

    public String getQua() {
        return qua;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getCourse() {
        return course;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public String getCourseType() {
        return courseType;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }
    
    
}
