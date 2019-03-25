package com.model.cards.concretetoolcards;

import com.model.cards.ToolCard;
import com.model.cards.WrongIdException;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class  ToolCardFactoryTest {
    ToolCardFactory demofactory = new ToolCardFactory();
    ToolCard demotoolcard0 = new GrozingPliers("CTA00", "Grozing Pliers", "After drafting, increase or decrease the value of the drafted die by 1. 1 may not change to 6, or 6 to 1.");
    ToolCard demotoolcard1 = new EglomiseBrush("CTA10", "Eglomise Brush", "Move any one die in your window ignoring the color restriction. You must obey all other placement restrictions");
    ToolCard demotoolcard2 = new CopperFoilBurnisher("CTA20", "Copper Foil Burnisher", "Move any one die in your window ignoring shade restriction. You must obey all other placement restrictions.");
    ToolCard demotoolcard3 = new Lathekin("CTA30", "Lathekin", "Move exactly two dice, obeying all placement restrictions.");
    ToolCard demotoolcard4 = new LensCutter("CTA40", "Lens Cutter", "After drafting, swap the drafted die with a die from the Round Track.");
    ToolCard demotoolcard5 = new FluxBrush("CTA50", "Flux Brush", "After drafting, reroll the drafted die. If it cannot be placed, return it to the Draft Pool.");
    ToolCard demotoolcard6 = new GlazingHammer("CTA60", "Glazing Hammer", "Re-roll all dice in the Draft Pool. This may only be used on your second turn before drafting.");
    ToolCard demotoolcard7 = new RunningPliers("CTA70", "Running Pliers", "After your turn, immediatly draft a die. Skip your next turn this round.");
    ToolCard demotoolcard8 = new CorkBackedStraightedge("CTA80", "Cork-backed Straighedge", "After drafting, place the diee in a spot that is not adjacent to another diee. You must obey all other placement restrictions.");
    ToolCard demotoolcard9 = new GrindingStone("CTA90", "Grinding Stone", "After drafting, flip the die to its opposite side. 6 flips to 1, 5 to 2, 4 to 3, etc.");
    ToolCard demotoolcard10 = new FluxRemover("CTA11", "Flux Remover", "After drafting, return the die to the DiceBag and pull 1 die from the bag. Choose a value and place the new die, obeying all placemente restrictions, or return it to the Draft Pool.");
    ToolCard demotoolcard11 = new TapWheel("CTA12", "Tap Wheel", "Move up two dice of the same color that match the color of a die on the Round Track. You must obey all placement restrictions.");

    ArrayList<ToolCard> testlist = new ArrayList<>();



    ToolCard demotoolcardtest = new EglomiseBrush("CTA11", "Eglomise Brush", "Move any one die in your window ignoring the color restriction. You must obey all other placement restrictions");
    @Test
    public void checkId(){
        Assert.assertFalse(demofactory.checkToolCardId("CTK00"));
        Assert.assertTrue(demofactory.checkToolCardId("CTA00"));
    }

    @Test
    public void checkCardCreation()throws WrongIdException {
        Assert.assertEquals(demotoolcard1, demofactory.createToolCard("CTA10", "Eglomise Brush", "Move any one die in your window ignoring the color restriction. You must obey all other placement restrictions"));
        Assert.assertEquals(demotoolcard0, demofactory.createToolCard("CTA00", "Grozing Pliers", "After drafting, increase or decrease the value of the drafted die by 1. 1 may not change to 6, or 6 to 1."));
        Assert.assertEquals(demotoolcard2, demofactory.createToolCard("CTA20", "Copper Foil Burnisher", "Move any one die in your window ignoring shade restriction. You must obey all other placement restrictions."));
        Assert.assertEquals(demotoolcard3, demofactory.createToolCard("CTA30", "Lathekin", "Move exactly two dice, obeying all placement restrictions."));
        Assert.assertEquals(demotoolcard4, demofactory.createToolCard("CTA40", "Lens Cutter", "After drafting, swap the drafted die with a die from the Round Track."));
        Assert.assertEquals(demotoolcard5, demofactory.createToolCard("CTA50", "Flux Brush", "After drafting, reroll the drafted die. If it cannot be placed, return it to the Draft Pool."));
        Assert.assertEquals(demotoolcard6, demofactory.createToolCard("CTA60", "Glazing Hammer", "Re-roll all dice in the Draft Pool. This may only be used on your second turn before drafting."));
        Assert.assertEquals(demotoolcard7, demofactory.createToolCard("CTA70", "Running Pliers", "After your turn, immediatly draft a die. Skip your next turn this round."));
        Assert.assertEquals(demotoolcard8, demofactory.createToolCard("CTA80", "Cork-backed Straighedge", "After drafting, place the diee in a spot that is not adjacent to another diee. You must obey all other placement restrictions."));
        Assert.assertEquals(demotoolcard9, demofactory.createToolCard("CTA90", "Grinding Stone", "After drafting, flip the die to its opposite side. 6 flips to 1, 5 to 2, 4 to 3, etc."));
        Assert.assertEquals(demotoolcard10, demofactory.createToolCard("CTA11", "Flux Remover", "After drafting, return the die to the DiceBag and pull 1 die from the bag. Choose a value and place the new die, obeying all placemente restrictions, or return it to the Draft Pool."));
        Assert.assertEquals(demotoolcard11, demofactory.createToolCard("CTA12", "Tap Wheel", "Move up two dice of the same color that match the color of a die on the Round Track. You must obey all placement restrictions."));

        Assert.assertNotEquals(demotoolcardtest, demofactory.createToolCard("CTA10", "Eglomise Brush", "Move any one die in your window ignoring the color restriction. You must obey all other placement restrictions"));
    }





    @Test
    public void testCreateClones()throws WrongIdException{
        testlist.add(demotoolcard0);
        testlist.add(demotoolcard1);
        testlist.add(demotoolcard2);
        testlist.add(demotoolcard3);
        testlist.add(demotoolcard4);
        testlist.add(demotoolcard5);
        testlist.add(demotoolcard6);
        testlist.add(demotoolcard7);
        testlist.add(demotoolcard8);
        testlist.add(demotoolcard9);
        testlist.add(demotoolcard10);
        testlist.add(demotoolcard11);
        List<ToolCard> testedlist = demofactory.cloneToolCards(testlist);
        for(int i = 0; i < testlist.size(); i++){
            Assert.assertEquals(testedlist.get(i), testlist.get(i));
        }
    }

}
