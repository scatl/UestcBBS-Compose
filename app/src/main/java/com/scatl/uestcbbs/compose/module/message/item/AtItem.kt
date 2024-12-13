package com.scatl.uestcbbs.compose.module.message.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.api.entity.message.MessageEntity
import com.scatl.uestcbbs.compose.ext.cardCorner
import com.scatl.uestcbbs.compose.ext.commonCardBg
import com.scatl.uestcbbs.compose.ext.pagePadding
import com.scatl.uestcbbs.compose.ext.randomBg
import com.scatl.uestcbbs.compose.ext.toAvatarUrl
import com.scatl.uestcbbs.compose.ext.toIntOrElse
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.router.Router
import com.scatl.uestcbbs.compose.widget.CommonIconNameView

/**
 * Created by sca_tl at 2024/7/26 15:01:40
 */
@Composable
fun AtItem(
    data: MessageEntity.Row
) {
    val navHostController = LocalNavController.current

    Column (
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier
            .commonCardBg {
                navHostController.navigate(
                    Router.ThreadDetailRouterEntity(
                        id = data.postId.toIntOrElse()
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
                val text = LocalContext.current.getString(R.string.message_at_dsp, data.subject)

                append(text = text)

                addLink(
                    clickable = LinkAnnotation.Clickable(
                        tag = "subject",
                        styles = TextLinkStyles(
                            style = SpanStyle(color = MaterialTheme.colorScheme.primary)
                        ),
                        linkInteractionListener = {
                            navHostController.navigate(
                                Router.ThreadDetailRouterEntity(
                                    id = data.postId.toIntOrElse()
                                )
                            )
                        }
                    ),
                    start = text.indexOf(data.subject.toString()),
                    end = text.indexOf(data.subject.toString()) + data.subject.toString().length
                )
            },
            fontSize = 14.sp
        )

        Text(
            text = data.summary.toString(),
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(cardCorner)
                )
                .padding(pagePadding)
        )
    }
}