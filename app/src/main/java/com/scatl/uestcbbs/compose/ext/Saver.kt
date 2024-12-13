package com.scatl.uestcbbs.compose.ext

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.scatl.uestcbbs.compose.module.collection.CollectionOrder
import com.scatl.uestcbbs.compose.widget.image.picker.MediaEntity

/**
 * Created by sca_tl at 2024/9/11 15:29:53
 */
val dpSaver = Saver<Dp, Float>(
    save = { it.value },
    restore = { restoredValue -> restoredValue.dp }
)

val collectionOrderSaver = Saver<CollectionOrder, String>(
    save = { it.order },
    restore = { restoredValue -> CollectionOrder.entries.find { it.order == restoredValue } }
)

@Composable
fun <T: Any> rememberMutableStateListOf(elements: List<T> = emptyList()): SnapshotStateList<T> {
    return rememberSaveable(saver = snapshotStateListSaver()) {
        elements.toList().toMutableStateList()
    }
}

private fun <T : Any> snapshotStateListSaver() = listSaver<SnapshotStateList<T>, T>(
    save = { stateList -> stateList.toList() },
    restore = { it.toMutableStateList() },
)