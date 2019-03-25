package com.model.cards.concretetoolcards;


import com.model.MoveAbortedException;
import com.model.WrongInputSyntaxException;
import com.model.PlayerInGame;
import com.model.cards.ToolCard;
import com.model.dice.*;
import com.model.patterns.DiceSpace;

import static com.Server.errorMessage;


/**ToolCard LensCutter: the effect of this card is that the user drafts a die and then swaps it with one in the
 * {@link com.model.gameboard.RoundBoard}.This card can reach two {@link ToolCardState}: the {@link ToolCardState#DIESTOCKPICK}
 * where the user input is parsed to select a die from the stock and the {@link ToolCardState#PLACEDIE} where the user input
 * is parsed to place the die. The placeDie of this card is slightly different: the next state is not {@link ToolCardState#PLACEDIE}
 * but {@link ToolCardState#ROUNDBOARDPICK}
 * The {@link DiceSpace} contained in this card is of {@link DiceColor#GREEN}
 */

public class LensCutter extends ToolCard {

    /**{@inheritDoc}
     * The {@link DiceSpace} is initialized with the color {@link DiceColor#GREEN}
     * @param id the string representing the card id
     * @param title the string representing the card title
     * @param description the string representing the description of the card
     */
    LensCutter(String id, String title, String description){
        super(id,title,description);
        toolCardSpace = new DiceSpace(DiceColor.GREEN, DiceShade.NEUTRAL);
    }

    /**{@inheritDoc}
     * In this method there three reachable {@link ToolCardState}: the first one is the {@link ToolCardState#DIESTOCKPICK} where
     * the string is parsed to get the selected die from the stock. The second one is the {@link ToolCardState#ROUNDBOARDPICK}
     * where the input string is parsed to get the round coordinates for dice swapping in the roundboard, and the swapping itself
     * takes place. The last {@link ToolCardState} is {@link ToolCardState#PLACEDIE} where the input is parsed to place the selected
     * die
     * @param userInput string corresponding to user input
     */

    @Override
    public void playerCommand(String userInput){

        //where selected round coordinates are temporally stored
        Integer[] roundCoordinates;

        //where the user input is parsed to select the die from the stock
        if(this.getState() == ToolCardState.DIESTOCKPICK) {
            this.dieStockPick(userInput);
        }

        //user input parsed to select the round coordinates
        else if(this.getState() == ToolCardState.ROUNDBOARDPICK)

                try {
                    //where the swapped die will be temporally stored
                    Dice swappedDie;

                    //swapping the die and setting it to selectedDie
                    roundCoordinates = parser.selectRoundCoordinates(gameBoard.getGameRoundBoard(), userInput);
                    swappedDie = gameBoard.getGameRoundBoard().changeDice(roundCoordinates[0], roundCoordinates[1], new Dice(parser.getSelectedDie()));
                    parser.setSelectedDie(swappedDie);

                    //updating user view
                    toolCardUser.notifyViewObserver(gameBoard.getGameRoundBoard().toString()+"\n"+"Swapped die: "+swappedDie.toString());


                    //checking if the swapped die is placeable
                    if (toolCardUser.getWindow().isPlaceable(swappedDie, true, true)) {
                        this.setState(ToolCardState.PLACEDIE, "time to place the die");
                    } else {
                        this.setState(ToolCardState.ABORTED, "You can't place this die, you lost your tokens");
                    }

                 //user decided to abort the card execution
                } catch (MoveAbortedException error) {
                    this.setState(ToolCardState.ABORTED, error.getMessage());

                //wrong syntax input
                } catch (WrongInputSyntaxException error) {
                    this.setState(ToolCardState.ROUNDBOARDPICK, errorMessage+ error.getMessage());
                }

        //where the input is parsed to place the die
        else if (this.getState() == ToolCardState.PLACEDIE) {

            //user has placed his die
            if(this.placeDie(userInput, true, true)){
                this.toolCardUser.setDicePlayed();

            }
        }

    }

    /**This method calls the {@link ToolCardInputHandler#pickStockDie(Stock, String)}: if the input parsing doesn't throw
     * any exception the next card state is set to {@link ToolCardState#ROUNDBOARDPICK} ,if the {@link MoveAbortedException}
     * is thrown the {@link ToolCardState} is set to {@link ToolCardState#ABORTED} otherwise the string input syntax is wrong
     * and the state remains {@link ToolCardState#DIESTOCKPICK}
     * @param command string representing user input
     */
    @Override
    protected void dieStockPick(String command){

        //parsing input to select the die from the stock
        try {
            parser.pickStockDie(gameBoard.getStock(), command);
            this.setState(ToolCardState.ROUNDBOARDPICK, "Select the die from the RoundBoard");

         //user aborted card execution
        } catch (MoveAbortedException error) {
            this.setState(ToolCardState.ABORTED, error.getMessage());

         //string not parsable
        } catch (Exception error) {
            this.setState(ToolCardState.DIESTOCKPICK, errorMessage + error.getMessage());
        }
    }


    /**
     * This method first checks if the player has already placed his die, then it proceeds to check if the {@link com.model.gameboard.RoundBoard}
     * is empty and the proceed with the card payment (multiplayer)
     * After the payment the {@link ToolCardState} is set to {@link ToolCardState#DIESTOCKPICK} so when the playerCommand
     * method is called the user input is parsed to pick a die from the stock
     * @param player object representing the player who wants to activate the card
     * @throws CardNotUsableException if the player has already placed a die or doesn't have enough tokens
     */

    @Override
    public void activateCard(PlayerInGame player) throws CardNotUsableException{
        this.checkDiePlaced(player);
        this.checkRoundBoardDie();
        this.payCard(player);
        this.setState(ToolCardState.DIESTOCKPICK,"Pick a die from the stock, this die will be swapped with one from the roundboard");
    }



    /**
     * This method first checks if the player has already placed his die, then it proceeds to check if the {@link com.model.gameboard.RoundBoard}
     * is empty and the proceed with the card payment (singleplayer)
     * After the payment the {@link ToolCardState} is set to {@link ToolCardState#DIESTOCKPICK} so when the playerCommand
     * method is called the user input is parsed to pick a die from the stock
     * @param player object representing the player who wants to activate the card
     * @throws CardNotUsableException if the player has already placed a die or doesn't have enough tokens
     */
    @Override
    public void activateCard(PlayerInGame player,Dice diePayment) throws CardNotUsableException{
        this.checkDiePlaced(player);
        this.checkRoundBoardDie();
        this.payCard(player,diePayment);
        this.setState(ToolCardState.DIESTOCKPICK,"Pick a die from the stock, this die will be swapped with one from the roundboard");


    }

}
