package com.fridge.dashboard.data

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object DashboardDateUtils {
    private val format = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)

    fun todayText(): String = synchronized(format) { format.format(Date()) }

    fun daysUntil(expireDate: String): Int? = synchronized(format) {
        val expiry = parse(expireDate) ?: return@synchronized null
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        TimeUnit.MILLISECONDS.toDays(expiry.time - today.timeInMillis).toInt()
    }

    fun isExpiringSoon(expireDate: String) = daysUntil(expireDate)?.let { it in 0..7 } == true

    fun isExpired(expireDate: String) = daysUntil(expireDate)?.let { it < 0 } == true

    private fun parse(value: String): Date? = try {
        format.isLenient = false
        format.parse(value)
    } catch (_: ParseException) {
        null
    }
}
