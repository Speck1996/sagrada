package com.model.cards.concretetoolcards;


import com.model.PlayerInGame;
import com.model.cards.ToolCard;
import com.model.dice.*;
import com.model.patterns.DiceSpace;


/**ToolCard GrindingStone: the effect of this card is that the player drafts a die
 * and he flip it (1 to 6 or 6 to 1, 2 to 5 and 5 to 2, 3 to 4 and 4 to 3):
 * if the die is placeable then he can proceed to the state {@link ToolCardState#PLACEDIE}
 * otherwise the entire operation is aborted and everything goes back as before
 * and {@link ToolCardState#PLACEDIE}.
 * The {@link DiceSpace} contained in this card is of {@link DiceColor#GREEN}
 */
public class GrindingStone extends ToolCard {


    /**
     * {@inheritDoc}
     * The {@link DiceSpace} is initialized with the color {@link DiceColor#GREEN}
     *
     * @param id          the string representing the card id
     * @param title       the string representing the card title
     * @param description the string representing the description of the card
     */
    GrindingStone(String id, String title, String description) {
        super(id, title, description);
        toolCardSpace = new DiceSpace(DiceColor.GREEN, DiceShade.NEUTRAL);
    }


    /**
     * {@inheritDoc}
     * In this method there are two  {@link ToolCardState}: in {@link ToolCardState#DIESTOCKPICK} the player can pick a die
     * from the {@link Stock}: if the dice is picked ({@link ToolCardInputHandler#getSelectedDie()} return a dice and not null)
     * it's then flipped. If the flipped dice can be placed the next {@link ToolCardState} is set to {@link ToolCardState#PLACEDIE}
     * where the input is parsed by the{@link ToolCardInputHandler} to place the die
     * otherwise the entire operations is aborted (everything goes back as before, the user loses his tokens)
     * and the {@link ToolCardState} is set to {@link ToolCardState#ABORTED}
     *
     * @param userInput string representing user input
     */
    @Override
    public void playerCommand(String userInput) {


        if (this.getState() == ToolCardState.DIESTOCKPICK) {

            //parsing user input
            this.dieStockPick(userInput);
            Dice selectedDie = parser.getSelectedDie();
            //if dice is selected it is flipped and card goes into place die mode
            if (selectedDie != null) {

                //flipping
                selectedDie.flipDice();


                //if placeable next state is placedie otherwise the card is aborted
                if (toolCardUser.getWindow().isPlaceable(parser.getSelectedDie(), true, true)) {
                    this.setState(ToolCardState.PLACEDIE, "New die value: " + parser.getSelectedDie().getShade().toString() + "\nTime to place the die");
                } else {
                    this.setState(ToolCardState.ABORTED, "You can't place this die, you lost your tokens");
                }
            }
        } else if (this.getState() == ToolCardState.PLACEDIE) {

            //user has placed his die
            if (this.placeDie(userInput, true, true)) {
                this.toolCardUser.setDicePlayed();
            }
        }

    }


    /**{@inheritDoc}
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
        this.setState(ToolCardState.DIESTOCKPICK,"Pick a die from the stock, this die will be flipped (6<-->1,5<-->2,4<-->3");
    }


    /**{@inheritDoc}
     * This method first checks if the player has already placed his die, then it proceeds to the card payment (multiplayer)
     * and the set the {@link ToolCardState} to {@link ToolCardState#DIESTOCKPICK} so when the playerCommand
     * method is called the user input is parsed to pick a die from the stock
     * @param player object representing the player who wants to activate the card
     * @throws CardNotUsableException if the player has already placed a die or doesn't have enough tokens
     */

    @Override
    public void activateCard(PlayerInGame player, Dice diePayment) throws CardNotUsableException{
        this.checkDiePlaced(player);
        this.payCard(player,diePayment);
        this.setState(ToolCardState.DIESTOCKPICK,"Pick a die from the stock, this die will be flipped (6<-->1,5<-->2,4<-->3");
    }
}
