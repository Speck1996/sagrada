package com.model.cards.concretetoolcards;


import com.model.MoveAbortedException;
import com.model.PlayerInGame;
import com.model.cards.ToolCard;
import com.model.dice.*;
import com.model.patterns.DiceSpace;

import static com.Server.errorMessage;


/**ToolCard FluxRemover: the effect of this card is that the player can pick a die from the
 * {@link Stock}: the selected die is inserted in the {@link DiceBag} and a new one is drafted.
 * The player can decide the shade value of the drafted die: if the modified
 * drafted die can't be placed it is put in the stock.
 * The playerCommand can reach three {@link ToolCardState}: the state {@link ToolCardState#DIESTOCKPICK},
 * {@link ToolCardState#USERDEMAND} and {@link ToolCardState#PLACEDIE}
 * The {@link DiceSpace} contained in this card is of {@link DiceColor#PURPLE}
 */


public class FluxRemover extends ToolCard {


    /**{@inheritDoc}
     * The {@link DiceSpace} is initialized with the color {@link DiceColor#PURPLE}
     * @param id the string representing the card id
     * @param title the string representing the card title
     * @param description the string representing the description of the card
     */
    FluxRemover(String id, String title, String description){
        super(id,title,description);
        toolCardSpace = new DiceSpace(DiceColor.PURPLE,DiceShade.NEUTRAL);
    }


    /** {@inheritDoc}
     * In this method there are three {@link ToolCardState}: in {@link ToolCardState#DIESTOCKPICK} the player can pick a die
     * from the {@link Stock}: if the dice is picked ({@link ToolCardInputHandler#getSelectedDie()} return a dice and not null)
     * it's then inserted in the bag and a new one is drafted. The next {@link ToolCardState} is set to {@link ToolCardState#USERDEMAND}
     * where the player can set new shade value.
     * After setting the shade value, if the dice is placeable the {@link ToolCardState} is set to {@link ToolCardState#PLACEDIE} and the
     * user input is parsed to place the die, otherwise the drafted die is put in the {@link Stock }
     * @param userInput string representing user input
     */
    @Override
    public void playerCommand(String userInput){
        Dice selectedDie;
        Dice draftedDie;

        DiceBag gameBag = gameBoard.getBag();


        if(this.getState() == ToolCardState.DIESTOCKPICK) {

            dieStockPick(userInput);
             selectedDie = parser.getSelectedDie();

            //the dice was selected
            if (selectedDie != null) {

                gameBag.insertDice(new Dice(selectedDie));


                //drawing the dice from the bag, if there is no dice the exception is thrown
                try {
                    draftedDie = gameBag.drawDice();
                } catch (NoDiceException error) {
                    this.setState(ToolCardState.ABORTED, error.getMessage() + " toolcard aborted");
                    return;
                }

                //setting the new dice in the input handler
                parser.setSelectedDie(draftedDie);

                //checking if the die is placeable
                if (toolCardUser.getWindow().isPlaceable(draftedDie, true, true)) {
                    this.setState(ToolCardState.USERDEMAND, "You drafted " + draftedDie.toString()+" now select the shade or press [a] to abort");
                } else {
                    gameBoard.getStock().insertDice(draftedDie);
                    this.setState(ToolCardState.EXECUTED, "You can't place this die,putting it back in the stock, you lost your tokens");
                }
            }
        }


        else if(this.getState() == ToolCardState.USERDEMAND) {

            //user wants to abort the card
            if (userInput.equals("a")) {
                this.setState(ToolCardState.ABORTED, "ToolCard aborted");
                return;
            }

            try {
                //storing the desired shade value
                int value = Integer.parseInt(userInput);

                //value must be included between 1 and 6
                if (!(value > 0 && value < 7)) {
                    this.setState(ToolCardState.USERDEMAND, errorMessage + "wrong input, you selected: " + value + " try again");
                    return;
                }

                draftedDie = parser.getSelectedDie();
                //setting the shade
                draftedDie.setShade(DiceShade.getByValue(value));

            } catch (Exception error) {
                this.setState(ToolCardState.USERDEMAND, errorMessage + error.getMessage());
                return;
            }

            //checking if the die is placeable
            if (toolCardUser.getWindow().isPlaceable(draftedDie, true, true)) {
                this.setState(ToolCardState.PLACEDIE, "Time to place the die: "+draftedDie.toString());
            } else {
                gameBoard.getStock().insertDice(draftedDie);
                this.setState(ToolCardState.EXECUTED, "You can't place this die. Dice put back in the stock, you lost your tokens");
            }
        }

        else if(this.getState() == ToolCardState.PLACEDIE){
            this.placeDie(userInput,true,true);

        }
    }

    /**
     * The placeDie for this toolcard is slightly different: if the player aborts the card execution the dice is put in the stock
     * @param command is the string representing the user input
     * @param colorRestriction if true the dice must respect color restriction otherwise it can be placed in not color matching dicespace
     * @param shadeRestriction if true the dice must respect shade restriction otherwise it can be placed in not shade matching dicespace
     */

    @Override
    protected boolean placeDie(String command, boolean colorRestriction, boolean shadeRestriction){
        try{
            parser.placePickedDie(command,true,true);
            this.toolCardUser.setDicePlayed();
            this.setState(ToolCardState.EXECUTED,"Die placed");
            return true;
        }catch (MoveAbortedException error){
            gameBoard.getStock().insertDice(parser.getSelectedDie());
            this.setState(ToolCardState.EXECUTED,error.getMessage());
            return true;
        }catch(Exception error){
            this.setState(ToolCardState.PLACEDIE,errorMessage+ error.getMessage());
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
        this.setState(ToolCardState.DIESTOCKPICK,"Pick a die from the stock, it will be inserted in the bag");

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
        this.setState(ToolCardState.DIESTOCKPICK,"Pick a die from the stock, it will be inserted in the bag");

    }


}
