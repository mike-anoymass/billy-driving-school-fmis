package billydrivingschoolfmis;

import javafx.scene.control.CheckBox;

public class Receipts {
    String id;
    String date;
    String amount;
    String mop;
    String bpo;
    String user;
    String ref;
    private CheckBox checkBox = new CheckBox();
    private String fullName;

    public Receipts(String id, String date, String amount, String mop, String bpo, String user, String ref, String fullName) {
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.mop = mop;
        this.bpo = bpo;
        this.user = user;
        this.ref = ref;
        this.fullName = fullName;
    }

    public void setId(String id) {
        this.id = id;
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

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }

    public String getFullname() {
        return fullName;
    }

    public void setFullname(String fullName) {
        this.fullName = fullName;
    }
}
