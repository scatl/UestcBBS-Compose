package com.scatl.uestcbbs.compose.module.snapshot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.scatl.uestcbbs.compose.ext.clickable
import com.scatl.uestcbbs.compose.ext.commonCardBg
import com.scatl.uestcbbs.compose.ext.pagePadding
import com.scatl.uestcbbs.compose.ext.toIntOrElse
import com.scatl.uestcbbs.compose.module.snapshot.entity.SnapshotData
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.router.Router
import com.scatl.uestcbbs.compose.util.formatTimestampYMDHMS
import java.io.File

/**
 * Created by sca_tl at 2024/9/14 16:49:57
 */
@Composable
fun SnapshotItem(
    item: SnapshotData
) {
    val navHostController = LocalNavController.current

    Column (
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier
            .commonCardBg { }
    ) {
        Text(
            text = "TID: ${item.tid}",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clickable(unbound = false) {
                    navHostController.navigate(
                        Router.ThreadDetailRouterEntity(
                            id = item.tid.toIntOrElse()
                        )
                    )
                }
        )
        Text(
            text = "主题: ${item.subject}",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clickable(unbound = false) {
                    navHostController.navigate(
                        Router.ThreadDetailRouterEntity(
                            id = item.tid.toIntOrElse()
                        )
                    )
                }
        )

        Column (
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(pagePadding)
        ) {
            Text(
                text = "共${item.snapshots.size}份快照",
                modifier = Modifier
                    .padding(bottom = 5.dp)
            )
            item.snapshots.forEachIndexed { index, s ->
                Row {
                    Text(
                        text = "快照${index + 1}: ${formatTimestampYMDHMS(File(s).name.toLongOrNull(), LocalContext.current)}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(unbound = false) {
                                navHostController.navigate(
                                    Router.ThreadDetailRouterEntity(
                                        id = item.tid.toIntOrElse(),
                                        snapshot = s
                                    )
                                )
                            }
                            .alpha(alpha = 0.5f),
                        fontSize = 15.sp
                    )
                }
            }
        }

    }
}