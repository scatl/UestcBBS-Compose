package com.scatl.uestcbbs.compose.net

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by sca_tl at 2023/7/20 15:20
 */
@JsonClass(generateAdapter = true)
data class BaseApiResult<T>(

    @Json(name = "code")
    val code: Int = 0,

    @Json(name = "message")
    val message: String? = "",

    @Json(name = "user")
    val user: User? = User(),

    @Json(name = "data")
    override val data: T?

) : APIResult<T, BaseApiResult.Failure<T>> {

    data class Failure<T>(val code: Int, val message: String?, val data: T?) : APIResult.Failure<T>

    override val isSuccess: Boolean get() = code == 0

    override val failure: Failure<T>? get() = if (isFailure) Failure(code, message, data) else null

}

@JsonClass(generateAdapter = true)
data class User(
    @Json(name = "new_grouppm_legacy")
    val newGrouppmLegacy: Boolean? = false,
    @Json(name = "new_notification")
    val newNotification: Int? = 0,
    @Json(name = "new_pm")
    val newPm: Int? = 0,
    @Json(name = "new_pm_legacy")
    val newPmLegacy: Boolean? = false,
    @Json(name = "uid")
    val uid: Int? = 0,
    @Json(name = "username")
    val username: String? = ""
)
