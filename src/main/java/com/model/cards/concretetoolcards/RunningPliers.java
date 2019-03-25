package com.model.cards.concretetoolcards;

import com.model.PlayerInGame;
import com.model.cards.ToolCard;
import com.model.dice.*;
import com.model.patterns.DiceSpace;

import static com.Server.errorMessage;


/**ToolCard RunningPliers: the effect of this ToolCard is that the player can play his second turn immediately after
 * passing the first one. This card is one of the special card that doesn't require user input during its execution
 *  * The playerCommand can reach only the standard card {@link ToolCardState} (NEUTRAL, EXECUTED)
 *  * The {@link DiceSpace} contained in this card is of {@link DiceColor#RED}
 */

public class RunningPliers extends ToolCard {

    /**{@inheritDoc}
     * The {@link DiceSpace} is initialized with the color {@link DiceColor#RED}
     * @param id the string representing the card id
     * @param title the string representing the card title
     * @param description the string representing the description of the card
     */
    RunningPliers(String id, String title, String description){
        super(id,title,description);
        toolCardSpace = new DiceSpace(DiceColor.RED,DiceShade.NEUTRAL);
    }


    /**This method uses the {@link PlayerInGame#toggleSkipTurn()}: the {@link PlayerInGame#getSkipTurn()}  identifies
     * if the player has activated the effect of this card and therefore must skip his second turn.
     * @param userInput representing user input, this card doesn't require it so in the activation phase a blank string is used
     *                  to automatically activate the card
     */

    @Override
    public void playerCommand(String userInput){

        //checks if the card is in the right state
        if(this.getState() == ToolCardState.NEUTRAL){

            //user boolean: if true the extraturn is activated in the controller and the second player turn is skipped
            toolCardUser.toggleSkipTurn();

            //the \n character at the end of the string identifies the fact the playerCommand doesn't require user
            //interaction, therefore the setState must handle some view updates that are normally done by the controller
            //(this updates are normally done at end of the last user input in a playerCommand call by the controller but
            //in this case there is no real user input)
            this.setState(ToolCardState.EXECUTED,"You will place the second die after you pass your turn. You won't be able to play your second turn thought\n");
        }
    }

    /**This method checks if it is the player first round, then proceed with the paymanet (multiplayer) and automatically
     * calls the playerCommand with a blank string to activate the effect
     * @param player object representing the player who wants to activate the card
     * @throws CardNotUsableException if it is not the player first turn
     */

    @Override
    public void activateCard(PlayerInGame player)throws CardNotUsableException{

        //checking if the card is activated during the first turn
        if(!gameBoard.isAscendant()){
            throw new CardNotUsableException(errorMessage + "can't use this card in your second turn");
        }

        this.payCard(player);
        this.playerCommand(" ");
    }

    /**This method checks if it is the player first round, then proceed with the paymanet (singleplayer) and automatically
     * calls the playerCommand with a blank string to activate the effect
     * @param player object representing the player who wants to activate the card
     * @throws CardNotUsableException if it is not the player first turn
     */
    @Override
    public void activateCard(PlayerInGame player,Dice diePayment)throws CardNotUsableException{

        //checking if the card is activated during the first turn
        if(!gameBoard.isAscendant()){
            throw new CardNotUsableException(errorMessage + "can't use this card in your second turn");
        }
        this.payCard(player,diePayment);
        this.playerCommand(" ");
    }
}
