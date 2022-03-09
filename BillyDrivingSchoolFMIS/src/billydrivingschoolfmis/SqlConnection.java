/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billydrivingschoolfmis;;

/**
 *
 * @author ANOYMASS
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlConnection {
    public static Connection DbConnector(){
        try{
            Connection conn = null;
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:billyFMIS.sqlite");
            conn.createStatement().execute("PRAGMA foreign_keys = ON");
            //conn.createStatement().execute("PRAGMA primary_keys = ON");
            return conn;
        }catch(ClassNotFoundException | SQLException e){
            System.err.println(e);
            System.exit(0);
        }
        
        return null;
    }
}
