package com.scatl.uestcbbs.compose.manager

import com.scatl.uestcbbs.compose.datastore.DataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Created by sca_tl at 2024/10/17 11:05:08
 */
object KeyboardManager {

    private val _keyboardHeight = MutableStateFlow(DataStore.keyboardHeight)
    val keyboardHeight: StateFlow<Float> = _keyboardHeight

    private val _keyboardVisibility = MutableStateFlow(false)
    val keyboardVisibility: StateFlow<Boolean> = _keyboardVisibility

    fun toggleKeyboardHeight(height: Float) {
        _keyboardHeight.value = height
        DataStore.keyboardHeight = height
    }

    fun toggleKeyboardVisibleChange(visible: Boolean) {
        _keyboardVisibility.value = visible
    }

}