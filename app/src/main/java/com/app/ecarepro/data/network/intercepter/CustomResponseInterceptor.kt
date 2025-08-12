package com.app.ecarepro.data.network.intercepter



import android.app.AlertDialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.app.ecarepro.ECateProApp
import com.app.ecarepro.R
import dagger.hilt.android.qualifiers.ApplicationContext
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
    /*condition  on global search for student and  staff tab searching  */
    private val ignoreItems = mutableListOf("Report/StudentList", "Report/StaffList")

    override fun intercept(chain: Interceptor.Chain): Response {

        var response = chain.proceed(chain.request())
        response = handleResponse(chain, response)
        return response
    }

    private fun handleResponse(chain: Interceptor.Chain, response: Response): Response {
        val responseBody = response.body
        val responseBodyString = responseBody .string() ?: ""
        if (isUrlIgnored(response.request.url.toUri().toString()))
            return response.newBuilder()
                .body(ResponseBody.create(responseBody.contentType(), responseBodyString)).build()
        try {

             val jsonObject = JSONObject(responseBodyString)


            val errorCode = jsonObject.optInt("errorCode", -1)
            val isDefaulter = jsonObject.optBoolean("isDefaulter", false)
            val message = jsonObject.optString("message", "An error occurred")

            when (errorCode) {
                0 -> {

                    return response.newBuilder()
                        .body(ResponseBody.create(responseBody.contentType(), responseBodyString))
                        .build()
                }
                1 -> {
                    showAlertErrorCodeDialog()

                   /* runBlocking {
                        if (!dialogShown) {
                            if (showRetryDialog()) {
                                response.close() // Close current response before retrying
                                  handleResponse(chain, chain.proceed(chain.request())) // Recursive retry
                            }
                        }
                    }*/
                }
                2 -> showMessageDialog(message,isDefaulter)
            }
        } catch (e: Exception) {
                    Log.d("CustomResponseInterceptor", "Error parsing JSON: ${e.message}")
        }

        return response.newBuilder()
            .body(ResponseBody.create(responseBody.contentType(), responseBodyString))
            .build()
    }
    private fun isUrlIgnored(url: String): Boolean =
        ignoreItems.any { url.contains(it, ignoreCase = true) }
    private suspend fun showRetryDialog(): Boolean {
        return suspendCoroutine { continuation ->
            dialogShown=true
            handler.post {
                val appContext =  context as ECateProApp
                val dialog = AlertDialog.Builder(appContext.getCurrentActivity())
                    .setTitle(context.getString(R.string.request_failed))
                    .setMessage(context.getString(R.string.an_error_occurred_would_you_like_to_retry))
                    .setPositiveButton(context.getString(R.string.retry)) { _, _ ->
                         dialogShown=false
                        continuation.resume(true) // Retry selected
                    }
//                        .setNegativeButton("Cancel") { _, _ ->
//                        dialogShown=false
//                        continuation.resume(false) // Cancel selected
//                    }
                        .setCancelable(false)
                    .create()
                dialog.show()
            }
        }
    }
    private fun showAlertErrorCodeDialog() {
        handler.post {
            val appContext =  context as ECateProApp
            handler.post {
                AlertDialog.Builder(appContext.getCurrentActivity())
                    .setTitle(context.getString(R.string.request_failed))
                    .setMessage(context.getString(R.string.something_went_wrong_please_try_again_later))
                    .setPositiveButton(context.getString(R.string.ok)) { dialog, _ -> dialog.dismiss()
                    }
                    .setCancelable(true)
                    .show()
            }
        }
    }
    private fun showMessageDialog(message: String, isDefaulter: Boolean) {
        if (isDefaulter){
            val appContext =  context as ECateProApp
            appContext.callMainActivityFunction()
        }else{
            handler.post {
                val appContext =  context as ECateProApp
                handler.post {
                    AlertDialog.Builder(appContext.getCurrentActivity())
                        .setTitle(context.getString(R.string.notice))
                        .setMessage(message)
                        .setPositiveButton(context.getString(R.string.ok)) { dialog, _ -> dialog.dismiss() }
                        .setCancelable(false)
                        .show()
                }
            }
        }

    }
}

