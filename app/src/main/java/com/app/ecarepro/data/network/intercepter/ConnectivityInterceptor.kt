package com.app.ecarepro.data.network.intercepter

 import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Handler
import android.os.Looper
 import android.util.Log
 import com.app.ecarepro.ECateProApp
 import dagger.hilt.android.qualifiers.ApplicationContext
 import kotlinx.coroutines.runBlocking
 import okhttp3.Interceptor
import okhttp3.Response

 import javax.inject.Inject

class ConnectivityInterceptor @Inject constructor(
    @ApplicationContext private val context: Context
) : Interceptor {

    @Volatile
    private var dialogShown = false // Prevent multiple dialogs

    private val handler = Handler(Looper.getMainLooper()) // Main thread handler

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isNetworkConnected()) {
            runBlocking { waitForNetworkConnection() } // Wait for connection in a coroutine
        }
        return chain.proceed(chain.request()) // Proceed with the API call
    }

    private suspend fun waitForNetworkConnection() {
        // Suspend until the network is connected
        while (!isNetworkConnected()) {
            showRetryDialogIfNeeded()
            kotlinx.coroutines.delay(1000) // Polling delay to avoid busy-waiting
        }
    }

    private fun isNetworkConnected(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        val isConnected = capabilities?.run {
            hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) && hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } ?: false
        Log.d("ConnectivityInterceptor", "Network connected: $isConnected")
        return isConnected
    }

    private fun showRetryDialogIfNeeded() {
        if (dialogShown) return // Only show one dialog at a time

        dialogShown = true
        handler.post {
            val dialog = AlertDialog.Builder((context as ECateProApp).getCurrentActivity())
                .setTitle("No Internet Connection")
                .setMessage("Please check your internet connection and try again.")
                .setPositiveButton("Retry") { _, _ ->
                    dialogShown = false // Allow new dialog if needed
                }
                .setCancelable(false)
                .create()
            dialog.show()
        }
    }
}
