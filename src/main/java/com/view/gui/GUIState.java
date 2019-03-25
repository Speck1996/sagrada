package com.view.gui;

/**
 * This Enumeration handles all different states for GUI player:
 * NORMAL: means the standard game situation
 * TOOLCARD: means that the player is currently using a toolcard
 * CHOOSING: means that the player pressed the Tool Card Button, but he or she has not yet chosen which Tool Card wants to use
 * RUNNINGPLIERS: means that the player is in the particular condition of Running Pliers Tool Card activation, so he or she
 * has one extra turn
 */
public enum GUIState {
    NORMAL, TOOLCARD, CHOOSING, RUNNINGPLIERS
}
