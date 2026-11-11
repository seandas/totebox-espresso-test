package com.seandas.totebox.ui

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.seandas.totebox.R
import com.seandas.totebox.data.Cart
import com.seandas.totebox.data.Catalog
import com.seandas.totebox.data.Product
import com.seandas.totebox.databinding.ActivityProductDetailBinding
import com.seandas.totebox.util.formatPrice

class ProductDetailActivity : ShopActivity() {
    companion object {
        const val EXTRA_PRODUCT_ID = "product_id"
        private const val STATE_QUANTITY = "quantity"
    }

    private lateinit var b: ActivityProductDetailBinding
    private lateinit var product: Product
    private var quantity = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!requireSession()) return
        product = Catalog.find(intent.getIntExtra(EXTRA_PRODUCT_ID, -1)) ?: run {
            finish()
            return
        }
        quantity = savedInstanceState?.getInt(STATE_QUANTITY, 1) ?: 1

        b = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(b.root)
        setUpToolbar(b.toolbar, getString(R.string.title_details), showBack = true)

        b.detailName.text = product.name
        b.detailCategory.text = product.category
        b.detailPrice.text = formatPrice(product.priceCents)
        if (product.isCrashTest) b.detailPrice.visibility = View.GONE
        b.detailDescription.text = product.description
        b.detailImage.setImageResource(product.imageRes)
        b.detailThumb.backgroundTintList = ColorStateList.valueOf(Color.parseColor(product.colorHex))

        b.qtyMinus.setOnClickListener { setQuantity(quantity - 1) }
        b.qtyPlus.setOnClickListener { setQuantity(quantity + 1) }
        b.addToCartButton.setOnClickListener { addToCart() }
        setQuantity(quantity)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(STATE_QUANTITY, quantity)
    }

    private fun setQuantity(value: Int) {
        quantity = value.coerceIn(1, Cart.MAX_QUANTITY)
        b.qtyValue.text = quantity.toString()
        b.qtyMinus.isEnabled = quantity > 1
        b.qtyPlus.isEnabled = quantity < Cart.MAX_QUANTITY
    }

    private fun addToCart() {
        Snackbar.make(b.root, addToCartMessage(product, quantity), Snackbar.LENGTH_LONG)
            .setAction(R.string.view_cart) { startActivity(Intent(this, CartActivity::class.java)) }
            .show()
    }
}
