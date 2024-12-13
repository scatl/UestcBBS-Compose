package com.scatl.uestcbbs.compose.module.message.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.api.entity.message.MessageEntity
import com.scatl.uestcbbs.compose.ext.commonCardBg
import com.scatl.uestcbbs.compose.ext.toAvatarUrl
import com.scatl.uestcbbs.compose.ext.toIntOrElse
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.router.Router
import com.scatl.uestcbbs.compose.theme.LocalCustomColors
import com.scatl.uestcbbs.compose.widget.CommonIconNameView

/**
 * Created by sca_tl at 2024/7/26 15:19:29
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RateItem(
    modifier: Modifier,
    data: MessageEntity.Row
) {
    val navHostController = LocalNavController.current

    Column (
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = modifier
            .commonCardBg {
                navHostController.navigate(
                    Router.ThreadDetailRouterEntity(
                        id = data.threadId.toIntOrElse()
                    )
                )
            }
    ) {
        CommonIconNameView(
            iconUrl = data.authorId.toAvatarUrl(),
            name = data.author.toString(),
            date = data.dateline
        ) {
            navHostController.navigate(
                Router.UserProfileRouterEntity(
                    uid = data.authorId,
                    name = data.author.toString()
                )
            )
        }

        Text(
            text = buildAnnotatedString {
                val text = LocalContext.current.getString(R.string.message_rate_dsp, data.subject, data.reason)

                append(text = text.plus("\n"))

                addLink(
                    clickable = LinkAnnotation.Clickable(
                        tag = "subject",
                        styles = TextLinkStyles(
                            style = SpanStyle(color = MaterialTheme.colorScheme.primary)
                        ),
                        linkInteractionListener = {
                            navHostController.navigate(
                                Router.ThreadDetailRouterEntity(
                                    id = data.threadId.toIntOrElse()
                                )
                            )
                        }
                    ),
                    start = text.indexOf(data.subject.toString()),
                    end = text.indexOf(data.subject.toString()) + data.subject.toString().length
                )

                if (data.credits?.water != null) {
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = LocalCustomColors.current.waterDrop
                        ),
                    ) {
                        append(stringResource(R.string.water)
                            .plus(": ")
                            .plus(if (data.credits.water < 0) "" else "+")
                            .plus(data.credits.water)
                            .plus("     ")
                        )
                    }
                }

                if (data.credits?.weiWang != null) {
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = LocalCustomColors.current.prestige
                        ),
                    ) {
                        append(stringResource(R.string.prestige)
                            .plus(": ")
                            .plus(if (data.credits.weiWang < 0) "" else "+")
                            .plus(data.credits.weiWang)
                        )
                    }
                }

            },
            fontSize = 14.sp
        )
    }
}