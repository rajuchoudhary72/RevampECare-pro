package com.app.ecarepro.data.network.intercepter

 import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Handler
import android.os.Looper
  import com.app.ecarepro.ECateProApp
 import dagger.hilt.android.qualifiers.ApplicationContext
 import kotlinx.coroutines.runBlocking
 import okhttp3.Interceptor
import okhttp3.Response

 import javax.inject.Inject

class ConnectivityInterceptor @Inject constructor(
    @ApplicationContext val mcontext: Context,
  ) : Interceptor {

    private val handler = Handler(Looper.getMainLooper()) // Handler for the main thread
    private var dialogShown = false // Prevent multiple dialogs


    override fun intercept(chain: Interceptor.Chain): Response {
        while (!isNetworkConnected()) {
            runBlocking {
                // Show retry dialog only once
                if (!dialogShown) {
                    showRetryDialog {
                        dialogShown = false // Reset flag once dialog closes
                    }
                }


            }
        }
        return chain.proceed(chain.request())
    }

    private fun isNetworkConnected(): Boolean {
        val connectivityManager = mcontext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false
    }



    private fun showRetryDialog(onDialogClosed: () -> Unit) {
        val appContext = mcontext as ECateProApp
        if (dialogShown) return // Only show one dialog at a time
        dialogShown = true

        handler.post {
            val dialog = AlertDialog.Builder(appContext.getCurrentActivity() )
                .setTitle("No Internet Connection")
                .setMessage("Please check your internet connection and try again.")
                .setPositiveButton("Retry") { _, _ ->
                    if (isNetworkConnected()) {
                        onDialogClosed() // Internet is available, allow request to proceed
                    } else {
                        dialogShown=false
                        showRetryDialog(onDialogClosed) // Retry if no internet still
                    }
                }
                .setCancelable(false)
                .create()
            dialog.show()
        }

    }


}



