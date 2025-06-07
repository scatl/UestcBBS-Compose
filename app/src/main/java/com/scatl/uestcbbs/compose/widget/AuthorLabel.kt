package com.scatl.uestcbbs.compose.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.scatl.uestcbbs.compose.ext.clickable
import com.scatl.uestcbbs.compose.ext.toAvatarUrl
import com.scatl.uestcbbs.compose.ext.toIntOrElse
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.router.Router

/**
 * Created by sca_tl at 2025/6/5 10:43:09
 */
@Composable
fun AuthorLabel(
    uid: Int?,
    name: String?,
    backgroundColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
) {
    val navHostController = LocalNavController.current
    Row (
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        modifier = Modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(50)
            )
            .clip(
                shape = RoundedCornerShape(50)
            )
            .clickable(unbound = false) {
                navHostController.navigate(
                    Router.UserProfileRouterEntity(
                    uid = uid.toIntOrElse(),
                    name = name.toString()
                ))
            }
            .padding(horizontal = 5.dp)
    ) {
        AsyncImage(
            model = uid.toAvatarUrl(),
            contentDescription = null,
            modifier = Modifier
                .size(15.dp)
                .clip(shape = RoundedCornerShape(50))
        )
        Text(
            text = name.toString(),
            color = MaterialTheme.colorScheme.primary,
            fontSize = 10.sp
        )
    }
}