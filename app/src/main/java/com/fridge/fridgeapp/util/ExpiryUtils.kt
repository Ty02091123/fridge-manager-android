package com.fridge.fridgeapp.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object ExpiryUtils {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)

    fun todayText(): String = dateFormat.format(Date())

    fun daysUntilExpiry(expireDate: String): Int? {
        val expiry = parseDate(expireDate) ?: return null
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return TimeUnit.MILLISECONDS.toDays(expiry.time - today.timeInMillis).toInt()
    }

    fun isExpiringSoon(expireDate: String): Boolean {
        val days = daysUntilExpiry(expireDate) ?: return false
        return days in 0..7
    }

    fun isExpired(expireDate: String): Boolean {
        val days = daysUntilExpiry(expireDate) ?: return false
        return days < 0
    }

    private fun parseDate(value: String): Date? {
        return try {
            dateFormat.isLenient = false
            dateFormat.parse(value)
        } catch (_: ParseException) {
            null
        }
    }
}
