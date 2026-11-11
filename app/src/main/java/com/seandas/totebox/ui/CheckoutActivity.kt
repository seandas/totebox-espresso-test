package com.seandas.totebox.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.textfield.TextInputLayout
import com.seandas.totebox.R
import com.seandas.totebox.data.Cart
import com.seandas.totebox.data.CheckoutField
import com.seandas.totebox.data.CheckoutForm
import com.seandas.totebox.data.CheckoutProblem
import com.seandas.totebox.data.CheckoutValidator
import com.seandas.totebox.data.Session
import com.seandas.totebox.databinding.ActivityCheckoutBinding
import com.seandas.totebox.util.FakeNetwork
import com.seandas.totebox.util.OrderNumbers
import com.seandas.totebox.util.formatPrice

class CheckoutActivity : ShopActivity() {
    override val showsCartIcon = false

    private lateinit var b: ActivityCheckoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!requireSession()) return
        if (!Session.isLoggedIn) { // guests log in before checking out
            logInToCheckOut()
            finish()
            return
        }
        if (Cart.itemCount == 0) {
            finish()
            return
        }
        b = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(b.root)
        setUpToolbar(b.toolbar, getString(R.string.title_checkout), showBack = true)

        b.checkoutTotal.text = getString(R.string.order_total, formatPrice(Cart.totalCents))
        b.paymentGroup.setOnCheckedChangeListener { _, checkedId ->
            b.cardSection.visibility = if (checkedId == R.id.pay_card) View.VISIBLE else View.GONE
        }
        CheckoutField.entries.forEach { field ->
            layoutFor(field).editText?.doAfterTextChanged { layoutFor(field).error = null }
        }
        b.placeOrderButton.setOnClickListener { placeOrder() }
    }

    private fun layoutFor(field: CheckoutField): TextInputLayout = when (field) {
        CheckoutField.NAME -> b.nameLayout
        CheckoutField.ADDRESS -> b.addressLayout
        CheckoutField.CITY -> b.cityLayout
        CheckoutField.ZIP -> b.zipLayout
        CheckoutField.CARD_NUMBER -> b.cardNumberLayout
        CheckoutField.CARD_EXPIRY -> b.cardExpiryLayout
        CheckoutField.CARD_CVV -> b.cardCvvLayout
    }

    private fun messageFor(field: CheckoutField, problem: CheckoutProblem): Int = when (problem) {
        CheckoutProblem.REQUIRED -> when (field) {
            CheckoutField.NAME -> R.string.error_name_required
            CheckoutField.ADDRESS -> R.string.error_address_required
            CheckoutField.CITY -> R.string.error_city_required
            CheckoutField.ZIP -> R.string.error_zip_required
            CheckoutField.CARD_NUMBER -> R.string.error_card_required
            CheckoutField.CARD_EXPIRY -> R.string.error_expiry_required
            CheckoutField.CARD_CVV -> R.string.error_cvv_required
        }
        CheckoutProblem.ZIP_FORMAT -> R.string.error_zip_format
        CheckoutProblem.CARD_NUMBER_FORMAT -> R.string.error_card_format
        CheckoutProblem.EXPIRY_FORMAT -> R.string.error_expiry_format
        CheckoutProblem.EXPIRY_PAST -> R.string.error_expiry_past
        CheckoutProblem.CVV_FORMAT -> R.string.error_cvv_format
    }

    private fun placeOrder() {
        val form = CheckoutForm(
            name = b.nameInput.text.toString(),
            address = b.addressInput.text.toString(),
            city = b.cityInput.text.toString(),
            zip = b.zipInput.text.toString(),
            payByCard = b.payCard.isChecked,
            cardNumber = b.cardNumberInput.text.toString(),
            cardExpiry = b.cardExpiryInput.text.toString(),
            cardCvv = b.cardCvvInput.text.toString(),
        )
        CheckoutField.entries.forEach { layoutFor(it).error = null }
        val problems = CheckoutValidator.validate(form)
        if (problems.isNotEmpty()) {
            problems.forEach { (field, problem) -> layoutFor(field).error = getString(messageFor(field, problem)) }
            layoutFor(problems.keys.first()).requestFocus()
            return
        }

        val total = Cart.totalCents
        b.placeOrderButton.isEnabled = false
        b.checkoutProgress.visibility = View.VISIBLE
        FakeNetwork.run(1200, { OrderNumbers.next() }) { orderNumber ->
            if (isDestroyed) return@run
            Cart.clear()
            startActivity(Intent(this, OrderCompleteActivity::class.java)
                .putExtra(OrderCompleteActivity.EXTRA_ORDER_NUMBER, orderNumber)
                .putExtra(OrderCompleteActivity.EXTRA_TOTAL_CENTS, total)
                .putExtra(OrderCompleteActivity.EXTRA_PAY_ON_DELIVERY, !form.payByCard))
            finish()
        }
    }
}
