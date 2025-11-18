package com.scatl.uestcbbs.compose.widget.image.picker

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

object MediaPickerStateManager {
    
    private var _galleryEntity: MutableState<GalleryEntity>? = null
    private var _currentAlbum: MutableState<AlbumEntity>? = null

    fun getOrCreateGalleryEntity(): MutableState<GalleryEntity> {
        if (_galleryEntity == null) {
            _galleryEntity = mutableStateOf(GalleryEntity())
        }
        return _galleryEntity!!
    }

    fun getOrCreateCurrentAlbum(): MutableState<AlbumEntity> {
        if (_currentAlbum == null) {
            _currentAlbum = mutableStateOf(AlbumEntity())
        }
        return _currentAlbum!!
    }
}

