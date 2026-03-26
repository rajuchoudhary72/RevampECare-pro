package com.app.ecarepro.core.domain.ext

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Patterns
import android.widget.Toast

/**
 * Utility object for handling common intents like email, phone calls, etc.
 * This can be used across all modules for consistent behavior.
 */
object IntentUtils {

    /**
     * Opens the default email client with the given email address
     * @param context The context to launch the intent
     * @param email The email address to send to
     */
    fun openEmail(context: Context, email: String?) {
        if (email.isNullOrBlank() || email == "NA") {
            Toast.makeText(context, "No email address available", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$email")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            // Try to launch the email client
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                // If SENDTO doesn't work, try with chooser
                val chooserIntent = Intent.createChooser(intent, "Send email via")
                chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(chooserIntent)
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to open email app: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Opens the dialer with the given phone number
     * @param context The context to launch the intent
     * @param phoneNumber The phone number to dial
     */
    fun openDialer(context: Context, phoneNumber: String?) {
        if (phoneNumber.isNullOrBlank() || phoneNumber == "NA") {
            Toast.makeText(context, "No phone number available", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phoneNumber")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to open dialer: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Opens a web browser with the given URL
     * @param context The context to launch the intent
     * @param url The URL to open
     */
    fun openUrl(context: Context, url: String?) {
        if (url.isNullOrBlank() || url == "NA") {
            Toast.makeText(context, "No URL available", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(if (url.startsWith("http")) url else "https://$url")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to open URL: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Detects if a string is an email address
     */
    fun isEmail(text: String?): Boolean {
        if (text.isNullOrBlank() || text == "NA") return false
        return Patterns.EMAIL_ADDRESS.matcher(text).matches()
    }

    /**
     * Detects if a string is a phone number
     * Only validates phone numbers with 10-15 digits (excluding country code +)
     * This prevents false positives with Aadhar numbers, PAN, etc.
     */
    fun isPhoneNumber(text: String?): Boolean {
        if (text.isNullOrBlank() || text == "NA") return false

        // Remove all non-digit characters except +
        val cleanedText = text.replace(Regex("[^0-9+]"), "")

        // Extract only digits (without +)
        val digitsOnly = cleanedText.replace("+", "")

        // Phone numbers should have exactly 10-15 digits
        // This excludes 12-digit Aadhar numbers and other ID numbers
        if (digitsOnly.length < 10 || digitsOnly.length > 15) {
            return false
        }

        // Use Android's built-in phone pattern for additional validation
        return Patterns.PHONE.matcher(text).matches()
    }

    /**
     * Auto-detects the type of content and opens the appropriate app
     */
    fun handleAutoLaunch(context: Context, text: String?) {
        when {
            isEmail(text) -> openEmail(context, text)
            isPhoneNumber(text) -> openDialer(context, text)
            else -> Toast.makeText(context, "Not a valid email or phone number", Toast.LENGTH_SHORT).show()
        }
    }
}
