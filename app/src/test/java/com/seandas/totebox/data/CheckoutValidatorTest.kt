package com.seandas.totebox.data

import java.time.YearMonth
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CheckoutValidatorTest {
    private val now = YearMonth.of(2026, 11)
    private val valid = CheckoutForm(
        name = "Sam Shopper", address = "1 Main St", city = "New York", zip = "10001",
        payByCard = true, cardNumber = "4111 1111 1111 1111", cardExpiry = "11/26", cardCvv = "123",
    )

    private fun problems(form: CheckoutForm) = CheckoutValidator.validate(form, now)

    @Test
    fun validCardOrderHasNoProblems() = assertTrue(problems(valid).isEmpty())

    @Test
    fun blankFormReportsEveryFieldInScreenOrder() {
        val blank = CheckoutForm(name = "", address = "", city = "", zip = "", payByCard = true)
        assertEquals(CheckoutField.entries.toList(), problems(blank).keys.toList())
        assertTrue(problems(blank).values.all { it == CheckoutProblem.REQUIRED })
    }

    @Test
    fun payOnDeliveryIgnoresTheCardFields() {
        val form = valid.copy(payByCard = false, cardNumber = "", cardExpiry = "", cardCvv = "")
        assertTrue(problems(form).isEmpty())
    }

    @Test
    fun formatProblems() {
        assertEquals(CheckoutProblem.ZIP_FORMAT, problems(valid.copy(zip = "1234"))[CheckoutField.ZIP])
        assertEquals(CheckoutProblem.CARD_NUMBER_FORMAT, problems(valid.copy(cardNumber = "4111"))[CheckoutField.CARD_NUMBER])
        assertEquals(CheckoutProblem.EXPIRY_FORMAT, problems(valid.copy(cardExpiry = "13/27"))[CheckoutField.CARD_EXPIRY])
        assertEquals(CheckoutProblem.CVV_FORMAT, problems(valid.copy(cardCvv = "12"))[CheckoutField.CARD_CVV])
    }

    @Test
    fun cardIsGoodThroughItsExpiryMonthAndRejectedAfter() {
        assertTrue(problems(valid.copy(cardExpiry = "11/26")).isEmpty())
        assertEquals(CheckoutProblem.EXPIRY_PAST, problems(valid.copy(cardExpiry = "10/26"))[CheckoutField.CARD_EXPIRY])
    }
}
