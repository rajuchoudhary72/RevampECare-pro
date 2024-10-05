package com.app.ecarepro.data.network.intercepter

 import android.app.Activity
 import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat.getString
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.app.ecarepro.R
import com.app.ecarepro.ui.mainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import com.app.ecarepro.ui.MainActivity
import com.app.ecarepro.ui.SystemViewModel
import com.app.ecarepro.ui.mainActivity
import com.app.ecarepro.utils.Constant

import javax.inject.Inject
import kotlin.jvm.java

class ConnectivityInterceptor @Inject constructor(
    @ApplicationContext val mcontext: Context,


 ) : Interceptor {

    private val handler = Handler(Looper.getMainLooper()) // Handler for the main thread


    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isNetworkConnected()) {
            // Show  on the main thread
           // handler.post { showRetryDialog(chain) }
//            val activity = getActivity(mcontext) as? MainActivity
//
//            // Check if Activity is valid
//            if (activity != null && !activity.isFinishing) {
//
//                activity.showRetryDialog(chain,mcontext)
//            } else {
//                // Handle the case where Activity is not available (e.g., show a Toast)
//                (mcontext as? android.app.Application)?.mainExecutor?.execute {
//                    Toast.makeText(mcontext, "No Internet Connection", Toast.LENGTH_SHORT).show()
//                }
//            }

//            val activity = getActivity(mcontext) as? MainActivity
//            activity!!.showRetryDialog(chain,mcontext)
           // mainActivity.showRetryDialog(chain)
            showAlert("No Internet Connection", "Please check your internet connection and try again.")

        }
        return chain.proceed(chain.request())
    }

    private fun isNetworkConnected(): Boolean {
        val connectivityManager = mcontext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false
    }

    private fun showAlert(title: String, message: String) {
        Handler(Looper.getMainLooper()).post {
            // Creating the alert dialog requires an Activity context
            AlertDialog.Builder(mcontext)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
             }}

    private fun showRetryDialog(chain: Interceptor.Chain) {
        val activity = getActivity(mcontext) // Get the current activity
        if (activity != null && !activity.isFinishing) { // Check if activity is valid
            activity.runOnUiThread { // Run on UI thread
                val dialog = AlertDialog.Builder(activity)
                    .setTitle("No Internet Connection")
                    .setMessage("Please check your internet connection and try again.")
                    .setPositiveButton("Retry") { _, _ ->
                        if (isNetworkConnected()) {
                            try {
                                chain.proceed(chain.request()) // Retry the API call
                            } catch (e: Exception) {
                                // Handle error if retry fails
                            }
                        } else {
                            showRetryDialog(chain) // Show dialog again if still no internet
                        }
                    }
                    .setCancelable(false)
                    .create()
                dialog.show()
            }
        }
    }

    private fun getActivity(context: Context): android.app.Activity? {
        if (context is android.app.Activity) {
            return context
        } else if (context is android.content.ContextWrapper) {
            return getActivity(context.baseContext)
        }
        return null
    }


}


