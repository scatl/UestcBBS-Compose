package com.scatl.uestcbbs.compose.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.ext.clickable

/**
 * Created by sca_tl at 2024/9/14 14:37:25
 */
@Composable
fun CommonConfirmDialog(
    showDialog: MutableState<Boolean>,
    icon: ImageVector,
    title: String,
    dsp: String,
    confirmText: String? = stringResource(id = R.string.confirm),
    cancelText: String? = stringResource(id = R.string.cancel),
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit,
    onCancelClick: (() -> Unit)? = { showDialog.value = false }
) {
    if (showDialog.value) {
        Dialog(
            onDismissRequest = { onDismissRequest.invoke() }
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = RoundedCornerShape(15.dp)
                    )
                    .padding(vertical = 30.dp, horizontal = 20.dp)
            ) {
                IconTitle(
                    icon = icon,
                    iconSize = 24.dp,
                    text = title,
                    textStyle = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                )
                Text(
                    text = dsp,
                    fontSize = 15.sp,
                    modifier = Modifier
                        .alpha(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(30.dp),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = cancelText ?: "",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clickable(
                                unbound = true
                            ) {
                                onCancelClick?.invoke()
                            }
                    )

                    Text(
                        text = confirmText ?: "",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clickable(
                                unbound = true
                            ) {
                                onConfirmClick.invoke()
                            }
                    )
                }
            }
        }
    }
}