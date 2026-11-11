package com.seandas.totebox.tests

import com.seandas.totebox.R
import com.seandas.totebox.pages.OrderPlacedPage
import com.seandas.totebox.support.FailureDemo
import com.seandas.totebox.support.Products.TOTE
import com.seandas.totebox.support.TestData
import com.seandas.totebox.support.expectToast
import com.seandas.totebox.support.string
import io.qameta.allure.kotlin.Description
import io.qameta.allure.kotlin.Epic
import io.qameta.allure.kotlin.Feature
import io.qameta.allure.kotlin.Severity
import io.qameta.allure.kotlin.SeverityLevel
import io.qameta.allure.kotlin.Story
import io.qameta.allure.kotlin.junit4.DisplayName
import org.junit.Test

/**
 * Tests that fail ON PURPOSE, one per kind of failure, to show what the framework reports:
 * the step that failed, a screenshot of the screen at that moment, the error, and the device log.
 * Skipped in normal runs (see [FailureDemo]).
 */
@FailureDemo
@Epic("Totebox")
@Feature("Failure demo (fails on purpose)")
class FailureDemoTest : BaseTest() {

    @Test
    @Story("Wrong expected value")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("DEMO: expects the wrong cart total")
    @Description(
        "Fails on purpose. The cart really shows a total of $29.98 for one Canvas Tote Bag; the test " +
            "expects $99.99. Allure marks it FAILED (an assertion, so a possible product defect) and " +
            "shows the expected and actual values with a screenshot of the cart.",
        useJavaDoc = false,
    )
    fun expectsWrongTotal() {
        openAppLoggedIn()
            .addToCart(TOTE)
            .openCart()
            .checkTotals(subtotal = "$24.99", shipping = "$4.99", total = "$99.99")
    }

    @Test
    @Story("Missing element")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("DEMO: expects an order confirmation after a half swipe")
    @Description(
        "Fails on purpose. Letting the slider go halfway does not place the order, so the order " +
            "confirmation never appears. Allure marks it BROKEN (Espresso could not find the view) and " +
            "attaches the screen, still on checkout, and the view hierarchy in the error.",
        useJavaDoc = false,
    )
    fun expectsConfirmationAfterHalfSwipe() {
        openAppLoggedIn()
            .addToCart(TOTE)
            .openCart()
            .tapCheckout()
            .enterShippingAddress(TestData.NAME, TestData.ADDRESS, TestData.CITY, TestData.ZIP)
            .choosePayOnDelivery()
            .swipeSlider(0.5f)
            .let { OrderPlacedPage() } // pretend the order went through
            .checkIsShown()
    }

    @Test
    @Story("Missing message")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("DEMO: waits for a toast that never comes")
    @Description(
        "Fails on purpose. A wrong password shows a field error, not a toast, so waiting for a " +
            "\"Logged in\" toast times out after 5 seconds. Allure marks it FAILED with the message " +
            "\"No toast saying ... appeared within 5 seconds\", and the screenshot shows the password " +
            "error the user actually saw.",
        useJavaDoc = false,
    )
    fun waitsForToastThatNeverComes() {
        val login = openApp()
            .enterEmail(TestData.VALID_EMAIL)
            .enterPassword(TestData.PASSWORD_NO_NUMBER)
        expectToast(string(R.string.login_success, TestData.VALID_EMAIL)) {
            login.tapLogIn()
        }
    }
}
