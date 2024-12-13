package com.scatl.uestcbbs.compose.util

import android.graphics.Color
import androidx.core.graphics.ColorUtils
import kotlin.math.pow
import kotlin.math.sqrt

object ColorUtil {

    fun colorDistance(colorA: String, colorB: String): Double {
        val aColorRGBArray = getColorRGBArray(colorA)
        val bColorRGBArray = getColorRGBArray(colorB)
        val aColorLAB = getColorLab(aColorRGBArray)
        val bColorLAB = getColorLab(bColorRGBArray)
        return calculateColorDistance(aColorLAB, bColorLAB)
    }

    private fun calculateColorDistance(aColorLAB: DoubleArray, bColorLAB: DoubleArray): Double {
        val lab = aColorLAB[0] - bColorLAB[0]
        val aab = aColorLAB[1] - bColorLAB[1]
        val bab = aColorLAB[2] - bColorLAB[2]

        val sum = lab.pow(2) + aab.pow(2) + bab.pow(2)
        return sqrt(sum)
    }

    private fun getColorRGBArray(color: String): IntArray {
        val cleanColor = color.replace("#", "")
        val colorInt = Integer.parseInt(cleanColor, 16)
        val r = Color.red(colorInt)
        val g = Color.green(colorInt)
        val b = Color.blue(colorInt)
        return intArrayOf(r, g, b)
    }

    private fun getColorLab(colorRGB: IntArray): DoubleArray {
        val outLab = doubleArrayOf(0.0,0.0,0.0)
        ColorUtils.RGBToLAB(colorRGB[0], colorRGB[1], colorRGB[2], outLab)
        return outLab
    }

}