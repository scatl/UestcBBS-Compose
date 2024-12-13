package com.scatl.uestcbbs.compose.widget.image.picker

import android.net.Uri
import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.io.Serializable

/**
 * Created by sca_tl on 2022/7/20 19:43
 */
@Parcelize
@JsonClass(generateAdapter = true)
data class MediaEntity (
    var id: Long? = -1,
    var absolutePath: String? = "",
    var relativePath: String? = "",
    var name: String? = "",
    var modifyDate: Long? = -1,
    var uri: Uri? = null,
    var albumName: String? = "",
    var mimeType: String? = "",
    var width: Int? = -1,
    var height: Int? = -1,
    var isGif: Boolean = false,
    var isHeic: Boolean = false,
    var isWebp: Boolean = false,
    var isVideo: Boolean = false
): Serializable, Parcelable {

    override fun equals(other: Any?): Boolean {
        if (this === other) { return true }
        if (other !is MediaEntity) { return false }
        if (absolutePath != null) { return absolutePath == other.absolutePath }
        return uri == other.uri
    }

    override fun hashCode(): Int {
        var result = absolutePath?.hashCode() ?: 0
        if (result == 0) {
            result = uri?.hashCode() ?: 0
        }
        return result
    }
}