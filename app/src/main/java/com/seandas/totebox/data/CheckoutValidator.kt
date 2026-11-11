package com.seandas.totebox.data

import java.time.YearMonth

enum class CheckoutField { NAME, ADDRESS, CITY, ZIP, CARD_NUMBER, CARD_EXPIRY, CARD_CVV }

enum class CheckoutProblem { REQUIRED, ZIP_FORMAT, CARD_NUMBER_FORMAT, EXPIRY_FORMAT, EXPIRY_PAST, CVV_FORMAT }

data class CheckoutForm(
    val name: String,
    val address: String,
    val city: String,
    val zip: String,
    val payByCard: Boolean,
    val cardNumber: String = "",
    val cardExpiry: String = "",
    val cardCvv: String = "",
)

/** Checks the checkout form. Returns the problems in screen order; empty means the form is valid. */
object CheckoutValidator {
    private val zipPattern = Regex("""\d{5}""")
    private val expiryPattern = Regex("""(0[1-9]|1[0-2])/(\d{2})""")
    private val cvvPattern = Regex("""\d{3}""")

    fun validate(form: CheckoutForm, now: YearMonth = YearMonth.now()): Map<CheckoutField, CheckoutProblem> {
        val problems = linkedMapOf<CheckoutField, CheckoutProblem>()

        if (form.name.isBlank()) problems[CheckoutField.NAME] = CheckoutProblem.REQUIRED
        if (form.address.isBlank()) problems[CheckoutField.ADDRESS] = CheckoutProblem.REQUIRED
        if (form.city.isBlank()) problems[CheckoutField.CITY] = CheckoutProblem.REQUIRED

        val zip = form.zip.trim()
        when {
            zip.isEmpty() -> problems[CheckoutField.ZIP] = CheckoutProblem.REQUIRED
            !zipPattern.matches(zip) -> problems[CheckoutField.ZIP] = CheckoutProblem.ZIP_FORMAT
        }

        if (form.payByCard) {
            val digits = form.cardNumber.filterNot { it == ' ' || it == '-' }
            when {
                digits.isEmpty() -> problems[CheckoutField.CARD_NUMBER] = CheckoutProblem.REQUIRED
                digits.length != 16 || !digits.all { it.isDigit() } ->
                    problems[CheckoutField.CARD_NUMBER] = CheckoutProblem.CARD_NUMBER_FORMAT
            }

            val expiry = form.cardExpiry.trim()
            val match = expiryPattern.matchEntire(expiry)
            when {
                expiry.isEmpty() -> problems[CheckoutField.CARD_EXPIRY] = CheckoutProblem.REQUIRED
                match == null -> problems[CheckoutField.CARD_EXPIRY] = CheckoutProblem.EXPIRY_FORMAT
                else -> {
                    val (month, year) = match.destructured
                    if (YearMonth.of(2000 + year.toInt(), month.toInt()).isBefore(now)) {
                        problems[CheckoutField.CARD_EXPIRY] = CheckoutProblem.EXPIRY_PAST
                    }
                }
            }

            val cvv = form.cardCvv.trim()
            when {
                cvv.isEmpty() -> problems[CheckoutField.CARD_CVV] = CheckoutProblem.REQUIRED
                !cvvPattern.matches(cvv) -> problems[CheckoutField.CARD_CVV] = CheckoutProblem.CVV_FORMAT
            }
        }
        return problems
    }
}
