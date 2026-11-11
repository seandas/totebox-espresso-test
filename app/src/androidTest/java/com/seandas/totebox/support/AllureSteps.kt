package com.seandas.totebox.support

import androidx.test.espresso.Espresso
import io.qameta.allure.android.allureScreenshot
import io.qameta.allure.kotlin.Allure

/**
 * Runs [block] as an Allure step and attaches a screenshot of the screen after it. If the step
 * fails, a screenshot of the failure is attached instead and the error is rethrown.
 */
fun <T> uiStep(name: String, block: () -> T): T = Allure.step(name) {
    try {
        val result = block()
        Espresso.onIdle() // let the screen settle (and simulated network calls finish) first
        allureScreenshot(name = "screenshot", scale = SCREENSHOT_SCALE)
        result
    } catch (failure: Throwable) {
        allureScreenshot(name = "failure", scale = SCREENSHOT_SCALE)
        throw failure
    }
}

// Half size keeps the report small while staying readable
private const val SCREENSHOT_SCALE = 0.5f
