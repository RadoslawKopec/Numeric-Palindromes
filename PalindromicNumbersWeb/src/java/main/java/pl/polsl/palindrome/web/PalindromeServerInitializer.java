/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package main.java.pl.polsl.palindrome.web;

import java.sql.SQLException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import main.java.pl.polsl.palindrome.web.model.PalindromeModel;

/**
 * Class which implements an interface ServletContextListener
 * to listen for servlet starting/destroying.
 * @author Radosław Kopeć
 * @version 1.0
 */
public class PalindromeServerInitializer implements ServletContextListener {
    
    /**
     * Method that will be executed when the context will be initialized
     * and it creates model and database connection.
     * @param event comes from servlet with basic info about initialization 
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {
        //inits the required dependecies to proccess web requests
        ServletContext context = event.getServletContext();
        //initParameters -> where my parameters are stored
        String drv = context.getInitParameter("DbDriver");
        String connText = context.getInitParameter("DbConnText");
        //sets the model and database in the context container
        PalindromeModel model = new PalindromeModel();
        context.setAttribute("PalindromeModel", model);
        try {
            Database database = new Database(drv, connText);
            context.setAttribute("Database", database);
        } catch (ClassNotFoundException | SQLException ex) {
            //do nothing, because initializing the database is the must have feature
            System.err.println(ex.getMessage());
        }
    }

    /**
     * Method that will be executed when the context will be destroyed
     * and it has to close database connection
     * @param event comes from servlet with basic info about destroying 
     */
    @Override
    public void contextDestroyed(ServletContextEvent event) {
        Database database = (Database)event.getServletContext().getAttribute("Database");
        try {
            database.close();
        } catch (Exception ex) {
            System.err.println("Database connection closing error. # " + ex.getMessage());
        }
    }
}
