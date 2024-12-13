package com.scatl.uestcbbs.compose.widget.image.picker

import android.net.Uri
import android.os.Parcelable
import androidx.compose.runtime.MutableState
import kotlinx.parcelize.Parcelize

/**
 * Created by sca_tl on 2022/7/22 16:16
 */
@Parcelize
data class AlbumEntity(
    var albumName: String? = "",

    /**
     * 相册绝对路径
     */
    var albumPath: String? = "",

    var coverImage: Uri? = null,
    var allMedia: MutableList<MediaEntity> = mutableListOf(),

    var selectedMedia: MutableList<MediaEntity> = mutableListOf(),
): Parcelable