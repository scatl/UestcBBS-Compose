package com.scatl.uestcbbs.compose.util

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult

/**
 * Created by sca_tl at 2024/8/2 9:51:53
 */
suspend fun getBitmapFromUrl(context: Context, url: String?): Bitmap? {
    val loader = context.imageLoader
    val request = ImageRequest.Builder(context)
        .data(url)
        .build()

    val result = loader.execute(request)
    return (result as? SuccessResult)?.drawable?.toBitmap()
}

fun Bitmap.toArgb8888(): Bitmap {
    if (config == Bitmap.Config.ARGB_8888) {
        return this
    }
    return copy(Bitmap.Config.ARGB_8888, true)
}