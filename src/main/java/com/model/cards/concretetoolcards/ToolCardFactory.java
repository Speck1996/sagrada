package com.model.cards.concretetoolcards;
import com.model.cards.ToolCard;
import com.model.cards.WrongIdException;

import java.util.ArrayList;
import java.util.List;

/** This class is the factory class for ToolCards. It consists of static methods useful
 * for ToolCard creation. The creation is based on a string named id, the convention used
 * for ToolCard is that the string is composed of 5 characters/numbers: the first three must be
 * "CTA" and the others two are digits.
 * The three operations this class can do are: checking if a given string matches the ToolCard defined id format of
 * String, creating the ToolCard (the creation is based on the string ID), cloning a given List of ToolCards
 * This class will be a parameter in the class CardLoader which parses the card list from a txt file to create
 * the decks used in the MainModel (the cloning operation is useful in this particular case when the MainModel requires
 * a deck to read the txt file just one time and giving the deck using the clones)
 * @see ToolCard
 * @see com.model.cards.CardLoader
 */



public class ToolCardFactory {


    /** This method check if the given String matches the id respects the ToolCard codification rule, checking
     * if the first three characters of the string match "CTA"
     * @param id the given string that has to be checked
     * @return true if id first three characters matches "CTA", false otherwise
     * @see ToolCard
     */
    public static boolean checkToolCardId(String id) {
        return id.startsWith("CTA");
    }


    /**This method create the desired ToolCard using the three given strings. The creation is based on the string id: every
     * ToolCard has is specific and unique id, if the id doesn't match any predefined ID returns and exception, otherwise the
     * the ToolCard is created and returned
     * @param id string that is the key to recognize the ToolCard, it is an attribute of the ToolCard itself too
     * @param title string put in the title field of the ToolCard, needed in its constructor
     * @param description string put in the description field of the ToolCard, needed in its constructor
     * @return the created ToolCard
     * @throws WrongIdException when the id doesn't match any predefined id
     * @see ToolCard
     */
    public static ToolCard createToolCard(String id, String title, String description) throws WrongIdException {
        switch (id) {
            case "CTA00":
                return new GrozingPliers(id,title,description);
            case "CTA10":
               return new EglomiseBrush(id, title, description);
            case "CTA20":
                return new CopperFoilBurnisher(id, title, description);
            case "CTA30":
                return new Lathekin(id, title, description);
            case "CTA40":
                return new LensCutter(id,title,description);
            case "CTA50":
                return new FluxBrush(id,title,description);
            case "CTA60":
                return new GlazingHammer(id,title,description);
            case "CTA70":
                return new RunningPliers(id,title,description);
            case "CTA80":
                return new CorkBackedStraightedge(id,title,description);
            case "CTA90":
                return new GrindingStone(id,title,description);
            case "CTA11":
                return new FluxRemover(id,title,description);
            case "CTA12":
                return new TapWheel(id,title,description);
            default:
                throw new WrongIdException("ID not recognized: "+ id);
        }

    }

    /**Method that creates a clone list of ToolCard based on the given list, for every card in the list
     * it checks the id and creates the corresponding card(id, title, description obtained through ToolCard respective
     * get methods), it throws the exception if the id doesn't match any predefined id. The card created is added in the list returned.
     * @param clonedList the list that will be cloned
     * @return toolCards the list of cloned cards
     * @throws WrongIdException when the id doens't match any predefined id
     * @see ToolCard
     */


    public static List<ToolCard> cloneToolCards(List<ToolCard> clonedList)throws WrongIdException{

        List<ToolCard> toolCards = new ArrayList<>();
        for(ToolCard tc: clonedList){

            switch (tc.getId()) {
                    case "CTA00":
                        toolCards.add(new GrozingPliers(tc.getId(), tc.getTitle(), tc.getDescription()));
                        break;
                    case "CTA10":
                        toolCards.add(new EglomiseBrush(tc.getId(), tc.getTitle(), tc.getDescription()));
                        break;
                    case "CTA20":
                        toolCards.add(new CopperFoilBurnisher(tc.getId(), tc.getTitle(), tc.getDescription()));
                        break;
                    case "CTA30":
                        toolCards.add(new Lathekin(tc.getId(), tc.getTitle(), tc.getDescription()));
                        break;
                    case "CTA40":
                        toolCards.add(new LensCutter(tc.getId(), tc.getTitle(), tc.getDescription()));
                        break;
                    case "CTA50":
                        toolCards.add(new FluxBrush(tc.getId(), tc.getTitle(), tc.getDescription()));
                        break;
                    case "CTA60":
                        toolCards.add(new GlazingHammer(tc.getId(), tc.getTitle(), tc.getDescription()));
                        break;
                    case "CTA70":
                        toolCards.add(new RunningPliers(tc.getId(), tc.getTitle(), tc.getDescription()));
                        break;
                    case "CTA80":
                        toolCards.add(new CorkBackedStraightedge(tc.getId(), tc.getTitle(), tc.getDescription()));
                        break;
                    case "CTA90":
                        toolCards.add(new GrindingStone(tc.getId(), tc.getTitle(), tc.getDescription()));
                        break;
                    case "CTA11":
                        toolCards.add(new FluxRemover(tc.getId(), tc.getTitle(), tc.getDescription()));
                        break;
                    case "CTA12":
                        toolCards.add(new TapWheel(tc.getId(), tc.getTitle(), tc.getDescription()));
                        break;
                    default:
                        throw new WrongIdException("ID not recognized: "+ tc.getId());
            }


        }
        return toolCards;
    }


}