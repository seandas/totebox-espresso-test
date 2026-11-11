package com.seandas.totebox.tests

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.IdlingRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.seandas.totebox.data.Session
import com.seandas.totebox.pages.LoginPage
import com.seandas.totebox.pages.ProductsPage
import com.seandas.totebox.support.TestData
import com.seandas.totebox.support.uiStep
import com.seandas.totebox.ui.LoginActivity
import com.seandas.totebox.util.AppIdling
import com.seandas.totebox.util.FakeNetwork
import io.qameta.allure.android.rules.LogcatRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

/**
 * Shared setup: every test starts logged out with an empty cart, waits for the app's simulated
 * network calls through its idling resource, and runs those calls without delay.
 */
@RunWith(AndroidJUnit4::class)
abstract class BaseTest {
    /** Attaches the device log to the report when a test fails. */
    @get:Rule
    val logcat = LogcatRule()

    private var scenario: ActivityScenario<*>? = null

    @Before
    fun resetApp() {
        Session.logOut()
        FakeNetwork.delayScale = 0f
        IdlingRegistry.getInstance().register(AppIdling.resource)
    }

    @After
    fun cleanUp() {
        scenario?.close()
        IdlingRegistry.getInstance().unregister(AppIdling.resource)
        FakeNetwork.delayScale = 1f
        Session.logOut()
    }

    /** Starts the app already logged in (skipping the login screen), on the product list. */
    protected fun openAppLoggedIn(email: String = TestData.VALID_EMAIL): ProductsPage =
        uiStep("Open the app logged in as $email") {
            Session.logIn(email)
            scenario = ActivityScenario.launch(LoginActivity::class.java)
            ProductsPage()
        }

    protected fun openApp(): LoginPage = uiStep("Open the app") {
        scenario = ActivityScenario.launch(LoginActivity::class.java)
        LoginPage()
    }
}
