package com.model.dice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**Enum class representing the colors that a die can have
 */
public enum DiceColor {
    /**Possible color values
     */
    NEUTRAL('N'), RED('R'), YELLOW('Y'), GREEN('G'), BLUE('B'), PURPLE('P');

    /**Abbreviation for the enum
     */
    private char abbreviation;

    /**List of all the possible abbreviations
     */
    private static final List<Character> listOfAbbreviation = Arrays.asList('N', 'R', 'Y', 'G', 'B', 'P');

    /**Constructor for the DiceColor
     * @param abbreviation char used to get the desired color
     */
    DiceColor(char abbreviation) {
        this.abbreviation = abbreviation;
    }

    /**Returns the corresponding abbreviation to the color
     * @return the character corresponding to the color associated
     */
    public char getAbbreviation() {
        return abbreviation;
    }

    /**Getter for the list of abbreviation
     * @return a list of characters of all the possible abbreviations
     */
    public static List<Character> getListOfAbbreviation() {
        return new ArrayList<>(listOfAbbreviation);
    }
}