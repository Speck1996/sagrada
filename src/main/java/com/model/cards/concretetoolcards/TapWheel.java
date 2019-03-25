package com.model.cards.concretetoolcards;

import com.model.MoveAbortedException;
import com.model.PlayerInGame;
import com.model.cards.ToolCard;
import com.model.dice.*;
import com.model.patterns.DiceSpace;
import com.model.patterns.NoMovableDiceException;

import static com.Server.errorMessage;


/**ToolCard TapWheel: the effect of this card is that the user can move a number of dice lower than three
 * (If he wants he can not move any die) of a color matching a chosen {@link com.model.gameboard.RoundBoard} dice
 * This card has four reachable {@link ToolCardState}: {@link ToolCardState#USERDEMAND} where the user input states if
 * the user wants to move the die, {@link ToolCardState#ROUNDBOARDPICK} where the user input must correspond to the coordinates
 * of a dice in the {@link com.model.gameboard.RoundBoard}, {@link ToolCardState#PICKWINDOWDIE} where the user input is parsed to pick a die from the window
 * matching the color of the selected dice in the {@link com.model.gameboard.RoundBoard}. Once a dice is selected the {@link ToolCardState}
 * is set to {@link ToolCardState#PLACEDIE} where the user can finally move his dice.
 * The {@link DiceSpace} contained in this card is of {@link DiceColor#BLUE}
 */


public class TapWheel extends ToolCard {

    /**Used to store the color of the chosen roundboard dice*/
    private DiceColor fixedColor;

    /**used to count how many dice are moved during the execution*/
    private int counter;

    /**{@inheritDoc}
     * The {@link DiceSpace} is initialized with the color {@link DiceColor#BLUE}
     * the counter is set to 0
     * @param id the string representing the card id
     * @param title the string representing the card title
     * @param description the string representing the description of the card
     */
    TapWheel(String id, String title, String description) {
        super(id, title, description);
        toolCardSpace = new DiceSpace(DiceColor.BLUE,DiceShade.NEUTRAL);
        this.counter = 0;
    }


    /**In this method there are four {@link ToolCardState}: the first {@link ToolCardState} reached is the {@link ToolCardState#USERDEMAND},
     * in this state the input is parsed to check if the user wants to move his first die or not. If the user string corresponds to
     * "Y" the next state is set to {@link ToolCardState#ROUNDBOARDPICK} to choose the color of {@link com.model.gameboard.RoundBoard} dice
     * This will be the color that all the movable dice must match otherwise if the string is "N" the next state is set to {@link ToolCardState#EXECUTED}.
     * After choosing the color the {@link ToolCardState} is set to {@link ToolCardState#PICKWINDOWDIE} where the user string parsed to select
     * a die from the window that must match the selected color. After picking the die from the window the {@link ToolCardState} is set to
     * {@link ToolCardState#PLACEDIE}, here the string is parsed to move the die: if the moved die is the first then the counter is increased
     * and the next state is set to {@link ToolCardState#USERDEMAND}, asking the user if he wants to move another die, otherwise the state is
     * set to {@link ToolCardState#EXECUTED}
     * @param userInput string representing user input
     */
    @Override
    public void playerCommand(String userInput) {


        //where the round coordinates are temporally stored
        Integer[] roundCoordinates;


        if (this.getState() == ToolCardState.ROUNDBOARDPICK) {

            //parsing round coordiates

            try {

                //parsing the coordinates and storing the color value of the dice selected
                roundCoordinates = parser.selectRoundCoordinates(gameBoard.getGameRoundBoard(), userInput);
                int round = roundCoordinates[0];
                int dieIndex = roundCoordinates[1];

                fixedColor = gameBoard.getGameRoundBoard().getDice(round).get(dieIndex).getColor();

                //checking if there is any movable die in the window
                toolCardUser.getWindow().checkMovable(fixedColor, true, true, null);

                this.setState(ToolCardState.PICKWINDOWDIE, "Select your first die to move, remember its color must be " + fixedColor.toString());

                //user decided to abort the card execution
            } catch (MoveAbortedException error) {
                this.setState(ToolCardState.ABORTED, error.getMessage());

                //no movable dice
            } catch (NoMovableDiceException error) {
                this.setState(ToolCardState.EXECUTED, errorMessage + error.getMessage() + " ToolCard execution automatically terminated");
            }

            //wrong syntax input
            catch (Exception error) {
                this.setState(ToolCardState.ROUNDBOARDPICK, errorMessage + error.getMessage() + " try again");
            }

        }

        //specific user request handling (in this particular case checks if the player wants to move the dice)
        else if (this.getState() == ToolCardState.USERDEMAND) {

            switch (userInput) {

                //user wants to move the die
                case "Y":

                    //first die still has to be moved and fixed color still has to be set
                    if (counter == 0) {
                        this.setState(ToolCardState.ROUNDBOARDPICK, "Choose the color of selectable movable dice");

                        //first die already moved, checking if there is the second movable die of the fixed color
                    } else {

                        //setting the state to pickwindowdie
                        try {
                            //already used coordinates are the coordinates of the dice already moved
                            toolCardUser.getWindow().checkMovable(fixedColor, true, true, parser.getAlreadyUsedCoordinates());
                            this.setState(ToolCardState.PICKWINDOWDIE, "Select your second die to move");

                        } catch (NoMovableDiceException error) {
                            this.setState(ToolCardState.EXECUTED, errorMessage + error.getMessage() + " ToolCard execution automatically terminated");
                        }
                    }
                    break;

                //user doesn't want to move the die
                case "N":
                    this.setState(ToolCardState.EXECUTED, "Dice not moved");
                    break;

                //user wants to abort the card execution
                case "a":
                    this.setState(ToolCardState.ABORTED, "ToolCard aborted");
                    break;

                //input not recognized
                default:
                    this.setState(ToolCardState.USERDEMAND, errorMessage + "wrong input\n" + "Pls insert [Y/N] or [a] to abort");
            }

         //user has to pick his die
        } else if (this.getState() == ToolCardState.PICKWINDOWDIE){
            this.pickWindowDie(userInput, fixedColor, DiceShade.NEUTRAL);

         }

        //user must place his die
        else if(this.getState() == ToolCardState.PLACEDIE){
                this.placeDie(userInput,true,true);
        }
    }


    /**The placeDie for this card is slightly different: if the placed die corresponds to the first one then the next state is set to {@link ToolCardState#USERDEMAND}
     * where the user can decide if he wants to move another die or not, otherwise the card state is set to {@link ToolCardState#EXECUTED}
     * @param command is the string representing the user input
     * @param colorRestriction if true the dice must respect color restriction otherwise it can be placed in not color matching dicespace
     * @param shadeRestriction if true the dice must respect shade restriction otherwise it can be placed in not shade matching dicespace
     */
    @Override
    protected boolean placeDie(String command, boolean colorRestriction, boolean shadeRestriction){


        try{
            //placing the die
            parser.placePickedDie(command,true,true);

            //user placed his first die: he can now decide if he wants to move the second die
            if(counter == 0){
                setState(ToolCardState.USERDEMAND,"Now decide if you want to move your second die: [Y/N] or [a] to abort");
                counter++;
                return true;
            //user moved both the dice
            }else{
                this.setState(ToolCardState.EXECUTED,"Die placed");
                return true;
            }

            //user aborted the move
        }catch (MoveAbortedException error){
            this.setState(ToolCardState.ABORTED,error.getMessage());

            //can't place die in this place
        }catch(Exception error){
            this.setState(ToolCardState.PLACEDIE,errorMessage+ error.getMessage());
        }
        return false;
    }


    /**
     * This method first checks if the player has already placed his die, then it proceeds to check if the {@link com.model.gameboard.RoundBoard}
     * is empty and the proceed with the card payment (multiplayer)
     * After the payment the counter is resetted and the {@link ToolCardState} is set to {@link ToolCardState#USERDEMAND} so when the playerCommand
     * method is called the user input is parsed to get user instruction of moving a dice or not
     * @param player object representing the player who wants to activate the card
     * @throws CardNotUsableException if the player has already placed a die or doesn't have enough tokens
     */
    @Override
    public void activateCard(PlayerInGame player) throws CardNotUsableException{
        this.checkRoundBoardDie();
        this.payCard(player);
        this.counter = 0;
        this.setState(ToolCardState.USERDEMAND,"Do you want to move any die [Y/N] or press [a] to abort");
    }



    /**
     * This method first checks if the player has already placed his die, then it proceeds to check if the {@link com.model.gameboard.RoundBoard}
     * is empty and the proceed with the card payment (singleplayer)
     * After the payment the counter is resetted and {@link ToolCardState} is set to {@link ToolCardState#USERDEMAND} so when the playerCommand
     * method is called the user input is parsed to get user instruction of moving a dice or not
     * @param player object representing the player who wants to activate the card
     * @throws CardNotUsableException if the player has already placed a die or doesn't have enough tokens
     */
    @Override
    public void activateCard(PlayerInGame player,Dice diePayment) throws CardNotUsableException{
        this.checkRoundBoardDie();
        this.payCard(player,diePayment);
        this.counter = 0;
        this.setState(ToolCardState.USERDEMAND,"Do you want to move any die [Y/N] or press [a] to abort");
    }

}
