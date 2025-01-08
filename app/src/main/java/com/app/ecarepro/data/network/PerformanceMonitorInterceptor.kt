package com.app.ecarepro.data.network

import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.HttpMetric
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import javax.inject.Inject

class PerformanceMonitorInterceptor @Inject constructor() : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()

        // Start Firebase Performance Monitoring HttpMetric
        val httpMetric: HttpMetric = FirebasePerformance.getInstance().newHttpMetric(
            request.url.toString(),
            when (request.method) {
                "GET" -> FirebasePerformance.HttpMethod.GET
                "POST" -> FirebasePerformance.HttpMethod.POST
                "PUT" -> FirebasePerformance.HttpMethod.PUT
                "DELETE" -> FirebasePerformance.HttpMethod.DELETE
                else -> FirebasePerformance.HttpMethod.OPTIONS
            }
        )
        httpMetric.start()

        val response: Response = try {
            chain.proceed(request)
        } catch (e: Exception) {
            httpMetric.stop()
            throw e
        }

        // Log metrics if response code is 200 and errorCode = 1
        if (response.isSuccessful) {
            val responseBodyString = response.body?.string() ?: ""

            try {
                val jsonResponse = JSONObject(responseBodyString)
                val errorCode = jsonResponse.optInt("errorCode", 0)

                if (errorCode == 1) {
                    trackWithFirebase(httpMetric, request, response)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // Rebuild the response since the body is consumed
            return response.newBuilder()
                .body(responseBodyString.toResponseBody(response.body?.contentType()))
                .build()
        }

        httpMetric.stop()
        return response
    }

    private fun trackWithFirebase(httpMetric: HttpMetric, request: Request, response: Response) {
        // Set HTTP response code
        httpMetric.setHttpResponseCode(response.code)

        // Set request and response payload sizes (if available)
        val requestBodySize = request.body?.contentLength() ?: 0L
        val responseBodySize = response.body?.contentLength() ?: 0L

        httpMetric.setRequestPayloadSize(requestBodySize)
        httpMetric.setResponsePayloadSize(responseBodySize)

        // Add custom attributes if needed
        httpMetric.putAttribute("errorCode", "1")
        httpMetric.putAttribute("trackedRequest", "true")

        // Stop the metric
        httpMetric.stop()

        println("Tracked request with Firebase Performance Monitoring: ${request.url}")
    }
}

// Extension function to convert String to ResponseBody
fun String.toResponseBody(contentType: okhttp3.MediaType?): okhttp3.ResponseBody {
    return okhttp3.ResponseBody.create(contentType, this)
}
