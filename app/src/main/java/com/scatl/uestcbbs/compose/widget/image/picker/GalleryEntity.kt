package com.scatl.uestcbbs.compose.widget.image.picker

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GalleryEntity(
    var albums: List<AlbumEntity> = mutableListOf(),
    var medias: List<MediaEntity> = mutableListOf()
): Parcelable
