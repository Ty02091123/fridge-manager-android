package com.fridge.fridgeapp

import com.fridge.fridgeapp.util.ExpiryUtils
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExpiryUtilsTest {
    @Test
    fun pastDateIsExpired() {
        assertTrue(ExpiryUtils.isExpired("2000-01-01"))
        assertFalse(ExpiryUtils.isExpiringSoon("2000-01-01"))
    }

    @Test
    fun farFutureDateIsFresh() {
        assertFalse(ExpiryUtils.isExpired("2999-12-31"))
        assertFalse(ExpiryUtils.isExpiringSoon("2999-12-31"))
    }

    @Test
    fun malformedDateIsRejected() {
        assertNull(ExpiryUtils.daysUntilExpiry("2026-02-31"))
        assertFalse(ExpiryUtils.isExpired("not-a-date"))
    }
}
