package com.scatl.uestcbbs.compose.widget.image.picker

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore

object MediaStoreUtil {

    @JvmStatic
    fun queryImages(context: Context, config: MediaPickerConfig): GalleryEntity {
        val images = arrayListOf<MediaEntity>()
        val albums = arrayListOf<AlbumEntity>()

        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.RELATIVE_PATH,
            MediaStore.Files.FileColumns.DATE_MODIFIED,
            MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME,
            MediaStore.Files.FileColumns.WIDTH,
            MediaStore.Files.FileColumns.HEIGHT,
            MediaStore.Files.FileColumns.MIME_TYPE
        )

        val sortOrder = "${MediaStore.Files.FileColumns.DATE_MODIFIED} DESC"
        var selection: String? = null
        var selectionArgs: Array<String>? = null

        //在这里补全
        val allMimeTypes = mutableListOf<String>().apply {
            addAll(config.allowImgMimeType)
            addAll(config.allowVideoMimeType)
        }
        
        if (allMimeTypes.isNotEmpty()) {
            val selectionBuilder = StringBuilder()
            val selectionArgsList = mutableListOf<String>()
            
            allMimeTypes.forEachIndexed { index, mimeType ->
                if (index > 0) {
                    selectionBuilder.append(" OR ")
                }
                if (mimeType.endsWith("/*")) {
                    selectionBuilder.append("${MediaStore.Files.FileColumns.MIME_TYPE} LIKE ?")
                    selectionArgsList.add("${mimeType.dropLast(1)}%")
                } else {
                    selectionBuilder.append("${MediaStore.Files.FileColumns.MIME_TYPE} = ?")
                    selectionArgsList.add(mimeType)
                }
            }
            selection = selectionBuilder.toString()
            selectionArgs = selectionArgsList.toTypedArray()
        }

        val albumName = mutableSetOf<String>()

        context.contentResolver.query(
            MediaStore.Files.getContentUri("external"),
            projection, selection, selectionArgs, sortOrder)?.use { cursor ->

            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID))
                val absolutePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA))
                val relativePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.RELATIVE_PATH))
                val dateModified = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED))
                val displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME))
                val uri = ContentUris.withAppendedId(MediaStore.Files.getContentUri("external"), id)
                var bucketName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME))
                val width = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.WIDTH))
                val height = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.HEIGHT))
                val mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE))

                if (bucketName.isNullOrEmpty()) {
                    bucketName = "根目录"
                }

                val mediaEntity = MediaEntity(
                    id = id,
                    absolutePath = absolutePath,
                    relativePath = relativePath,
                    name = displayName,
                    modifyDate = dateModified,
                    uri = uri,
                    albumName = bucketName,
                    mimeType = mimeType,
                    width = width,
                    height = height
                )

                when(mimeType) {
                    "image/gif" -> mediaEntity.isGif = true
                    "image/heic" -> mediaEntity.isHeic = true
                    "image/webp" -> mediaEntity.isWebp = true
                }

                if (mimeType?.startsWith("video/") == true) {
                    mediaEntity.isVideo = true
                }

                images.add(mediaEntity)
                albumName.add(mediaEntity.relativePath ?: "空路径")
            }
        }

        albums.add(
            AlbumEntity(
                albumName = "全部媒体",
                coverImage = images[0].uri,
                allMedia = images,
                selectedMedia = arrayListOf()
            )
        )

        albumName.forEach {
            val albumImages = images.filter { mediaEntity ->
                mediaEntity.relativePath == it
            }
            albums.add(
                AlbumEntity(
                    albumName = albumImages[0].albumName,
                    albumPath = it,
                    coverImage = albumImages[0].uri,
                    allMedia = albumImages as ArrayList<MediaEntity>,
                    selectedMedia = arrayListOf()
                )
            )
        }

        return GalleryEntity(albums, images)
    }

}