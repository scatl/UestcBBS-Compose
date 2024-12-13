package com.scatl.uestcbbs.compose.module.home

import androidx.compose.foundation.background
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.scatl.uestcbbs.compose.ext.clickable
import com.scatl.uestcbbs.compose.ext.pagePadding
import com.scatl.uestcbbs.compose.router.Router
import kotlin.math.max
import kotlin.math.min
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.router.LocalNavController

/**
 * Created by sca_tl at 2024/4/22 20:36:31
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeSearchBar(
    backgroundAlpha: MutableFloatState,
    searchBarHeight: Dp
) {
    var text by rememberSaveable { mutableStateOf("") }
    val expand by rememberSaveable { mutableStateOf(false) }
    var padding by remember { mutableStateOf(pagePadding) }
    val navHostController = LocalNavController.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(searchBarHeight)
            .background(
                color = MaterialTheme.colorScheme.surface.copy(
                    alpha = if (expand) {
                        1f
                    } else {
                        backgroundAlpha.floatValue
                    }
                )
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
                .graphicsLayer {
                    alpha = if (expand) 1f else backgroundAlpha.floatValue + 0.4f
                }
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
                .padding(
                    horizontal = 20.dp,
                    vertical = 15.dp
                ),
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

//        SearchBar(
//            modifier = Modifier
//                .align(Alignment.TopCenter)
//                .padding(
//                    horizontal = animateDpAsState(
//                        targetValue = padding,
//                        label = "search_bar_padding",
//                        animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing)
//                    ).value
//                )
//                .graphicsLayer {
//                    alpha = if (expand) 1f else backgroundAlpha.floatValue + 0.5f
//                },
//            colors = SearchBarDefaults.colors(
//                containerColor = MaterialTheme.colorScheme.surfaceContainer,
//            ),
//            expanded = expand,
//            onExpandedChange = {
//                expand = it
//                padding = if (expand) 0.dp else pagePadding
//                SharedFlowBus.with(Event.CHANGE_MAIN_NAVIGATION_VISIBILITY).tryEmit(BaseEvent.MainNavVisibleEvent(!it))
//            },
//            inputField = {
//                SearchBarDefaults.InputField(
//                    query = text,
//                    onQueryChange = { text = it },
//                    onSearch = {
//
//                    },
//                    expanded = expand,
//                    onExpandedChange = {
//                        expand = it
//                        padding = if (expand) 0.dp else pagePadding
//                        SharedFlowBus.with(Event.CHANGE_MAIN_NAVIGATION_VISIBILITY).tryEmit(BaseEvent.MainNavVisibleEvent(!it))
//                    },
//                    placeholder = { Text(text = "搜索帖子或用户") },
//                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) }
//                )
//            }
//        ) {
//            repeat(4) { idx ->
//                val resultText = "Suggestion $idx"
//                ListItem(
//                    headlineContent = { Text(resultText) },
//                    supportingContent = { Text("Additional info") },
//                    leadingContent = { Icon(Icons.Filled.Star, contentDescription = null) },
//                    modifier = Modifier
//                        .clickable {
//                            text = resultText
//                            //active = false
//                        }
//                        .fillMaxWidth()
//                        .padding(vertical = 4.dp)
//                )
//            }
//        }
    }
}