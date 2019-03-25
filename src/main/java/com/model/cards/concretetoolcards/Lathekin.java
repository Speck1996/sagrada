package com.model.cards.concretetoolcards;



import com.model.MoveAbortedException;
import com.model.PlayerInGame;
import com.model.cards.ToolCard;
import com.model.dice.*;
import com.model.patterns.DiceSpace;
import com.model.patterns.NoMovableDiceException;


import static com.Server.errorMessage;

/**ToolCard Lathekin: the effect of this card is that a user moves to dice respecting placement restrictions:
 * If in the mid of the execution is found that the user can't place two die the {@link ToolCardState} is set to
 * {@link ToolCardState#ABORTED}. This card works with two {@link ToolCardState} and a integer counting if the user
 * has moved his first die or not. In {@link ToolCardState#PICKWINDOWDIE} the users input is parsed to pick the window
 * die, in {@link ToolCardState#PLACEDIE} the user place the picked die (he can't move the die back where it was): if
 * the counter is 0 then the {@link ToolCardState} after the {@link ToolCardState#PLACEDIE} is {@link ToolCardState#PICKWINDOWDIE}
 * otherwise the toolcard execution is finished.
 *  * The {@link DiceSpace} contained in this card is of {@link DiceColor#YELLOW}
 */
public class Lathekin extends ToolCard {

    /**Integer used to count how many dice are moved*/
    private int counter;

    /**{@inheritDoc}
     *The counter is initialized to 0
     *The {@link DiceSpace} is initialized with the color {@link DiceColor#YELLOW}
     * @param id the string representing the card id
     * @param title the string representing the card title
     * @param description the string representing the description of the card
     */

    public Lathekin(String id, String title, String description) {
        super(id, title, description);
        this.toolCardSpace = new DiceSpace(DiceColor.YELLOW,DiceShade.NEUTRAL);
        counter = 0;
    }


    /**{@inheritDoc}
     * In this method there are two {@link ToolCardState}: in {@link ToolCardState#PICKWINDOWDIE} the user input is
     * parsed by the {@link ToolCardInputHandler} to pick the window die. If the die is picked the next {@link ToolCardState}
     * is set to {@link ToolCardState#PLACEDIE}: if the die that has to be placed is the first die the next {@link ToolCardState}
     * is set to {@link ToolCardState#PICKWINDOWDIE} otherwise is set to {@link ToolCardState#EXECUTED}
     * @param userInput string corresponding to user input
     */
    @Override
    public void playerCommand(String userInput){

        //picking die from window
        if(this.getState() == ToolCardState.PICKWINDOWDIE)
                this.pickWindowDie(userInput,DiceColor.NEUTRAL,DiceShade.NEUTRAL);

        //placing the die
        else if(this.getState() == ToolCardState.PLACEDIE)
                this.placeDie(userInput,true,true);
    }


    /**The place die for this ToolCard is slightly different: after parsing the input and placing the die the next state
     * is set depending on the counter (if the user has moved his first die the the next {@link ToolCardState} is
     * {@link ToolCardState#PICKWINDOWDIE} otherwise the card is executed.
     * @param command is the string representing the user input
     * @param colorRestriction if true the dice must respect color restriction otherwise it can be placed in not color matching dicespace
     * @param shadeRestriction if true the dice must respect shade restriction otherwise it can be placed in not shade matching dicespace
     */
    @Override
    protected boolean placeDie(String command, boolean colorRestriction, boolean shadeRestriction){

        try{

            //after placing the die the user is notified with the updated window
            parser.placePickedDie(command,true,true);
            toolCardUser.notifyViewObserver(toolCardUser.getWindow().toString());

            //user has moved his first die
            if(counter == 0){

                //next state is PICKWINDOWDIE,where the user has to pick his second die
                try{
                    toolCardUser.getWindow().checkMovable(DiceColor.NEUTRAL, true, true, parser.getAlreadyUsedCoordinates());
                    setState(ToolCardState.PICKWINDOWDIE,"Move your second die");

                    //updating the counter
                    counter ++;
                    return true;
                 //after moving the first die the are no movable dice anymore
                }catch (NoMovableDiceException error) {
                    setState(ToolCardState.ABORTED, "You can't move a second die, you lost your tokens");
                }

            //user has moved his second die
            }else{
                this.setState(ToolCardState.EXECUTED,"Die placed");
                return true;
            }

         //user aborted the move
        }catch (MoveAbortedException error){
            this.setState(ToolCardState.ABORTED,error.getMessage());
        //can't place die in this place
        }catch(Exception error){
            this.setState(ToolCardState.PLACEDIE,errorMessage+  error.getMessage());
        }
        return false;
    }



    /**
     * This method first checks if there is a movable die in the window, then it proceeds to the card payment (multiplayer)
     * and the set the {@link ToolCardState} to {@link ToolCardState#PICKWINDOWDIE} so when the playerCommand
     * method is called the user input is parsed to pick a die from the window.
     * The counter is resetted to 0 during the activation phase
     * @param player object representing the player who wants to activate the card
     * @throws CardNotUsableException if there is no movable die or the player doesn't have enough tokens
     */

    @Override
    public void activateCard(PlayerInGame player) throws CardNotUsableException{
        this.checkMovableDice(player,true,true);
        this.payCard(player);
        counter = 0;
        this.setState(ToolCardState.PICKWINDOWDIE,"Move your first die");

    }

    /**
     * This method first checks if there is a movable die in the window, then it proceeds to the card payment (singleplayer)
     * and the set the {@link ToolCardState} to {@link ToolCardState#PICKWINDOWDIE} so when the playerCommand
     * method is called the user input is parsed to pick a die from the window.
     * The counter is resetted to 0 during the activation phase
     * @param player object representing the player who wants to activate the card
     * @throws CardNotUsableException if there is no movable die or the player doesn't have enough tokens
     */

    @Override
    public void activateCard(PlayerInGame player,Dice diePayment) throws CardNotUsableException{
        this.checkMovableDice(player,true,true);
        this.payCard(player,diePayment);
        counter = 0;
        this.setState(ToolCardState.PICKWINDOWDIE,"Move your first die");

    }

}
