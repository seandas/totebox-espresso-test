package com.seandas.totebox.support

import android.view.View
import android.widget.TextView
import androidx.test.espresso.matcher.BoundedMatcher
import com.google.android.material.textfield.TextInputLayout
import org.hamcrest.Description
import org.hamcrest.Matcher

/** Matches a TextInputLayout showing [expected] as its error, or no error when [expected] is null. */
fun hasError(expected: String?): Matcher<View> =
    object : BoundedMatcher<View, TextInputLayout>(TextInputLayout::class.java) {
        override fun describeTo(description: Description) {
            description.appendText(if (expected == null) "has no error" else "has error \"$expected\"")
        }

        override fun matchesSafely(item: TextInputLayout): Boolean = item.error?.toString() == expected
    }

/** Matches a TextInputLayout whose helper text is [expected]. */
fun hasHelperText(expected: String): Matcher<View> =
    object : BoundedMatcher<View, TextInputLayout>(TextInputLayout::class.java) {
        override fun describeTo(description: Description) {
            description.appendText("has helper text \"$expected\"")
        }

        override fun matchesSafely(item: TextInputLayout): Boolean = item.helperText?.toString() == expected
    }

/** Matches a TextView whose whole text matches [regex], for values that change each run (like order numbers). */
fun withTextMatching(regex: Regex): Matcher<View> =
    object : BoundedMatcher<View, TextView>(TextView::class.java) {
        override fun describeTo(description: Description) {
            description.appendText("has text matching /$regex/")
        }

        override fun matchesSafely(item: TextView): Boolean = regex.matches(item.text)
    }
