package com.seandas.totebox.data

import com.seandas.totebox.util.CrashTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CatalogTest {
    @Test
    fun crashTestItemIsNotARealProductButCanBeFound() {
        assertFalse(Catalog.products.any { it.isCrashTest })
        assertTrue(Catalog.find(Catalog.crashTestItem.id)!!.isCrashTest)
        assertEquals(8, Catalog.products.size)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun crashTestThrowsIndexOutOfBounds() {
        Cart.clearAll()
        CrashTest.crash()
    }
}
