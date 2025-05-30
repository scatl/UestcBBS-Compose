package com.scatl.uestcbbs.compose.theme

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.addListener
import kotlin.math.hypot
import androidx.core.graphics.createBitmap

fun Context.activeRevealView(
    animModel: RevealAnimModel,
    clickX: Float,
    clickY: Float,
    animTime: Long = 800,
    createRevealComplete: () -> Unit,
    revealAnimFinish: () -> Unit
) {
    val windows = (this as Activity).window
    val rootView = windows.decorView.rootView as ViewGroup
    captureView(rootView, windows){
        val bitmap = it
        val revealView = RevealView(animModel, Pair(clickX, clickY), this, bitmap)
        rootView.addView(revealView)
        createRevealComplete()
        revealView.animActive(animTime) {
            rootView.removeView(revealView)
            revealAnimFinish()
        }
    }
}

private fun captureView(view: View, window: Window, bitmapCallback: (Bitmap)->Unit) {
    // Above Android O, use PixelCopy
    val bitmap = createBitmap(view.width, view.height)
    val location = IntArray(2)
    view.getLocationInWindow(location)
    PixelCopy.request(window,
        Rect(location[0], location[1], location[0] + view.width, location[1] + view.height),
        bitmap,
        {
            if (it == PixelCopy.SUCCESS) {
                bitmapCallback.invoke(bitmap)
            }
        },
        Handler(Looper.getMainLooper())
    )
}

@SuppressLint("ViewConstructor")
private class RevealView(
    private val revealAnimModel: RevealAnimModel,
    private val clickPosition: Pair<Float, Float> = Pair(0f, 0f),
    context: Context,
    private var bitmap: Bitmap
) : View(context) {
    private var maskRadius = 0f
    private val paint = Paint(ANTI_ALIAS_FLAG)

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) = with(canvas) {
        val layer = saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null)
        when (revealAnimModel) {
            RevealAnimModel.EXPEND -> {
                drawBitmap(bitmap, 0f, 0f, null)
                paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
                drawCircle(clickPosition.first, clickPosition.second, maskRadius, paint)
            }
            RevealAnimModel.SHRINK -> {
                drawCircle(clickPosition.first, clickPosition.second, maskRadius, paint)
                paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
                drawBitmap(bitmap, 0f, 0f, paint)
            }
        }
        paint.xfermode = null
        restoreToCount(layer)
    }

    fun animActive(animTime: Long, animFinish: () -> Unit) {
        val radiusRange = when (revealAnimModel) {
            RevealAnimModel.EXPEND -> Pair(0f, hypot(rootView.width.toFloat(), rootView.height.toFloat()))
            RevealAnimModel.SHRINK -> Pair(hypot(rootView.width.toFloat(), rootView.height.toFloat()), 0f)
        }
        ValueAnimator.ofFloat(radiusRange.first, radiusRange.second).apply {
            duration = animTime
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { valueAnimator ->
                maskRadius = valueAnimator.animatedValue as Float
                invalidate()
            }
            addListener(onEnd = {
                animFinish()
            })
        }.start()
    }
}

enum class RevealAnimModel {
    EXPEND,
    SHRINK,
}