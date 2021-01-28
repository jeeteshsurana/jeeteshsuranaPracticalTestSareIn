package com.example.basicstructurecoroutine.core.util

import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy
import com.google.android.exoplayer2.upstream.HttpDataSource
import java.io.IOException
import java.net.HttpURLConnection
import kotlin.math.min
import kotlin.math.pow

class CustomLoadPolicy : DefaultLoadErrorHandlingPolicy() {

    override fun getBlacklistDurationMsFor(
        dataType: Int,
        loadDurationMs: Long,
        exception: IOException?,
        errorCount: Int
    ): Long {

        if (exception is HttpDataSource.InvalidResponseCodeException &&
            exception.responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR
        ) {
            return DEFAULT_TRACK_BLACKLIST_MS
        }
        return super.getBlacklistDurationMsFor(dataType, loadDurationMs, exception, errorCount)
    }

    override fun getRetryDelayMsFor(
        dataType: Int,
        loadDurationMs: Long,
        exception: IOException?,
        errorCount: Int
    ): Long {
        return if (exception is HttpDataSource.HttpDataSourceException) {
            min(((errorCount - 1).toDouble().pow(2.0) * 1000).toLong(), 5000)
        } else {
            C.TIME_UNSET
        }
    }

    override fun getMinimumLoadableRetryCount(dataType: Int): Int {
        return Int.MAX_VALUE
    }
}
