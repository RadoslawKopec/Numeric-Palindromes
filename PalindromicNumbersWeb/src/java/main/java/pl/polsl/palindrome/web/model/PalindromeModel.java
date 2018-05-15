/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.pl.polsl.palindrome.web.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Makes a calculation and checks the values in the correctness context.
 *
 * @author Radosław Kopeć
 * @version 3.0
 *
 */
public class PalindromeModel {
    
    /**
     * Gives the report which value from specific range is palindrom.
     * @param from is the start value of range
     * @param to is the end value of range
     * @return dictionary with report if this value is palindrom
     * @throws PalindromeModelException when the given range is invalid
     */
    public Map<Integer, Boolean> checksForPalindroms(int from, int to) throws PalindromeModelException{
        if(from > to)
            throw new PalindromeModelException("Invalid range.");
        //creates a hash map to store results
        HashMap<Integer, Boolean> report = new HashMap<>();
        for(int i = from; i <= to; i++){
            boolean isPalindrom = checkForPalindrome(i);
            report.put(i, isPalindrom);
        }
        return report;
    }

    /**
     * This method checks if the number is a Palindrome or not and return
     * answer.
     *
     * @param actNumber Currently calculated number.
     * @return type boolean says is that palindrome or no.
     */
    private boolean checkForPalindrome(int actNumber) {
        int actNumberButInsideAndThisIsAWorker, lastNumber, reverse; //Some variables what we will use  
        actNumberButInsideAndThisIsAWorker = actNumber;
        reverse = 0;

        while (actNumberButInsideAndThisIsAWorker > 0) {
            lastNumber = actNumberButInsideAndThisIsAWorker % 10;// cut the last number and give it do reverse   
            actNumberButInsideAndThisIsAWorker /= 10;
            reverse = reverse * 10 + lastNumber;   // building a rotated number
        }

        return reverse == actNumber; // If reverse is the same like ActNumberButInside - this is a palindrome
    }
}
