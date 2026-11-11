package com.seandas.totebox.data

/**
 * Who is shopping: a logged-in account or a guest. There is no server: any email and password that
 * meet [LoginRules] log in, except [LOCKED_EMAIL], which tests a login refused after the form checks.
 */
object Session {
    const val LOCKED_EMAIL = "locked@totebox.app"

    enum class LoginResult { OK, LOCKED, INVALID }

    var email: String? = null
        private set

    val isLoggedIn: Boolean get() = email != null

    /** Browsing without an account. Guests can fill a cart but must log in to check out. */
    var isGuest: Boolean = false
        private set

    /** Logged in or browsing as a guest: either may see the store. */
    val isShopping: Boolean get() = isLoggedIn || isGuest

    fun continueAsGuest() {
        isGuest = true
    }

    fun check(email: String, password: String): LoginResult = when {
        email.equals(LOCKED_EMAIL, ignoreCase = true) -> LoginResult.LOCKED
        LoginRules.isValidEmail(email) && LoginRules.isValidPassword(password) -> LoginResult.OK
        else -> LoginResult.INVALID
    }

    /** Logging in keeps the cart, so a guest's items carry over. */
    fun logIn(email: String) {
        this.email = email
        isGuest = false
    }

    fun logOut() {
        email = null
        isGuest = false
        Cart.clearAll()
    }
}
