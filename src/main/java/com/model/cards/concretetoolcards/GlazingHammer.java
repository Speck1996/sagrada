package com.model.cards.concretetoolcards;


import com.model.PlayerInGame;
import com.model.cards.ToolCard;
import com.model.dice.*;
import com.model.patterns.DiceSpace;

import static com.Server.errorMessage;


/**ToolCard GlazingHammer: the effect of this card is that every dice in the {@link Stock} is rolled. This is one of
 * the particular card that doesn't require user input.
 * The playerCommand can reach only the standard card {@link ToolCardState} (NEUTRAL, EXECUTED)
 * The {@link DiceSpace} contained in this card is of {@link DiceColor#BLUE}
 */

public class GlazingHammer extends ToolCard {

    /**{@inheritDoc}
     * The {@link DiceSpace} is initialized with the color {@link DiceColor#BLUE}
     * @param id the string representing the card id
     * @param title the string representing the card title
     * @param description the string representing the description of the card
     */
    GlazingHammer(String id, String title, String description){
        super(id,title,description);
        toolCardSpace = new DiceSpace(DiceColor.BLUE,DiceShade.NEUTRAL);

    }

    /** {@inheritDoc}
     *In this method the dice are rolled and the {@link ToolCardState} is set to {@link ToolCardState#EXECUTED}
     * @param userInput string representing user input
     */

    @Override
    public void playerCommand(String userInput){

        if(this.getState() == ToolCardState.NEUTRAL) {

            //rick rolled Never gonna give you up Never gonna let you down
             for (Dice die : gameBoard.getStock().getDice()) {
                 die.rollDice();
                 toolCardUser.notifyViewObserver("New dice value " + die.toString());
             }

             toolCardUser.notifyViewObserver("Stock: " + gameBoard.getStock());

            //the \n character at the end of the string identifies the fact the playerCommand doesn't require user
            //interaction, therefore the setState must handle some view updates that are normally done by the controller
            //(this updates are normally done at end of the last user input in a playerCommand call by the controller but
            //in this case there is no real user input)
             this.setState(ToolCardState.EXECUTED,"all done\n");
        }
    }


    /**
     * This method first checks if the player is in his second turn,then checks if the player has played his dice
     * and the proceeds to the card payment (multiplayer). Since this card doesn't need user input, the player command
     * is activated inside the activate card.
     * @param player object representing the player who wants to activate the card
     * @throws CardNotUsableException if the player has already placed a die or doesn't have enough tokens
     */
    @Override
    public void activateCard(PlayerInGame player) throws CardNotUsableException {
        if(gameBoard.isAscendant()){
            throw new CardNotUsableException(errorMessage + "This is not your second turn, you can't use this card");
        }
        if(gameBoard.getCurrentPlayer().hasPlayedDice()){
            throw new CardNotUsableException(errorMessage + "You can use this card only before placing your die");
        }
        this.payCard(player);
        this.playerCommand(" ");
    }


    /**
     * This method first checks if the player is in his second turn,then checks if the player has played his dice
     * and the proceeds to the card payment (singleplayer). Since this card doesn't need user input, the player command
     * is activated inside the activate card with a random string (in this case blank space)
     * @param player object representing the player who wants to activate the card
     * @throws CardNotUsableException if the player has already placed a die or doesn't have enough tokens
     */
    @Override
    public void activateCard(PlayerInGame player, Dice diePayment) throws CardNotUsableException {
        if(gameBoard.isAscendant()){
            throw new CardNotUsableException(errorMessage + "This is not your second turn, you can't use this card");
        }
        if(gameBoard.getCurrentPlayer().hasPlayedDice()){
            throw new CardNotUsableException(errorMessage + "You can use this card only before placing your die");
        }
        this.payCard(player,diePayment);
        this.playerCommand(" ");
    }
}
