package com.scatl.uestcbbs.compose.module.main

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.ViewCompat
import androidx.lifecycle.lifecycleScope
import com.scatl.uestcbbs.compose.datastore.DataStore
import com.scatl.uestcbbs.compose.db.AppDataBase
import com.scatl.uestcbbs.compose.ext.dp2px
import com.scatl.uestcbbs.compose.ext.isNotNullAndEmpty
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.sp2px
import com.scatl.uestcbbs.compose.manager.AccountManager
import com.scatl.uestcbbs.compose.manager.KeyboardManager
import com.scatl.uestcbbs.compose.manager.LanguageManager
import com.scatl.uestcbbs.compose.module.dayquestion.DayQuestionService
import com.scatl.uestcbbs.compose.router.NavGraph
import com.scatl.uestcbbs.compose.theme.AppTheme
import com.scatl.uestcbbs.compose.widget.WatermarkDrawable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var dataBase: AppDataBase

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { _, insets -> insets }
        initInterfaceLanguage()
        startService()
        requestPermission()
        setContent {
            AppTheme {
                TheftProofWaterMarkBackground {
                    Box {
                        NavGraph(
                            dataBase = dataBase
                        )
                        DetectKeyboardChange()
                    }
                }
            }
        }
    }

    private fun initInterfaceLanguage() {
        val value = LanguageManager
            .InterfaceLanguage
            .entries
            .find {
                it.value == DataStore.interfaceLanguage
            }
        LanguageManager.updateInterfaceLanguage(value, this)
    }

    private fun startService() {
        lifecycleScope.launchSafety {
            delay(3000)
            if (dataBase.getAccountDao().getSignedInAccount() != null
                && dataBase.getDayQuestionDao().getAll().isNotNullAndEmpty()) {
                startService(Intent(this@MainActivity, DayQuestionService::class.java))
            }
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 0)
        }
    }
}

@Composable
private fun TheftProofWaterMarkBackground(
    content: @Composable () -> Unit
) {
    val signedInAccount by AccountManager.signedInAccount.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        content()

        val waterMark = signedInAccount?.uid?.ifEmpty { signedInAccount?.name } ?: ""

        if (waterMark.isNotEmpty()) {
            val drawable = WatermarkDrawable().apply {
                mText = waterMark
                mTextColor = 0x01ff0000.toInt()
                mTextSize = 100.sp2px
                mRotation = 0f
            }
            val configuration = LocalConfiguration.current
            val screenWidth = configuration.screenWidthDp.dp.dp2px.toInt()
            val screenHeight = configuration.screenHeightDp.dp.dp2px.toInt()
            Image(
                bitmap = drawable.toBitmap(screenWidth, screenHeight).asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
private fun DetectKeyboardChange() {
    val density = LocalDensity.current
    val view = LocalView.current

    DisposableEffect(view) {
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            val rect = android.graphics.Rect()
            view.getWindowVisibleDisplayFrame(rect)
            val keyboardHeight = view.height - rect.bottom

            if (keyboardHeight > view.height * 0.15) {
                KeyboardManager.toggleKeyboardHeight(with(density) { keyboardHeight.toDp().value })
                KeyboardManager.toggleKeyboardVisibleChange(true)
            } else {
                KeyboardManager.toggleKeyboardVisibleChange(false)
            }
        }

        view.viewTreeObserver.addOnGlobalLayoutListener(listener)

        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(listener)
        }
    }
}