package com.model.cards.concretetoolcards;


import com.model.PlayerInGame;
import com.model.cards.ToolCard;
import com.model.dice.*;
import com.model.patterns.DiceSpace;


/**ToolCard CopperFoilBurnisher: the effect of this card is that the player can
 * move a die from the window ignoring shade restriction,using the playerCommand method. The two states
 * that can be activated in the method playerCommand are {@link ToolCardState#PICKWINDOWDIE}
 * and {@link ToolCardState#PLACEDIE}.
 * The {@link DiceSpace} contained in this card is of {@link DiceColor#RED}
 */

public class CopperFoilBurnisher extends ToolCard {


    /**{@inheritDoc}
     * The {@link DiceSpace} is initialized with the color {@link DiceColor#RED}
     * @param id the string representing the card id
     * @param title the string representing the card title
     * @param description the string representing the description of the card
     */
    public CopperFoilBurnisher(String id, String title, String description){
        super(id, title, description);
        this.toolCardSpace = new DiceSpace(DiceColor.RED,DiceShade.NEUTRAL);
    }

    /**{@inheritDoc}
     * In this method there are two {@link ToolCardState}: in {@link ToolCardState#PICKWINDOWDIE} the player can pick a die
     * from his {@link com.model.patterns.WindowPatternCard}. If the dice is picked the next state is set to {@link ToolCardState#PLACEDIE},
     * so in the next method call the input is parsed to place the die
     * @param userInput string representing user input
     */
    @Override
    public void playerCommand(String userInput){
        if(this.getState() == ToolCardState.PICKWINDOWDIE)
                this.pickWindowDie(userInput,DiceColor.NEUTRAL,DiceShade.NEUTRAL);
        else if(this.getState() == ToolCardState.PLACEDIE)
                this.placeDie(userInput,true,false);
    }


    /**
     * This method first checks if there is a movable die in the window, then it proceeds to the card payment (multiplayer)
     * and the set the {@link ToolCardState} to {@link ToolCardState#PICKWINDOWDIE} so when the playerCommand
     * method is called the user input is parsed to pick a die from the window
     * @param player object representing the player who wants to activate the card
     * @throws CardNotUsableException if there is no movable die or the player doesn't have enough tokens
     */
    @Override
    public void activateCard(PlayerInGame player) throws CardNotUsableException{
        this.checkMovableDice(player,true,false);
        //check if player has enough tokens
        this.payCard(player);
        this.setState(ToolCardState.PICKWINDOWDIE,"Remember you can move the die ignoring shade restrictions");
    }

    /**This method first checks if there is a movable die in the window, then it proceeds to the card payment (singleplayer)
     * and the set the {@link ToolCardState} to {@link ToolCardState#PICKWINDOWDIE} so when the playerCommand
     * method is called the user input is parsed to pick a die from the window
     * @param player object representing the player who wants to activate the card
     * @param diePayment the die used to pay the card
     * @throws CardNotUsableException if there is no movable die or the player selected die doesn't matche the card dicespace color
     */
    @Override
    public void activateCard(PlayerInGame player,Dice diePayment) throws CardNotUsableException{
        this.checkMovableDice(player,true,false);
        this.payCard(player,diePayment);
        this.setState(ToolCardState.PICKWINDOWDIE,"Remember you can move the die ignoring shade restrictions");
    }
}
