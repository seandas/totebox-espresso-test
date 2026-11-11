package com.seandas.totebox.data

/** The shopping cart and the saved-for-later list, kept in memory for the app session. */
object Cart {
    const val MAX_QUANTITY = 10
    const val FREE_SHIPPING_THRESHOLD_CENTS = 5000
    const val SHIPPING_CENTS = 499

    private val lines = linkedMapOf<Int, Int>() // product id -> quantity
    private val saved = linkedMapOf<Int, Int>() // saved for later: product id -> quantity

    /** Adds up to [quantity], stopping at [MAX_QUANTITY] per product. Returns how many were actually added. */
    fun add(productId: Int, quantity: Int = 1): Int {
        val current = quantityOf(productId)
        val updated = (current + quantity).coerceIn(current, MAX_QUANTITY)
        if (updated > current) lines[productId] = updated
        return updated - current
    }

    fun setQuantity(productId: Int, quantity: Int) {
        if (quantity <= 0) lines.remove(productId) else lines[productId] = quantity.coerceAtMost(MAX_QUANTITY)
    }

    fun remove(productId: Int) {
        lines.remove(productId)
    }

    /** Empties the cart (after an order). Saved-for-later items stay. */
    fun clear() = lines.clear()

    /** Empties the cart and the saved list (on log out). */
    fun clearAll() {
        lines.clear()
        saved.clear()
    }

    /** Moves a cart line to the saved list, adding to any quantity already saved. */
    fun saveForLater(productId: Int) {
        val quantity = lines.remove(productId) ?: return
        saved[productId] = (savedQuantityOf(productId) + quantity).coerceAtMost(MAX_QUANTITY)
    }

    /**
     * Moves a saved item back to the cart, up to the cart's per-product limit. Whatever does not fit
     * stays saved. Returns how many were moved.
     */
    fun moveToCart(productId: Int): Int {
        val quantity = saved[productId] ?: return 0
        val moved = add(productId, quantity)
        if (moved == quantity) saved.remove(productId) else saved[productId] = quantity - moved
        return moved
    }

    /**
     * Saves a product from the store list. A product already in the cart moves there with its
     * quantity (so it is never in both places); otherwise one is saved.
     */
    fun saveFromStore(productId: Int) {
        if (productId in lines) saveForLater(productId)
        else if (productId !in saved) saved[productId] = 1
    }

    fun isSaved(productId: Int): Boolean = productId in saved

    fun deleteSaved(productId: Int) {
        saved.remove(productId)
    }

    fun savedQuantityOf(productId: Int): Int = saved[productId] ?: 0

    val savedCount: Int get() = saved.values.sum()

    fun savedItems(): List<Pair<Product, Int>> =
        saved.mapNotNull { (id, quantity) -> Catalog.find(id)?.let { it to quantity } }

    fun quantityOf(productId: Int): Int = lines[productId] ?: 0

    val itemCount: Int get() = lines.values.sum()

    fun items(): List<Pair<Product, Int>> =
        lines.mapNotNull { (id, quantity) -> Catalog.find(id)?.let { it to quantity } }

    val subtotalCents: Int get() = items().sumOf { (product, quantity) -> product.priceCents * quantity }

    /** Free over the threshold; nothing to ship when the cart is empty. */
    val shippingCents: Int
        get() {
            val subtotal = subtotalCents
            return if (subtotal == 0 || subtotal >= FREE_SHIPPING_THRESHOLD_CENTS) 0 else SHIPPING_CENTS
        }

    val totalCents: Int get() = subtotalCents + shippingCents
}
