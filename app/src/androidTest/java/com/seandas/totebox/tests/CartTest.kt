package com.seandas.totebox.tests

import com.seandas.totebox.R
import com.seandas.totebox.support.Products.MUG
import com.seandas.totebox.support.Products.NOTEBOOK
import com.seandas.totebox.support.Products.TOTE
import com.seandas.totebox.support.string
import io.qameta.allure.kotlin.Epic
import io.qameta.allure.kotlin.Feature
import io.qameta.allure.kotlin.Severity
import io.qameta.allure.kotlin.SeverityLevel
import io.qameta.allure.kotlin.Story
import io.qameta.allure.kotlin.junit4.DisplayName
import org.junit.Test

@Epic("Totebox")
@Feature("Cart")
class CartTest : BaseTest() {

    private val free get() = string(R.string.shipping_free)

    @Test
    @Story("Quantities and totals")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("+ and − change the quantity and the totals")
    fun quantityButtonsUpdateTotals() {
        openAppLoggedIn()
            .addToCart(TOTE)
            .openCart()
            .checkQuantity(TOTE, 1)
            .checkDecreaseDisabled(TOTE)
            .checkTotals(subtotal = "$24.99", shipping = "$4.99", total = "$29.98")
            .increaseQuantity(TOTE)
            .checkQuantity(TOTE, 2)
            .checkTotals(subtotal = "$49.98", shipping = "$4.99", total = "$54.97")
            .decreaseQuantity(TOTE)
            .checkQuantity(TOTE, 1)
            .checkTotals(subtotal = "$24.99", shipping = "$4.99", total = "$29.98")
    }

    @Test
    @Story("Quantities and totals")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Shipping is free just over $50 and charged below it")
    fun shippingIsFreeFromFiftyDollars() {
        // Just over the threshold: $24.99 + 2 × $12.99 = $50.97. (Just under, $49.98, is
        // checked in quantityButtonsUpdateTotals.)
        openAppLoggedIn()
            .addToCart(TOTE)
            .addToCart(NOTEBOOK)
            .addToCart(NOTEBOOK)
            .openCart()
            .checkTotals(subtotal = "$50.97", shipping = free, total = "$50.97")
            .checkFreeShippingHint(shown = false)
            .decreaseQuantity(NOTEBOOK)
            .checkTotals(subtotal = "$37.98", shipping = "$4.99", total = "$42.97")
            .checkFreeShippingHint(shown = true)
    }

    @Test
    @Story("Empty cart")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Removing the last item shows the empty cart, and Start shopping returns to the store")
    fun removingLastItemShowsEmptyCart() {
        openAppLoggedIn()
            .addToCart(TOTE)
            .openCart()
            .remove(TOTE)
            .checkEmpty()
            .checkTabCounts(cart = 0, saved = 0)
            .startShopping()
            .checkIsShown()
            .checkCartBadge(0)
    }

    @Test
    @Story("Cart badge")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("The cart badge counts every item added")
    fun cartBadgeCountsItems() {
        openAppLoggedIn()
            .checkCartBadge(0)
            .addToCart(TOTE)
            .addToCart(MUG)
            .addToCart(TOTE)
            .checkCartBadge(3)
            .openCart()
            .checkQuantity(TOTE, 2)
            .checkQuantity(MUG, 1)
    }
}
