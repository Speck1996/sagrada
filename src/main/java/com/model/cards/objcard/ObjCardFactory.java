package com.model.cards.objcard;

import com.model.cards.ObjCard;
import com.model.cards.WrongIdException;
import com.model.dice.DiceColor;
import com.model.dice.DiceShade;

import java.util.ArrayList;
import java.util.List;


/** This class is the factory class for ObjCards. It consists of static methods useful
 * for ObjCard creation. The creation is based on a string named id, the first two letters
 * identify the Object Card, the third identifies what kind of Obj Card is (T-- Private,
 * F-- Public) the forth is a letter that identifies the color if the Obj Card is a private obj card (P-- purple,
 * G-- green, R-- red, B -- blue, Y -- yellow) otherwise is a number and represents the multiplying factor of the public object card (0 stands for 1), the last digit is a number in both kind of ObjCard
 * This factory is a parameter of the CardLoader, has 3 methods: one checks if a given string matches the codification of a
 * generic ObjCard (first two character are "CO"), one creates the corresponding ObjCard based on the id, the last one creates
 * return a clone list of ObjCard from a given list.
 * @see ObjCard
 * @see com.model.cards.CardLoader
 */



public class ObjCardFactory  {


    /**This method checks if a given string matches the ObjCard codification (two first characters must be "CO")
     * @param id given string to check
     * @return true if the id starts with "CO" otherwise it return false
     */
    public static boolean checkObjId(String id) {
        return id.startsWith("CO");
    }

    /**This method creates the corresponding ObjCard based on the id, returns an exception if the id doesn't match
     * any predefined id
     * @param id string on which the creation is based
     * @param title string that will be put in the title field of the ObjCard
     * @param description string that will be put in the description field of the ObjCard
     * @param multiplyingFactor integer that will be put in the multiplyingFactor of the ObjCard
     * @return ObjCard created ObjCard
     * @throws WrongIdException if the string id doesn't match any predefined string
     * @see ObjCard
     */

    public static ObjCard createObjCard(String id, String title, String description, int multiplyingFactor)throws WrongIdException{                                 //returns the right obj card based on the id, multiplying factor represents
                                                                                                                                // the value that multiplies the points got by the card algorithm// (for private obj card is 1 by default)
            switch (id) {
                case "COTP0":
                    return new PrivateObjCard(id, title, description, multiplyingFactor, DiceColor.PURPLE);
                case "COTR0":
                    return new PrivateObjCard(id, title, description, multiplyingFactor, DiceColor.RED);
                case "COTY0":
                    return new PrivateObjCard(id,title,description,multiplyingFactor,DiceColor.YELLOW);
                case "COTG0":
                    return new PrivateObjCard(id,title,description,multiplyingFactor, DiceColor.GREEN);
                case "COTB0":
                    return new PrivateObjCard(id,title,description,multiplyingFactor, DiceColor.BLUE);
                case "COF60":
                    return new RowColorVariety(id,title,description,multiplyingFactor);
                case "COF50":
                    return new ColumnColorVariety(id,title,description,multiplyingFactor);
                case "COF40":
                    return new ColumnShadeVariety(id,title,description,multiplyingFactor);
                case "COF51":
                    return new RowShadeVariety(id,title,description,multiplyingFactor);
                case "COF52":
                    return new ShadeVariety(id,title,description,multiplyingFactor);
                case "COF41":
                    return new ColorVariety(id,title,description,multiplyingFactor);
                case "COF20":
                    return new PairsShades(id,title,description,multiplyingFactor, DiceShade.FIVE, DiceShade.SIX);  //DeepShades
                case "COF21":
                    return new PairsShades(id,title,description,multiplyingFactor, DiceShade.THREE, DiceShade.FOUR);  //MediumShades
                case "COF22":
                    return new PairsShades(id,title,description,multiplyingFactor, DiceShade.ONE, DiceShade.TWO);  //LightShades
                case "COF00":
                    return new ColorDiagonals(id,title,description,multiplyingFactor);
                default:
                    throw new WrongIdException("ID not recognized: "+ id);
            }

    }

    /**this method creates a list of objcard that is a clone of the given list (of obj cards). The clonation is based on the id
     * of the cards in the list, the id is checked and a new cloned card is created with the parameters of the analyzed card of the given list
     * the cloned card is added in the list and the list is returned
     * @param clonedList list of card to be cloned
     * @return objCards list of cloned ObjCard
     * @throws WrongIdException if the id of a card of the given list doesn't match a predefined id
     * @see ObjCard
     */


    public static List<ObjCard> cloneObjCards(List<ObjCard> clonedList)throws WrongIdException{

        List<ObjCard> objCards = new ArrayList<>();
        for(ObjCard oc: clonedList){
            switch(oc.getId()){

                    case "COTP0":
                        objCards.add( new PrivateObjCard(oc.getId(),oc.getTitle(),oc.getDescription(), oc.getFactor(), DiceColor.PURPLE));
                        break;
                    case "COTR0":
                        objCards.add(new PrivateObjCard(oc.getId(), oc.getTitle(), oc.getDescription(),oc.getFactor(), DiceColor.RED));
                        break;
                    case "COTY0":
                        objCards.add(new PrivateObjCard(oc.getId(),oc.getTitle(),oc.getDescription(),oc.getFactor(),DiceColor.YELLOW));
                        break;
                    case "COTG0":
                        objCards.add(new PrivateObjCard(oc.getId(),oc.getTitle(),oc.getDescription(),oc.getFactor(), DiceColor.GREEN));
                        break;
                    case "COTB0":
                        objCards.add(new PrivateObjCard(oc.getId(),oc.getTitle(),oc.getDescription(),oc.getFactor(), DiceColor.BLUE));
                        break;
                    case "COF60":
                        objCards.add(new RowColorVariety(oc.getId(),oc.getTitle(),oc.getDescription(),oc.getFactor()));
                        break;
                    case "COF50":
                        objCards.add(new ColumnColorVariety(oc.getId(),oc.getTitle(),oc.getDescription(),oc.getFactor()));
                        break;
                    case "COF40":
                        objCards.add( new ColumnShadeVariety(oc.getId(),oc.getTitle(),oc.getDescription(),oc.getFactor()));
                        break;
                    case "COF51":
                        objCards.add(new RowShadeVariety(oc.getId(),oc.getTitle(),oc.getDescription(),oc.getFactor()));
                        break;
                    case "COF52":
                        objCards.add(new ShadeVariety(oc.getId(),oc.getTitle(),oc.getDescription(),oc.getFactor()));
                        break;
                    case "COF41":
                        objCards.add(new ColorVariety(oc.getId(),oc.getTitle(),oc.getDescription(),oc.getFactor()));
                        break;
                    case "COF20":
                        objCards.add(new PairsShades(oc.getId(),oc.getTitle(),oc.getDescription(),oc.getFactor(), DiceShade.FIVE, DiceShade.SIX));  //DeepShades
                        break;
                    case "COF21":
                        objCards.add(new PairsShades(oc.getId(),oc.getTitle(),oc.getDescription(),oc.getFactor(), DiceShade.THREE, DiceShade.FOUR));  //MediumShades
                        break;
                    case "COF22":
                        objCards.add(new PairsShades(oc.getId(),oc.getTitle(),oc.getDescription(),oc.getFactor(), DiceShade.ONE, DiceShade.TWO));  //LightShades
                        break;
                    case "COF00":
                        objCards.add(new ColorDiagonals(oc.getId(),oc.getTitle(),oc.getDescription(),oc.getFactor()));
                        break;
                    default:
                        throw new WrongIdException("ID not recognized: "+ oc.getId());

                }
            }

     return objCards;
    }
}
