package com.scatl.uestcbbs.compose.module.forum.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scatl.uestcbbs.compose.api.entity.ForumDetailEntity
import com.scatl.uestcbbs.compose.api.entity.ForumDetailEntity.ThreadType
import com.scatl.uestcbbs.compose.ext.pagePadding
import com.scatl.uestcbbs.compose.R

/**
 * Created by sca_tl at 2024/7/25 9:13:33
 */
@Composable
fun ForumThreadsFilterItem(
    data: ForumDetailEntity,
    selectedTypeId: Int?,
    onTypeSelected: (typeId: Int?) -> Unit
) {
    if (data.threadTypes.isNullOrEmpty()) {
        return
    }

    val titleAll = stringResource(id = R.string.all)

    val types = remember(data.fid) {
        mutableStateListOf<ThreadType>().apply {
            add(ThreadType(
                name = titleAll,
                typeId = null
            ))
            addAll(data.threadTypes)
        }
    }

    val haptic = LocalHapticFeedback.current

    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surfaceContainer)
            .padding(start = pagePadding)
    ) {
        Text(
            text = "${stringResource(id = R.string.category)}：",
            fontSize = 14.sp
        )

        LazyRow (
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .padding(horizontal = pagePadding)
        ) {
            itemsIndexed(types) { index, item ->
                FilterChip(
                    label = {
                        Text(
                            text = item.name.toString(),
                            fontSize = 12.sp
                        )
                    },
                    shape = RoundedCornerShape(15.dp),
                    border = null,
                    colors = FilterChipDefaults.filterChipColors().copy(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    selected = selectedTypeId == item.typeId,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onTypeSelected(item.typeId)
                    }
                )
            }
        }
    }

}