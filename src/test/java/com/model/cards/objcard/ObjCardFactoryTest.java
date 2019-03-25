package com.model.cards.objcard;

import com.model.cards.ObjCard;
import com.model.cards.WrongIdException;
import com.model.dice.DiceColor;
import com.model.dice.DiceShade;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


public class ObjCardFactoryTest {
    ObjCardFactory demoobjcardfactory = new ObjCardFactory();
    ObjCard demoocard= new RowColorVariety("COF60","Row Color Variety","Rows with no repeated colors",6);
    ArrayList<ObjCard> clonedlist = new ArrayList<>();
    @Test
    public void checkIdTest(){
        Assert.assertFalse(demoobjcardfactory.checkObjId("CATT"));
        Assert.assertTrue(demoobjcardfactory.checkObjId("COTT"));
    }

    @Test
    public void checkCreateCard()throws WrongIdException{

            Assert.assertEquals(demoocard, (demoobjcardfactory.createObjCard("COF60", "Row Color Variety", "Rows with no repeated colors", 6)));


       demoocard = new ColumnColorVariety("COF50","Column Color Variety","Columns with no repeated colors.",5);
        clonedlist.add(demoocard);
        Assert.assertEquals(demoocard,(demoobjcardfactory.createObjCard("COF50","Column Color Variety","Columns with no repeated colors.",5)));
        demoocard = new ColumnShadeVariety("COF40","Column Shade Variety","Columns with no repeadted values.",4);
        clonedlist.add(demoocard);
        Assert.assertEquals(demoocard,(demoobjcardfactory.createObjCard("COF40","Column Shade Variety","Columns with no repeadted values.",4)));
        demoocard = new RowShadeVariety("COF51", "Row Shade Variety", "Rows with no repeated values.", 5);
        clonedlist.add(demoocard);
        Assert.assertEquals(demoocard,(demoobjcardfactory.createObjCard("COF51", "Row Shade Variety", "Rows with no repeated values.", 5)));
        demoocard = new ColorVariety("COF41", "Color Variety", "Sets of one of each color anywhere.", 4);
        clonedlist.add(demoocard);
        Assert.assertEquals(demoocard,(demoobjcardfactory.createObjCard("COF41", "Color Variety", "Sets of one of each color anywhere.", 4)));
        demoocard = new ShadeVariety("COF52","Shade Variety", "Sets of one of each value anywhere.", 5);
        clonedlist.add(demoocard);
        Assert.assertEquals(demoocard,(demoobjcardfactory.createObjCard("COF52","Shade Variety", "Sets of one of each value anywhere.", 5)));
        demoocard = new PairsShades("COF20","Deep Shades", "Sets of 5 & 6 values anywhere.", 2, DiceShade.FIVE, DiceShade.SIX);  //DeepShades
        clonedlist.add(demoocard);
        Assert.assertEquals(demoocard,(demoobjcardfactory.createObjCard("COF20","Deep Shades", "Sets of 5 & 6 values anywhere.", 2)));
        demoocard = new PairsShades("COF21", "Medium Shades", "Sets of 3 & 4 values anywhere.",2, DiceShade.THREE, DiceShade.FOUR);  //MediumShades
        clonedlist.add(demoocard);
        Assert.assertEquals(demoocard,(demoobjcardfactory.createObjCard("COF21", "Medium Shades", "Sets of 3 & 4 values anywhere.",2)));
        demoocard = new PairsShades("COF22","Light Shades", "Sets of 1 & 2 values anywhere.", 2, DiceShade.ONE, DiceShade.TWO);  //LightShades
        clonedlist.add(demoocard);
        Assert.assertEquals(demoocard,(demoobjcardfactory.createObjCard("COF22","Light Shades", "Sets of 1 & 2 values anywhere.", 2)));
        demoocard = new ColorDiagonals("COF00", "Color diagonals", "Count of diagonally adjacent same color dice.", 1);
        clonedlist.add(demoocard);
        Assert.assertEquals(demoocard,(demoobjcardfactory.createObjCard("COF00", "Color diagonals", "Count of diagonally adjacent same color dice.", 1)));
        demoocard = new PrivateObjCard("COTP0", "Shades of Purple", "Sum of values on purple dice", 1,DiceColor.PURPLE);
        clonedlist.add(demoocard);
        Assert.assertEquals(demoocard,(demoobjcardfactory.createObjCard("COTP0", "Shades of Purple", "Sum of values on purple dice", 1)));
        demoocard = new PrivateObjCard("COTR0", "Shades of Red", "Sum of values on red dice", 1,DiceColor.RED);
        clonedlist.add(demoocard);
        Assert.assertEquals(demoocard,(demoobjcardfactory.createObjCard("COTR0", "Shades of Red", "Sum of values on red dice", 1)));
        demoocard = new PrivateObjCard("COTY0", "Shades of Yellow", "Sum of values on yellow dice", 1,DiceColor.YELLOW);
        clonedlist.add(demoocard);
        Assert.assertEquals(demoocard,(demoobjcardfactory.createObjCard("COTY0", "Shades of Yellow", "Sum of values on yellow dice", 1)));
        demoocard = new PrivateObjCard("COTG0", "Shades of Green", "Sum of values on green dice", 1,DiceColor.GREEN);
        clonedlist.add(demoocard);
        Assert.assertEquals(demoocard,(demoobjcardfactory.createObjCard("COTG0", "Shades of Green", "Sum of values on green dice", 1)));
        demoocard = new PrivateObjCard("COTB0", "Shades of Blue", "Sum of values on blue dice", 1,DiceColor.BLUE);
        clonedlist.add(demoocard);
        Assert.assertEquals(demoocard,(demoobjcardfactory.createObjCard("COTB0", "Shades of Blue", "Sum of values on blue dice", 1)));

    }

    @Test
    public void checkCloneObjCard() throws WrongIdException{
        List<ObjCard> testlist = new ArrayList<>();
        testlist = demoobjcardfactory.cloneObjCards(clonedlist);
        for(int i = 0; i < clonedlist.size();i++){
            Assert.assertEquals(testlist.get(i),clonedlist.get(i));
        }


    }
}
