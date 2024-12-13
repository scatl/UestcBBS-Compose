package com.scatl.uestcbbs.compose.di.module

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.scatl.uestcbbs.compose.Constants
import com.scatl.uestcbbs.compose.api.service.LegacyService
import com.scatl.uestcbbs.compose.datastore.DataStoreDelegate
import com.scatl.uestcbbs.compose.datastore.settingsDataStore
import com.scatl.uestcbbs.compose.db.AppDataBase
import com.scatl.uestcbbs.compose.di.module.ApiModule.BindBBSApiHttpClient
import com.scatl.uestcbbs.compose.net.interceptor.AuthorizationInterceptor
import com.scatl.uestcbbs.compose.util.SSLUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Created by sca_tl at 2024/9/24 16:50:17
 */
@Module
@InstallIn(SingletonComponent::class)
class LegacyApiModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class BindLegacyBBSApiHttpClient

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class BindLegacyBBSApiRetrofit

    @Provides
    @Singleton
    @BindLegacyBBSApiHttpClient
    fun provideBBSApiOkhttpClient(@ApplicationContext context: Context, appDataBase: AppDataBase): OkHttpClient {
        val builder = OkHttpClient
            .Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(AuthorizationInterceptor(appDataBase))

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
    @BindLegacyBBSApiRetrofit
    fun provideRetrofit(@BindBBSApiHttpClient okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BBS_URL)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @Provides
    fun provideLegacyService(
        @BindLegacyBBSApiRetrofit retrofit: Retrofit
    ): LegacyService = retrofit.create(LegacyService::class.java)
}