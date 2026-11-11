package com.seandas.totebox.pages

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.seandas.totebox.R
import com.seandas.totebox.support.dragSliderHandle
import com.seandas.totebox.support.hasError
import com.seandas.totebox.support.string
import com.seandas.totebox.support.uiStep
import org.hamcrest.Matchers.allOf

/** The checkout form, ending in the "Swipe to place order" slider. */
class CheckoutPage {
    private val slider = onView(withId(R.id.place_order_slider))
    private val sliderLabel = onView(allOf(withId(R.id.swipe_label), isDescendantOfA(withId(R.id.place_order_slider))))

    // ---- Actions ----

    fun enterShippingAddress(name: String, address: String, city: String, zip: String) = apply {
        uiStep("Enter the shipping address: $name, $address, $city $zip") {
            type(R.id.name_input, name)
            type(R.id.address_input, address)
            type(R.id.city_input, city)
            type(R.id.zip_input, zip)
        }
    }

    fun chooseCard(number: String, expiry: String, cvv: String) = apply {
        uiStep("Pay by card ending ${number.takeLast(4)}, expiry $expiry") {
            onView(withId(R.id.pay_card)).perform(scrollTo(), click())
            type(R.id.card_number_input, number)
            type(R.id.card_expiry_input, expiry)
            type(R.id.card_cvv_input, cvv)
        }
    }

    fun choosePayOnDelivery() = apply {
        uiStep("Choose Pay on delivery") { onView(withId(R.id.pay_on_delivery)).perform(scrollTo(), click()) }
    }

    /** Drags the handle all the way, expecting the order to go through. */
    fun swipeToPlaceOrder(): OrderPlacedPage {
        uiStep("Swipe the slider all the way to place the order") {
            slider.perform(scrollTo(), dragSliderHandle(1f))
        }
        return OrderPlacedPage()
    }

    /** Drags the handle to [fraction] of the track and lets go (or all the way when the form is invalid). */
    fun swipeSlider(fraction: Float) = apply {
        uiStep("Swipe the slider ${(fraction * 100).toInt()}% of the way and let go") {
            slider.perform(scrollTo(), dragSliderHandle(fraction))
        }
    }

    // ---- Checks ----

    fun checkIsShown() = apply {
        uiStep("Check the checkout screen is shown") {
            slider.perform(scrollTo()).check(matches(isDisplayed()))
        }
    }

    fun checkOrderTotal(expected: String) = apply {
        val text = string(R.string.order_total, expected)
        uiStep("Check \"$text\"") {
            onView(withId(R.id.checkout_total)).perform(scrollTo()).check(matches(withText(text)))
        }
    }

    /** The slider is back at the start, asking for a swipe (no order was placed). */
    fun checkSliderReady() = apply {
        uiStep("Check the slider is ready: \"${string(R.string.swipe_to_place_order)}\"") {
            sliderLabel.check(matches(withText(R.string.swipe_to_place_order)))
        }
    }

    fun checkFieldError(layoutId: Int, expected: String) = apply {
        uiStep("Check the error \"$expected\"") {
            onView(withId(layoutId)).perform(scrollTo()).check(matches(hasError(expected)))
        }
    }

    private fun type(inputId: Int, text: String) {
        onView(withId(inputId)).perform(scrollTo(), replaceText(text), closeSoftKeyboard())
    }
}
