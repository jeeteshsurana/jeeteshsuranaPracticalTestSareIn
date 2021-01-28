package com.example.basicstructurecoroutine.connection

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * Created by Jeeteshurana.
 */
class HeaderInterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response = chain.run {
        val builder: Request.Builder = request().newBuilder()
        proceed(builder.build())
    }
}
