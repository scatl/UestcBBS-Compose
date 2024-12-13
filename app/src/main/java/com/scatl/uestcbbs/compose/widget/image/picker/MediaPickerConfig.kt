package com.scatl.uestcbbs.compose.widget.image.picker

import android.net.Uri
import android.os.Parcelable
import com.scatl.uestcbbs.compose.moshi.UriJsonAdapter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import kotlinx.parcelize.Parcelize

/**
 * Created by sca_tl at 2024/10/29 17:47:29
 */
@Parcelize
@JsonClass(generateAdapter = true)
data class MediaPickerConfig(
    var initMedia: MutableList<MediaEntity> = mutableListOf(),
    var maxSelect: Int = Int.MAX_VALUE
): Parcelable {
    companion object {
        fun fromJson(data: String): MediaPickerConfig {
            runCatching {
                val jsonAdapter = Moshi.Builder().build().adapter(MediaPickerConfig::class.java)
                return jsonAdapter.fromJson(data) ?: MediaPickerConfig()
            }
            return MediaPickerConfig()
        }

        fun toJson(config: MediaPickerConfig): String {
            runCatching {
                val jsonString = Moshi
                    .Builder()
                    .add(UriJsonAdapter())
                    .build()
                    .adapter(MediaPickerConfig::class.java)
                    .toJson(config)
                return jsonString
            }.onFailure {
                it.printStackTrace()
            }
            return ""
        }
    }
}