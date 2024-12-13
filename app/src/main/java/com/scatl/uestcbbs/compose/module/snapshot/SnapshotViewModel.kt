package com.scatl.uestcbbs.compose.module.snapshot

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scatl.uestcbbs.compose.api.entity.ThreadDetailEntity
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.module.snapshot.entity.SnapshotData
import com.scatl.uestcbbs.compose.widget.refresh.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by sca_tl at 2024/9/14 15:58:32
 */
@HiltViewModel
class SnapshotViewModel @Inject constructor(
    val snapshotRepository: SnapshotRepository
) : ViewModel() {

    private val _snapshotData = MutableStateFlow(UiState<SnapshotStateList<SnapshotData>>().init())
    val snapshotData: StateFlow<UiState<SnapshotStateList<SnapshotData>>> = _snapshotData

    suspend fun saveSnapshot(data: ThreadDetailEntity): Boolean {
        return withContext(Dispatchers.IO) {
            snapshotRepository.saveSnapshot(data)
        }
    }

    fun getAllSnapshot() {
        viewModelScope.launchSafety {
            withContext(Dispatchers.IO) {
                val result = snapshotRepository.getAllSnapshot()
                if (result.isEmpty()) {
                    _snapshotData.value.empty().apply { data = null }
                } else {
                    _snapshotData.value.success(data = result.toMutableStateList())
                }
            }
        }
    }

    suspend fun deleteAllSnapshot(): Boolean {
        return withContext(Dispatchers.IO) {
            snapshotRepository.deleteAllSnapshot()
        }
    }

}