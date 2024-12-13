package com.scatl.uestcbbs.compose.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.removeAllBlank
import kotlinx.coroutines.delay

/**
 * Created by sca_tl at 2024/8/30 11:18:38
 */
@Composable
fun TextInputDialog(
    initContent: String = "",
    showDialog: Boolean,
    cancelable: Boolean = true,
    title: String = "",
    label: String,
    icon: ImageVector?,
    onDismissRequest: () -> Unit,
    onConfirmClick: (inputText: String?) -> Unit
) {
    if (showDialog) {
        val focusRequester = remember { FocusRequester() }
        val scope = rememberCoroutineScope()

        val content = rememberSaveable { mutableStateOf(initContent) }

        LaunchedEffect(Unit) {
            scope.launchSafety {
                delay(100)
                focusRequester.requestFocus()
            }
        }

        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(
                dismissOnClickOutside = cancelable,
                dismissOnBackPress = cancelable
            )
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(15.dp),
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(vertical = 30.dp, horizontal = 20.dp),
            ) {
                IconTitle(
                    icon = icon,
                    iconSize = 24.dp,
                    text = title,
                    textStyle = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                OutlinedTextField(
                    value = content.value.removeAllBlank().toString(),
                    onValueChange = {
                        content.value = it
                    },
                    label = { Text(text = label) },
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .fillMaxWidth()
                )

                Box (
                    contentAlignment = Alignment.CenterEnd,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(id = R.string.confirm),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clickable {
                                onConfirmClick.invoke(content.value)
                            }
                    )
                }
            }
        }
    }
}