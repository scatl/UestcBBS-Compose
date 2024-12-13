package com.scatl.uestcbbs.compose.widget

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.scatl.uestcbbs.compose.widget.refresh.LottieResConfig
import com.scatl.uestcbbs.compose.widget.refresh.EmptyContent
import com.scatl.uestcbbs.compose.widget.refresh.LoadingContent
import com.scatl.uestcbbs.compose.widget.refresh.RetryType
import com.scatl.uestcbbs.compose.widget.refresh.UiState

/**
 * Created by sca_tl at 2024/5/22 19:35:22
 */
@Composable
@SuppressLint("ModifierParameter")
fun<T> StatusLayout(
    uiState: UiState<T>,
    loadingModifier: Modifier = Modifier.fillMaxSize(),
    emptyModifier: Modifier = Modifier.fillMaxSize(),
    onRetry: (retryType: RetryType) -> Unit = {},
    lottieResConfig: LottieResConfig = LottieResConfig(),
    content: @Composable () -> Unit,
) {
    val data = uiState.data
    val initState = uiState.initState
    val error = uiState.isError
    val errorData = uiState.errorData

    if (data == null) {
        if (initState && !error) {
            LoadingContent(
                modifier = loadingModifier,
                lottieResConfig = lottieResConfig
            )
        } else {
            EmptyContent(
                modifier = emptyModifier,
                error = error,
                errorData = errorData,
                lottieResConfig = lottieResConfig
            ) {
                onRetry(RetryType.Init)
            }
        }
    } else {
        content()
    }
}