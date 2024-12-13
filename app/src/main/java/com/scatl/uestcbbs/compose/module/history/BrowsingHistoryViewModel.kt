package com.scatl.uestcbbs.compose.module.history

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scatl.uestcbbs.compose.db.entity.BrowsingHistoryDBEntity
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.module.home.HomeRepository
import com.scatl.uestcbbs.compose.module.home.newpost.entity.NewThreadData
import com.scatl.uestcbbs.compose.widget.refresh.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by sca_tl at 2024/9/10 20:03:38
 */
@HiltViewModel
class BrowsingHistoryViewModel @Inject constructor(
    private val browsingHistoryRepository: BrowsingHistoryRepository
) : ViewModel() {

    private val _browsingHistoryData = MutableStateFlow(UiState<SnapshotStateList<BrowsingHistoryDBEntity>>().init())
    val browsingHistoryData: StateFlow<UiState<SnapshotStateList<BrowsingHistoryDBEntity>>> = _browsingHistoryData

    fun getBrowsingHistory(start: Long?, end: Long?) {
        viewModelScope.launchSafety {
            withContext(Dispatchers.IO) {
                val data = browsingHistoryRepository.getBrowsingHistory(start, end)
                if (data.isNotEmpty()) {
                    _browsingHistoryData.value.success(
                        data = data
                    )
                } else {
                    _browsingHistoryData.value.empty().apply {
                        this.data = null
                    }
                }
            }
        }
    }

    fun deleteAll(start: Long?, end: Long?) {
        viewModelScope.launchSafety {
            _browsingHistoryData.value = withContext(Dispatchers.IO) {
                browsingHistoryRepository.deleteAll(start, end)
                _browsingHistoryData.value.empty().apply {
                    this.data = null
                }
            }
        }
    }
}