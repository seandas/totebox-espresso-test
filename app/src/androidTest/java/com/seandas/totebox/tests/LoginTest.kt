package com.seandas.totebox.tests

import com.seandas.totebox.R
import com.seandas.totebox.support.TestData
import com.seandas.totebox.support.expectToast
import com.seandas.totebox.support.string
import io.qameta.allure.kotlin.Epic
import io.qameta.allure.kotlin.Feature
import io.qameta.allure.kotlin.Severity
import io.qameta.allure.kotlin.SeverityLevel
import io.qameta.allure.kotlin.Story
import io.qameta.allure.kotlin.junit4.DisplayName
import org.junit.Test

@Epic("Totebox")
@Feature("Login")
class LoginTest : BaseTest() {

    @Test
    @Story("Valid login")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("A valid email and password log in and show a toast")
    fun validCredentialsLogIn() {
        val login = openApp()
            .enterEmail(TestData.VALID_EMAIL)
            .enterPassword(TestData.VALID_PASSWORD)
        val products = expectToast(string(R.string.login_success, TestData.VALID_EMAIL)) {
            login.tapLogInExpectingSuccess()
        }
        products.checkIsShown()
            .checkMenuAccountItem(loggedIn = true)
    }

    @Test
    @Story("Field requirements")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("The email and password requirements are shown on the login screen")
    fun requirementsAreShown() {
        openApp()
            .checkIsShown()
            .checkHelperTexts()
    }

    @Test
    @Story("Invalid login")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Logging in with empty fields shows both required errors")
    fun emptyFieldsShowRequiredErrors() {
        openApp()
            .tapLogIn()
            .checkEmailError(string(R.string.error_email_required))
            .checkPasswordError(string(R.string.error_password_required))
            .checkIsShown()
    }

    @Test
    @Story("Invalid login")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("An email without a valid domain is rejected")
    fun invalidEmailIsRejected() {
        openApp()
            .enterEmail(TestData.INVALID_EMAIL)
            .enterPassword(TestData.VALID_PASSWORD)
            .tapLogIn()
            .checkEmailError(string(R.string.error_email_invalid))
            .checkPasswordError(null)
            .checkIsShown()
    }

    @Test
    @Story("Invalid login")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("A password shorter than 8 characters is rejected")
    fun shortPasswordIsRejected() {
        checkPasswordIsRejected(TestData.PASSWORD_TOO_SHORT)
    }

    @Test
    @Story("Invalid login")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("A password without a number is rejected")
    fun passwordWithoutNumberIsRejected() {
        checkPasswordIsRejected(TestData.PASSWORD_NO_NUMBER)
    }

    @Test
    @Story("Invalid login")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("A password without a special character is rejected")
    fun passwordWithoutSpecialCharacterIsRejected() {
        checkPasswordIsRejected(TestData.PASSWORD_NO_SPECIAL)
    }

    @Test
    @Story("Invalid login")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("A locked account is refused even with a valid password")
    fun lockedAccountIsRefused() {
        openApp()
            .enterEmail(TestData.LOCKED_EMAIL)
            .enterPassword(TestData.VALID_PASSWORD)
            .tapLogIn()
            .checkLoginError(string(R.string.error_account_locked))
            .checkIsShown()
    }

    @Test
    @Story("Invalid login")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("An error clears as soon as the user types in that field")
    fun errorClearsWhenTyping() {
        openApp()
            .tapLogIn()
            .checkEmailError(string(R.string.error_email_required))
            .typeIntoEmail("s")
            .checkEmailError(null)
            .checkPasswordError(string(R.string.error_password_required))
    }

    @Test
    @Story("Guest")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Continue without login opens the store as a guest")
    fun continueWithoutLogin() {
        openApp()
            .continueWithoutLogin()
            .checkIsShown()
            .checkMenuAccountItem(loggedIn = false)
    }

    private fun checkPasswordIsRejected(password: String) {
        openApp()
            .enterEmail(TestData.VALID_EMAIL)
            .enterPassword(password)
            .tapLogIn()
            .checkPasswordError(string(R.string.error_password_rules))
            .checkEmailError(null)
            .checkIsShown()
    }
}
