package com.scatl.uestcbbs.compose.api.service

import com.scatl.uestcbbs.compose.api.entity.SignInEntity
import com.scatl.uestcbbs.compose.api.entity.request.LoginRequestEntity
import com.scatl.uestcbbs.compose.net.BaseApiResult
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Created by sca_tl at 2024/7/9 15:34:35
 */
interface AuthService {
    @POST("auth/signin")
    @Headers("X-Uestc-Bbs-Captcha-Type: hcaptcha")
    suspend fun signIn(
        @Header("X-Uestc-Bbs-Captcha") hCaptcha: String,
        @Body requestBody: LoginRequestEntity
    ): BaseApiResult<SignInEntity>
}