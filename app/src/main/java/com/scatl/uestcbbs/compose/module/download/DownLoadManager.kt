package com.scatl.uestcbbs.compose.module.download

import android.content.Context
import android.net.Uri
import android.provider.ContactsContract.Data
import androidx.documentfile.provider.DocumentFile
import com.scatl.uestcbbs.compose.datastore.DataStore
import java.net.URLDecoder

/**
 * Created by sca_tl at 2023/2/28 14:52
 */
object DownLoadManager {

    @JvmStatic
    fun initDownloadFolderUri(context: Context) {
        if (!isDownloadFolderUriAccessible(context)) {
            DataStore.downloadFolderUri = ""
        }
    }

    @JvmStatic
    fun isDownloadFolderUriAccessible(context: Context): Boolean {
        for (persistedUriPermission in context.contentResolver.persistedUriPermissions) {
            if (persistedUriPermission.uri.toString() == DataStore.downloadFolderUri) {
                return true
            }
        }
        return false
    }

    @JvmStatic
    fun getDownloadFolder() =
        URLDecoder
            .decode(DataStore.downloadFolderUri, "UTF-8")
            .replace("content://com.android.externalstorage.documents/tree/primary:", "/storage/emulated/0/")
            .replace("content://com.android.providers.downloads.documents/tree/raw:", "")
            .plus("/")

    @JvmStatic
    fun isFileExist(context: Context, name: String?): Boolean {
        DocumentFile
            .fromTreeUri(context, Uri.parse(DataStore.downloadFolderUri))
            ?.listFiles()
            ?.find { it.name == name }
            ?.let { return true }
            ?: return false
    }

    @JvmStatic
    fun getExistFile(context: Context, name: String?): DocumentFile? {
        DocumentFile
            .fromTreeUri(context, Uri.parse(DataStore.downloadFolderUri))
            ?.listFiles()
            ?.find { it.name == name }
            ?.let { return it }
            ?: return null
    }

}