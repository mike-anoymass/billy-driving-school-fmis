package billydrivingschoolfmis;

import javafx.scene.control.CheckBox;

public class Receipt {
    String id;
    String studentID;
    String date;
    String amount;
    String mop;
    String bpo;
    String user;
    String ref;

    public Receipt(String id, String studentID, String date,
                   String amount, String mop, String bpo, String user, String ref) {
        this.id = id;
        this.studentID = studentID;
        this.date = date;
        this.amount = amount;
        this.mop = mop;
        this.bpo = bpo;
        this.user = user;
        this.ref = ref;
    }





    public void setId(String id) {
        this.id = id;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setMop(String mop) {
        this.mop = mop;
    }

    public void setBpo(String bpo) {
        this.bpo = bpo;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getId() {
        return id;
    }

    public String getStudentID() {
        return studentID;
    }

    public String getDate() {
        return date;
    }

    public String getAmount() {
        return amount;
    }

    public String getMop() {
        return mop;
    }

    public String getBpo() {
        return bpo;
    }

    public String getUser() {
        return user;
    }

    public String getRef() {
        return ref;
    }
}
