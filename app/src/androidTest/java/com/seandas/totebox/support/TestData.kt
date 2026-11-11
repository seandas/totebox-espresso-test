package com.seandas.totebox.support

import androidx.annotation.StringRes
import androidx.test.platform.app.InstrumentationRegistry

/** Inputs for the login rules: a valid email, and a password of 8+ characters with a number and a special character. */
object TestData {
    const val VALID_EMAIL = "sean@example.com"
    const val VALID_PASSWORD = "secret#42"
    const val LOCKED_EMAIL = "locked@totebox.app"
    const val INVALID_EMAIL = "sean@example"
    const val PASSWORD_TOO_SHORT = "abc1!xy"       // 7 characters
    const val PASSWORD_NO_NUMBER = "abcdefg!"
    const val PASSWORD_NO_SPECIAL = "abcdefgh1"

    // Checkout
    const val NAME = "Sean Das"
    const val ADDRESS = "1 Main St"
    const val CITY = "New York"
    const val ZIP = "10001"
    const val CARD_NUMBER = "4111 1111 1111 1111" // a standard test card number
    const val CARD_EXPIRY = "12/30"
    const val CARD_CVV = "123"
}

/** A string from the app's resources, so expected texts match what the app shows. */
fun string(@StringRes id: Int, vararg args: Any): String =
    InstrumentationRegistry.getInstrumentation().targetContext.getString(id, *args)
