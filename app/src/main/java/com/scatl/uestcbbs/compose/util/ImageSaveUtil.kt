package com.scatl.uestcbbs.compose.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.provider.MediaStore
import coil.annotation.ExperimentalCoilApi
import coil.imageLoader
import com.elvishew.xlog.XLog
import com.scatl.uestcbbs.compose.BuildConfig
import com.scatl.uestcbbs.compose.ext.isGTESdk29
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

object ImageSaveUtil {

    const val TAG = "ImageSaveUtil"

    @OptIn(ExperimentalCoilApi::class)
    @JvmStatic
    fun saveImages(
        context: Context,
        urls: List<String>,
        callback: (successCount: Int) -> Unit = { }
    ) {
        val okHttpClient = OkHttpClient()
        CoroutineScope(Dispatchers.IO).launch {
            var successCount = 0
            val jobs = urls.map { url ->
                async {
                    try {
                        val cachedSnapshot = context.imageLoader.diskCache?.openSnapshot(url)
                        if (cachedSnapshot != null) {
                            XLog.tag(TAG).d("cachedSnapshot != null")
                            val inputStream = cachedSnapshot.data.toFile().inputStream()
                            if (saveToAlbum(inputStream, context)) {
                                cachedSnapshot.close()
                                successCount ++
                            } else {
                                XLog.tag(TAG).d("save fail")
                            }
                        } else {
                            XLog.tag(TAG).d("cachedSnapshot == null, download")
                            val request = Request.Builder().url(url).build()
                            val response = okHttpClient.newCall(request).execute()
                            if (response.isSuccessful) {
                                response.body.byteStream().use { inputStream ->
                                    if (saveToAlbum(inputStream, context)) {
                                        successCount ++
                                    } else {
                                        XLog.tag(TAG).d("download: saveToAlbum fail")
                                    }
                                }
                            } else {
                                XLog.tag(TAG).d("download: response fail")
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            jobs.awaitAll()

            withContext(Dispatchers.Main) {
                callback(successCount)
            }
        }
    }

    @JvmStatic
    fun saveToAlbum(bitmap: Bitmap, context: Context): Boolean {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val inputStream = ByteArrayInputStream(byteArrayOutputStream.toByteArray())
        return saveToAlbum(inputStream, context)
    }

    @JvmStatic
    fun saveToAlbum(inputStream: InputStream, context: Context): Boolean {
        val byteArrayOutputStream = ByteArrayOutputStream()
        inputStream.use { input ->
            byteArrayOutputStream.use { output ->
                copyStream(input, output)
            }
        }
        val cachedInputStream1 = ByteArrayInputStream(byteArrayOutputStream.toByteArray())
        val cachedInputStream2 = ByteArrayInputStream(byteArrayOutputStream.toByteArray())

        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        cachedInputStream1.use {
            BitmapFactory.decodeStream(cachedInputStream1, null, options)
        }
        val mimeType = if (options.outMimeType?.startsWith("image/") == true) {
            options.outMimeType
        } else {
            "image/jpeg"
        }

        val fileName = UUID.randomUUID().toString()

        return if (isGTESdk29()) {
            saveToAlbumAboveAndroid10(context, cachedInputStream2, fileName, mimeType)
        } else {
            saveToAlbumBelowAndroid10(context, cachedInputStream2, fileName, mimeType)
        }
    }

    @JvmStatic
    private fun saveToAlbumAboveAndroid10(
        context: Context,
        inputStream: InputStream,
        fileName: String,
        mimeType: String
    ): Boolean {
        val resolver = context.contentResolver
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.DESCRIPTION, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, mimeType)
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/${BuildConfig.APPLICATION_ID}/")
        }

        val insertUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        if (insertUri == null) {
            XLog.tag(TAG).d("insertUri == null, values:${values}}")
            return false
        }
        val outputStream = resolver.openOutputStream(insertUri)
        if (outputStream == null) {
            XLog.tag(TAG).d("outputStream == null")
            return false
        }

        try {
            inputStream.use { input ->
                outputStream.use { output ->
                    copyStream(input, output)
                }
            }

            val imageValues = ContentValues().apply {
                put(MediaStore.Images.Media.IS_PENDING, 0)
            }
            resolver.update(insertUri, imageValues, null, null)
        } catch (e: Exception) {
            XLog.tag(TAG).d(e)
            e.printStackTrace()
            return false
        }
        return true
    }

    @JvmStatic
    private fun saveToAlbumBelowAndroid10(
        context: Context,
        inputStream: InputStream,
        fileName: String,
        mimeType: String
    ): Boolean {
        try {
            val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), BuildConfig.APPLICATION_ID)

            if (!directory.exists()) {
                directory.mkdirs()
            }

            val file = File(directory, fileName)
            FileOutputStream(file).use { outputStream ->
                copyStream(inputStream, outputStream)
            }

            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DATA, file.absolutePath)
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            }
            context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    @JvmStatic
    private fun copyStream(inputStream: InputStream, outputStream: OutputStream) {
        val buffer = ByteArray(1024 * 4)
        var len: Int
        while (inputStream.read(buffer).also { len = it } != -1) {
            outputStream.write(buffer, 0, len)
        }
        outputStream.flush()
    }
}