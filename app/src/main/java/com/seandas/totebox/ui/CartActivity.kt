package com.seandas.totebox.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.seandas.totebox.R
import com.seandas.totebox.data.Cart
import com.seandas.totebox.data.Product
import com.seandas.totebox.data.Session
import com.seandas.totebox.databinding.ActivityCartBinding
import com.seandas.totebox.util.formatPrice

/** The cart, with a second tab for items saved for later. */
class CartActivity : ShopActivity() {
    companion object {
        /** Opens the screen on the "Saved for later" tab. */
        const val EXTRA_SHOW_SAVED = "show_saved"
        private const val STATE_SHOWING_SAVED = "showing_saved"
    }

    override val showsCartIcon = false

    private lateinit var b: ActivityCartBinding
    private val cartAdapter = CartAdapter(
        onQuantityChange = { productId, quantity -> Cart.setQuantity(productId, quantity); render() },
        onRemove = { productId -> Cart.remove(productId); render() },
        onSaveForLater = ::saveForLater,
    )
    private val savedAdapter = SavedAdapter(onMoveToCart = ::moveToCart, onDelete = ::deleteSaved)
    private lateinit var cartTab: TabLayout.Tab
    private lateinit var savedTab: TabLayout.Tab

    private val showingSaved get() = b.cartTabs.selectedTabPosition == savedTab.position

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!requireSession()) return
        b = ActivityCartBinding.inflate(layoutInflater)
        setContentView(b.root)
        setUpToolbar(b.toolbar, getString(R.string.title_cart), showBack = true)

        b.cartList.layoutManager = LinearLayoutManager(this)
        b.cartList.adapter = cartAdapter
        b.savedList.layoutManager = LinearLayoutManager(this)
        b.savedList.adapter = savedAdapter

        cartTab = b.cartTabs.newTab().setId(R.id.tab_cart)
        savedTab = b.cartTabs.newTab().setId(R.id.tab_saved)
        b.cartTabs.addTab(cartTab)
        val startOnSaved = savedInstanceState?.getBoolean(STATE_SHOWING_SAVED)
            ?: intent.getBooleanExtra(EXTRA_SHOW_SAVED, false)
        b.cartTabs.addTab(savedTab, startOnSaved)
        b.cartTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) = render()
            override fun onTabUnselected(tab: TabLayout.Tab) = Unit
            override fun onTabReselected(tab: TabLayout.Tab) = Unit
        })

        b.checkoutButton.setOnClickListener {
            if (Session.isLoggedIn) startActivity(Intent(this, CheckoutActivity::class.java)) else logInToCheckOut()
        }
        b.startShoppingButton.setOnClickListener { goToProducts() }
    }

    override fun onResume() {
        super.onResume()
        if (::b.isInitialized) render()
    }

    // Already here: just switch tabs
    override fun openSavedItems() {
        savedTab.select()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (::b.isInitialized) outState.putBoolean(STATE_SHOWING_SAVED, showingSaved)
    }

    private fun saveForLater(product: Product) {
        Cart.saveForLater(product.id)
        render()
        notify(getString(R.string.saved_for_later, product.name))
    }

    private fun moveToCart(product: Product) {
        val moved = Cart.moveToCart(product.id)
        render()
        notify(if (moved == 0) getString(R.string.cart_limit_reached, Cart.MAX_QUANTITY, product.name)
        else getString(R.string.moved_to_cart, moved, product.name))
    }

    private fun deleteSaved(product: Product) {
        Cart.deleteSaved(product.id)
        render()
        notify(getString(R.string.deleted_saved, product.name))
    }

    private fun notify(message: String) = Snackbar.make(b.root, message, Snackbar.LENGTH_SHORT).show()

    private fun render() {
        cartTab.text = getString(R.string.tab_cart, Cart.itemCount)
        savedTab.text = getString(R.string.tab_saved, Cart.savedCount)

        val items = Cart.items()
        cartAdapter.submit(items)
        val saved = Cart.savedItems()
        savedAdapter.submit(saved)

        val onCart = !showingSaved
        val cartEmpty = items.isEmpty()
        b.cartList.visibility = if (onCart && !cartEmpty) View.VISIBLE else View.GONE
        b.cartEmpty.visibility = if (onCart && cartEmpty) View.VISIBLE else View.GONE
        b.cartSummary.visibility = if (onCart && !cartEmpty) View.VISIBLE else View.GONE
        b.savedList.visibility = if (!onCart && saved.isNotEmpty()) View.VISIBLE else View.GONE
        b.savedEmpty.visibility = if (!onCart && saved.isEmpty()) View.VISIBLE else View.GONE

        b.cartSubtotal.text = formatPrice(Cart.subtotalCents)
        b.cartShipping.text =
            if (Cart.shippingCents == 0) getString(R.string.shipping_free) else formatPrice(Cart.shippingCents)
        b.cartTotal.text = formatPrice(Cart.totalCents)
        b.freeShippingHint.visibility = if (Cart.shippingCents > 0) View.VISIBLE else View.GONE
    }
}
