package com.scatl.uestcbbs.compose.module.auth

import com.scatl.uestcbbs.compose.api.entity.request.LoginRequestEntity
import com.scatl.uestcbbs.compose.base.BaseRepository
import javax.inject.Inject

/**
 * Created by sca_tl at 2024/4/23 9:30:10
 */
class AuthRepository @Inject constructor(): BaseRepository() {

    suspend fun signIn(
        hCaptcha: String,
        requestBody: LoginRequestEntity
    ) = authService.signIn(hCaptcha, requestBody)

}