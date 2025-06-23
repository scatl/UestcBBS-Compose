package com.scatl.uestcbbs.compose.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Poll
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Created by sca_tl at 2024/7/13 9:27
 */
@Composable
fun IconTitle(
    modifier: Modifier = Modifier,
    icon: ImageVector?,
    iconSize: Dp,
    iconTint: Color = LocalContentColor.current,
    iconPosition: IconPosition = IconPosition.LEFT,
    text: String,
    textStyle: TextStyle = TextStyle(),
    gap: Dp = 5.dp,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(gap),
        modifier = modifier
    ) {
        if (iconPosition == IconPosition.RIGHT) {
            Text(
                text = text,
                style = textStyle
            )
        }
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(iconSize)
            )
        }
        if (iconPosition == IconPosition.LEFT) {
            Text(
                text = text,
                style = textStyle
            )
        }
    }
}

enum class IconPosition {
    LEFT, RIGHT
}