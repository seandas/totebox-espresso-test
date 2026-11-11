package com.seandas.totebox.tests

import com.seandas.totebox.support.Products.MUG
import com.seandas.totebox.support.Products.PLANT
import com.seandas.totebox.support.Products.TOTE
import io.qameta.allure.kotlin.Epic
import io.qameta.allure.kotlin.Feature
import io.qameta.allure.kotlin.Severity
import io.qameta.allure.kotlin.SeverityLevel
import io.qameta.allure.kotlin.Story
import io.qameta.allure.kotlin.junit4.DisplayName
import org.junit.Test

@Epic("Totebox")
@Feature("Saved for later")
class SavedForLaterTest : BaseTest() {

    @Test
    @Story("From the cart")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Save for later moves an item from the cart to the Saved for later tab")
    fun saveFromCartMovesItemToSavedTab() {
        openAppLoggedIn()
            .addToCart(TOTE)
            .addToCart(MUG)
            .openCart()
            .saveForLater(TOTE)
            .checkTabCounts(cart = 1, saved = 1)
            // Saved items are not charged: only the mug is left
            .checkTotals(subtotal = "$18.99", shipping = "$4.99", total = "$23.98")
            .openSavedTab()
            .checkItem(TOTE, quantity = 1)
    }

    @Test
    @Story("From the cart")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Move to cart brings a saved item back into the cart")
    fun moveToCartBringsItemBack() {
        openAppLoggedIn()
            .addToCart(TOTE)
            .addToCart(MUG)
            .openCart()
            .saveForLater(TOTE)
            .openSavedTab()
            .moveToCart(TOTE)
            .checkEmpty()
            .checkTabCounts(cart = 2, saved = 0)
            .openCartTab()
            .checkQuantity(TOTE, 1)
            .checkTotals(subtotal = "$43.98", shipping = "$4.99", total = "$48.97")
    }

    @Test
    @Story("From the cart")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Delete removes a saved item and leaves the cart alone")
    fun deleteRemovesOnlyTheSavedItem() {
        openAppLoggedIn()
            .addToCart(TOTE)
            .addToCart(MUG)
            .openCart()
            .saveForLater(TOTE)
            .openSavedTab()
            .delete(TOTE)
            .checkEmpty()
            .checkTabCounts(cart = 1, saved = 0)
            .openCartTab()
            .checkQuantity(MUG, 1)
    }

    @Test
    @Story("From the product list")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Save for later on the product list hides the product, shows a toast and saves it")
    fun saveFromProductListHidesProduct() {
        openAppLoggedIn()
            .checkProductListed(PLANT)
            .saveForLater(PLANT)
            .checkProductListed(PLANT, listed = false)
            .openSavedItemsFromMenu()
            .checkItem(PLANT, quantity = 1)
            .checkTabCounts(cart = 0, saved = 1)
    }

    @Test
    @Story("From the product list")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("A saved product moved to the cart is back in the product list")
    fun movedProductReturnsToProductList() {
        openAppLoggedIn()
            .saveForLater(PLANT)
            .openSavedItemsFromMenu()
            .moveToCart(PLANT)
            .goBackToProducts()
            .checkProductListed(PLANT)
            .checkCartBadge(1)
    }
}
