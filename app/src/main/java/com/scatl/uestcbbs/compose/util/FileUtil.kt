package com.scatl.uestcbbs.compose.util

import android.webkit.MimeTypeMap
import com.scatl.uestcbbs.compose.App
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.util.Locale

/**
 * created by sca_tl at 2023/3/2 21:14
 */
object FileUtil {

    @JvmStatic
    fun formatFileSize(size: Long): String {
        val df = DecimalFormat("#0.00")
        return when(size) {
            in Long.MIN_VALUE .. 1024 -> {
                df.format(size.toDouble()) + "B"
            }
            in 1024 .. 1024 * 1024 -> {
                df.format(size.toDouble() / 1024) + "KB"
            }
            in 1024 * 1024 .. 1024 * 1024 * 1024 -> {
                df.format(size.toDouble() / (1024 * 1024).toDouble()) + "MB"
            }
            else -> {
                ""
            }
        }
    }

    @JvmStatic
    fun getMimeType(name: String?): String {
        if (name == null) {
            return "*/*"
        }
        val extension = MimeTypeMap.getFileExtensionFromUrl(name)
        return if (extension != null) {
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.lowercase(Locale.getDefault())) ?: "*/*"
        } else {
            "*/*"
        }
    }

    @JvmStatic
    fun getDirectorySize(directory: File?): Long {
        var size: Long = 0
        try {
            directory?.listFiles()?.let {
                for (i in it.indices) {
                    size = if (it[i].isDirectory) {
                        size + getDirectorySize(it[i])
                    } else {
                        size + it[i].length()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return size
    }

    @JvmStatic
    fun deleteDir(dir: File?, deleteSelf: Boolean): Boolean {
        if (dir == null || !dir.exists() || !dir.isDirectory) {
            return false
        }

        try {
            for (file in dir.listFiles()!!) {
                if (file.isFile) {
                    file.delete()
                } else if (file.isDirectory) {
                    deleteDir(file, false)
                }
            }
            if (deleteSelf) {
                dir.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

        return true
    }

    fun saveStringToFile(dir: File, fileName: String, content: String): Boolean {
        return try {
            val file = File(dir, fileName)
            FileOutputStream(file).use { fos ->
                fos.write(content.toByteArray())
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}