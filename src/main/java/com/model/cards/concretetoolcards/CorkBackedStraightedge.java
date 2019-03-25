package com.model.cards.concretetoolcards;


import static com.Server.errorMessage;


import com.model.MoveAbortedException;
import com.model.PlayerInGame;
import com.model.cards.ToolCard;
import com.model.dice.*;
import com.model.patterns.DiceSpace;

/**ToolCard CorkBackedStraightEdge: the effect of this card is that the player can place
 * a die in a isolated space using the playerCommand method. The states that can be reached player
 * command are {@link ToolCardState#DIESTOCKPICK} and {@link ToolCardState#PLACEDIE}.
 * The {@link DiceSpace} contained in this card is of {@link DiceColor#YELLOW}
 */
public class CorkBackedStraightedge extends ToolCard {

    /**{@inheritDoc}
     * The {@link DiceSpace} is initialized with the color {@link DiceColor#YELLOW}
     * @param id the string representing the card id
     * @param title the string representing the card title
     * @param description the string representing the description of the card
     */
   CorkBackedStraightedge(String id, String title, String description){

        super(id, title,description);
        this.toolCardSpace = new DiceSpace(DiceColor.YELLOW,DiceShade.NEUTRAL);
   }


    /** {@inheritDoc}
     * In this method there are two  {@link ToolCardState}: in {@link ToolCardState#DIESTOCKPICK} the player can pick a die
     * from the {@link Stock}. If the dice is picked the next state is set to {@link ToolCardState#PLACEDIE},
     * so in the next method call the input is parsed to place the isolated dice
     * @param userInput string representing user input
     */

   @Override
   public void playerCommand(String userInput){

            //picking die from stock
            if(this.getState() == ToolCardState.DIESTOCKPICK) {
                try {

                    parser.pickStockDie(gameBoard.getStock(), userInput);

                    //checks if the die can be placed, if not the card is aborted
                    if (toolCardUser.getWindow().isPlaceableIsolate(parser.getSelectedDie())) {
                        this.setState(ToolCardState.PLACEDIE, "Now place the die");
                    } else {
                        this.setState(ToolCardState.ABORTED, "You picked a die you can't place, you lost your tokens");
                    }


                    //user aborted the move
                } catch (MoveAbortedException error) {
                    this.setState(ToolCardState.ABORTED, error.getMessage());

                    //can't place die in this place
                } catch (Exception error) {
                    this.setState(ToolCardState.DIESTOCKPICK, errorMessage + error.getMessage());

                }
            }
            else if(this.getState() == ToolCardState.PLACEDIE) {
                //picking space coordinates
                this.placeDie(userInput, true, true);
            }

   }


    /**The placeDie for this toolcard is slightly different: the dice has to be placed in an isolated space
     * @param command is the string representing the user input
     * @param colorRestriction if true the dice must respect color restriction otherwise it can be placed in not color matching dicespace
     * @param shadeRestriction if true the dice must respect shade restriction otherwise it can be placed in not shade matching dicespace
     */
   @Override
   protected boolean placeDie(String command, boolean colorRestriction, boolean shadeRestriction){
        Integer[] coordinates;

        //picking space coordinates
        try{
            coordinates = parser.getCoordinates(command);
            toolCardUser.getWindow().placeDiceIsolated(coordinates[0],coordinates[1],parser.getSelectedDie());
            toolCardUser.notifyViewObserver(toolCardUser.getWindow().toString());
            this.toolCardUser.setDicePlayed();

            this.setState(ToolCardState.EXECUTED,"Die placed in the isolated space");
            return  true;
            //user aborted the move
        }catch (MoveAbortedException error){
            this.setState(ToolCardState.ABORTED,error.getMessage());
            return false;
            //can't place die in this place
        }catch(Exception error){
            this.setState(ToolCardState.PLACEDIE,errorMessage + error.getMessage());
            return false;
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
       this.setState(ToolCardState.DIESTOCKPICK,"Pick a die from the stock, you have to place this die in an isolated position");
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
    public  void activateCard(PlayerInGame player,Dice diePayment) throws CardNotUsableException{

       this.checkDiePlaced(player);
       this.payCard(player,diePayment);
       this.setState(ToolCardState.DIESTOCKPICK,"Pick a die from the stock, you have to place this die in an isolated position");
   }
}
