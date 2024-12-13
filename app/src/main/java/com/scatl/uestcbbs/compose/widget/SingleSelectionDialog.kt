package com.scatl.uestcbbs.compose.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.unboundClickable

/**
 * Created by sca_tl at 2024/7/1 16:43:47
 */
@Composable
fun <T> SingleSelectionDialog(
    data: List<Pair<String, T>>,
    title: String,
    icon: @Composable RowScope.() -> Unit = { },
    selected: T,
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    onSelect: (data: T) -> Unit
) {
    if (showDialog) {
        val configuration = LocalConfiguration.current
        val screenHeight = configuration.screenHeightDp.dp

        val listState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()

        var selectedInternal by remember { mutableStateOf(selected) }

        LaunchedEffect(Unit) {
            coroutineScope.launchSafety {
                data.indexOfFirst { it.second == selectedInternal }.let {
                    listState.scrollToItem(index = it)
                }
            }
        }

        Dialog(onDismissRequest = onDismissRequest) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .heightIn(0.dp, screenHeight * 0.6f)
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(start = 0.dp, end = 0.dp, top = 20.dp, bottom = 20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier
                        .padding(start = 20.dp, end = 20.dp)
                ) {
                    icon()
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(bottom = 5.dp)
                ) {
                    itemsIndexed(data) { index, item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clipToBounds()
                                .unboundClickable {
                                    selectedInternal = item.second
                                    onSelect(item.second)
                                }
                                .padding(start = 20.dp, end = 20.dp)
                                .height(40.dp)
                        ) {
                            RadioButton(
                                selected = item.second == selectedInternal,
                                onClick = null
                            )
                            Text(
                                text = item.first
                            )
                        }
                    }
                }
            }

        }
    }
}