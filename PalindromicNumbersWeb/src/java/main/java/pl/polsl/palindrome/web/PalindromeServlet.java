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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import java.util.UUID;
import java.util.stream.Stream;
import javax.json.JsonObjectBuilder;
import javax.json.JsonStructure;
import javax.json.JsonWriter;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import main.java.pl.polsl.palindrome.web.model.PalindromeModel;
import main.java.pl.polsl.palindrome.web.model.PalindromeModelException;

/**
 * The main servlet to creates a result for the user request.
 * @author Radosław Kopeć
 * @version 1.0
 */
public class PalindromeServlet extends HttpServlet {

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
            PalindromeModel model = (PalindromeModel)request.getServletContext().getAttribute("PalindromeModel");
            Database db = (Database)request.getServletContext().getAttribute("Database");
            String lowerRange = request.getParameter("lowerRangeInput");
            String upperRange = request.getParameter("upperRangeInput");
            //execute request
            try {
                //insert or update new visitor
                String visitorGuid = incrementVisitorCount(response, db, request.getCookies());
                //checks for empty gields
                if(lowerRange == null || "".equals(lowerRange) ||  upperRange == null || "".equals(upperRange)){
                    JsonObject errorMessage = createJsonErrorMessage("All fields needs to be filled.");
                    out.writeObject(errorMessage);
                }
                //convert the string values to integers
                int fromRangeInt = Integer.parseInt(lowerRange);
                int upperRangeInt = Integer.parseInt(upperRange);
                //execute model method
                Map<Integer, Boolean> result = model.checksForPalindroms(fromRangeInt, upperRangeInt);
                //save this servlet execution
                insertHistoryEntry(db, visitorGuid, fromRangeInt, upperRangeInt, result);
                //build response
                int visitCount = this.readVisitorCountByUserGuid(db, visitorGuid);
                jsonResponse = createJsonResultMessage(result, visitCount);
            } catch (PalindromeModelException | NumberFormatException | SQLException ex) {
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
     * Creates the json result message.
     * @param visitCount is the count of visits
     * @param result is the map result
     * @return json representation of the result
     */
    private JsonObject createJsonResultMessage(Map<Integer, Boolean> result, int visitCount){
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder()
                .add("visitCount", visitCount);
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        result.entrySet().stream()
                .map(s -> Json.createObjectBuilder()
                            .add("value", s.getKey())
                            .add("isPalindrome", s.getValue()))
                .forEach(jsonArrayBuilder::add);
        jsonObjectBuilder.add("resultArray", jsonArrayBuilder);
        return jsonObjectBuilder.build();
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
     * Inserts the result of the model to the database as a history.
     * @param db is the database object
     * @param from is an user input arg
     * @param to is an user input arg
     * @param guid is the visitor guid
     * @param result is an result of the model
     * @throws SQLException if there will be error in inserting new entry
     */
    private void insertHistoryEntry(Database db, String guid, int from, int to, Map<Integer, Boolean> result) throws SQLException{
        int palindromesCount = 0, nonPalindromesCount = 0;
        for(Entry<Integer, Boolean> singleResult : result.entrySet()) {
            if(singleResult.getValue())
                palindromesCount++;
            else
                nonPalindromesCount++;
        }
        try(Statement statement = db.createNewStatement()){
            statement.executeUpdate("INSERT INTO History "
                    + "(FromRange, ToRange, PalindromesCount, NonPalindromesCount, VisitorGUID) " 
                    + "VALUES "
                    + "(" + from + "," + to + "," + palindromesCount + "," + nonPalindromesCount + ",'" + guid + "')");
        }
    }
    
    /**
     * Increments the visitor count by the user and updates the entry in the database.
     * @param response is a future response to the client
     * @param db is a database
     * @param cookies is a cookie collection
     * @return the identifier of the visitor
     * @throws SQLException if there will be error in inserting new entry
     */
    private String incrementVisitorCount(HttpServletResponse response, Database db, Cookie[] cookies) throws SQLException{
        final String cookieName = "VisitorGuid";
        Optional<String> guidCookie = cookies != null ? 
                Stream.of(cookies)
                .filter(s -> s.getName().equals(cookieName))
                .map(s -> s.getValue())
                .findFirst() : Optional.empty();
        try(Statement statement = db.createNewStatement()) {
            if(guidCookie.isPresent()){
                statement.executeUpdate("UPDATE Visitor "
                        + "SET VisitCount = VisitCount + 1 "
                        + "WHERE VisitorGUID = '" + guidCookie.get() + "'");
            } else {
                guidCookie = Optional.of(UUID.randomUUID().toString());
                response.addCookie(new Cookie(cookieName, guidCookie.get()));
                statement.executeUpdate("INSERT INTO Visitor "
                        + "(VisitorGUID, VisitCount) "
                        + "VALUES ('" + guidCookie.get() + "', 1)");
            }
        }
        return guidCookie.get();
    }
    
    /**
     * Reads the visitor count of the user which id is
     * stored as a cookie.
     * @param db is a database handler
     * @param guid is user identifier
     * @return visits count
     * @throws SQLException if connection with database will be lost 
     */
    private int readVisitorCountByUserGuid(Database db, String guid) throws SQLException{
        try(Statement statement = db.createNewStatement()) {
            ResultSet rs = statement.executeQuery("SELECT VisitCount FROM Visitor "
                    + "WHERE VisitorGUID = '" + guid + "'");
            rs.next();
            return rs.getInt("VisitCount");
        }
    }
}
