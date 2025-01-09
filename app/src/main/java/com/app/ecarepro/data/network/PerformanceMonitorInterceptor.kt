package com.app.ecarepro.data.network

import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.HttpMetric
import com.google.firebase.perf.trace
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


        val response: Response = try {
            chain.proceed(request)
        } catch (e: Exception) {
            throw e
        }

        // Log metrics if response code is 200 and errorCode = 1
        if (response.isSuccessful) {
            val responseBodyString = response.body?.string() ?: ""

            try {
                val jsonResponse = JSONObject(responseBodyString)
                val errorCode = jsonResponse.optInt("errorCode", 0)

                if (errorCode == 1) {
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
                    trackWithFirebase(
                        httpMetric,
                        request,
                        response,
                        responseBodyString.toByteArray().size
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // Rebuild the response since the body is consumed
            return response.newBuilder()
                .body(responseBodyString.toResponseBody(response.body?.contentType()))
                .build()
        }
        return response
    }

    private fun trackWithFirebase(
        httpMetric: HttpMetric,
        request: Request,
        response: Response,
        size: Int
    ) {
        httpMetric.trace {
            setHttpResponseCode(response.code)
            val requestBodySize = request.body?.contentLength() ?: 0L

            setRequestPayloadSize(requestBodySize)
            setResponsePayloadSize(size.toLong())

            // Add custom attributes if needed
            putAttribute("errorCode", "1")
            putAttribute("trackedRequest", "true")


            println("Tracked request with Firebase Performance Monitoring: ${request.url}")
        }
    }
}

// Extension function to convert String to ResponseBody
fun String.toResponseBody(contentType: okhttp3.MediaType?): okhttp3.ResponseBody {
    return okhttp3.ResponseBody.create(contentType, this)
}
