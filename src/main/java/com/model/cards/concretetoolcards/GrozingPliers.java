package com.model.cards.concretetoolcards;


import com.model.MoveAbortedException;
import com.model.PlayerInGame;
import com.model.cards.ToolCard;
import com.model.dice.*;
import com.model.patterns.DiceSpace;

import static com.Server.errorMessage;

/**ToolCard GrozingPliers: the effect of this card is that the player drafts a die, decide if he wants to increase
 * or decrease its shade value and in the end places it. In this card {@link ToolCard#placeDie(String, boolean, boolean)}
 * is overridden because after drafting a die the card is set to {@link ToolCardState#USERDEMAND}, the state where the
 * input is parsed to see if the user wants to increase or decrease the die value:
 * if the die is placeable then he can proceed to the state {@link ToolCardState#PLACEDIE}
 * otherwise the entire operation is aborted and everything goes back as before.
 * The {@link DiceSpace} contained in this card is of {@link DiceColor#PURPLE}
 */
public class GrozingPliers extends ToolCard {

    /**{@inheritDoc}
     * The {@link DiceSpace} is initialized with the color {@link DiceColor#PURPLE}
     * @param id the string representing the card id
     * @param title the string representing the card title
     * @param description the string representing the description of the card
     */
    GrozingPliers(String id, String title, String description){
       super(id,title,description);
       toolCardSpace = new DiceSpace(DiceColor.PURPLE,DiceShade.NEUTRAL);
    }


    /**{@inheritDoc}
     * In this method there are three {@link ToolCardState}: in {@link ToolCardState#DIESTOCKPICK} string corresponding to
     * userInput is parsed by the {@link ToolCardInputHandler#pickStockDie(Stock, String)}: if the die is picked then the
     * next {@link ToolCardState} is set to {@link ToolCardState#USERDEMAND}: in this state the input is parsed to increase
     * (string corresponding to "I") or decrease (string corresponding to "D")  the shade value of the die . In this state is also possible to abort
     * (string corresponding to "a") the ToolCard execution otherwise the input is not recognized and the state remains {@link ToolCardState#USERDEMAND}.
     * If the user can't increase the dice shade six or decrease the dice shade one.
     * After increasing or decreasing the shade value the {@link ToolCardState} is set to {@link ToolCardState#PLACEDIE} where
     * the input is parsed by the {@link ToolCardInputHandler} to place the die
     * @param userInput string corresponding to user input
     */
    @Override
    public void playerCommand(String userInput){

        //picking the die
        if(this.getState() == ToolCardState.DIESTOCKPICK) {
            this.dieStockPick(userInput);
        }

        //if the card is in this state the user input is parsed by the card itself (the effects that require user specific
        //input are always handled by the card)
        else if(this.getState() == ToolCardState.USERDEMAND) {

            switch (userInput) {

                //user wants to increase the die shade
                case "I":


                    try {
                        //increasing the shade value
                        parser.getSelectedDie().nextValue();

                        //checking if the die is placeable
                        if (toolCardUser.getWindow().isPlaceable(parser.getSelectedDie(), true, true)) {
                            this.setState(ToolCardState.PLACEDIE, "Time to place the die " + parser.getSelectedDie().toString());
                        } else {
                            this.setState(ToolCardState.ABORTED, "You can't place this die, you lost your tokens");
                        }

                        //user can't increase the shade value of six
                    } catch (MinMaxReachedException error) {
                        this.setState(ToolCardState.USERDEMAND, errorMessage + error.getMessage()+ ", try again");
                    }
                    break;

                //user wants to decrease the die shade
                case "D":


                    try {

                        //decreasing the value
                        parser.getSelectedDie().previousValue();

                        //checking if the die is placeable
                        if (toolCardUser.getWindow().isPlaceable(parser.getSelectedDie(), true, true)) {
                            this.setState(ToolCardState.PLACEDIE, "Time to place the die " + parser.getSelectedDie().toString());
                        } else {
                            this.setState(ToolCardState.ABORTED, "You can't place this die, you lost your tokens");
                        }

                       //user can't decrease the value of one
                    } catch (MinMaxReachedException error) {
                        this.setState(ToolCardState.USERDEMAND, errorMessage + error.getMessage()+ ", try again");
                    }
                    break;

                    //user wants to abort the card execution
                case "a":

                    this.setState(ToolCardState.ABORTED, "Toolcard Aborted");
                    break;

                default:
                    this.setState(ToolCardState.USERDEMAND, errorMessage + "Wrong input,try again");
            }

        }
        //placing the die
        else if (this.getState() == ToolCardState.PLACEDIE){


            if(this.placeDie(userInput,true,true)){
                this.toolCardUser.setDicePlayed();
            }

        }

   }


    /**This method calls the {@link ToolCardInputHandler#pickStockDie(Stock, String)}: if the input parsing doesn't throw
     * any exception the next card state is set to {@link ToolCardState#USERDEMAND} ,if the {@link MoveAbortedException}
     * is thrown the {@link ToolCardState} is set to {@link ToolCardState#ABORTED} otherwise the string input syntax is wrong
     * and the state remains {@link ToolCardState#DIESTOCKPICK}
     * @param command string representing user input
     */
   //specific dieStockPick for this card
    @Override
    protected void dieStockPick(String command){

        //picking the die and setting the state to userDemand
        try {
            parser.pickStockDie(gameBoard.getStock(),command);
            this.setState(ToolCardState.USERDEMAND,"Now decide if you want to increase [I] or decrease the value [D], [a] to abort");

         //user wants to abort toolcard execution
        }catch (MoveAbortedException error){
            this.setState(ToolCardState.ABORTED,error.getMessage());

         //wrong input syntax
        }catch (Exception error){
            this.setState(ToolCardState.DIESTOCKPICK,errorMessage+ error.getMessage());

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
        this.setState(ToolCardState.DIESTOCKPICK,"Pick a die from the stock, you will have to decide if you want to increase or decrease the value of the die");
   }



    /**{@inheritDoc}
     * This method first checks if the player has already placed his die, then it proceeds to the card payment (singleplayer)
     * and the set the {@link ToolCardState} to {@link ToolCardState#DIESTOCKPICK} so when the playerCommand
     * method is called the user input is parsed to pick a die from the stock
     * @param player object representing the player who wants to activate the card
     * @throws CardNotUsableException if the player has already placed a die or the selected die doesn't match the card
     * {@link DiceSpace} color
     */
    @Override
    public void activateCard(PlayerInGame player,Dice diePayment) throws CardNotUsableException{
        this.checkDiePlaced(player);
        this.payCard(player,diePayment);
        this.setState(ToolCardState.DIESTOCKPICK,"Pick a die from the stock, you will have to decide if you want to increase or decrease the value of the die");
    }

}
