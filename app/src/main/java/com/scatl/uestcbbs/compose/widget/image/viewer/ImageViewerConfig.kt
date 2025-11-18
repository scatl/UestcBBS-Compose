package com.scatl.uestcbbs.compose.widget.image.viewer

import com.squareup.moshi.JsonClass
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

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
        private val configMap = ConcurrentHashMap<String, ImageViewerConfig>()

        fun saveConfig(config: ImageViewerConfig): String? {
            return runCatching {
                val configId = UUID.randomUUID().toString()
                configMap[configId] = config
                return configId
            }.getOrElse {
                null
            }
        }

        fun getConfig(configId: String): ImageViewerConfig? {
            return runCatching {
                val config = configMap.get(configId)
                return config
            }.getOrElse {
                null
            }
        }
    }
}
