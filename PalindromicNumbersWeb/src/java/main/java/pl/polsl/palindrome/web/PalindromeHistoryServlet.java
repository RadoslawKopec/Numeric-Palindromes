/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.pl.polsl.palindrome.web;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Stream;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonStructure;
import javax.json.JsonWriter;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The servlet which is resposingle for returning a history of returned
 * palindromes.
 * @author Radosław Kopeć
 * @version 1.0
 */
public class PalindromeHistoryServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        JsonStructure jsonResponse = null; 
        try (JsonWriter out = Json.createWriter(response.getWriter())) {
            //gets the main references
            Database db = (Database)request.getServletContext().getAttribute("Database");
            //execute request
            try {
                //insert or update new visitor
                Cookie visitorGuid = getGuidFromCookie(request.getCookies());
                if(visitorGuid == null)
                    jsonResponse = createJsonErrorMessage("You didn't perform any operations yet.");
                else {
                    jsonResponse = createJsonResult(db, visitorGuid);
                }
            } catch (SQLException ex) {
                jsonResponse = createJsonErrorMessage(ex.getClass().getName() + ": " + ex.getMessage());
            } finally {
               out.write(jsonResponse);
            }
        }
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /**
     * Gets our session guid from cookies collection.
     * @param cookies from request
     * @return guid cookie
     */
    private Cookie getGuidFromCookie(Cookie[] cookies){
        if(cookies == null)
            return null;
        else {
            return Stream.of(cookies)
                    .filter(s -> "VisitorGuid".equals(s.getName()))
                    .findFirst()
                    .get();
        }
    }
    
    /**
     * Creates a error json message to the client.
     * @param message is an error message
     * @return json value
     */
    private JsonObject createJsonErrorMessage(String message){
        JsonObject value = Json.createObjectBuilder()
                    .add("isError", true)
                    .add("errorMessage", message)
                    .build();
        return value;
    }

    /**
     * Creates a json result from database content.
     * @param db is the database instance
     * @param visitorGuid is the visitor identity
     * @return json value
     * @throws SQLException when will be an issue with performing a query
     */
    private JsonStructure createJsonResult(Database db, Cookie visitorGuid) throws SQLException {
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        try(Statement statement = db.createNewStatement()){
            ResultSet rs = statement.executeQuery("SELECT * FROM History "
                    + "WHERE VisitorGUID = '" + visitorGuid.getValue() + "'");
            while(rs.next()){
                JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder()
                        .add("from", rs.getInt("FromRange"))
                        .add("to", rs.getInt("ToRange"))
                        .add("palindromesCount", rs.getInt("PalindromesCount"))
                        .add("nonPalindromesCount", rs.getInt("NonPalindromesCount"))
                        .add("id", rs.getInt("HistoryID"));
                jsonArrayBuilder.add(jsonObjectBuilder);
            }
        }
        return jsonArrayBuilder.build();
    }
}
