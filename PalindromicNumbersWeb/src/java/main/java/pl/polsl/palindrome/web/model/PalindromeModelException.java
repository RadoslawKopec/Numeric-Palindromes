/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.pl.polsl.palindrome.web.model;

/**
 * This method handles exceptions thrown
 *
 * @author Radosław Kopeć
 * @version 2.0
 */
public class PalindromeModelException extends Exception {

    /**
     * constructs new exception with specified message.
     *
     * @param message Specified message.
     */
    public PalindromeModelException(String message) {
        super(message);
    }

}
