package com.seandas.totebox.pages

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.seandas.totebox.R
import com.seandas.totebox.support.string
import com.seandas.totebox.support.uiStep
import com.seandas.totebox.support.withTextMatching

/** The order confirmation screen. */
class OrderPlacedPage {
    fun checkIsShown() = apply {
        uiStep("Check the order was placed") { onView(withId(R.id.order_thanks)).check(matches(isDisplayed())) }
    }

    /** Order numbers are random, so this checks the format: TB- and 6 digits. */
    fun checkOrderNumberFormat() = apply {
        uiStep("Check the order number looks like TB-######") {
            val label = Regex.escape(string(R.string.order_number, "")) // "Order number: "
            onView(withId(R.id.order_number)).check(matches(withTextMatching(Regex(label + "TB-\\d{6}"))))
        }
    }

    fun checkTotal(expected: String, payOnDelivery: Boolean) = apply {
        val text = string(if (payOnDelivery) R.string.order_total_due else R.string.order_total_paid, expected)
        uiStep("Check \"$text\"") { onView(withId(R.id.order_total)).check(matches(withText(text))) }
    }

    fun continueShopping(): ProductsPage {
        uiStep("Tap Continue shopping") { onView(withId(R.id.continue_button)).perform(click()) }
        return ProductsPage()
    }
}
