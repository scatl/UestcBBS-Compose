package com.scatl.uestcbbs.compose.module.message.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.scatl.uestcbbs.compose.api.entity.message.MessageEntity
import com.scatl.uestcbbs.compose.ext.commonCardBg
import com.scatl.uestcbbs.compose.ext.toAvatarUrl
import com.scatl.uestcbbs.compose.ext.toIntOrElse
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.router.Router
import com.scatl.uestcbbs.compose.router.linkNavigate
import com.scatl.uestcbbs.compose.widget.CommonIconNameView

/**
 * Created by sca_tl at 2024/7/30 17:01:08
 */
@Composable
fun FriendItem(
    modifier: Modifier,
    data: MessageEntity.Row
) {
    val navHostController = LocalNavController.current
    val uriHandler = LocalUriHandler.current

    Column (
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = modifier
            .commonCardBg()
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
            text = AnnotatedString.Companion.fromHtml(
                htmlString = data.htmlMessage.toString(),
                linkInteractionListener = {
                    linkNavigate(
                        uriHandler = uriHandler,
                        url = (it as? LinkAnnotation.Url?)?.url,
                        navHostController = navHostController
                    )
                }
            ),
            fontSize = 14.sp
        )
    }
}