package com.scatl.uestcbbs.compose.net.interceptor

import com.scatl.uestcbbs.compose.db.AppDataBase
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * Created by sca_tl at 2023/7/21 14:01
 */
class AuthorizationInterceptor @Inject constructor(
    val dataBase: AppDataBase
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val newBuilder = chain.request().newBuilder()

        newBuilder.addHeader("X-Uestc-Bbs", "1")

        //重要：添加账号时需要考虑已经登录的情况，这种情况不能带上已登录账号的Cookies，否则登录后返回的Cookies缺了数据
        if (!chain.request().url.toString().contains("auth/signin")) {
            newBuilder.addHeader("Authorization", dataBase.getAccountDao().getSignedInAccount()?.token.toString())
            newBuilder.addHeader("Cookie", getCookies())
        }

        return chain.proceed(newBuilder.build())
    }

    private fun getCookies(): String {
        dataBase.getAccountDao().getSignedInAccount()?.cookies?.let {
            val stringBuilder = StringBuilder()
            for (c in it) {
                stringBuilder.append(c).append(";")
            }
            return stringBuilder.toString()
        }
        return ""
    }

}