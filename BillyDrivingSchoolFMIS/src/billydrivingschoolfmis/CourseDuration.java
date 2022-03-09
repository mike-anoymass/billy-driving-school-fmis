/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billydrivingschoolfmis;

/**
 *
 * @author ANOYMASS
 */

public class CourseDuration {
    private String code;
    private String duration;
    private String amount;
    private String date;

    public CourseDuration(String code, String duration, String amount, String date) {
        this.code = code;
        this.duration = duration;
        this.amount = amount;
        this.date = date;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }
    
    

    public void setCode(String code) {
        this.code = code;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getAmount() {
        return amount;
    }

    public String getCode() {
        return code;
    }

    public String getDuration() {
        return duration;
    }
   
}
