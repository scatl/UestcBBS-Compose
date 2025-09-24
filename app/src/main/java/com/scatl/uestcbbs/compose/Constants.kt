package com.scatl.uestcbbs.compose

/**
 * Created by sca_tl at 2024/7/9 15:08:26
 */
object Constants {
    const val BBS_URL = "https://bbs.uestc.edu.cn"
    const val BBS_API_BASE_URL = "${BBS_URL}/star/api/v1/"
    const val BING_BASE_URL = "https://cn.bing.com"
    const val BBS_LOGO = "https://bbs.uestc.edu.cn/assets/qshp-logo-D_kvMs8m.png"
    const val HCAPTCHA_SITE_KEY = "52100d97-0777-4497-8852-e380d5b3430b"
    const val DAY_MILLIS = 24 * 60 * 60 * 1000
    val INTERNAL_FIDS = intArrayOf(174, 395, 263, 267, 378)

    const val APP_AUTHOR_BBS_UID = 217992
    const val APP_AUTHOR_BBS_NAME = "sca_tl"
    const val APP_OPEN_SOURCE_GITHUB_URL = "https://github.com/scatl/UestcBBS-Compose"
    const val APP_AUTHOR_EMAIL = "sca_tl@foxmail.com"
}

object ForumPicture {
    private val urlMapping: Map<Int, String> = mapOf(
        174 to "https://bbs.uestc.edu.cn/assets/%E5%B0%B1%E4%B8%9A%E5%88%9B%E4%B8%9A-CA8pQW91.jpg",
        219 to "https://bbs.uestc.edu.cn/assets/%E5%87%BA%E5%9B%BD%E7%95%99%E5%AD%A6-CRsXK5HY.jpg",
        430 to "https://bbs.uestc.edu.cn/assets/%E5%85%AC%E8%80%83%E9%80%89%E8%B0%83-BW-KZ-YB.jpg",
        199 to "https://bbs.uestc.edu.cn/assets/%E4%BF%9D%E7%A0%94%E8%80%83%E7%A0%94-Cj65eRW5.jpg",

        403 to "https://bbs.uestc.edu.cn/assets/%E9%83%A8%E9%97%A8%E7%9B%B4%E9%80%9A%E8%BD%A6-BRdhlpX5.jpg",
        25  to "https://bbs.uestc.edu.cn/assets/%E6%B0%B4%E6%89%8B%E4%B9%8B%E5%AE%B6-CCli9Pfu.jpg",
        236 to "https://bbs.uestc.edu.cn/assets/%E6%A0%A1%E5%9B%AD%E7%83%AD%E7%82%B9-BDgUeKah.jpg",
        313 to "https://bbs.uestc.edu.cn/assets/%E9%B9%8A%E6%A1%A5-C2KWNufZ.jpg",
        45  to "https://bbs.uestc.edu.cn/assets/%E6%83%85%E6%84%9F%E4%B8%93%E5%8C%BA-DAAfi_2U.jpg",
        370 to "https://bbs.uestc.edu.cn/assets/%E5%90%83%E5%96%9D%E7%8E%A9%E4%B9%90-AGugGH8R.jpg",
        309 to "https://bbs.uestc.edu.cn/assets/%E6%88%90%E7%94%B5%E9%94%90%E8%AF%84-r5gt-qZM.jpg",
        225 to "https://bbs.uestc.edu.cn/assets/%E4%BA%A4%E9%80%9A%E5%87%BA%E8%A1%8C-B-unEUfc.jpg",
        17  to "https://bbs.uestc.edu.cn/assets/%E5%90%8C%E5%9F%8E%E5%90%8C%E4%B9%A1-D6hmIo_B.jpg",
        237 to "https://bbs.uestc.edu.cn/assets/%E6%AF%95%E4%B8%9A%E6%84%9F%E8%A8%80-CJF1udI8.jpg",
        326 to "https://bbs.uestc.edu.cn/assets/%E6%96%B0%E7%94%9F%E4%B8%93%E5%8C%BA-BlA8Cd4H.jpg",

        20  to "https://bbs.uestc.edu.cn/assets/%E5%AD%A6%E6%9C%AF%E4%BA%A4%E6%B5%81-Bp9Atsry.jpg",
        382 to "https://bbs.uestc.edu.cn/assets/%E8%80%83%E8%AF%95%E4%B8%93%E5%8C%BA-DxWlHIDO.jpg",
        70  to "https://bbs.uestc.edu.cn/assets/%E7%A8%8B%E5%BA%8F%E5%91%98%E4%B9%8B%E5%AE%B6-lAloCBUO.jpg",
        316 to "https://bbs.uestc.edu.cn/assets/%E8%87%AA%E7%84%B6%E7%A7%91%E5%AD%A6-JLUrFhw1.jpg",
        66  to "https://bbs.uestc.edu.cn/assets/%E7%94%B5%E5%AD%90%E6%95%B0%E7%A0%81-5Bp51oP_.jpg",
        121 to "https://bbs.uestc.edu.cn/assets/IC%E7%94%B5%E8%AE%BE-B_DYD0Jx.jpg",

        61  to "https://bbs.uestc.edu.cn/assets/%E4%BA%8C%E6%89%8B%E4%B8%93%E5%8C%BA-Cu4O_qR9.jpg",
        255 to "https://bbs.uestc.edu.cn/assets/%E6%88%BF%E5%B1%8B%E7%A7%9F%E8%B5%81-DZ-FpLBa.jpg",
        111 to "https://bbs.uestc.edu.cn/assets/%E5%BA%97%E9%93%BA%E4%B8%93%E5%8C%BA-BnOAhO_0.jpg",
        305 to "https://bbs.uestc.edu.cn/assets/%E5%A4%B1%E7%89%A9%E6%8B%9B%E9%A2%86-Cv7wIRLq.jpg",
        391 to "https://bbs.uestc.edu.cn/assets/%E6%8B%BC%E8%BD%A6%E5%90%8C%E8%A1%8C-CrXcWNla.jpg",
        183 to "https://bbs.uestc.edu.cn/assets/%E5%85%BC%E8%81%8C%E4%BF%A1%E6%81%AF%E5%8F%91%E5%B8%83%E6%A0%8F-qqQcj1nt.jpg",

        312 to "https://bbs.uestc.edu.cn/assets/%E8%B7%91%E6%AD%A5%E5%AE%B6%E5%9B%AD-DcfkRv_V.jpg",
        244 to "https://bbs.uestc.edu.cn/assets/%E6%88%90%E7%94%B5%E9%AA%91%E8%BF%B9-D9T7fZO_.jpg",
        115 to "https://bbs.uestc.edu.cn/assets/%E5%86%9B%E4%BA%8B%E5%9B%BD%E9%98%B2-B76OJ5FX.jpg",
        55  to "https://bbs.uestc.edu.cn/assets/%E8%A7%86%E8%A7%89%E8%89%BA%E6%9C%AF-D2KMYJAm.jpg",
        149 to "https://bbs.uestc.edu.cn/assets/%E5%BD%B1%E8%A7%86%E5%A4%A9%E5%9C%B0-CeDZm0eG.jpg",
        74  to "https://bbs.uestc.edu.cn/assets/%E9%9F%B3%E4%B9%90%E7%A9%BA%E9%97%B4-BrPfxujg.jpg",
        118 to "https://bbs.uestc.edu.cn/assets/%E4%BD%93%E5%9D%9B%E9%A3%8E%E4%BA%91-BQYLqRaO.jpg",
        114 to "https://bbs.uestc.edu.cn/assets/%E6%96%87%E4%BA%BA%E5%A2%A8%E5%AE%A2-CBK4EmNG.jpg",
        334 to "https://bbs.uestc.edu.cn/assets/%E6%83%85%E7%B3%BB%E8%88%9E%E7%BC%98-CI3zRSLT.jpg",
        140 to "https://bbs.uestc.edu.cn/assets/%E5%8A%A8%E6%BC%AB%E6%97%B6%E4%BB%A3-BF6KvglC.jpg",
        888 to "https://bbs.uestc.edu.cn/assets/default-DklHFxMh.jpg",
        208 to "https://bbs.uestc.edu.cn/assets/%E7%A4%BE%E5%9B%A2%E4%BA%A4%E6%B5%81%E4%B8%AD%E5%BF%83-BoMTJvPr.jpg",

        2   to "https://bbs.uestc.edu.cn/assets/%E7%AB%99%E5%8A%A1%E5%85%AC%E5%91%8A-BgVmak4C.jpg",
        46  to "https://bbs.uestc.edu.cn/assets/%E7%AB%99%E5%8A%A1%E7%BB%BC%E5%90%88--bpgsGJP.jpg",

        -1 to "https://bbs.uestc.edu.cn/assets/default-DklHFxMh.jpg"
    )

    operator fun get(id: Int?): String? {
        return urlMapping[id] ?: urlMapping[-1]
    }
}

enum class UserLevel(var levelName: String, var minScore: Int, var maxScore: Int) {
    SHUICAO("水草", Int.MIN_VALUE, 0),
    KEDOU("蝌蚪", 0, 29),
    XIAMI("虾米", 30, 99),
    HEXIE("河蟹", 100, 499),
    NIQIU("泥鳅", 500, 799),
    CAOYU("草鱼", 800, 1199),
    YONGYU("鳙鱼", 1200, 1999),
    LIYU("鲤鱼", 2000, 2999),
    NIANYU("鲶鱼", 3000, 4549),
    BAIQI("白鳍", 4500, 6999),
    HAITUN("海豚", 7000, 9999),
    SHAYU("鲨鱼", 10000, 14999),
    NIJIJING("逆戟鲸", 15000, 29999),
    CHUANQIKEDOU("传奇蝌蚪", 30000, Int.MAX_VALUE);

    companion object {
        fun getLevelByScore(score: Int?): UserLevel? {
            if (score == null) {
                return null
            }
            return entries.firstOrNull { it.minScore <= score && score <= it.maxScore }
        }

        fun getNextLevelByScore(score: Int?): UserLevel? {
            if (score == null) {
                return null
            }
            val currentLevel = getLevelByScore(score)
            val allLevels = entries.toTypedArray()
            val currentIndex = allLevels.indexOf(currentLevel)

            return if (currentIndex < allLevels.size - 1) {
                allLevels[currentIndex + 1]
            } else {
                null
            }
        }
    }
}