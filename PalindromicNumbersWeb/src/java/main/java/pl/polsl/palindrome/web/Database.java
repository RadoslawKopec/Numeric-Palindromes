/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package main.java.pl.polsl.palindrome.web;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Implementation of the database communication.
 * @author Radosław Kopeć
 * @version 1.0
 */
public class Database implements AutoCloseable{
    /**
     * It is a database connection.
     */
    private final Connection conn;
    
    /**
     * Creates new Database object.
     * @param drv is the jdbc driver
     * @param connText is the jdbc connection string to the database
     * @throws ClassNotFoundException when driver was not found
     * @throws SQLException when there was a problem with database connection
     */
    public Database(String drv, String connText) throws ClassNotFoundException, SQLException {
        //strategical point of the app, driver needs to be found,
        //and the connection needs to be succesful
        Class.forName(drv);
        conn = DriverManager.getConnection(connText);
    }
    
    /**
     * Creates new statement for the database.
     * @return statement instance
     * @throws SQLException when the unexpected error occurs
     */
    public Statement createNewStatement() throws SQLException{
        return conn.createStatement();
    }

    /**
     * Closes the database connection.
     * @throws Exception when the SQLException occurs
     */
    @Override
    public void close() throws Exception {
        conn.close();
    }
}
