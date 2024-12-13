package com.scatl.uestcbbs.compose.di.module

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.VideoFrameDecoder
import coil.disk.DiskCache
import com.scatl.uestcbbs.compose.datastore.DataStoreDelegate
import com.scatl.uestcbbs.compose.datastore.settingsDataStore
import com.scatl.uestcbbs.compose.ext.isGTESdk28
import com.scatl.uestcbbs.compose.util.SSLUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Created by sca_tl at 2024/9/24 16:42:54
 */
@Module
@InstallIn(SingletonComponent::class)
class CoilModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class BindCoilHttpClient

    @Provides
    @Singleton
    @BindCoilHttpClient
    fun provideCoilOkhttpClient(@ApplicationContext context: Context): OkHttpClient {
        val builder = OkHttpClient
            .Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .sslSocketFactory(SSLUtil.getSSLSocketFactory(), SSLUtil.getTrustManager())
            .hostnameVerifier(SSLUtil.getHostNameVerifier())

        //todo 这里不能使用DataStore类，app.context还没初始化
        val ignoreSSL by DataStoreDelegate(context.settingsDataStore, booleanPreferencesKey("ignoreSSL"), false)
        if (ignoreSSL) {
            builder
                .sslSocketFactory(SSLUtil.getSSLSocketFactory(), SSLUtil.getTrustManager())
                .hostnameVerifier(SSLUtil.getHostNameVerifier())
        }

        return builder.build()
    }

    @Singleton
    @Provides
    fun provideCoilImageLoader(@ApplicationContext context: Context,
                               @BindCoilHttpClient okHttpClient: OkHttpClient): ImageLoader {
        return ImageLoader
            .Builder(context)
            .okHttpClient {
                okHttpClient
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("coil_image_cache"))
                    .maxSizePercent(0.02)
                    .build()
            }
            .components {
                if (isGTESdk28()) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
                add(VideoFrameDecoder.Factory())
            }
            .crossfade(300)
            .build()
    }
}