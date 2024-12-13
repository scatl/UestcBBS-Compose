package com.scatl.uestcbbs.compose.net.interceptor

import com.scatl.uestcbbs.compose.net.User
import com.scatl.uestcbbs.compose.net.interceptor.ResponseInterceptor.Result
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import okhttp3.Interceptor
import okhttp3.Response
import java.nio.charset.Charset

/**
 * Created by sca_tl at 2024/7/25 17:00:43
 */
class MessageInterceptor: Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        val source = response.body.source()
        source.request(Long.MAX_VALUE)
        val buffer = source.buffer
        val string = buffer.clone().readString(Charset.defaultCharset())

        val jsonAdapter = Moshi.Builder().build().adapter(Result::class.java)

        val result = try {
            jsonAdapter.fromJson(string)
        } catch (e: Exception) {
            e.printStackTrace()
            Result()
        }



        return response
    }

    @JsonClass(generateAdapter = true)
    data class Result(
        @Json(name = "user")
        val user: User? = User(),
    )

}