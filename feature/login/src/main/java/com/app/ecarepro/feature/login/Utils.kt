package com.app.ecarepro.feature.login

import android.content.Context
import android.content.Intent
import android.net.Uri

fun Context.dial(phone: String) {
    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
    startActivity(intent)
}

fun Context.sendMail(email: String) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
    }
    startActivity(Intent.createChooser(intent, "Send Email"))
}