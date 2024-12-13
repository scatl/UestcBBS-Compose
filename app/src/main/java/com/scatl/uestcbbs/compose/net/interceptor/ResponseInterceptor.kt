package com.scatl.uestcbbs.compose.net.interceptor

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.nio.charset.Charset

/**
 * Created by sca_tl at 2023/7/20 20:27
 */
class ResponseInterceptor: Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        val source = response.body.source()
        source.request(Long.MAX_VALUE)
        val buffer = source.buffer
        val string = buffer.clone().readString(Charset.defaultCharset())

        val originCode = response.code
        val originMsg = response.message

        if (string.isEmpty()) {
            return response
        } else {
            if (originCode != 200) {
                val jsonAdapter = Moshi.Builder().build().adapter(Result::class.java)

                val result = try {
                    jsonAdapter.fromJson(string)
                } catch (e: Exception) {
                    Result(-1, originMsg)
                }

                val body = if (result?.code != 0) {
                    jsonAdapter.toJson(result).toResponseBody(response.body.contentType())
                } else {
                    string.toResponseBody(response.body.contentType())
                }
                return response.newBuilder().body(body).code(originCode).message(result?.message ?: "").build()
            }

            return response
        }
    }

    @JsonClass(generateAdapter = true)
    data class Result(
        @Json(name = "code")
        var code: Int = Int.MAX_VALUE,

        @Json(name = "message")
        var message: String? = "",
    )

}