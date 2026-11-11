package com.seandas.totebox.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.seandas.totebox.R
import com.seandas.totebox.data.Cart
import com.seandas.totebox.data.Catalog
import com.seandas.totebox.data.Product
import com.seandas.totebox.databinding.ActivityProductsBinding
import com.seandas.totebox.util.FakeNetwork

class ProductsActivity : ShopActivity() {
    override val menuItemForThisScreen = R.id.action_home

    private lateinit var b: ActivityProductsBinding
    private val adapter = ProductAdapter(onOpen = ::openDetails, onAdd = ::addToCart, onSave = ::saveForLater)
    private var products: List<Product> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!requireSession()) return
        b = ActivityProductsBinding.inflate(layoutInflater)
        setContentView(b.root)
        setUpToolbar(b.toolbar, getString(R.string.title_products), showBack = false)

        b.productsList.layoutManager = LinearLayoutManager(this)
        b.productsList.adapter = adapter

        ArrayAdapter.createFromResource(this, R.array.sort_options, android.R.layout.simple_spinner_item).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            b.sortSpinner.adapter = it
        }
        b.sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) = showSorted()
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }

        loadProducts()
    }

    override fun onResume() {
        super.onResume()
        // Saved items may have been moved back to the cart or deleted on another screen
        if (::b.isInitialized && products.isNotEmpty()) showSorted()
    }

    private fun loadProducts() {
        b.productsLoading.visibility = View.VISIBLE
        b.productsList.visibility = View.GONE
        FakeNetwork.run(900, { Catalog.products }) { loaded ->
            if (isDestroyed) return@run
            products = loaded
            b.productsLoading.visibility = View.GONE
            showSorted()
        }
    }

    /** Shows the loaded products in the chosen order, leaving out any saved for later, then the crash test item. */
    private fun showSorted() {
        if (products.isEmpty()) return // still loading
        val shown = products.filterNot { Cart.isSaved(it.id) }
        val sorted = when (b.sortSpinner.selectedItemPosition) {
            1 -> shown.sortedBy { it.priceCents }
            2 -> shown.sortedByDescending { it.priceCents }
            else -> shown.sortedBy { it.name }
        }
        adapter.submit(sorted + Catalog.crashTestItem) // always last, whatever the sort
        b.productsList.visibility = View.VISIBLE
    }

    private fun openDetails(product: Product) {
        startActivity(Intent(this, ProductDetailActivity::class.java)
            .putExtra(ProductDetailActivity.EXTRA_PRODUCT_ID, product.id))
    }

    private fun saveForLater(product: Product) {
        Cart.saveFromStore(product.id)
        refreshCartBadge() // the product may have come out of the cart
        showSorted()
        Toast.makeText(this, getString(R.string.moved_to_saved, product.name), Toast.LENGTH_SHORT).show()
    }

    private fun addToCart(product: Product) {
        Snackbar.make(b.root, addToCartMessage(product, 1), Snackbar.LENGTH_SHORT).show()
    }
}
