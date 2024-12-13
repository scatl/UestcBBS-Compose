package com.scatl.uestcbbs.compose.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.scatl.uestcbbs.compose.util.formatTimestamp

/**
 * Created by sca_tl at 2024/7/26 15:30:13
 */
@Composable
fun CommonIconNameView(
    iconUrl: String?,
    name: String?,
    date: Int?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = { }
) {
    Row (
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        modifier = modifier
    ) {
        AsyncImage(
            model = iconUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(40.dp)
                .clip(shape = RoundedCornerShape(50))
                .clickable {
                    onClick.invoke()
                }
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = name.toString(),
                fontSize = 15.sp,
                lineHeight = 15.sp,
                modifier = Modifier
                    .clickable {
                        onClick.invoke()
                    }
            )
            Text(
                text = formatTimestamp(date, LocalContext.current),
                fontSize = 12.sp,
                lineHeight = 12.sp,
                modifier = Modifier.alpha(0.5f)
            )
        }
    }
}