package com.scatl.uestcbbs.compose.ext

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * Created by sca_tl at 2025/6/10 9:51:42
 */
fun Map<String, String>.toRequestBodyMap(
    mediaType: MediaType? = "multipart/form-data".toMediaTypeOrNull()
): MutableMap<String, RequestBody> {
    val map = mutableMapOf<String, RequestBody>()
    this.forEach { (t, u) ->
        map[t] = u.toRequestBody(mediaType)
    }
    return map
}