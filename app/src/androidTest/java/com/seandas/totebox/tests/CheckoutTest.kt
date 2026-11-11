package com.seandas.totebox.tests

import com.seandas.totebox.R
import com.seandas.totebox.support.Products
import com.seandas.totebox.support.TestData
import com.seandas.totebox.support.string
import io.qameta.allure.kotlin.Epic
import io.qameta.allure.kotlin.Feature
import io.qameta.allure.kotlin.Severity
import io.qameta.allure.kotlin.SeverityLevel
import io.qameta.allure.kotlin.Story
import io.qameta.allure.kotlin.junit4.DisplayName
import org.junit.Test

@Epic("Totebox")
@Feature("Checkout")
class CheckoutTest : BaseTest() {

    // Canvas Tote Bag is $24.99; under $50 adds $4.99 shipping
    private val product = Products.TOTE
    private val totalWithShipping = "$29.98"

    @Test
    @Story("Purchase")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Swiping the slider all the way places a pay-on-delivery order")
    fun swipePlacesPayOnDeliveryOrder() {
        openAppLoggedIn()
            .addToCart(product)
            .openCart()
            .tapCheckout()
            .enterShippingAddress(TestData.NAME, TestData.ADDRESS, TestData.CITY, TestData.ZIP)
            .choosePayOnDelivery()
            .swipeToPlaceOrder()
            .checkIsShown()
            .checkOrderNumberFormat()
            .checkTotal(totalWithShipping, payOnDelivery = true)
            .continueShopping()
            .checkIsShown()
    }

    @Test
    @Story("Purchase")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Swiping the slider all the way places a card order")
    fun swipePlacesCardOrder() {
        openAppLoggedIn()
            .addToCart(product)
            .openCart()
            .tapCheckout()
            .enterShippingAddress(TestData.NAME, TestData.ADDRESS, TestData.CITY, TestData.ZIP)
            .chooseCard(TestData.CARD_NUMBER, TestData.CARD_EXPIRY, TestData.CARD_CVV)
            .swipeToPlaceOrder()
            .checkIsShown()
            .checkOrderNumberFormat()
            .checkTotal(totalWithShipping, payOnDelivery = false)
    }

    @Test
    @Story("Swipe to place order")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Letting go halfway does not place the order")
    fun halfSwipeDoesNotPlaceOrder() {
        openAppLoggedIn()
            .addToCart(product)
            .openCart()
            .tapCheckout()
            .enterShippingAddress(TestData.NAME, TestData.ADDRESS, TestData.CITY, TestData.ZIP)
            .choosePayOnDelivery()
            .swipeSlider(0.5f)
            .checkSliderReady()
            .checkIsShown()
    }

    @Test
    @Story("Swipe to place order")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("A full swipe on an empty form shows the errors and resets the slider")
    fun fullSwipeOnEmptyFormShowsErrors() {
        openAppLoggedIn()
            .addToCart(product)
            .openCart()
            .tapCheckout()
            .swipeSlider(1f)
            .checkFieldError(R.id.name_layout, string(R.string.error_name_required))
            .checkFieldError(R.id.card_number_layout, string(R.string.error_card_required))
            .checkSliderReady()
    }
}
