package com.seandas.totebox.pages

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.Visibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import com.seandas.totebox.R
import com.seandas.totebox.support.expectToast
import com.seandas.totebox.support.listHasRow
import com.seandas.totebox.support.string
import com.seandas.totebox.support.uiStep

/** The product list, the first screen after logging in or continuing as a guest. */
class ProductsPage {
    private val productsList = onView(withId(R.id.products_list))
    private val sortSpinner = onView(withId(R.id.sort_spinner))

    fun checkIsShown() = apply {
        uiStep("Check the product list is shown") {
            productsList.check(matches(isDisplayed()))
            sortSpinner.check(matches(isDisplayed()))
        }
    }

    fun addToCart(productName: String) = apply {
        uiStep("Add \"$productName\" to the cart") {
            productsList.perform(
                RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                    hasDescendant(withText(productName)),
                ),
            )
            onView(withContentDescription(string(R.string.add_named_to_cart, productName))).perform(click())
        }
    }

    /** Taps the product's "Save for later" button and checks the toast that confirms it. */
    fun saveForLater(productName: String) = apply {
        expectToast(string(R.string.moved_to_saved, productName)) {
            uiStep("Tap Save for later on \"$productName\"") {
                onView(withId(R.id.products_list)).perform(
                    RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(hasDescendant(withText(productName))),
                )
                onView(withContentDescription(string(R.string.save_named_for_later, productName))).perform(click())
            }
        }
    }

    fun checkProductListed(productName: String, listed: Boolean = true) = apply {
        uiStep(if (listed) "Check \"$productName\" is in the product list" else "Check \"$productName\" is not in the product list") {
            check(listHasRow(R.id.products_list, productName) == listed) {
                "\"$productName\" ${if (listed) "is missing from" else "is still in"} the product list"
            }
        }
    }

    /** The badge on the cart icon; 0 means it is hidden. */
    fun checkCartBadge(count: Int) = apply {
        uiStep(if (count == 0) "Check the cart badge is hidden" else "Check the cart badge shows $count") {
            val badge = onView(withId(R.id.cart_badge))
            if (count == 0) badge.check(matches(withEffectiveVisibility(Visibility.GONE)))
            else badge.check(matches(withText(count.toString())))
        }
    }

    fun openSavedItemsFromMenu(): SavedForLaterPage {
        uiStep("Open Saved items from the menu") {
            openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
            onView(withText(R.string.menu_saved)).perform(click())
        }
        return SavedForLaterPage()
    }

    fun openCart(): CartPage {
        uiStep("Open the cart") { onView(withId(R.id.cart_action)).perform(click()) }
        return CartPage()
    }

    /** Opens the ⋮ menu and checks its account item: "Log out" when logged in, "Log in" for a guest. */
    fun checkMenuAccountItem(loggedIn: Boolean) = apply {
        val expected = string(if (loggedIn) R.string.menu_log_out else R.string.menu_log_in)
        uiStep("Check the menu offers \"$expected\"") {
            openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
            onView(withText(expected)).check(matches(isDisplayed()))
        }
        uiStep("Close the menu") { pressBack() }
    }
}
