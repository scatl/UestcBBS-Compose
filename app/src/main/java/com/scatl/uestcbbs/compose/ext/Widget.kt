package com.scatl.uestcbbs.compose.ext

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavHostController

/**
 * Created by sca_tl at 2024/5/13 10:12:58
 */
@Composable
fun <K> LoadInitialDataIfNeeded(
    key: K?,
    loadFunction: () -> Unit
) {
    val initialized = rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(key) {
        if (!initialized.value) {
            loadFunction()
            initialized.value = true
        }
    }
}

fun <T : Any> NavHostController.navigateAndClean(route: T) {
    navigate(route) {
        popUpTo(0) {
            inclusive = true
        }
        launchSingleTop = true
        restoreState = true
    }
}