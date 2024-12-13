package com.scatl.uestcbbs.compose.module.forum.item

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.scatl.uestcbbs.compose.ext.pagePadding
import com.scatl.uestcbbs.compose.ext.unboundClickable
import com.scatl.uestcbbs.compose.R

/**
 * Created by sca_tl at 2024/7/25 10:40:52
 */
@Composable
fun ForumThreadsTitleItem (
    onSortSelected: (sort: String) -> Unit
) {
    val arrowRotated = remember { mutableStateOf(false) }
    val currentTitle = remember { mutableStateOf("") }
    val rotationDegree by animateFloatAsState(
        targetValue = if (arrowRotated.value) 180f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "arrow_rotation"
    )

    //1: 最新回复  2: 最新发表  3: 最多回复  4: 最热主题
    val titles = remember { mutableStateListOf<Pair<String, String>>() }
    if (titles.isEmpty()) {
        titles.add(Pair("1", stringResource(id = R.string.new_reply_title)))
        titles.add(Pair("2", stringResource(id = R.string.new_post_title)))
        titles.add(Pair("3", stringResource(id = R.string.most_reply_title)))
        titles.add(Pair("4", stringResource(id = R.string.hot_post_title)))
        currentTitle.value = titles[0].second
    }

    Row (
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(pagePadding)
    ) {
        Text(
            text = stringResource(id = R.string.normal_post_title),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )

        Box {
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier
                    .unboundClickable {
                        arrowRotated.value = !arrowRotated.value
                    }
            ) {
                Text(
                    text = currentTitle.value,
                    fontSize = 14.sp
                )
                Icon(
                    imageVector = Icons.Outlined.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier
                        .rotate(rotationDegree)
                )
            }

            DropdownMenu(
                expanded = arrowRotated.value,
                onDismissRequest = { arrowRotated.value = false },
            ) {
                titles.forEach {
                    DropdownMenuItem(
                        text = { Text(text = it.second) },
                        onClick = {
                            arrowRotated.value = false
                            currentTitle.value = it.second
                            onSortSelected(it.first)
                        }
                    )
                }
            }
        }
    }
}