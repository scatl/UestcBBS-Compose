package com.scatl.uestcbbs.compose.net.interceptor

import com.scatl.uestcbbs.compose.api.entity.SignInEntity
import com.scatl.uestcbbs.compose.net.BaseApiResult
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.nio.charset.Charset

/**
 * Created by sca_tl at 2023/7/20 20:27
 */
class SignInInterceptor: Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())

        if (chain.request().url.toString().contains("auth/signin")) {
            val jsonAdapter: JsonAdapter<BaseApiResult<SignInEntity>> = Moshi
                .Builder()
                .build()
                .adapter(Types.newParameterizedType(BaseApiResult::class.java, SignInEntity::class.java))

            val source = originalResponse.body.source()
            source.request(Long.MAX_VALUE)
            val buffer = source.buffer
            val string = buffer.clone().readString(Charset.defaultCharset())

            if (string.isNotEmpty()) {
                val resultEntity = jsonAdapter.fromJson(string)
                if (resultEntity?.code == 0) {
                    val cookieHeaders = originalResponse.headers("Set-Cookie")
                    resultEntity.data?.cookie = cookieHeaders
                    resultEntity.data?.uid = resultEntity.user?.uid?.toString()

                    val newBody = jsonAdapter.toJson(resultEntity).toResponseBody(originalResponse.body.contentType())

                    return originalResponse.newBuilder()
                        .body(newBody)
                        .build()
                }
            }
        }

        return originalResponse
    }

}