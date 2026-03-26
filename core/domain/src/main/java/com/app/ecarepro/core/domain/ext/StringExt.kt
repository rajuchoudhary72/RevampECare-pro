package com.app.ecarepro.core.domain.ext

/**
 * Converts a numeric string to its ordinal form.
 * Examples: "1" -> "1st", "2" -> "2nd", "3" -> "3rd", "4" -> "4th", etc.
 * If the string is not a valid number, returns the original string.
 *
 * @return The ordinal form of the number or the original string if not a number
 */
fun String.toOrdinal(): String {
    val number = this.toIntOrNull() ?: return this
    return number.toOrdinal()
}

/**
 * Converts a number to its ordinal form.
 * Examples: 1 -> "1st", 2 -> "2nd", 3 -> "3rd", 4 -> "4th", etc.
 *
 * @return The ordinal form of the number
 */
fun Int.toOrdinal(): String {
    val suffix = when {
        this % 100 in 11..13 -> "th"
        this % 10 == 1 -> "st"
        this % 10 == 2 -> "nd"
        this % 10 == 3 -> "rd"
        else -> "th"
    }
    return "$this$suffix"
}
