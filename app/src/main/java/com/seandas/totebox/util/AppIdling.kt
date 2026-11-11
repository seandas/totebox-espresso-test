package com.seandas.totebox.util

import android.os.Handler
import android.os.Looper
import androidx.test.espresso.idling.CountingIdlingResource

/**
 * Counts the app's background work, so Espresso tests can wait for it instead of sleeping.
 * Tests register [resource] with IdlingRegistry.
 */
object AppIdling {
    val resource = CountingIdlingResource("totebox-background-work")
}

/** Stands in for a network call: runs [work] after [delayMs] and reports the result on the main thread. */
object FakeNetwork {
    private val main = Handler(Looper.getMainLooper())

    /** Multiplies every delay. Tests can set 0f for a fast suite, or a larger value to check loading states. */
    @Volatile
    var delayScale = 1f

    fun <T> run(delayMs: Long, work: () -> T, onDone: (T) -> Unit) {
        AppIdling.resource.increment()
        main.postDelayed({
            try {
                onDone(work())
            } finally {
                AppIdling.resource.decrement()
            }
        }, (delayMs * delayScale).toLong())
    }
}

object OrderNumbers {
    fun next(): String = "TB-" + (100000..999999).random()
}
