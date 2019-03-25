package com.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class MainModelTest {

    @Test
    public void loginLogoutTest() {
        MainModel model = MainModel.getModel();

        try {
            model.login("aaaaaaaaaaaaaaaaa", "a");
            fail("It was expected a LoginException");
        } catch (LoginException e) {
            assertEquals("username too long (max 16)", e.getMessage());
        }


        String token = null;
        try {
            token = model.login("abc", "abc");
            assertEquals("abc", model.getPlayerByToken(token).getUsername());
        } catch (LoginException e) {
            fail("unexpected exception");
        }

        try {
            model.login("abc", "abc");
            fail("It was expected a LoginException");
        } catch (LoginException e) {
            assertEquals("user already logged", e.getMessage());
        }

        model.logout(token);
        assertNull(model.getPlayerByToken(token));

        try {
            token = model.login("zzz", "z");
            assertEquals("zzz", model.getPlayerByToken(token).getUsername());

            PlayerInGame p = new PlayerInGame(model.getPlayerByToken(token), null);
            p.setOffline();
            model.logout(p);
            assertNull("zzz", model.getPlayerByToken(token));
        } catch (LoginException e) {
            fail("unexpected exception");
        }

        try {
            token = model.login("zzz", "z");
            assertEquals("zzz", model.getPlayerByToken(token).getUsername());

            PlayerInGame p = new PlayerInGame(model.getPlayerByToken(token), null);
            model.logout(p);
            assertNotNull("zzz", model.getPlayerByToken(token));
        } catch (LoginException e) {
            fail("unexpected exception");
        }



        try {
            model.login("aa", "a");
            fail("It was expected a LoginException");
        } catch (LoginException e) {
            assertEquals("wrong password", e.getMessage());
        }

    }


}