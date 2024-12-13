package com.scatl.uestcbbs.compose.moshi

import android.net.Uri
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.ToJson

/**
 * Created by sca_tl at 2024/11/4 16:37:36
 */
class UriJsonAdapter {
    @FromJson
    fun fromJson(json: String): Uri? {
        return Uri.parse(json)
    }

    @ToJson
    fun toJson(uri: Uri?): String {
        return uri.toString()
    }
}