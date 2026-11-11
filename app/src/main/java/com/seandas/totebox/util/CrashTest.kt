package com.seandas.totebox.util

import com.seandas.totebox.data.Cart

/** Crashes the app on purpose, for testing crash handling. Used only by the "Crash test item". */
object CrashTest {
    fun crash() {
        val lines = Cart.items()
        // Deliberate bug: reads one past the end of the list, which throws an uncaught
        // IndexOutOfBoundsException and closes the app
        lines[lines.size]
    }
}
