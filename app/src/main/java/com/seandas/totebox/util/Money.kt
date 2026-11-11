package com.seandas.totebox.util

import java.util.Locale

/** 2499 -> "$24.99" */
fun formatPrice(cents: Int): String = String.format(Locale.US, "$%d.%02d", cents / 100, cents % 100)
