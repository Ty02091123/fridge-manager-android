package com.fridge.dashboard

import com.fridge.dashboard.data.DashboardDateUtils
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DashboardDateUtilsTest {
    @Test
    fun pastDateIsExpired() {
        assertTrue(DashboardDateUtils.isExpired("2000-01-01"))
        assertFalse(DashboardDateUtils.isExpiringSoon("2000-01-01"))
    }

    @Test
    fun futureDateIsFresh() {
        assertFalse(DashboardDateUtils.isExpired("2999-12-31"))
        assertFalse(DashboardDateUtils.isExpiringSoon("2999-12-31"))
    }

    @Test
    fun invalidDateReturnsNull() {
        assertNull(DashboardDateUtils.daysUntil("2026-02-31"))
        assertNull(DashboardDateUtils.daysUntil("bad-date"))
    }
}
