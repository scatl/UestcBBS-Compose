package com.scatl.uestcbbs.compose.module.forum.item

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.Help
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.scatl.uestcbbs.compose.ForumPicture
import com.scatl.uestcbbs.compose.api.entity.IndexEntity
import com.scatl.uestcbbs.compose.ext.cardCorner
import com.scatl.uestcbbs.compose.ext.pagePadding
import com.scatl.uestcbbs.compose.ext.randomBg
import com.scatl.uestcbbs.compose.ext.toIntOrElse
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.router.Router

/**
 * Created by sca_tl at 2024/7/11 16:17:26
 */
@Composable
fun ForumCategoryChild(
    data: IndexEntity.Forum
) {
    if (data.children.isNullOrEmpty()) {
        return
    }
    val navHostController = LocalNavController.current
    LazyVerticalGrid(
        columns = GridCells.Adaptive(160.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = pagePadding)
            .heightIn(min = 80.dp, max = 2000.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        state = rememberLazyGridState(),
        userScrollEnabled = false
    ) {
        itemsIndexed(data.children!!) { index, item ->
            Box(
                contentAlignment = Alignment.BottomStart,
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(cardCorner))
                    .clickable {
                        navHostController.navigate(Router.ForumDetailRouterEntity(
                            fid = item.fid.toIntOrElse()
                        ))
                    }
            ) {
                AsyncImage(
                    model = ForumPicture[item.fid],
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .aspectRatio(1.5f),
                    contentDescription = null
                )

                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                mutableListOf(Color.Black.copy(alpha = 0.4f), Color.Transparent),
                                startY = 50f,
                                endY = 0f
                            )
                        )
                        .padding(5.dp)
                ) {
                    if ((item.todayPosts?:0) > (item.yesterdayPosts?:0)) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowUpward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp).padding(bottom = 1.dp)
                        )
                    }
                    Text(
                        text = item.name.toString().plus(if (item.todayPosts == 0) "" else "(${item.todayPosts})"),
                        color = Color.White,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

//                    Icon(
//                        imageVector = Icons.Outlined.Info,
//                        contentDescription = null,
//                        tint = Color.White.copy(alpha = 0.5f),
//                        modifier = Modifier
//                            .align(Alignment.BottomEnd)
//                            .padding(5.dp)
//                            .size(15.dp)
//                    )
            }
        }
    }
}