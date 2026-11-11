package com.seandas.totebox.ui

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.seandas.totebox.R
import com.seandas.totebox.data.Cart
import com.seandas.totebox.data.Product
import com.seandas.totebox.data.Session
import com.seandas.totebox.util.CrashTest

/** Shared toolbar, cart badge, log in/out and session guard for the store screens. */
abstract class ShopActivity : AppCompatActivity() {

    /** Screens that are part of buying (cart, checkout) hide the cart icon. */
    protected open val showsCartIcon = true

    /** The overflow menu item that would open this screen, hidden here because it would do nothing. */
    protected open val menuItemForThisScreen: Int? = null

    private var cartBadge: TextView? = null
    private var cartAction: View? = null

    /**
     * Sends the visitor to the login screen unless they are logged in or browsing as a guest
     * (the session is lost if the app is killed).
     */
    protected fun requireSession(): Boolean {
        if (Session.isShopping) return true
        startActivity(Intent(this, LoginActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
        finish()
        return false
    }

    protected fun setUpToolbar(toolbar: MaterialToolbar, title: CharSequence, showBack: Boolean) {
        setSupportActionBar(toolbar)
        supportActionBar?.title = title
        supportActionBar?.setDisplayHomeAsUpEnabled(showBack)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_shop, menu)
        val cartItem = menu.findItem(R.id.action_cart)
        cartItem.isVisible = showsCartIcon
        cartItem.actionView?.let { view ->
            cartAction = view
            cartBadge = view.findViewById(R.id.cart_badge)
            view.setOnClickListener { startActivity(Intent(this, CartActivity::class.java)) }
        }
        refreshCartBadge()
        return true
    }

    // Runs each time the menu opens, so it stays right after a guest logs in on another screen
    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        // Guests see "Log in" where signed-in shoppers see "Log out"
        menu.findItem(R.id.action_logout)?.setTitle(if (Session.isLoggedIn) R.string.menu_log_out else R.string.menu_log_in)
        menuItemForThisScreen?.let { menu.findItem(it)?.isVisible = false }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_home -> {
            goToProducts()
            true
        }
        R.id.action_saved -> {
            openSavedItems()
            true
        }
        R.id.action_settings -> {
            startActivity(Intent(this, SettingsActivity::class.java))
            true
        }
        R.id.action_logout -> {
            if (Session.isLoggedIn) Session.logOut() // a guest keeps the cart when going to log in
            startActivity(Intent(this, LoginActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        refreshCartBadge()
    }

    protected fun refreshCartBadge() {
        val count = Cart.itemCount
        cartBadge?.apply {
            text = count.toString()
            visibility = if (count > 0) View.VISIBLE else View.GONE
        }
        cartAction?.contentDescription = resources.getQuantityString(R.plurals.cart_description, count, count)
    }

    /** Adds to the cart, refreshes the badge and returns the message to show (the cart caps each product). */
    protected fun addToCartMessage(product: Product, quantity: Int): String {
        if (product.isCrashTest) CrashTest.crash()
        val added = Cart.add(product.id, quantity)
        refreshCartBadge()
        return if (added == 0) getString(R.string.cart_limit_reached, Cart.MAX_QUANTITY, product.name)
        else getString(R.string.added_to_cart, added, product.name)
    }

    /** Opens the cart screen on its "Saved for later" tab. */
    protected open fun openSavedItems() {
        startActivity(Intent(this, CartActivity::class.java).putExtra(CartActivity.EXTRA_SHOW_SAVED, true))
    }

    /** Opens the login screen for a guest who wants to check out; checkout opens after logging in. */
    protected fun logInToCheckOut() {
        startActivity(Intent(this, LoginActivity::class.java).putExtra(LoginActivity.EXTRA_FOR_CHECKOUT, true))
    }

    protected fun goToProducts() {
        startActivity(Intent(this, ProductsActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP))
        finish()
    }
}
