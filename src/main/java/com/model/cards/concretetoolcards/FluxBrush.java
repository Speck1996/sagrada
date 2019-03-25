package com.model.cards.concretetoolcards;

import com.model.PlayerInGame;
import com.model.cards.ToolCard;
import com.model.dice.*;
import com.model.patterns.DiceSpace;

/**ToolCard FluxBrush: the effect of this card is that the player drafts a die
 * and he rolls it: if the dice is placeable then he can proceed to the state {@link ToolCardState#PLACEDIE}
 * otherwise the modified die is put back in the stock
 * and {@link ToolCardState#PLACEDIE}.
 * The {@link DiceSpace} contained in this card is of {@link DiceColor#PURPLE}
 */


public class FluxBrush extends ToolCard {


    /**{@inheritDoc}
     * The {@link DiceSpace} is initialized with the color {@link DiceColor#PURPLE}
     * @param id the string representing the card id
     * @param title the string representing the card title
     * @param description the string representing the description of the card
     */
    FluxBrush(String id, String title, String description){
        super(id,title,description);
        toolCardSpace = new DiceSpace(DiceColor.PURPLE,DiceShade.NEUTRAL);

    }

    /** {@inheritDoc}
     * In this method there are two  {@link ToolCardState}: in {@link ToolCardState#DIESTOCKPICK} the player can pick a die
     * from the {@link Stock}: if the dice is picked ({@link ToolCardInputHandler#getSelectedDie()} return a dice and not null)
     * it's then rolled. If the rolled dice can be placed the next {@link ToolCardState} is set to {@link ToolCardState#PLACEDIE}
     * so in the next method call the input is parsed by the {@link ToolCardInputHandler} to place the rolled dice otherwise
     * the dice is inserted bag in the {@link Stock} and the {@link ToolCardState} is set to {@link ToolCardState#EXECUTED}
     * @param userInput string representing user input
     */
    @Override
    public void playerCommand(String userInput){

        Dice selectedDie;
        if(this.getState() == ToolCardState.DIESTOCKPICK) {

            this.dieStockPick(userInput);
            selectedDie = parser.getSelectedDie();

            //dice selection went ok
            if (selectedDie != null) {

                //applying the effect
                selectedDie.rollDice();


                if (toolCardUser.getWindow().isPlaceable(selectedDie, true, true)) {
                    this.setState(ToolCardState.PLACEDIE, "New die value " + selectedDie.getShade().toString());
                } else {
                    gameBoard.getStock().insertDice(new Dice(selectedDie));
                    this.setState(ToolCardState.EXECUTED, "New die value " + selectedDie.getShade().toString() + " You can't place this die, put back in the stock");
                }
            }
        }

        else if (this.getState() == ToolCardState.PLACEDIE) {

            //user has placed his die
            if (this.placeDie(userInput, true, true)){
                this.toolCardUser.setDicePlayed();
            }
        }
    }



    /**
     * This method first checks if the player has already placed his die, then it proceeds to the card payment (multiplayer)
     * and the set the {@link ToolCardState} to {@link ToolCardState#DIESTOCKPICK} so when the playerCommand
     * method is called the user input is parsed to pick a die from the stock
     * @param player object representing the player who wants to activate the card
     * @throws CardNotUsableException if the player has already placed a die or doesn't have enough tokens
     */

    @Override
    public void activateCard(PlayerInGame player) throws CardNotUsableException{
        this.checkDiePlaced(player);
        this.payCard(player);
        this.setState(ToolCardState.DIESTOCKPICK,"Pick a die from the stock, this die will be rolled ");

    }

    /**
     * This method first checks if the player has already placed his die, then it proceeds to the card payment (singleplayer)
     * and the set the {@link ToolCardState} to {@link ToolCardState#DIESTOCKPICK} so when the playerCommand
     * method is called the user input is parsed to pick a die from the stock
     * @param player object representing the player who wants to activate the card
     * @throws CardNotUsableException if the player has already placed a die or the selected die doesn't match the card
     * dicespace color
     */
    @Override
    public void activateCard(PlayerInGame player,Dice diePayment) throws CardNotUsableException{
        this.checkDiePlaced(player);
        this.payCard(player,diePayment);
        this.setState(ToolCardState.DIESTOCKPICK,"Pick a die from the stock, this die will be rolled ");

    }
}
