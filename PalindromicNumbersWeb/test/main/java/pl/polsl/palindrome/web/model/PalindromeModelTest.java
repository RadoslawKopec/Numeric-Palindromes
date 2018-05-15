package main.java.pl.polsl.palindrome.server.model;

import main.java.pl.polsl.palindrome.web.model.PalindromeModelException;
import main.java.pl.polsl.palindrome.web.model.PalindromeModel;
import java.util.Map;
import org.junit.Test;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * This class checks the correct functioning of the model methods. First program
 will test "checkForPalindrome" method with valid and invalid value. After
 this test, program will be force to throw the exception after method get the
 correct value.
 *
 *
 * @author Radosław Kopeć
 * @version 3.0
 */
public class PalindromeModelTest {
    private PalindromeModel target;

    /**
     * Executed before each test.
     * It will initialize a model.
     */
    @Before
    public void initModel(){
        target = new PalindromeModel();
    }
    
    /**
     * Tests the checksForPalindroms method for throwing
     * an exception if the given range is invalid.
     */
    @Test
    public void testChecksForPalindromsThrowsExceptionIfInvalidRange(){
        final int from = 10;
        final int to = 1;
        try {
            target.checksForPalindroms(from, to);
            fail("Should throw an exception if the range is invalid.");
        } catch (PalindromeModelException ignored) { }
    }
    
    /**
     * Tests the checksForPalindroms method for returning
     * map with one entry if the from value is the same as to.
     */
    @Test
    public void testChecksForPalindromsShouldReturnSingleValueIfFromAndToIsEqual(){
        final int fromAndTo = 1;
        try {
            Map<Integer, Boolean> result = target.checksForPalindroms(fromAndTo, fromAndTo);
            assertEquals("Should return one element collection of map", 1, result.size());
        } catch (PalindromeModelException ex) {
            fail("The test method should never be here");
        }
    }
    
    /**
     * Tests the checksForPalindroms for returning a success result
     * for all single digits.
     */
    @Test
    public void testChecksForPalindromsShouldReturnTrueForSingleDigits(){
        final int from = 0;
        final int to = 9;
        try {
            Map<Integer, Boolean> result = target.checksForPalindroms(from, to);
            boolean allValuesArePalindroms = result.entrySet()
                    .stream().allMatch(s -> s.getValue());
            assertTrue("All values with in map should be palindroms", allValuesArePalindroms);
        } catch (PalindromeModelException ex) {
            fail("The test method should never be here");
        }
    }
    
    /**
     * Tests the checksForPalindroms for returning a correct true result.
     */
    @Test
    public void testChecksForPalindromsShouldReturnTrueForPalindrom(){
        final int from = 171;
        final int to = 171;
        try {
            Map<Integer, Boolean> result = target.checksForPalindroms(from, to);
            boolean isPalindrom = result.get(from);
            assertTrue("For 171 value the model should return true", isPalindrom);
        } catch (PalindromeModelException ex) {
            fail("The test method should never be here");
        }
    }
    
    /**
     * Tests the checksForPalindroms for returning a correct false result.
     */
    @Test
    public void testChecksForPalindromsShouldReturnFalseForNonPalindrom(){
        final int from = 96958;
        final int to = 96958;
        try {
            Map<Integer, Boolean> result = target.checksForPalindroms(from, to);
            boolean isPalindrom = result.get(from);
            assertFalse("For 96958 value the model should return false", isPalindrom);
        } catch (PalindromeModelException ex) {
            fail("The test method should never be here");
        }
    }
}
