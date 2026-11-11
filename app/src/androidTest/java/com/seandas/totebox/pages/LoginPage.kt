package com.seandas.totebox.pages

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.espresso.matcher.ViewMatchers.Visibility
import com.seandas.totebox.R
import com.seandas.totebox.support.hasError
import com.seandas.totebox.support.hasHelperText
import com.seandas.totebox.support.string
import com.seandas.totebox.support.uiStep

/** The login screen. */
class LoginPage {
    private val emailInput = onView(withId(R.id.email_input))
    private val passwordInput = onView(withId(R.id.password_input))
    private val emailLayout = onView(withId(R.id.email_layout))
    private val passwordLayout = onView(withId(R.id.password_layout))
    private val loginButton = onView(withId(R.id.login_button))
    private val guestButton = onView(withId(R.id.guest_button))
    private val loginError = onView(withId(R.id.login_error))

    // ---- Actions ----

    fun enterEmail(email: String) = apply {
        uiStep("Enter email \"$email\"") { emailInput.perform(replaceText(email), closeSoftKeyboard()) }
    }

    fun enterPassword(password: String) = apply {
        // The report shows the length, never the password itself
        uiStep("Enter a password (${password.length} characters, hidden)") { passwordInput.perform(replaceText(password), closeSoftKeyboard()) }
    }

    /** Types into the email field as a user would (one key at a time), for checks that react to typing. */
    fun typeIntoEmail(text: String) = apply {
        uiStep("Type \"$text\" into the email field") { emailInput.perform(typeText(text), closeSoftKeyboard()) }
    }

    /** Taps Log in where the login is expected to fail; stays on this page. */
    fun tapLogIn() = apply {
        uiStep("Tap Log in") { loginButton.perform(click()) }
    }

    /** Taps Log in where the login is expected to succeed. */
    fun tapLogInExpectingSuccess(): ProductsPage {
        uiStep("Tap Log in") { loginButton.perform(click()) }
        return ProductsPage()
    }

    /** Logs in from the checkout gate; checkout opens next. */
    fun logInToCheckOut(email: String, password: String): CheckoutPage {
        enterEmail(email).enterPassword(password)
        uiStep("Tap Log in") { loginButton.perform(click()) }
        return CheckoutPage()
    }

    /** Back from the checkout gate returns to the cart. */
    fun goBackToCart(): CartPage {
        uiStep("Press Back") { pressBack() }
        return CartPage()
    }

    fun logIn(email: String, password: String): ProductsPage =
        enterEmail(email).enterPassword(password).tapLogInExpectingSuccess()

    fun continueWithoutLogin(): ProductsPage {
        uiStep("Tap Continue without login") { guestButton.perform(click()) }
        return ProductsPage()
    }

    // ---- Checks ----

    fun checkIsShown() = apply {
        uiStep("Check the login screen is shown") {
            loginButton.check(matches(isDisplayed()))
            guestButton.check(matches(isDisplayed()))
        }
    }

    /** Opened from the cart by a guest: explains why, and offers no guest option. */
    fun checkCheckoutGate() = apply {
        uiStep("Check the login screen asks the guest to log in to check out") {
            onView(withId(R.id.checkout_login_message)).check(matches(withText(R.string.login_to_check_out)))
            onView(withId(R.id.checkout_login_message)).check(matches(isDisplayed()))
            guestButton.check(matches(withEffectiveVisibility(Visibility.GONE)))
        }
    }

    fun checkHelperTexts() = apply {
        uiStep("Check the email and password requirements are shown") {
            emailLayout.check(matches(hasHelperText(string(R.string.helper_email))))
            passwordLayout.check(matches(hasHelperText(string(R.string.helper_password))))
        }
    }

    fun checkEmailError(expected: String?) = apply {
        uiStep(if (expected == null) "Check the email field has no error" else "Check the email error \"$expected\"") {
            emailLayout.check(matches(hasError(expected)))
        }
    }

    fun checkPasswordError(expected: String?) = apply {
        uiStep(if (expected == null) "Check the password field has no error" else "Check the password error \"$expected\"") {
            passwordLayout.check(matches(hasError(expected)))
        }
    }

    fun checkLoginError(expected: String) = apply {
        uiStep("Check the login error \"$expected\"") {
            loginError.check(matches(withText(expected)))
            loginError.check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        }
    }
}
