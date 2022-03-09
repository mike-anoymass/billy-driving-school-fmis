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

import javafx.beans.property.*;

public class UserCredentials {
    
    private final static String PCUSERNAME_PROP_NAME = "pcusermame";
    private final ReadOnlyStringWrapper pcUsername;
    private final static String USERNAME_PROP_NAME = "usermame";
    private final ReadOnlyStringWrapper username;
    
    private final static String PASSWORD_PROP_NAME = "password";
    private StringProperty password;
    
    public UserCredentials(){
        pcUsername = new ReadOnlyStringWrapper(this, PCUSERNAME_PROP_NAME ,"Welcome "+ System.getProperty("user.name"));
        
        username = new ReadOnlyStringWrapper(this, USERNAME_PROP_NAME , "");
       
        password = new SimpleStringProperty(this,PASSWORD_PROP_NAME, "" );

    }
    
    public final String getUserName(){
        return username.get();
    }
    
    public StringProperty userNameProperty(){
        return username;
    }
    
    public final String getPcUserName(){
        return pcUsername.get();
    }
    
    public ReadOnlyStringProperty userPcNameProperty(){
        return pcUsername.getReadOnlyProperty();
    }
    
    public final String getPassword(){
        return password.get();
    }
    
    public final void setPassword(String password ){
        this.password.set(password);
        
    }
    
    public StringProperty passwordProperty(){
        return password;
    }
    
}
