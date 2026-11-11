package com.seandas.totebox.data

import com.seandas.totebox.R

/** The store's products. Fixed sample data, so the app needs no server. */
object Catalog {
    val products = listOf(
        Product(1, "Canvas Tote Bag", "Bags", 2499,
            "Heavy cotton canvas tote with an inside pocket. Fits a laptop and lunch.", "#2E7D6B",
            R.drawable.ic_product_tote),
        Product(2, "Ceramic Travel Mug", "Kitchen", 1899,
            "Double-walled 12 oz mug with a leak-proof lid.", "#C25B3A",
            R.drawable.ic_product_mug),
        Product(3, "Wireless Earbuds", "Electronics", 5999,
            "Bluetooth earbuds with a 24-hour charging case.", "#3949AB",
            R.drawable.ic_product_earbuds),
        Product(4, "Desk Plant", "Home", 1499,
            "Easy-care pothos in a 4-inch pot. Happy in low light.", "#558B2F",
            R.drawable.ic_product_plant),
        Product(5, "Notebook Set", "Stationery", 1299,
            "Three dotted A5 notebooks with lay-flat binding.", "#8D6E63",
            R.drawable.ic_product_notebook),
        Product(6, "Steel Water Bottle", "Outdoors", 2199,
            "Insulated 24 oz bottle. Keeps drinks cold for 24 hours.", "#0277BD",
            R.drawable.ic_product_bottle),
        Product(7, "Wool Beanie", "Apparel", 1999,
            "Soft merino beanie, one size fits most.", "#6A1B9A",
            R.drawable.ic_product_beanie),
        Product(8, "Phone Stand", "Electronics", 999,
            "Adjustable aluminum stand for phones and small tablets.", "#455A64",
            R.drawable.ic_product_phone_stand),
    )

    /**
     * Shown last in the product list, whatever the sort, but not part of [products]. Adding it to
     * the cart crashes the app on purpose, to test crash handling.
     */
    val crashTestItem = Product(99, "Crash test item", "Add this item to crash the app", 0,
        "Adding this item to the cart crashes the app on purpose, to test crash handling.", "#C62828",
        R.drawable.ic_product_crash, isCrashTest = true)

    fun find(id: Int): Product? = (products + crashTestItem).firstOrNull { it.id == id }
}
