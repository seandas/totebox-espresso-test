package com.seandas.totebox.data

import androidx.annotation.DrawableRes

data class Product(
    val id: Int,
    val name: String,
    val category: String,
    val priceCents: Int,
    val description: String,
    val colorHex: String,
    /** A generic picture of the item, drawn in white over [colorHex]. */
    @DrawableRes val imageRes: Int,
    /** The "Crash test item": adding it to the cart crashes the app on purpose (see util/CrashTest.kt). */
    val isCrashTest: Boolean = false,
)
