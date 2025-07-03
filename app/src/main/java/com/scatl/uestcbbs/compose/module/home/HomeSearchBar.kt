package com.scatl.uestcbbs.compose.module.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.ext.clickable
import com.scatl.uestcbbs.compose.ext.pagePadding
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.router.Router
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import kotlin.math.min

/**
 * Created by sca_tl at 2024/4/22 20:36:31
 */
@Composable
fun HomeSearchBar(
    backgroundAlpha: MutableFloatState,
    searchBarHeight: Dp,
    hazeState: HazeState
) {
    val navHostController = LocalNavController.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(searchBarHeight)
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = backgroundAlpha.floatValue)
                )
        )

        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .systemBarsPadding()
                .padding(top = pagePadding)
                .padding(horizontal = 20.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(40.dp)
                )
                .clip(
                    shape = RoundedCornerShape(40.dp)
                )
                .clickable(unbound = false) {
                    navHostController.navigate(Router.SearchRouterEntity)
                }
                .hazeEffect(hazeState) {
                    alpha = 1 - backgroundAlpha.floatValue
                }
                .border(
                    width = 0.5.dp,
                    color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(40.dp)
                )
                .graphicsLayer {
                    alpha = backgroundAlpha.floatValue + 0.4f
                }
                .padding(
                    horizontal = 20.dp,
                    vertical = 15.dp
                )
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                modifier = Modifier
                    .alpha(
                        alpha = 1f - 0.5f * min(1f, backgroundAlpha.floatValue)
                    )
            )
            Text(
                text = stringResource(R.string.search_text_filed_hint),
                modifier = Modifier
                    .alpha(
                        alpha = 1f - 0.5f * min(1f, backgroundAlpha.floatValue)
                    )
            )
        }
    }
}