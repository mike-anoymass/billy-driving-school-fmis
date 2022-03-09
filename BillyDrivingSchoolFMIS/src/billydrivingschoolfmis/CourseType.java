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
public class CourseType {
    private String name;
    private String days;
    
    public CourseType(String days, String name){
        this.days = days;
        this.name = name;


    }
    
    public void setDays(String days){
        this.days = days;

        
    }
    
    public void setName(String name){
        this.name = name;
    }


    
    public String getDays(){
        return days;
    }
    
    public String getName(){
        return name;
    }


}
