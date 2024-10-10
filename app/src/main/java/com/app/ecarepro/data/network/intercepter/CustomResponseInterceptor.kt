package com.app.ecarepro.data.network.intercepter



import android.app.AlertDialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.app.ecarepro.ECateProApp
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody
import org.json.JSONObject
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CustomResponseInterceptor @Inject constructor(
    @ApplicationContext private val context: Context
) : Interceptor {

    private val handler = Handler(Looper.getMainLooper())
    private var dialogShown = false // Prevent multiple dialogs

    override fun intercept(chain: Interceptor.Chain): Response {

        var response = chain.proceed(chain.request())
        response = handleResponse(chain, response)
        return response
    }

    private fun handleResponse(chain: Interceptor.Chain, response: Response): Response {
        val responseBody = response.body
        val responseBodyString = responseBody .string() ?: ""

        try {

             val jsonObject = JSONObject(responseBodyString)


            val errorCode = jsonObject.optInt("errorCode", -1)
            val message = jsonObject.optString("message", "An error occurred")

            when (errorCode) {
                0 -> {

                    return response.newBuilder()
                        .body(ResponseBody.create(responseBody.contentType(), responseBodyString))
                        .build()
                }
                1 -> {

                    runBlocking {
                        if (!dialogShown) {
                            if (showRetryDialog()) {
                                response.close() // Close current response before retrying
                                  handleResponse(chain, chain.proceed(chain.request())) // Recursive retry
                            }
                        }
                    }
                }
                2 -> showMessageDialog(message)
            }
        } catch (_: Exception) { }

        return response.newBuilder()
            .body(ResponseBody.create(responseBody.contentType(), responseBodyString))
            .build()
    }

    private suspend fun showRetryDialog(): Boolean {
        return suspendCoroutine { continuation ->
            dialogShown=true
            handler.post {
                val appContext =  context as ECateProApp
                val dialog = AlertDialog.Builder(appContext.getCurrentActivity())
                    .setTitle("Request Failed")
                    .setMessage("An error occurred. Would you like to retry?")
                    .setPositiveButton("Retry") { _, _ ->
                         dialogShown=false
                        continuation.resume(true) // Retry selected
                    } .setNegativeButton("Cancel") { _, _ ->
                        dialogShown=false
                        continuation.resume(false) // Cancel selected
                    }
                        .setCancelable(false)
                    .create()
                dialog.show()
            }
        }
    }

    private fun showMessageDialog(message: String) {
        handler.post {
        val appContext =  context as ECateProApp
        handler.post {
            AlertDialog.Builder(appContext.getCurrentActivity())
                .setTitle("Notice")
                .setMessage(message)
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .setCancelable(false)
                .show()
        }
    }}
}

