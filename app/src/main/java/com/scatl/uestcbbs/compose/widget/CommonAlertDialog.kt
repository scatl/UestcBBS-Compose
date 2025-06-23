package com.scatl.uestcbbs.compose.widget

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.ext.clickable

/**
 * Created by sca_tl at 2024/9/5 10:18:14
 */
@Composable
fun CommonAlertDialog(
    icon: ImageVector? = Icons.Outlined.WarningAmber,
    showDialog: Boolean,
    cancelable: Boolean = true,
    title: String,
    text: String,
    confirmText: String = stringResource(id = R.string.confirm),
    cancelText: String? = "取消",
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit,
    onCancelClick: (() -> Unit)? = null
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onDismissRequest.invoke() },
            icon = {
                icon?.let {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(35.dp)
                    )
                }
            },
            title = {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = text,
                )
            },
            confirmButton = {
                Text(
                    text = confirmText,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clickable(
                            unbound = true
                        ) {
                            onConfirmClick.invoke()
                        }
                )
            },
            dismissButton = {
                cancelText?.let {
                    Text(
                        text = cancelText,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clickable(
                                unbound = true
                            ) {
                                if (onCancelClick == null) {
                                    onDismissRequest.invoke()
                                } else {
                                    onCancelClick.invoke()
                                }
                            }
                            .padding(end = 30.dp)
                    )
                }
            }
        )
    }
}