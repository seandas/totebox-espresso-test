package com.seandas.totebox.pages

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.seandas.totebox.R
import com.seandas.totebox.support.inRow
import com.seandas.totebox.support.string
import com.seandas.totebox.support.uiStep

/** The cart screen's "Saved for later" tab. */
class SavedForLaterPage {

    // ---- Actions ----

    fun moveToCart(product: String) = apply {
        uiStep("Move \"$product\" to the cart") {
            onView(withContentDescription(string(R.string.move_named_to_cart, product))).perform(click())
        }
    }

    fun delete(product: String) = apply {
        uiStep("Delete \"$product\" from saved items") {
            onView(withContentDescription(string(R.string.delete_named, product))).perform(click())
        }
    }

    fun openCartTab(): CartPage {
        uiStep("Open the \"Cart\" tab") { onView(withId(R.id.tab_cart)).perform(click()) }
        return CartPage()
    }

    /** Back to the product list (when this screen was opened from the menu there). */
    fun goBackToProducts(): ProductsPage {
        uiStep("Press Back") { pressBack() }
        return ProductsPage()
    }

    // ---- Checks ----

    fun checkItem(product: String, quantity: Int) = apply {
        uiStep("Check \"$product\" is saved with quantity $quantity") {
            inRow(R.id.saved_list, product, R.id.saved_item_qty)
                .check(matches(withText(string(R.string.saved_quantity, quantity))))
        }
    }

    fun checkEmpty() = apply {
        uiStep("Check nothing is saved for later") {
            onView(withId(R.id.saved_empty_text)).check(matches(isDisplayed()))
        }
    }

    fun checkTabCounts(cart: Int, saved: Int) = apply { CartPage().checkTabCounts(cart, saved) }
}
