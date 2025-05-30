package com.scatl.uestcbbs.compose.widget.image.viewer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.ext.showToast
import com.scatl.uestcbbs.compose.util.ImageSaveUtil

/**
 * Created by sca_tl at 2025/5/21 10:56:11
 */
class ImageSaveService: Service() {

    companion object {
        const val TAG = "ImageSaveService"
        const val CHANNEL_ID = 10001
    }

    private val notificationManager by lazy {
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {
            createNotificationChannel(
                NotificationChannel(CHANNEL_ID.toString(), "图片下载服务通知", NotificationManager.IMPORTANCE_DEFAULT)
            )
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val urls = intent?.getStringArrayExtra("urls")

        "图片后台保存中，前往通知栏查看进度".showToast(this)

        if (urls != null) {
            ImageSaveUtil.saveImages(this, urls.asList()) {
                sendNotification(it)
            }
        } else {
            stopSelf()
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?) = null

    private fun sendNotification(result: ImageSaveUtil.ImageSaveResult) {
        val title: String
        val indeterminate: Boolean
        val content: String
        if (result.status == ImageSaveUtil.ImageSaveStatus.PENDING) {
            title = "正在保存图片"
            content = "共${result.totalCount}张图片，已成功保存${result.successCount}张"
            indeterminate = true
        } else {
            if (result.status == ImageSaveUtil.ImageSaveStatus.ALL_SUCCESS) {
                title = "图片全部保存成功"
                content = "${result.successCount}张图片全部保存成功"
            } else {
                title = "图片部分保存成功"
                content = "保存成功${result.successCount}张，保存失败${result.failCount}张"
            }
            indeterminate = false
        }

        val builder = NotificationCompat
            .Builder(this, CHANNEL_ID.toString())
            .setGroupSummary(true)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(false)
            .setContentTitle(title)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setProgress(result.totalCount, result.successCount, indeterminate)
        notificationManager.notify(CHANNEL_ID, builder.build())
    }
}