package com.scatl.uestcbbs.compose.module.video

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import com.scatl.uestcbbs.compose.theme.DarkTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by sca_tl at 2025/11/13 10:53:47
 */
@AndroidEntryPoint
class VideoPlayerActivity: ComponentActivity() {

    companion object {
        fun open(context: Context, videoUrl: String, videoName: String?) {
            val intent = Intent(context, VideoPlayerActivity::class.java).apply {
                putExtra("videoUrl", videoUrl)
                putExtra("videoName", videoName)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { _, insets -> insets }
        val videoUrl = intent.getStringExtra("videoUrl").toString()
        val videoName = intent.getStringExtra("videoName")
        setContent {
            DarkTheme {
                VideoPlayerScreen(videoUrl, videoName)
            }
        }
    }

}