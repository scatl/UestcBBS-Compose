package com.scatl.uestcbbs.compose.di.module

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.scatl.uestcbbs.compose.Constants
import com.scatl.uestcbbs.compose.api.service.AuthService
import com.scatl.uestcbbs.compose.api.service.BingService
import com.scatl.uestcbbs.compose.api.service.ForumService
import com.scatl.uestcbbs.compose.api.service.IndexService
import com.scatl.uestcbbs.compose.api.service.MessageService
import com.scatl.uestcbbs.compose.api.service.PostService
import com.scatl.uestcbbs.compose.api.service.SearchService
import com.scatl.uestcbbs.compose.api.service.SystemService
import com.scatl.uestcbbs.compose.api.service.TopListService
import com.scatl.uestcbbs.compose.api.service.UserService
import com.scatl.uestcbbs.compose.datastore.DataStoreDelegate
import com.scatl.uestcbbs.compose.datastore.settingsDataStore
import com.scatl.uestcbbs.compose.db.AppDataBase
import com.scatl.uestcbbs.compose.net.interceptor.AuthorizationInterceptor
import com.scatl.uestcbbs.compose.net.interceptor.SignInInterceptor
import com.scatl.uestcbbs.compose.net.interceptor.ResponseInterceptor
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
 * created by sca_tl at 2023/7/19 20:59
 */
@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class BindBBSApiHttpClient

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class BindBBSApiRetrofit

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class BindBingRetrofit

    @Provides
    @Singleton
    @BindBBSApiHttpClient
    fun provideBBSApiOkhttpClient(@ApplicationContext context: Context, appDataBase: AppDataBase): OkHttpClient {
        val builder = OkHttpClient
            .Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(SignInInterceptor())
            .addInterceptor(AuthorizationInterceptor(appDataBase))
            .addInterceptor(ResponseInterceptor())

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
    @BindBBSApiRetrofit
    fun provideRetrofit(@BindBBSApiHttpClient okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BBS_API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    @BindBingRetrofit
    fun provideBingRetrofit(@BindBBSApiHttpClient okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BING_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @Provides
    fun provideBingService(
        @BindBingRetrofit retrofit: Retrofit
    ): BingService = retrofit.create(BingService::class.java)

    @Provides
    fun provideAuthService(
        @BindBBSApiRetrofit retrofit: Retrofit
    ): AuthService = retrofit.create(AuthService::class.java)

    @Provides
    fun provideTopListService(
        @BindBBSApiRetrofit retrofit: Retrofit
    ): TopListService = retrofit.create(TopListService::class.java)

    @Provides
    fun provideIndexService(
        @BindBBSApiRetrofit retrofit: Retrofit
    ): IndexService = retrofit.create(IndexService::class.java)

    @Provides
    fun provideUserService(
        @BindBBSApiRetrofit retrofit: Retrofit
    ): UserService = retrofit.create(UserService::class.java)

    @Provides
    fun provideSystemService(
        @BindBBSApiRetrofit retrofit: Retrofit
    ): SystemService = retrofit.create(SystemService::class.java)

    @Provides
    fun providePostService(
        @BindBBSApiRetrofit retrofit: Retrofit
    ): PostService = retrofit.create(PostService::class.java)

    @Provides
    fun provideSearchService(
        @BindBBSApiRetrofit retrofit: Retrofit
    ): SearchService = retrofit.create(SearchService::class.java)

    @Provides
    fun provideForumService(
        @BindBBSApiRetrofit retrofit: Retrofit
    ): ForumService = retrofit.create(ForumService::class.java)

    @Provides
    fun provideMsgService(
        @BindBBSApiRetrofit retrofit: Retrofit
    ): MessageService = retrofit.create(MessageService::class.java)
}