package com.seandas.totebox.support

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.PerformException
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withParent
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf

/** The row of list [listId] that shows [name]. */
fun rowOf(listId: Int, name: String): Matcher<View> =
    allOf(withParent(withId(listId)), hasDescendant(withText(name)))

/** View [viewId] inside the row of list [listId] that shows [name]. */
fun inRow(listId: Int, name: String, viewId: Int): ViewInteraction =
    onView(allOf(withId(viewId), isDescendantOfA(rowOf(listId, name))))

/** Scrolls list [listId] to the row showing [name]; fails if there is none. */
fun scrollListTo(listId: Int, name: String) {
    onView(withId(listId)).perform(RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(hasDescendant(withText(name))))
}

/**
 * True if list [listId] has a row showing [name] anywhere in its data, not just on screen.
 * RecyclerViewActions.scrollTo searches every item, and fails when none matches.
 */
fun listHasRow(listId: Int, name: String): Boolean = try {
    scrollListTo(listId, name)
    true
} catch (notFound: PerformException) {
    false
}
