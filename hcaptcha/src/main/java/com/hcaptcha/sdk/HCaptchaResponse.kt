package com.hcaptcha.sdk

sealed class HCaptchaResponse {
    data class Success(val token: String) : HCaptchaResponse()
    data class Failure(val error: HCaptchaError) : HCaptchaResponse()
    data object Open : HCaptchaResponse()
    data object Loaded: HCaptchaResponse()
}
