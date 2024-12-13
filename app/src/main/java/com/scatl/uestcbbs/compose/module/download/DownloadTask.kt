package com.scatl.uestcbbs.compose.module.download

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import com.elvishew.xlog.XLog
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.datastore.DataStore
import com.scatl.uestcbbs.compose.ext.showToast
import com.scatl.uestcbbs.compose.util.FileUtil
import com.scatl.uestcbbs.compose.util.SSLUtil
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Created by sca_tl at 2023/5/31 15:56
 */
class DownloadTask(val context: Context) {

    companion object {
        const val TAG = "DownloadTask"

        const val CHANNEL_ID = "download_notification"
        const val GROUP_ID = 1000
        const val GROUP_KEY = "download_group"

        fun get(context: Context): DownloadTask {
            return DownloadTask(context)
        }
    }

    private val notificationManager by lazy {
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {
            createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    ContextCompat.getString(context, R.string.download_notification_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }
    }

    private val time by lazy { System.currentTimeMillis() }
    private val notifyId by lazy { System.currentTimeMillis().toInt() }

    private var mUrl: String = ""
    private var mFileName: String = ""

    fun setUrl(url: String?) = apply {
        mUrl = url?:""
    }

    fun setFileName(name: String?) = apply {
        mFileName = name?:"downloadFile"
    }

    private val progressListener = object : ProgressListener {
        override fun update(bytesRead: Long, contentLength: Long, done: Boolean) {
            val progress = (bytesRead * 100 / contentLength).toInt()

            XLog.tag(TAG).d("${bytesRead}, ${contentLength}, ${done}")

            sendNotification(
                content = "$progress%, ${(FileUtil.formatFileSize(contentLength))}",
                progress = progress
            )
        }
    }

    fun start() {
        if (mUrl.isEmpty()) {
            ContextCompat.getString(context, R.string.download_url_empty).showToast(context)
            return
        }

        ContextCompat.getString(context, R.string.download_start_background).showToast(context)
        sendNotification(ContextCompat.getString(context, R.string.download_prepare),0, true)

        val clientBuilder = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .addNetworkInterceptor { chain ->
                val originalResponse = chain.proceed(chain.request())
                originalResponse
                    .newBuilder()
                    .body(ProgressResponseBody(originalResponse.body, progressListener))
                    .build()
            }

        if (DataStore.ignoreSSL) {
            clientBuilder
                .sslSocketFactory(SSLUtil.getSSLSocketFactory(), SSLUtil.getTrustManager())
                .hostnameVerifier(SSLUtil.getHostNameVerifier())
        }

        val request: Request = Request.Builder().get().url(mUrl).build()
        clientBuilder
            .build()
            .newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    sendNotification(
                        content = ContextCompat.getContextForLanguage(context).getString(R.string.download_fail, e.message),
                        progress = 0,
                        indeterminate = false,
                    )
                }

                override fun onResponse(call: Call, response: Response) {
                    XLog.tag(TAG).d("onResponse")

                    val inputStream = response.body.byteStream()

                    val outputStream = if (DownLoadManager.isDownloadFolderUriAccessible(context)) {
                        DownLoadManager.getExistFile(context, mFileName)?.delete()
                        val file = DocumentFile
                            .fromTreeUri(context, Uri.parse(DataStore.downloadFolderUri))
                            ?.createFile(FileUtil.getMimeType(mFileName), mFileName)
                        file?.uri?.let { context.contentResolver.openOutputStream(it) }
                    } else {
                        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), mFileName)
                        FileOutputStream(file)
                    }

                    outputStream?.use {
                        inputStream.use { input ->
                            input.copyTo(it, 10 * 1024)
                        }
                    }
                }
            })
    }

    private fun sendNotification(content: String?,
                                 progress: Int,
                                 indeterminate: Boolean = false) {

        var pendingIntent: PendingIntent? = null

        if (progress >= 100) {
            Handler(Looper.getMainLooper()).post {
                ContextCompat.getContextForLanguage(context).getString(R.string.download_success, mFileName)
            }

            pendingIntent = PendingIntent.getActivity(context, 0, getViewIntent(),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        val notification = NotificationCompat
            .Builder(context, CHANNEL_ID)
            .setWhen(time)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(false)
            .setContentTitle(
                if (progress >= 100) {
                    ContextCompat.getContextForLanguage(context).getString(R.string.download_success, mFileName)
                } else {
                    ContextCompat.getContextForLanguage(context).getString(R.string.download_pending, mFileName)
                }
            )
            .setGroup(GROUP_KEY)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setProgress(100, progress, indeterminate)
            .build()
        notificationManager.notify(notifyId, notification)
        notificationManager.notify(GROUP_ID, buildGroup(time))
    }

    private fun buildGroup(time: Long) =
        NotificationCompat
            .Builder(context, CHANNEL_ID)
            .setContentTitle(ContextCompat.getString(context, R.string.download_notification_title))
            .setWhen(time)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setShowWhen(true)
            .setGroup(GROUP_KEY)
            .setGroupSummary(true)
            .build()

    private fun getViewIntent(): Intent {
        return if (DownLoadManager.isDownloadFolderUriAccessible(context)) {
            //DocumentFile分享不需要FileProvider
            val documentFile = DownLoadManager.getExistFile(context, mFileName)
            Intent(Intent.ACTION_VIEW).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                setDataAndType(documentFile?.uri, documentFile?.type)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        } else {
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), mFileName)
            val intentUri = FileProvider.getUriForFile(context, "com.scatl.uestcbbs.compose.BBSFileProvider", file)

            Intent(Intent.ACTION_VIEW).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                setDataAndType(intentUri, FileUtil.getMimeType(file.name))
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        }
    }

}