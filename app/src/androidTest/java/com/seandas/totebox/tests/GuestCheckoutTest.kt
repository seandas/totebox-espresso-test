package com.seandas.totebox.tests

import com.seandas.totebox.support.Products.TOTE
import com.seandas.totebox.support.TestData
import io.qameta.allure.kotlin.Epic
import io.qameta.allure.kotlin.Feature
import io.qameta.allure.kotlin.Severity
import io.qameta.allure.kotlin.SeverityLevel
import io.qameta.allure.kotlin.Story
import io.qameta.allure.kotlin.junit4.DisplayName
import org.junit.Test

@Epic("Totebox")
@Feature("Guest checkout")
class GuestCheckoutTest : BaseTest() {

    @Test
    @Story("Login gate")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("A guest logs in at checkout, keeps the cart and completes the purchase")
    fun guestLogsInAtCheckoutAndBuys() {
        openApp()
            .continueWithoutLogin()
            .addToCart(TOTE)
            .openCart()
            .tapCheckoutAsGuest()
            .checkCheckoutGate()
            .logInToCheckOut(TestData.VALID_EMAIL, TestData.VALID_PASSWORD)
            .checkOrderTotal("$29.98") // the guest's cart came through the login
            .enterShippingAddress(TestData.NAME, TestData.ADDRESS, TestData.CITY, TestData.ZIP)
            .choosePayOnDelivery()
            .swipeToPlaceOrder()
            .checkIsShown()
            .checkTotal("$29.98", payOnDelivery = true)
    }

    @Test
    @Story("Login gate")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Going back from the checkout login returns to the cart, still full")
    fun backFromCheckoutLoginKeepsCart() {
        openApp()
            .continueWithoutLogin()
            .addToCart(TOTE)
            .openCart()
            .tapCheckoutAsGuest()
            .checkCheckoutGate()
            .goBackToCart()
            .checkQuantity(TOTE, 1)
            .checkTabCounts(cart = 1, saved = 0)
    }
}
