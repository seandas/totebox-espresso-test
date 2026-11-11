package com.seandas.totebox.ui

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import com.seandas.totebox.R
import com.seandas.totebox.databinding.ActivityOrderCompleteBinding
import com.seandas.totebox.util.formatPrice

class OrderCompleteActivity : ShopActivity() {
    companion object {
        const val EXTRA_ORDER_NUMBER = "order_number"
        const val EXTRA_TOTAL_CENTS = "total_cents"
        const val EXTRA_PAY_ON_DELIVERY = "pay_on_delivery"
    }

    override val showsCartIcon = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!requireSession()) return
        val b = ActivityOrderCompleteBinding.inflate(layoutInflater)
        setContentView(b.root)
        setUpToolbar(b.toolbar, getString(R.string.title_order_complete), showBack = false)

        val total = formatPrice(intent.getIntExtra(EXTRA_TOTAL_CENTS, 0))
        b.orderNumber.text = getString(R.string.order_number, intent.getStringExtra(EXTRA_ORDER_NUMBER) ?: "")
        b.orderTotal.text = if (intent.getBooleanExtra(EXTRA_PAY_ON_DELIVERY, false)) {
            getString(R.string.order_total_due, total)
        } else {
            getString(R.string.order_total_paid, total)
        }
        b.continueButton.setOnClickListener { goToProducts() }

        // Back returns to the store, not to the finished checkout
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = goToProducts()
        })
    }
}
