package com.app.ecarepro.core.domain.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Common utilities for profile formatting and data manipulation
 */
object ProfileUtils {

    /**
     * Format date string to display format (dd MMM yyyy)
     */
    fun formatDate(dateString: String?): String {
        if (dateString.isNullOrBlank()) return "NA"

        return try {
            val formats = listOf(
                "yyyy-MM-dd",
                "dd-MMM-yyyy",
                "dd MMM yyyy",
                "dd/MM/yyyy",
                "M/d/yyyy h:mm:ss a",
                "MM/dd/yyyy h:mm:ss a"
            )
            for (format in formats) {
                try {
                    val inputFormat = SimpleDateFormat(format, Locale.US)
                    val date = inputFormat.parse(dateString)
                    if (date != null) {
                        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.US)
                        return outputFormat.format(date)
                    }
                } catch (e: Exception) {
                    continue
                }
            }
            dateString
        } catch (e: Exception) {
            dateString
        }
    }

    /**
     * Formats a date string into a human-readable relative time label.
     *
     * Examples:
     *  < 1 min      → "Just now"
     *  1–59 min     → "20 min ago"
     *  1–23 hr      → "1 hr ago" / "12 hrs ago"
     *  Yesterday    → "Yesterday"
     *  2–30 days    → "5 Mar"
     *  1–11 months  → "1 month ago" / "3 months ago"
     *  12+ months   → "1 year ago" / "2 years ago"
     *
     * Falls back to the raw [dateString] if parsing fails.
     */
    fun formatRelativeTime(dateString: String?): String {
        if (dateString.isNullOrBlank()) return ""
        val date = parseDate(dateString) ?: return dateString
        val now = Date()
        val diffMs = now.time - date.time
        if (diffMs < 0) return "Just now"          // future date guard

        val diffSec = diffMs / 1_000
        val diffMin = diffSec / 60
        val diffHr  = diffMin / 60

        if (diffSec < 60) return "Just now"
        if (diffMin < 60) return "$diffMin min ago"
        if (diffHr  < 24) return if (diffHr == 1L) "1 hr ago" else "$diffHr hrs ago"

        val nowCal  = Calendar.getInstance()
        val dateCal = Calendar.getInstance().apply { time = date }

        // Yesterday: previous calendar day
        val yesterdayCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
        if (dateCal.get(Calendar.YEAR) == yesterdayCal.get(Calendar.YEAR) &&
            dateCal.get(Calendar.DAY_OF_YEAR) == yesterdayCal.get(Calendar.DAY_OF_YEAR)
        ) return "Yesterday"

        val diffDays = ((nowCal.timeInMillis - dateCal.timeInMillis) / (1_000 * 60 * 60 * 24)).toInt()

        // 2–30 days: show "5 Mar"
        if (diffDays <= 30) {
            return SimpleDateFormat("d MMM", Locale.getDefault()).format(date)
        }

        // Count full months
        val diffMonths = (nowCal.get(Calendar.YEAR) - dateCal.get(Calendar.YEAR)) * 12 +
                (nowCal.get(Calendar.MONTH) - dateCal.get(Calendar.MONTH))

        if (diffMonths < 12) {
            return if (diffMonths == 1) "1 month ago" else "$diffMonths months ago"
        }

        val diffYears = diffMonths / 12
        return if (diffYears == 1) "1 year ago" else "$diffYears years ago"
    }

    /** Parses [dateString] to epoch milliseconds, or null if parsing fails. */
    fun parseDateMillis(dateString: String?): Long? =
        if (dateString.isNullOrBlank()) null else parseDate(dateString)?.time

    private fun parseDate(dateString: String): Date? {
        // 1. Unix epoch (milliseconds or seconds) — pure numeric string
        dateString.toLongOrNull()?.let { epoch ->
            return if (epoch > 1_000_000_000_000L) Date(epoch)   // milliseconds
            else Date(epoch * 1_000)                              // seconds
        }

        // 2. ISO 8601 with timezone offset e.g. "2024-03-15T10:30:00+05:30"
        //    SimpleDateFormat can't parse the colon in "+05:30", so strip it first.
        val iso8601Colon = Regex("""(\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}(?:\.\d+)?)[+-](\d{2}):(\d{2})$""")
        iso8601Colon.find(dateString)?.let { match ->
            val normalized = dateString.replace(Regex("""([+-]\d{2}):(\d{2})$"""), "$1$2")
            val fmt = if ('.' in normalized) "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
                      else "yyyy-MM-dd'T'HH:mm:ssZ"
            try {
                SimpleDateFormat(fmt, Locale.US).parse(normalized)?.let { return it }
            } catch (_: Exception) { }
        }

        // 3. All other string formats — tried in order (most specific first)
        val formats = listOf(
            // API format: "Sat, Feb 28, 2026 12:58 PM"
            "EEE, MMM dd, yyyy hh:mm a",
            "EEE, MMM d, yyyy hh:mm a",
            "EEE, MMM dd, yyyy h:mm a",
            "EEE, MMM d, yyyy h:mm a",
            "EEE, MMM dd, yyyy hh:mm:ss a",
            "EEE, MMM d, yyyy hh:mm:ss a",
            // ISO 8601 — datetime with milliseconds
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
            "yyyy-MM-dd'T'HH:mm:ss.SSS",
            // ISO 8601 — datetime without milliseconds
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd'T'HH:mm:ssZ",
            "yyyy-MM-dd'T'HH:mm:ss",
            // Space-separated datetime
            "yyyy-MM-dd HH:mm:ss.SSS",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd HH:mm",
            // 12-hour AM/PM — various separators
            "MM/dd/yyyy hh:mm:ss a",
            "MM/dd/yyyy h:mm:ss a",
            "MM/dd/yyyy hh:mm a",
            "M/d/yyyy hh:mm:ss a",
            "M/d/yyyy h:mm:ss a",
            "M/d/yyyy h:mm a",
            "dd/MM/yyyy hh:mm:ss a",
            "dd/MM/yyyy h:mm:ss a",
            "d/M/yyyy h:mm:ss a",
            "dd-MM-yyyy h:mm:ss a",
            "dd-MMM-yyyy h:mm:ss a",
            // 24-hour — various separators
            "MM/dd/yyyy HH:mm:ss",
            "dd/MM/yyyy HH:mm:ss",
            "dd-MM-yyyy HH:mm:ss",
            "dd-MMM-yyyy HH:mm:ss",
            "d/M/yyyy HH:mm:ss",
            // RFC 2822 (HTTP date)
            "EEE, dd MMM yyyy HH:mm:ss z",
            "EEE, dd MMM yyyy HH:mm:ss Z",
            // Date only — various formats
            "yyyy-MM-dd",
            "yyyy/MM/dd",
            "MM/dd/yyyy",
            "dd/MM/yyyy",
            "d/M/yyyy",
            "dd-MM-yyyy",
            "dd-MMM-yyyy",
            "d-MMM-yyyy",
            "dd MMM yyyy",
            "d MMM yyyy",
            "MMM dd, yyyy",
            "MMM d, yyyy",
            "MMMM dd, yyyy",
            "MMMM d, yyyy",
            "d MMMM yyyy",
            "dd MMMM yyyy",
        )
        for (format in formats) {
            try {
                val sdf = SimpleDateFormat(format, Locale.US).apply { isLenient = false }
                val parsed = sdf.parse(dateString)
                if (parsed != null) return parsed
            } catch (_: Exception) { }
        }
        return null
    }

    /**
     * Return default value if string is null or blank
     */
    fun String?.orDefault(defaultValue: String = "NA"): String {
        return if (this.isNullOrBlank()) defaultValue else this
    }

    /**
     * Format currency amount
     */
    fun formatCurrency(amount: Double): String {
        return if (amount == amount.toLong().toDouble()) {
            "₹${amount.toLong()}"
        } else {
            "₹%.2f".format(amount)
        }
    }

    /**
     * Get first name from full name
     */
    fun getFirstName(fullName: String?): String {
        return fullName?.split(" ")?.firstOrNull() ?: "User"
    }

    /**
     * Format a Double as Indian Rupees with comma separators (e.g. ₹ 1,23,456.00)
     */
    fun Double.toRupees(): String = "₹ %,.2f".format(this)

    /**
     * Convert a Double amount to an English words representation (e.g. "Rupees One Lakh only")
     */
    fun Double.toWordsRupees(): String {
        val rupees = toLong()
        val words = numberToWords(rupees).replaceFirstChar { it.uppercase() }
        return "Rupees $words only"
    }

    /**
     * Convert a Long number to English words using Indian numbering (lakh/crore)
     */
    fun numberToWords(n: Long): String {
        if (n == 0L) return "zero"
        val ones = listOf(
            "", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine",
            "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen",
            "seventeen", "eighteen", "nineteen"
        )
        val tens = listOf("", "", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety")
        return when {
            n < 20          -> ones[n.toInt()]
            n < 100         -> "${tens[(n / 10).toInt()]}${if (n % 10 != 0L) " ${ones[(n % 10).toInt()]}" else ""}"
            n < 1_000       -> "${ones[(n / 100).toInt()]} hundred${if (n % 100 != 0L) " ${numberToWords(n % 100)}" else ""}"
            n < 1_00_000    -> "${numberToWords(n / 1_000)} thousand${if (n % 1_000 != 0L) " ${numberToWords(n % 1_000)}" else ""}"
            n < 1_00_00_000 -> "${numberToWords(n / 1_00_000)} lakh${if (n % 1_00_000 != 0L) " ${numberToWords(n % 1_00_000)}" else ""}"
            else            -> "${numberToWords(n / 1_00_00_000)} crore${if (n % 1_00_00_000 != 0L) " ${numberToWords(n % 1_00_00_000)}" else ""}"
        }
    }
}
