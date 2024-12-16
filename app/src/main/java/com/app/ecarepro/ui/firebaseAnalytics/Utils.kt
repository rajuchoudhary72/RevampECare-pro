package com.app.ecarepro.ui.firebaseAnalytics

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

fun getNetworkType(context: Context): String {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val network = connectivityManager.activeNetwork ?: return "none"
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return "none"

    return when {
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "wifi"
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "mobile"
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "ethernet"
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> "bluetooth"
        else -> "unknown"
    }
}
