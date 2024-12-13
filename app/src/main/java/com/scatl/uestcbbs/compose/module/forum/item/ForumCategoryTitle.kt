package com.scatl.uestcbbs.compose.module.forum.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.api.entity.IndexEntity
import com.scatl.uestcbbs.compose.ext.pagePadding
import com.scatl.uestcbbs.compose.ext.randomBg
import com.scatl.uestcbbs.compose.module.forum.entity.ForumCategoryData

/**
 * Created by sca_tl at 2024/7/11 15:55:40
 */
@Composable
fun ForumCategoryTitle(data: IndexEntity.Forum) {
    Column(
        verticalArrangement = Arrangement.spacedBy(3.dp),
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.surface)
            .fillMaxWidth()
            .padding(start = pagePadding, end = pagePadding, top = pagePadding + 10.dp, bottom = pagePadding)
    ) {
        Text(
            text = data.name.toString(),
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp
        )

        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            if (!data.moderators.isNullOrEmpty()) {
                Text(
                    text = "${stringResource(R.string.category_moderator)}:",
                    fontSize = 13.sp
                )
                LazyRow {
                    items(data.moderators.size) {
                        Text(
                            text = data.moderators.getOrNull(it).toString(),
                            fontSize = 11.sp,
                            lineHeight = 12.sp,
                            modifier = Modifier
                                .background(color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(3.dp))
                                .padding(start = 5.dp, end = 5.dp, top = 2.dp, bottom = 1.dp)
                        )
                    }
                }
            }
        }

    }
}