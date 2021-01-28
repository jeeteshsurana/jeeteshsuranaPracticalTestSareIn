package com.example.basicstructurecoroutine.connection

import android.content.Context
import com.example.basicstructurecoroutine.core.model.response.YouTubeListResponseItem
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Created by JeeteshSurana.
 */
interface RetrofitInterface {

    @GET("fetchFeeds")
    suspend fun getYoutubeList(): Response<ArrayList<YouTubeListResponseItem>>

    companion object {
        operator fun invoke(
            context: Context,
            networkInterceptor: NetworkInterceptor,
            headerInterceptor: HeaderInterceptor
        ): RetrofitInterface {

            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY

            val cacheSize = 20 * 1024 * 1024L // 20 MB

            fun provideCache(): Cache? {
                var cache: Cache? = null
                try {
                    cache = Cache(File(context.cacheDir, "Cache_directory"), cacheSize)
                } catch (e: Exception) {
                    /* TODO("WHen Crashlytics is merged report it")*/
                }
                return cache
            }

            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.MINUTES)
                .readTimeout(90, TimeUnit.SECONDS)
                .writeTimeout(90, TimeUnit.SECONDS)
                .cache(provideCache())
                .addInterceptor(headerInterceptor)
                .addInterceptor(networkInterceptor)
                .addInterceptor(logging)
                .build()

            return Retrofit.Builder().baseUrl(baseURL)
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).addConverterFactory(
                    GsonConverterFactory.create()
                )
                .build()
                .create(RetrofitInterface::class.java)
        }
    }

}
