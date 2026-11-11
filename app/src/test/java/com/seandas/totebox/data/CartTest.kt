package com.seandas.totebox.data

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CartTest {
    @Before
    fun emptyCart() = Cart.clearAll()

    @Test
    fun addReportsHowManyWereAdded() {
        assertEquals(3, Cart.add(1, 3))
        assertEquals(3, Cart.quantityOf(1))
    }

    @Test
    fun addStopsAtTheLimitPerProduct() {
        Cart.add(1, 8)
        assertEquals(2, Cart.add(1, 5))
        assertEquals(0, Cart.add(1))
        assertEquals(Cart.MAX_QUANTITY, Cart.quantityOf(1))
    }

    @Test
    fun shippingIsChargedBelowTheThresholdAndFreeFromIt() {
        Cart.add(1) // $24.99
        assertEquals(Cart.SHIPPING_CENTS, Cart.shippingCents)
        assertEquals(2499 + Cart.SHIPPING_CENTS, Cart.totalCents)

        Cart.add(3) // + $59.99
        assertEquals(0, Cart.shippingCents)
        assertEquals(8498, Cart.totalCents)
    }

    @Test
    fun emptyCartHasNoShipping() {
        assertEquals(0, Cart.shippingCents)
        assertEquals(0, Cart.totalCents)
    }

    @Test
    fun saveForLaterMovesTheLineOutOfTheCart() {
        Cart.add(1, 2)
        Cart.add(3)
        Cart.saveForLater(1)
        assertEquals(0, Cart.quantityOf(1))
        assertEquals(2, Cart.savedQuantityOf(1))
        assertEquals(1, Cart.itemCount)
        assertEquals(5999, Cart.subtotalCents) // saved items are not charged
    }

    @Test
    fun saveForLaterKeepsWhatDoesNotFitInTheCart() {
        Cart.add(1, 2)
        Cart.saveForLater(1) // 2 saved
        Cart.add(1, 10)
        assertEquals(8, Cart.saveForLater(1))
        assertEquals(Cart.MAX_QUANTITY, Cart.savedQuantityOf(1))
        assertEquals(2, Cart.quantityOf(1)) // nothing lost: 10 saved + 2 in the cart
        assertEquals(0, Cart.saveForLater(1)) // saved list is full
        assertEquals(2, Cart.quantityOf(1))
    }

    @Test
    fun moveToCartBringsTheSavedQuantityBack() {
        Cart.add(1, 2)
        Cart.saveForLater(1)
        assertEquals(2, Cart.moveToCart(1))
        assertEquals(2, Cart.quantityOf(1))
        assertEquals(0, Cart.savedCount)
    }

    @Test
    fun moveToCartKeepsWhatDoesNotFitUnderTheLimit() {
        Cart.add(1, 4)
        Cart.saveForLater(1)
        Cart.add(1, 8)
        assertEquals(2, Cart.moveToCart(1))
        assertEquals(Cart.MAX_QUANTITY, Cart.quantityOf(1))
        assertEquals(2, Cart.savedQuantityOf(1))
    }

    @Test
    fun deleteSavedRemovesOnlyTheSavedItem() {
        Cart.add(1)
        Cart.add(2)
        Cart.saveForLater(2)
        Cart.deleteSaved(2)
        assertEquals(0, Cart.savedCount)
        assertEquals(1, Cart.quantityOf(1))
    }

    @Test
    fun placingAnOrderKeepsSavedItems() {
        Cart.add(1)
        Cart.add(2)
        Cart.saveForLater(2)
        Cart.clear()
        assertEquals(0, Cart.itemCount)
        assertEquals(1, Cart.savedQuantityOf(2))
    }

    @Test
    fun savingFromTheStoreSavesOne() {
        Cart.saveFromStore(4)
        assertEquals(1, Cart.savedQuantityOf(4))
        assertEquals(true, Cart.isSaved(4))
        Cart.saveFromStore(4) // already saved: no change
        assertEquals(1, Cart.savedQuantityOf(4))
    }

    @Test
    fun savingFromTheStoreMovesTheWholeCartLine() {
        Cart.add(4, 3)
        Cart.saveFromStore(4)
        assertEquals(0, Cart.quantityOf(4))
        assertEquals(3, Cart.savedQuantityOf(4))
    }

    @Test
    fun settingQuantityToZeroRemovesTheLine() {
        Cart.add(2, 2)
        Cart.setQuantity(2, 0)
        assertEquals(0, Cart.itemCount)
    }
}
