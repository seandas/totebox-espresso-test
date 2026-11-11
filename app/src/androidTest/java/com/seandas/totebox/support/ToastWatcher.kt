package com.seandas.totebox.support

import android.view.accessibility.AccessibilityEvent
import java.util.concurrent.TimeoutException
import androidx.test.platform.app.InstrumentationRegistry

/**
 * Checks that a toast containing [text] appears while [action] runs.
 *
 * Espresso can't see toasts reliably on recent Android versions (they are drawn by the system), so
 * this listens for the accessibility event every toast sends instead.
 */
fun <T> expectToast(text: String, timeoutMs: Long = 5_000, action: () -> T): T {
    var result: Result<T>? = null
    uiStep("Check a toast says \"$text\"") {
        try {
            InstrumentationRegistry.getInstrumentation().uiAutomation.executeAndWaitForEvent(
                { result = runCatching(action) },
                { event ->
                    event.eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED &&
                        event.className?.contains("Toast") == true &&
                        event.text.any { it.contains(text) }
                },
                timeoutMs,
            )
        } catch (noToast: TimeoutException) {
            result?.exceptionOrNull()?.let { throw it } // the action itself failed: report that instead
            throw AssertionError("No toast saying \"$text\" appeared within ${timeoutMs / 1000} seconds")
        }
    }
    return result!!.getOrThrow()
}
