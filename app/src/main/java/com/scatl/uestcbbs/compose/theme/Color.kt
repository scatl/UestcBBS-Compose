package com.scatl.uestcbbs.compose.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LightCustomColors = CustomColors(
    userProfileLevelBg = Color(color = 0xFFBBDEFB),
    waterDrop = Color(color = 0xFF5493EA),
    prestige = Color(color = 0xFFDE9D5D),
    unreadBadgeText = Color(color = 0xFFFFFFFF),

    meLevelCardBg = Color(color = 0xFFE8F3E8),
    meLevelCardDsp = Color(color = 0xFF2E8B57),
    meWaterCardBg = Color(color = 0xFFE0F0F6),
    meWaterCardDsp = Color(color = 0xFF0D47A1),
    meWaterTask = Color(color = 0xFF59B2D1),
    meMagicShop = Color(color = 0xFFFF9C87),
    meMedalCenter = Color(color = 0xFFC9A6D1),
    meWealth = Color(color = 0xFF0BBCB3),
    meCreditHistory = Color(color = 0xFF4BB3FF),
    meBrowserHistory = Color(color = 0xFF5B9FAB),
    meSnapshot = Color(color = 0xFFCC8B54),
    meCollection = Color(color = 0xFFB070AA),

    threadLikeProgress = Color(color = 0xFFFF9A2E),
    threadDislikeProgress = Color(color = 0xFF6AA1FF),
    threadDetailReplyAward = Color(color = 0xFFF26C4F),

    webViewContentBorder = Color(color = 0xFFDDDDDD),
    webViewCodeBg = Color(color = 0xFFF4F4F4),

    threadTitleVoteLabel = Color(color = 0xFFFA541C),
    threadTitleNewComerStart = Color(color = 0xFF59B7AC),
    threadTitleNewComerEnd = Color(color = 0xFFA0BE96),
    threadTitleDigestStart = Color(color = 0xFFC08313),
    threadTitleDigestEnd = Color(color = 0xFFA27A16),
    threadTitleGreatStart = Color(color = 0xFF2D65A1),
    threadTitleGreatEnd = Color(color = 0xFF73D58B),
    threadTitleRecommendStart = Color(color = 0xFAAD14CC),
    threadTitleRecommendEnd = Color(color = 0xF56C6C99),
    threadTitleReplyAwardStart = Color(color = 0xFFD37622),
    threadTitleReplyAwardEnd = Color(color = 0xFFD05024),

    darkRoomWarning = Color(color = 0xFFCCAF12),
    darkRoomError = Color(color = 0xFFCC4347)
)

val DarkCustomColors = CustomColors(
    userProfileLevelBg = Color(color = 0xFF0D47A1),
    waterDrop = Color(color = 0xFF285189),
    prestige = Color(color = 0xFFB77B3F),
    unreadBadgeText = Color(color = 0xFF5B0307),

    meLevelCardBg = Color(color = 0xFF284141),
    meLevelCardDsp = Color(color = 0xFF76C076),
    meWaterCardBg = Color(color = 0xFF3B5061),
    meWaterCardDsp = Color(color = 0xFF4D9DBA),
    meWaterTask = Color(color = 0xFF59B2D1),
    meMagicShop = Color(color = 0xFFF48E78),
    meMedalCenter = Color(color = 0xFFC9A6D1),
    meWealth = Color(color = 0xFF0BBCB3),
    meCreditHistory = Color(color = 0xFF4BB3FF),
    meBrowserHistory = Color(color = 0xFF5B9FAB),
    meSnapshot = Color(color = 0xFFCC8B54),
    meCollection = Color(color = 0xFFCC69D0),

    threadLikeProgress = Color(color = 0xFFD3832E),
    threadDislikeProgress = Color(color = 0xFF4876C5),
    threadDetailReplyAward = Color(color = 0xFFB55A29),

    webViewContentBorder = Color(color = 0xFF6F6F6F),
    webViewCodeBg = Color(color = 0xFF181B1C),

    threadTitleVoteLabel = Color(color = 0xFFFA541C),
    threadTitleNewComerStart = Color(color = 0xFF59B7AC),
    threadTitleNewComerEnd = Color(color = 0xFFA0BE96),
    threadTitleDigestStart = Color(color = 0xFFC08313),
    threadTitleDigestEnd = Color(color = 0xFFA27A16),
    threadTitleGreatStart = Color(color = 0xFF2D65A1),
    threadTitleGreatEnd = Color(color = 0xFF73D58B),
    threadTitleRecommendStart = Color(color = 0xFAAD14CC),
    threadTitleRecommendEnd = Color(color = 0xF56C6C99),
    threadTitleReplyAwardStart = Color(color = 0xFFD37622),
    threadTitleReplyAwardEnd = Color(color = 0xFFD05024),

    darkRoomWarning = Color(color = 0xFFCCAF12),
    darkRoomError = Color(color = 0xFFCC4347)
)

@Immutable
data class CustomColors(
    val userProfileLevelBg: Color = Color.Unspecified,
    val waterDrop: Color = Color.Unspecified,
    val prestige: Color = Color.Unspecified,
    val unreadBadgeText: Color = Color.Unspecified,

    val meLevelCardBg: Color = Color.Unspecified,
    val meLevelCardDsp: Color = Color.Unspecified,
    val meWaterCardBg: Color = Color.Unspecified,
    val meWaterCardDsp: Color = Color.Unspecified,
    val meWaterTask: Color = Color.Unspecified,
    val meMagicShop: Color = Color.Unspecified,
    val meMedalCenter: Color = Color.Unspecified,
    val meWealth: Color = Color.Unspecified,
    val meCreditHistory: Color = Color.Unspecified,
    val meCollection: Color = Color.Unspecified,
    val meBrowserHistory: Color = Color.Unspecified,
    val meSnapshot: Color = Color.Unspecified,

    val threadLikeProgress: Color = Color.Unspecified,
    val threadDislikeProgress: Color = Color.Unspecified,
    val threadDetailReplyAward: Color = Color.Unspecified,

    val webViewContentBorder: Color = Color.Unspecified,
    val webViewCodeBg: Color = Color.Unspecified,

    val threadTitleVoteLabel: Color = Color.Unspecified,
    val threadTitleNewComerStart: Color = Color.Unspecified,
    val threadTitleNewComerEnd: Color = Color.Unspecified,
    val threadTitleDigestStart: Color = Color.Unspecified,
    val threadTitleDigestEnd: Color = Color.Unspecified,
    val threadTitleGreatStart: Color = Color.Unspecified,
    val threadTitleGreatEnd: Color = Color.Unspecified,
    val threadTitleRecommendStart: Color = Color.Unspecified,
    val threadTitleRecommendEnd: Color = Color.Unspecified,
    val threadTitleReplyAwardStart: Color = Color.Unspecified,
    val threadTitleReplyAwardEnd: Color = Color.Unspecified,

    val darkRoomWarning: Color = Color.Unspecified,
    val darkRoomError: Color = Color.Unspecified
)

val LocalCustomColors = staticCompositionLocalOf { CustomColors() }