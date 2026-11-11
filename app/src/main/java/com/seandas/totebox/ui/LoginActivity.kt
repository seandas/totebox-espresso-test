package com.seandas.totebox.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.seandas.totebox.R
import com.seandas.totebox.data.LoginRules
import com.seandas.totebox.data.Session
import com.seandas.totebox.databinding.ActivityLoginBinding
import com.seandas.totebox.util.FakeNetwork

class LoginActivity : AppCompatActivity() {
    companion object {
        /** Set when a guest is sent here from the cart; checkout opens after logging in. */
        const val EXTRA_FOR_CHECKOUT = "for_checkout"
    }

    private lateinit var b: ActivityLoginBinding
    private val forCheckout get() = intent.getBooleanExtra(EXTRA_FOR_CHECKOUT, false)
    private var loading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Session.isLoggedIn) {
            openNext()
            return
        }
        b = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.emailInput.doAfterTextChanged { b.emailLayout.error = null }
        b.passwordInput.doAfterTextChanged { b.passwordLayout.error = null }
        b.loginButton.setOnClickListener { attemptLogin() }
        b.guestButton.setOnClickListener {
            Session.continueAsGuest()
            openProducts()
        }
        // From the cart: explain why, and offer no guest option (back returns to the cart)
        b.checkoutLoginMessage.visibility = if (forCheckout) View.VISIBLE else View.GONE
        b.guestButton.visibility = if (forCheckout) View.GONE else View.VISIBLE
        b.passwordInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                attemptLogin()
                true
            } else false
        }
    }

    private fun attemptLogin() {
        if (loading) return // the keyboard's Done key can fire while a login is already running
        val email = b.emailInput.text.toString().trim()
        val password = b.passwordInput.text.toString()
        b.emailLayout.error = null
        b.passwordLayout.error = null
        b.loginError.visibility = View.GONE

        var valid = true
        if (email.isEmpty()) {
            b.emailLayout.error = getString(R.string.error_email_required)
            valid = false
        } else if (!LoginRules.isValidEmail(email)) {
            b.emailLayout.error = getString(R.string.error_email_invalid)
            valid = false
        }
        if (password.isEmpty()) {
            b.passwordLayout.error = getString(R.string.error_password_required)
            valid = false
        } else if (!LoginRules.isValidPassword(password)) {
            b.passwordLayout.error = getString(R.string.error_password_rules)
            valid = false
        }
        if (!valid) return

        setLoading(true)
        FakeNetwork.run(800, { Session.check(email, password) }) { result ->
            if (isDestroyed) return@run
            setLoading(false)
            when (result) {
                Session.LoginResult.OK -> {
                    Session.logIn(email)
                    // Application context: the toast outlives this screen, which closes next
                    Toast.makeText(applicationContext, getString(R.string.login_success, email), Toast.LENGTH_SHORT).show()
                    openNext()
                }
                Session.LoginResult.LOCKED -> showError(R.string.error_account_locked)
                Session.LoginResult.INVALID -> showError(R.string.error_login_failed)
            }
        }
    }

    private fun showError(message: Int) {
        b.loginError.setText(message)
        b.loginError.visibility = View.VISIBLE
    }

    private fun setLoading(loading: Boolean) {
        this.loading = loading
        b.loginButton.isEnabled = !loading
        b.guestButton.isEnabled = !loading
        b.loginProgress.visibility = if (loading) View.VISIBLE else View.GONE
    }

    private fun openNext() {
        if (forCheckout) {
            startActivity(Intent(this, CheckoutActivity::class.java))
            finish()
        } else {
            openProducts()
        }
    }

    private fun openProducts() {
        startActivity(Intent(this, ProductsActivity::class.java))
        finish()
    }
}
