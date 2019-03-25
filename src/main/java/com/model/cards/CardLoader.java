package com.model.cards;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.io.*;

import static com.model.cards.concretetoolcards.ToolCardFactory.checkToolCardId;
import static com.model.cards.concretetoolcards.ToolCardFactory.cloneToolCards;
import static com.model.cards.concretetoolcards.ToolCardFactory.createToolCard;
import static com.model.cards.objcard.ObjCardFactory.checkObjId;
import static com.model.cards.objcard.ObjCardFactory.cloneObjCards;
import static com.model.cards.objcard.ObjCardFactory.createObjCard;


/**Class used to pars a given file containing the deck list of all the typology of cards. Once created it stores them
 * in its attributes so when a deck copy is required in a new game it can just clone the list withoud parsing the file
 * again
 */
public  class CardLoader {
    /**The list where will be store the ToolCardDeck
     */
    private List<ToolCard> toolCardDeck = new ArrayList<>(); //declaring and initializing the deck

    /**The list where will be stored the privateObjectiveCardDeck
     */
    private List<ObjCard> privateObjCardDeck = new ArrayList<>();//declaring and initializing the deck
    /**THe list where will be stored the publicObjectiveCardDeck
     */
    private List<ObjCard> publicObjCardDeck = new ArrayList<>();//declaring and initializing the deck


    /**String used to store the path of the file to be parsed
     */
    private static final String PATH = "configfiles/cards/CardList.txt";

    /**Method used to parse the file and create the deck
     */
    public void loadCards(){




           try( BufferedReader bufferedReader= new BufferedReader(new FileReader(PATH))) {

               String line;

               while ((line = bufferedReader.readLine()) != null) {
                   String tokenize[] = line.split(":");  //splitting the line (syntax is like ID:firstid:DESCRIPTION:firstdescr and so on)

                   try {
                          if (checkToolCardId(tokenize[1])) {                    //checking if the id is valid

                              System.out.println("Creating " + tokenize[1] + " " +tokenize[3] +" " +tokenize[5]);
                               toolCardDeck.add(createToolCard(tokenize[1], tokenize[3], tokenize[5]));        //creating and storing the card, the given txt must respect a strict syntax, (every specific index in the array as a specific meaning)
                          }
                        else if (checkObjId(tokenize[1])) {   //checking if the id is valid

                           if (tokenize[1].charAt(2) == 'T') {               //the 3rd character of the obj card id represent the tipology (T = private, F = public)

                               System.out.println("Creating " + tokenize[1] + " " +tokenize[3] +" " +tokenize[5]);

                               privateObjCardDeck.add(createObjCard(tokenize[1], tokenize[3], tokenize[5],Integer.parseInt(tokenize[7])));        //creating and storing the card
                           }
                           if (tokenize[1].charAt(2) == 'F') {
                               System.out.println("Creating " + tokenize[1] + " " +tokenize[3] +" " +tokenize[5]);

                               publicObjCardDeck.add(createObjCard(tokenize[1], tokenize[3], tokenize[5], Integer.parseInt(tokenize[7])));
                           }

                       }
                   }
                   catch(WrongIdException e){
                       System.out.println("There is something wrong with your card id " + tokenize[1]);
                   }
                   catch(NullPointerException e){
                       e.printStackTrace();
                   }
                   catch(ArrayIndexOutOfBoundsException e){
                       System.out.println("Wrong Syntax:" + " "+ line);
                   }
                   if(!(tokenize[0].equalsIgnoreCase("ID"))){            //temporary check on the syntax, this will be tweaked
                       System.out.println("wrong syntax");
                   }
               }
           }
           catch (IOException e) {
               e.printStackTrace();
           }


       }


    /**Method used to get a copy of the ToolCardDeck
     * @return a copy of ToolCardDeck
     * @throws WrongIdException if a card doesn't match a predefined id
     */
    public List<ToolCard> getToolCardDeck()throws WrongIdException{
        return cloneToolCards(toolCardDeck);                    //cloning toolcards
    }


    /**Method used to get a copy of the PublicObjCardDeck
     * @return a copy of PublicObjCardDeck
     * @throws WrongIdException if a card doesn't match a predefined id
    */
    public List<ObjCard> getPublicObjCardDeck()throws WrongIdException {  //cloning publicobjcards
        return cloneObjCards(publicObjCardDeck);
    }

    /**Method used to get a copy of the PrivateObjCardDeck
     * @return a copy of PrivateObjCardDeck
     * @throws WrongIdException if a card doesn't match a predefined id
     */
    public List<ObjCard> getPrivateObjCardDeck()throws WrongIdException {  //cloning privateobjcards
        return cloneObjCards(privateObjCardDeck);
    }
}
