package com.scatl.uestcbbs.compose.module.post

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.AddLink
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.Attachment
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.FormatBold
import androidx.compose.material.icons.outlined.FormatItalic
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material.icons.outlined.FormatStrikethrough
import androidx.compose.material.icons.outlined.FormatUnderlined
import androidx.compose.material.icons.outlined.Fullscreen
import androidx.compose.material.icons.outlined.FullscreenExit
import androidx.compose.material.icons.outlined.InsertEmoticon
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material.icons.outlined.Poll
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohamedrejeb.richeditor.model.RichTextState
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.px2dp
import com.scatl.uestcbbs.compose.manager.KeyboardManager
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.router.Router
import com.scatl.uestcbbs.compose.widget.EmotionPanel
import com.scatl.uestcbbs.compose.widget.image.picker.MediaPickerConfig
import kotlinx.coroutines.delay

/**
 * Created by sca_tl at 2025/7/1 15:37:36
 */
@Composable
fun CreatePostBottomBar(
    modifier: Modifier = Modifier,
    showBottomPanel: MutableState<Boolean>,
    showEmotionPanel: MutableState<Boolean>,
    showFullScreen:Boolean = false,
    showVote: Boolean = false,
    hasVote: Boolean = false,
    hasReplyCredit: Boolean = false,
    isAnonymous: Boolean = false,
    isFullScreen: MutableState<Boolean> = mutableStateOf(false),
    showReplyCredit: Boolean = false,
    showAnonymous: Boolean = false,
    state: RichTextState,
    onVoteClick: (() -> Unit)? = null,
    onReplyCreditClick: (() -> Unit)? = null,
    onAnonymousClick: (() -> Unit)? = null,
    onFullScreenClick: (() -> Unit)? = null,
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    val navHostController = LocalNavController.current

    val keyboardHeight by KeyboardManager.keyboardHeight.collectAsState()
    val keyboardVisibility by KeyboardManager.keyboardVisibility.collectAsState()
    val navHeight = WindowInsets.navigationBars.getBottom(LocalDensity.current).px2dp

    suspend fun hideKeyBoard() {
        if (keyboardVisibility) {
            keyboardController?.hide()
            delay(300)
        }
    }

    LaunchedEffect(keyboardVisibility) {
        if (keyboardVisibility.not()) {
            if (showEmotionPanel.value.not()) {
                showBottomPanel.value = false
            }
        }
    }

    Column(
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            RichTextStyleButton(
                onClick = {
                    showBottomPanel.value = true
                    showEmotionPanel.value = !showEmotionPanel.value
                    if (showEmotionPanel.value) {
                        keyboardController?.hide()
                    } else {
                        keyboardController?.show()
                    }
                },
                isSelected = showEmotionPanel.value,
                icon = Icons.Outlined.InsertEmoticon
            )

            RichTextStyleButton(
                onClick = {

                },
                isSelected = false,
                icon = Icons.Outlined.Attachment
            )

            RichTextStyleButton(
                onClick = {
                    scope.launchSafety {
                        hideKeyBoard()
                        val config = MediaPickerConfig.toJson(MediaPickerConfig())
                        navHostController.navigate(Router.MediaPickerRouterEntity(config))
                    }
                },
                isSelected = false,
                icon = Icons.Outlined.Photo
            )

            if (showVote) {
                RichTextStyleButton(
                    onClick = {
                        scope.launchSafety {
                            hideKeyBoard()
                            onVoteClick?.invoke()
                        }
                    },
                    isSelected = hasVote,
                    icon = Icons.Outlined.Poll
                )
            }

            AnimatedVisibility(
                visible = showReplyCredit
            ) {
                RichTextStyleButton(
                    onClick = {
                        scope.launchSafety {
                            hideKeyBoard()
                            onReplyCreditClick?.invoke()
                        }
                    },
                    isSelected = hasReplyCredit,
                    icon = Icons.Outlined.WaterDrop
                )
            }

            AnimatedVisibility(
                visible = showAnonymous
            ) {
                RichTextStyleButton(
                    onClick = {
                        onAnonymousClick?.invoke()
                    },
                    isSelected = isAnonymous,
                    icon = painterResource(id = R.drawable.ic_comedy_mask)
                )
            }

            AnimatedVisibility(
                visible = showFullScreen
            ) {
                RichTextStyleButton(
                    onClick = {
                        onFullScreenClick?.invoke()
                    },
                    isSelected = false,
                    icon = if (isFullScreen.value) Icons.Outlined.FullscreenExit else Icons.Outlined.Fullscreen
                )
            }

            RichTextStyleButton(
                onClick = {
                    showBottomPanel.value = false
                    showEmotionPanel.value = false
                    keyboardController?.hide()

//                        state.addTextAfterSelection("@@@@")
//                        state.addSpanStyle(SpanStyle(color = Color.Red), TextRange(0, 4))
                },
                isSelected = false,
                icon = Icons.Outlined.AlternateEmail
            )

            Box(
                Modifier
                    .height(20.dp)
                    .width(1.dp)
                    .background(
                        color = MaterialTheme.colorScheme.outline
                    )
            )

            RichTextStyleButton(
                onClick = {
                    state.toggleSpanStyle(H1SpanStyle)
                },
                isSelected = state.currentSpanStyle.fontSize == H1SpanStyle.fontSize
                        && state.currentSpanStyle.fontWeight == FontWeight.Bold,
                icon = painterResource(R.drawable.ic_format_h1)
            )

            RichTextStyleButton(
                onClick = {
                    state.toggleSpanStyle(H2SpanStyle)
                },
                isSelected = state.currentSpanStyle.fontSize == H2SpanStyle.fontSize
                        && state.currentSpanStyle.fontWeight == FontWeight.Bold,
                icon = painterResource(R.drawable.ic_format_h2)
            )

            RichTextStyleButton(
                onClick = {
                    state.toggleSpanStyle(H3SpanStyle)
                },
                isSelected = state.currentSpanStyle.fontSize == H3SpanStyle.fontSize
                        && state.currentSpanStyle.fontWeight == FontWeight.Bold,
                icon = painterResource(R.drawable.ic_format_h3)
            )

            RichTextStyleButton(
                onClick = {
                    state.toggleSpanStyle(H4SpanStyle)
                },
                isSelected = state.currentSpanStyle.fontSize == H4SpanStyle.fontSize
                        && state.currentSpanStyle.fontWeight == FontWeight.Bold,
                icon = painterResource(R.drawable.ic_format_h4)
            )

            RichTextStyleButton(
                onClick = {
                    state.toggleSpanStyle(H5SpanStyle)
                },
                isSelected = state.currentSpanStyle.fontSize == H5SpanStyle.fontSize
                        && state.currentSpanStyle.fontWeight == FontWeight.Bold,
                icon = painterResource(R.drawable.ic_format_h5)
            )

            RichTextStyleButton(
                onClick = {
                    state.toggleSpanStyle(H6SpanStyle)
                },
                isSelected = state.currentSpanStyle.fontSize == H6SpanStyle.fontSize
                        && state.currentSpanStyle.fontWeight == FontWeight.Bold,
                icon = painterResource(R.drawable.ic_format_h6)
            )

            RichTextStyleButton(
                onClick = {
                    state.toggleSpanStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                isSelected = state.currentSpanStyle.fontWeight == FontWeight.Bold,
                icon = Icons.Outlined.FormatBold
            )

            RichTextStyleButton(
                onClick = {
                    state.toggleSpanStyle(
                        SpanStyle(
                            fontStyle = FontStyle.Italic
                        )
                    )
                },
                isSelected = state.currentSpanStyle.fontStyle == FontStyle.Italic,
                icon = Icons.Outlined.FormatItalic
            )

            RichTextStyleButton(
                onClick = {
                    state.toggleSpanStyle(
                        SpanStyle(
                            textDecoration = TextDecoration.Underline
                        )
                    )
                },
                isSelected = state.currentSpanStyle.textDecoration?.contains(TextDecoration.Underline) == true,
                icon = Icons.Outlined.FormatUnderlined
            )

            RichTextStyleButton(
                onClick = {
                    state.toggleSpanStyle(
                        SpanStyle(
                            textDecoration = TextDecoration.LineThrough
                        )
                    )
                },
                isSelected = state.currentSpanStyle.textDecoration?.contains(TextDecoration.LineThrough) == true,
                icon = Icons.Outlined.FormatStrikethrough
            )

            RichTextStyleButton(
                onClick = {

                },
                isSelected = state.isLink,
                icon = Icons.Outlined.AddLink
            )

            RichTextStyleButton(
                onClick = {
                    state.toggleUnorderedList()
                },
                isSelected = state.isUnorderedList,
                icon = Icons.AutoMirrored.Outlined.FormatListBulleted,
            )

            RichTextStyleButton(
                onClick = {
                    state.toggleOrderedList()
                },
                isSelected = state.isOrderedList,
                icon = Icons.Outlined.FormatListNumbered,
            )

            RichTextStyleButton(
                onClick = {
                    state.toggleCodeSpan()
                },
                isSelected = state.isCodeSpan,
                icon = Icons.Outlined.Code,
            )
        }

        AnimatedVisibility(
            visible = showBottomPanel.value,
        ) {
            Box(
                modifier = Modifier
                    .height(keyboardHeight.dp - navHeight)
            )

            AnimatedVisibility(
                visible = showEmotionPanel.value
            ) {
                EmotionPanel(
                    modifier = Modifier
                        .height(keyboardHeight.dp - navHeight)
                        .fillMaxWidth()
                ) {
                    state.addTextAfterSelection("![${it.id}](s)")
                }
            }
        }
    }

}

@Composable
private fun RichTextStyleButton(
    onClick: () -> Unit,
    icon: Any,
    tint: Color? = null,
    isSelected: Boolean = false,
) {
    IconButton(
        modifier = Modifier
            // Workaround to prevent the rich editor
            // from losing focus when clicking on the button
            // (Happens only on Desktop)
            .focusProperties { canFocus = false },
        onClick = onClick,
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = if (isSelected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onBackground
            },
        ),
    ) {
        if (icon is ImageVector) {
            Icon(
                imageVector = icon,
                contentDescription = icon.name,
                tint = tint ?: LocalContentColor.current,
                modifier = Modifier
                    .background(
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            Color.Transparent
                        },
                        shape = CircleShape
                    )
                    .padding(2.dp)
            )
        } else if (icon is Painter) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = tint ?: LocalContentColor.current,
                modifier = Modifier
                    .background(
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            Color.Transparent
                        },
                        shape = CircleShape
                    )
                    .padding(2.dp)
            )
        }
    }
}

internal val H1SpanStyle = SpanStyle(fontSize = 34.sp, fontWeight = FontWeight.Bold)
internal val H2SpanStyle = SpanStyle(fontSize = 30.sp, fontWeight = FontWeight.Bold)
internal val H3SpanStyle = SpanStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold)
internal val H4SpanStyle = SpanStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
internal val H5SpanStyle = SpanStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
internal val H6SpanStyle = SpanStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold)