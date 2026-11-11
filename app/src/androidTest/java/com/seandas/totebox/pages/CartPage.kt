package com.seandas.totebox.pages

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.espresso.matcher.ViewMatchers.Visibility
import com.seandas.totebox.R
import com.seandas.totebox.support.inRow
import com.seandas.totebox.support.string
import com.seandas.totebox.support.uiStep
import org.hamcrest.Matchers.not

/** The cart screen's "Cart" tab: lines, totals and checkout. The other tab is [SavedForLaterPage]. */
class CartPage {
    private val cartList = onView(withId(R.id.cart_list))

    // ---- Actions ----

    fun increaseQuantity(product: String) = apply {
        uiStep("Tap + for \"$product\"") { button(R.string.increase_named, product).perform(click()) }
    }

    fun decreaseQuantity(product: String) = apply {
        uiStep("Tap − for \"$product\"") { button(R.string.decrease_named, product).perform(click()) }
    }

    fun remove(product: String) = apply {
        uiStep("Remove \"$product\" from the cart") { button(R.string.remove_named, product).perform(click()) }
    }

    fun saveForLater(product: String) = apply {
        uiStep("Save \"$product\" for later") { button(R.string.save_named_for_later, product).perform(click()) }
    }

    fun openSavedTab(): SavedForLaterPage {
        uiStep("Open the \"Saved for later\" tab") { onView(withId(R.id.tab_saved)).perform(click()) }
        return SavedForLaterPage()
    }

    fun tapCheckout(): CheckoutPage {
        uiStep("Tap Checkout") { onView(withId(R.id.checkout_button)).perform(click()) }
        return CheckoutPage()
    }

    /** Checkout as a guest: the login screen opens first. */
    fun tapCheckoutAsGuest(): LoginPage {
        uiStep("Tap Checkout as a guest") { onView(withId(R.id.checkout_button)).perform(click()) }
        return LoginPage()
    }

    fun startShopping(): ProductsPage {
        uiStep("Tap Start shopping") { onView(withId(R.id.start_shopping_button)).perform(click()) }
        return ProductsPage()
    }

    // ---- Checks ----

    fun checkIsShown() = apply {
        uiStep("Check the cart is shown") { onView(withId(R.id.cart_tabs)).check(matches(isDisplayed())) }
    }

    fun checkQuantity(product: String, quantity: Int) = apply {
        uiStep("Check \"$product\" has quantity $quantity") {
            inRow(R.id.cart_list, product, R.id.cart_item_qty).check(matches(withText(quantity.toString())))
        }
    }

    /** At 1 the − button is disabled: removing is a separate action. */
    fun checkDecreaseDisabled(product: String) = apply {
        uiStep("Check − is disabled for \"$product\"") {
            button(R.string.decrease_named, product).check(matches(not(isEnabled())))
        }
    }

    /** Pass "Free" (or the free-shipping string) for [shipping] when there is no charge. */
    fun checkTotals(subtotal: String, shipping: String, total: String) = apply {
        uiStep("Check subtotal $subtotal, shipping $shipping, total $total") {
            onView(withId(R.id.cart_subtotal)).check(matches(withText(subtotal)))
            onView(withId(R.id.cart_shipping)).check(matches(withText(shipping)))
            onView(withId(R.id.cart_total)).check(matches(withText(total)))
        }
    }

    fun checkFreeShippingHint(shown: Boolean) = apply {
        uiStep(if (shown) "Check the free-shipping hint is shown" else "Check the free-shipping hint is hidden") {
            onView(withId(R.id.free_shipping_hint))
                .check(matches(withEffectiveVisibility(if (shown) Visibility.VISIBLE else Visibility.GONE)))
        }
    }

    fun checkTabCounts(cart: Int, saved: Int) = apply {
        val cartTab = string(R.string.tab_cart, cart)
        val savedTab = string(R.string.tab_saved, saved)
        uiStep("Check the tabs read \"$cartTab\" and \"$savedTab\"") {
            onView(withText(cartTab)).check(matches(isDisplayed()))
            onView(withText(savedTab)).check(matches(isDisplayed()))
        }
    }

    fun checkEmpty() = apply {
        uiStep("Check the cart is empty") {
            onView(withId(R.id.cart_empty_text)).check(matches(isDisplayed()))
            onView(withId(R.id.start_shopping_button)).check(matches(isDisplayed()))
            cartList.check(matches(withEffectiveVisibility(Visibility.GONE)))
        }
    }

    private fun button(labelId: Int, product: String) = onView(withContentDescription(string(labelId, product)))
}
