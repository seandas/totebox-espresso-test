package com.seandas.totebox.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SessionTest {
    @Before
    fun signedOut() = Session.logOut()

    @Test
    fun guestCanShopButIsNotLoggedIn() {
        Session.continueAsGuest()
        assertTrue(Session.isShopping)
        assertFalse(Session.isLoggedIn)
    }

    @Test
    fun loggingInKeepsTheGuestCart() {
        Session.continueAsGuest()
        Cart.add(1, 2)
        Session.logIn("sam@example.com")
        assertTrue(Session.isLoggedIn)
        assertFalse(Session.isGuest)
        assertEquals(2, Cart.quantityOf(1))
    }

    @Test
    fun anyEmailAndPasswordMeetingTheRulesLogsIn() {
        assertEquals(Session.LoginResult.OK, Session.check("sam@example.com", "secret#42"))
        assertEquals(Session.LoginResult.INVALID, Session.check("sam@example.com", "secret42"))
        assertEquals(Session.LoginResult.INVALID, Session.check("not-an-email", "secret#42"))
    }

    @Test
    fun lockedAccountIsRefusedEvenWithAValidPassword() {
        assertEquals(Session.LoginResult.LOCKED, Session.check("LOCKED@totebox.app", "secret#42"))
    }

    @Test
    fun logOutEndsGuestModeAndEmptiesTheCartAndSavedItems() {
        Session.continueAsGuest()
        Cart.add(1)
        Cart.add(2)
        Cart.saveForLater(2)
        Session.logOut()
        assertFalse(Session.isShopping)
        assertEquals(0, Cart.itemCount)
        assertEquals(0, Cart.savedCount)
    }
}
