package com.model.cards.concretetoolcards;

/**Enum class with all the reachable state of a ToolCard
 * @see com.model.cards.ToolCard
 */
public enum ToolCardState {
        NEUTRAL, DIESTOCKPICK, USERDEMAND,PLACEDIE,PICKWINDOWDIE,ROUNDBOARDPICK,EXECUTED, ABORTED;

        //NEUTRAL --> START STATE
        //DIESTOCKPICK --> STOCK INDEX SELECTION
        //USERDEMAND --> PARTICULAR USER INTERACTION (EXAMPLE: INCREASE/DECREASE DIE VALUE)
        //PLACEDIE --> PLACE MODIFIED DIE
        //MOVEDIE -->MOVE A DIE
        //ROUNDBOARDPICK --> PICKING A ROUNDBOARD DIE COORDINATES
        //EXECUTED --> EFFECT APPLIED
        //ABORTED --> TOOLCARD EXECUTION ABORTED DUE USER DECISION
}
