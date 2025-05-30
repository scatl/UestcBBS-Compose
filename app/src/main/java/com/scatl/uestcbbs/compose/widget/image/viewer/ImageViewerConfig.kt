package com.scatl.uestcbbs.compose.widget.image.viewer

import androidx.core.text.htmlEncode
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import okio.ByteString.Companion.encodeUtf8
import java.net.URLEncoder
import java.nio.charset.Charset

@JsonClass(generateAdapter = true)
data class ImageViewerConfig(
    var images: MutableList<ImageViewerItem> = mutableListOf(),
    var initialIndex: Int = 0
) {
    @JsonClass(generateAdapter = true)
    data class ImageViewerItem(
        var originUrl: String?,
        var thumbUrl: String?
    )

    companion object {
        fun toJson(data: ImageViewerConfig): String {
            runCatching {
                val jsonString = Moshi
                    .Builder()
                    .build()
                    .adapter(ImageViewerConfig::class.java)
                    .toJson(data)
                return jsonString
            }
            return ""
        }

        fun fromJson(data: String): ImageViewerConfig {
            runCatching {
                val jsonAdapter = Moshi.Builder().build().adapter(ImageViewerConfig::class.java)
                return jsonAdapter.fromJson(data) ?: ImageViewerConfig()
            }
            return ImageViewerConfig()
        }
    }
}
