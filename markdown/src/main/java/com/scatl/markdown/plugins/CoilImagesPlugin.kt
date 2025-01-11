package com.scatl.markdown.plugins

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.text.Spanned
import android.widget.TextView
import coil.Coil
import coil.ImageLoader
import coil.request.Disposable
import coil.request.ImageRequest
import coil.target.Target
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.MarkwonSpansFactory
import io.noties.markwon.image.AsyncDrawable
import io.noties.markwon.image.AsyncDrawableLoader
import io.noties.markwon.image.AsyncDrawableScheduler
import io.noties.markwon.image.DrawableUtils
import io.noties.markwon.image.ImageSpanFactory
import org.commonmark.node.Image
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by tanlei02 at 2025/1/3 10:07:10
 */
class CoilImagesPlugin(
    context: Context,
    coilStore: CoilStore,
    imageLoader: ImageLoader
) : AbstractMarkwonPlugin() {

    private var coilAsyncDrawableLoader = CoilAsyncDrawableLoader(context, coilStore, imageLoader)

    interface CoilStore {
        fun load(drawable: AsyncDrawable): ImageRequest

        fun cancel(disposable: Disposable)
    }

    companion object {

        val cachedSize = mutableMapOf<String, Pair<Int, Int>>()

        fun create(context: Context): CoilImagesPlugin {
            return create(
                context = context,
                imageLoader = Coil.imageLoader(context),
                coilStore = object : CoilStore {
                    override fun load(drawable: AsyncDrawable): ImageRequest {
                        return ImageRequest.Builder(context)
                            .data(drawable.destination)
                            .build()
                    }

                    override fun cancel(disposable: Disposable) {
                        disposable.dispose()
                    }
            })
        }

        fun create(context: Context, imageLoader: ImageLoader): CoilImagesPlugin {
            return create(
                context = context,
                imageLoader = imageLoader,
                coilStore = object : CoilStore {
                    override fun load(drawable: AsyncDrawable): ImageRequest {
                        return ImageRequest.Builder(context)
                            .data(drawable.destination)
                            .build()
                    }

                    override fun cancel(disposable: Disposable) {
                        disposable.dispose()
                    }
            })
        }

        fun create(context: Context, imageLoader: ImageLoader, coilStore: CoilStore): CoilImagesPlugin {
            return CoilImagesPlugin(context, coilStore, imageLoader)
        }
    }

    override fun configureSpansFactory(builder: MarkwonSpansFactory.Builder) {
        builder.setFactory(Image::class.java, ImageSpanFactory())
    }

    override fun configureConfiguration(builder: MarkwonConfiguration.Builder) {
        builder.asyncDrawableLoader(coilAsyncDrawableLoader)
    }

    override fun beforeSetText(textView: TextView, markdown: Spanned) {
        AsyncDrawableScheduler.unschedule(textView)
    }

    override fun afterSetText(textView: TextView) {
        AsyncDrawableScheduler.schedule(textView)
    }

    class CoilAsyncDrawableLoader internal constructor(
        private val context: Context,
        private val coilStore: CoilStore,
        private val imageLoader: ImageLoader
    ) : AsyncDrawableLoader() {
        private val cache: MutableMap<AsyncDrawable, Disposable?> = HashMap(2)

        override fun load(drawable: AsyncDrawable) {
            val loaded = AtomicBoolean(false)
            val target = AsyncDrawableTarget(drawable, loaded)
            val request = coilStore.load(drawable).newBuilder()
                .target(target)
                .build()
            // @since 4.5.1 execute can return result _before_ disposable is created,
            //  thus `execute` would finish before we put disposable in cache (and thus result is
            //  not delivered)
            val disposable = imageLoader.enqueue(request)
            // if flag was not set, then job is running (else - finished before we got here)
            if (!loaded.get()) {
                // mark flag
                loaded.set(true)
                cache[drawable] = disposable
            }
        }

        override fun cancel(drawable: AsyncDrawable) {
            val disposable = cache.remove(drawable)
            if (disposable != null) {
                coilStore.cancel(disposable)
            }
        }

        override fun placeholder(drawable: AsyncDrawable): Drawable? {
            return GradientDrawable()
            //return null
        }

        private inner class AsyncDrawableTarget(
            private val drawable: AsyncDrawable,
            private val loaded: AtomicBoolean
        ) : Target {
            override fun onSuccess(result: Drawable) {
                // @since 4.5.1 check finished flag (result can be delivered _before_ disposable is created)
                if (cache.remove(drawable) != null || !loaded.get()) {
                    // mark
                    loaded.set(true)
                    if (drawable.isAttached) {
                        if (cachedSize[drawable.destination] == null) {
                            if (drawable.destination.startsWith("file:///android_asset/emotion")) {
                                val radio = result.intrinsicWidth.toFloat() / result.intrinsicHeight.toFloat()
                                val width = (drawable.lastKnowTextSize * radio * 1.5f).toInt()
                                val height = (drawable.lastKnowTextSize * 1.5f).toInt()
                                result.bounds = Rect(0, 0, width, height)
                                cachedSize[drawable.destination] = Pair(width, height)
                            } else {
                                DrawableUtils.applyIntrinsicBoundsIfEmpty(result)
                                cachedSize[drawable.destination] = Pair(result.intrinsicWidth, result.intrinsicHeight)
                            }
                        } else {
                            result.bounds = Rect(
                                0,
                                0,
                                cachedSize[drawable.destination]!!.first,
                                cachedSize[drawable.destination]!!.second
                            )
                        }
                        drawable.result = result

//                        DrawableUtils.applyIntrinsicBoundsIfEmpty(result)
//                        drawable.result = result
                    }
                }
            }

            override fun onStart(placeholder: Drawable?) {
//                if (placeholder != null && drawable.isAttached) {
//                    DrawableUtils.applyIntrinsicBoundsIfEmpty(placeholder)
//                    drawable.result = placeholder
//                }

                if (drawable.destination.startsWith("file:///android_asset/emotion").not() &&
                    cachedSize[drawable.destination] != null &&
                    placeholder != null) {
                        placeholder.bounds = Rect(
                            0,
                            0,
                            cachedSize[drawable.destination]!!.first,
                            cachedSize[drawable.destination]!!.second
                        )
                        drawable.bounds = placeholder.bounds
                    drawable.result = placeholder
                } else if (placeholder != null) {

                }

//                if (placeholder != null) {
//                    placeholder.bounds = Rect(
//                        0,
//                        0,
//                        1000,
//                        300
//                    )
//                    //DrawableUtils.applyIntrinsicBoundsIfEmpty(placeholder)
//                    drawable.result = placeholder
//                }
            }

            override fun onError(error: Drawable?) {
                if (cache.remove(drawable) != null) {
                    if (error != null && drawable.isAttached) {
                        DrawableUtils.applyIntrinsicBoundsIfEmpty(error)
                        drawable.result = error
                    }
                }
            }
        }
    }

}