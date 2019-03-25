package com.model.dice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**Enum class for the possible shade value of a die
 */
public enum DiceShade {
    /**Possible shade value
     */
    NEUTRAL('0'), ONE('1'), TWO('2'), THREE('3'), FOUR('4'), FIVE('5'), SIX('6');

    /**Character abbreviation for the shade value*/
    private char abbreviation;

    /**list of all usable abbreviation
     */
    private static final List<Character> listOfAbbreviation = Arrays.asList('0', '1', '2', '3', '4', '5', '6');

    /**Constructor of the DiceShade
     * @param abbreviation used to return the corresponding diceshade
     */
    DiceShade(char abbreviation) {
        this.abbreviation = abbreviation;
    }

    /**Return the character abbreviation corresponding to the one associated
     * @return the abbreviation of the DiceShade
     */
    public char getAbbreviation() {
        return abbreviation;
    }

    /**Method used to get the next shade value
     * @return the next shade value
     * @throws MinMaxReachedException if the shade is {@link DiceShade#SIX}
     */
    public DiceShade getNext() throws MinMaxReachedException{
        try {
            return values()[ordinal() + 1];
        }
        catch (ArrayIndexOutOfBoundsException e) {
            throw new MinMaxReachedException("You reached max possible value of shade");
        }
    }

    /**Method used to get the previous shade value
     * @return the previous shade value
     * @throws MinMaxReachedException if the shade is {@link DiceShade#ONE}
     */
    public DiceShade getPrevious() throws MinMaxReachedException{
        try {
            return values()[ordinal() - 1];
        }
        catch (ArrayIndexOutOfBoundsException e) {
            throw new MinMaxReachedException("You reached min possible value of shade");
        }
    }

    /**Returns a random shade value (except {@link DiceShade#NEUTRAL}
     * @return the randomized shade
     */
    public DiceShade roll(){
        int randomindex = ((int) (Math.random() * 6)+1);
        return values()[randomindex];
    }


    /**Method used to get the corresponding DiceShade value to an intenger
     * @param i the integer used to get the corresponding DiceShade value
     * @return the corresponding dice shade value
     */
    public static DiceShade getByValue(int i) {   //useful to convert from integer to the enum value
        for (DiceShade ds : values()) {
            if (ds.ordinal() == i) {
                return ds;
            }
        }
        return null;
    }

    /**Method used to get the list of all possible abbreviations
     * @return the list of all the abbreviations
     */
    public static List<Character> getListOfAbbreviation() {
        return new ArrayList<>(listOfAbbreviation);
    }

}