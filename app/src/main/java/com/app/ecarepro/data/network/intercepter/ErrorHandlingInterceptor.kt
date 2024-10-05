package com.app.ecarepro.data.network.intercepter

import android.content.Context
import android.widget.Toast
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import javax.inject.Inject

class ErrorHandlingInterceptor @Inject constructor(
    @ApplicationContext private val mcontext: Context,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())


        try {
            val responseBody = response.body.string()
            val jsonObject = JSONObject(responseBody)

            val errorCode = jsonObject.getInt("errorCode")

            when (errorCode) {

                1 ->  {
                    (mcontext as? android.app.Application)?.mainExecutor?.execute {
                        Toast.makeText(mcontext, "Something went wrong", Toast.LENGTH_SHORT).show()
                    }
                }

                2 ->  {
                    (mcontext as? android.app.Application)?.mainExecutor?.execute {
                        Toast.makeText(mcontext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                }

            }

        } catch (e: Exception) {

            throw IOException("Error parsing response: ${e.message}")
        }

        return chain.proceed(chain.request())
    }
}

