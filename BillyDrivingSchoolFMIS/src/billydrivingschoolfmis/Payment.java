package billydrivingschoolfmis;

public class Payment {
    private String id;
    private String expense;
    private String date;
    private String amount;
    private String mode;
    private String ref;
    private String user;

    public Payment(String id, String expense, String date, String amount, String mode, String ref, String user) {
        this.id = id;
        this.expense = expense;
        this.date = date;
        this.amount = amount;
        this.mode = mode;
        this.ref = ref;
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExpense() {
        return expense;
    }

    public void setExpense(String expense) {
        this.expense = expense;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
