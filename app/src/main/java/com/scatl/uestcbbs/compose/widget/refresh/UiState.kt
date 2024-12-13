package com.scatl.uestcbbs.compose.widget.refresh

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Created by sca_tl at 2024/7/29 14:12:52
 */
@Stable
class UiState<T>() {
    var data by mutableStateOf<T?>(null)
    var errorData by mutableStateOf<Throwable?>(null)
    var isRefreshing by mutableStateOf(false)
    var initState by mutableStateOf(false)
    var hasMore by mutableStateOf(false)
    var isSuccess by mutableStateOf(false)
    var isError by mutableStateOf(false)
    var isLoadingMore by mutableStateOf(false)

    fun init(data: T? = null) = this.apply {
        this.data = data
        initState = true
        isError = false
    }

    fun success(data: T? = null, hasMore: Boolean = true) = this.apply {
        this.data = data
        this.hasMore = hasMore
        isSuccess = true
        isLoadingMore = false
        isRefreshing = false
        initState = false
        isError = false
    }

    fun refreshing() = this.apply {
        isRefreshing = true
        isSuccess = false
        initState = false
        isError = false
    }

    fun loadingMore() = this.apply {
        isRefreshing = false
        initState = false
        isError = false
        hasMore = true
        isLoadingMore = true
        isSuccess = false
    }

    fun empty(errorData: Throwable? = null, initState: Boolean = false) = this.apply {
        this.initState = initState
        this.errorData = errorData
        isSuccess = false
        isLoadingMore = false
        isRefreshing = false
        isError = false
        hasMore = false
    }

    fun error(errorData: Throwable? = null, initState: Boolean = false) = this.apply {
        this.initState = initState
        this.errorData = errorData
        isSuccess = false
        isLoadingMore = false
        isRefreshing = false
        isError = true
        hasMore = true
    }
}
