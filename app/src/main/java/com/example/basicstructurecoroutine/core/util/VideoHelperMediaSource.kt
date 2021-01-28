package com.example.basicstructurecoroutine.core.util

import android.net.Uri
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
import com.google.android.exoplayer2.upstream.DataSource

/**
 * Created by Jeetesh surana.
 */
object VideoHelperMediaSource {
    fun createMediaSource(
        type: String?,
        uri: Uri,
        mDefaultDataSourceFactory: DataSource.Factory
    ): Any? {
        return when (type) {
            HLS_MEDIA -> getHlsMediaSource(uri, mDefaultDataSourceFactory)
            DASH_MEDIA -> getDAShMediaSource(uri, mDefaultDataSourceFactory)
            MSS_MEDIA -> getMSSMediaSource(uri, mDefaultDataSourceFactory)
            else -> ProgressiveMediaSource.Factory(mDefaultDataSourceFactory)
                .setLoadErrorHandlingPolicy(CustomLoadPolicy()).createMediaSource(uri)
        }
    }

    private fun getHlsMediaSource(
        uri: Uri,
        mDefaultDataSourceFactory: DataSource.Factory
    ): HlsMediaSource? {
        return HlsMediaSource.Factory(mDefaultDataSourceFactory)
            .setLoadErrorHandlingPolicy(CustomLoadPolicy()).createMediaSource(uri)
    }

    private fun getDAShMediaSource(
        uri: Uri,
        mDefaultDataSourceFactory: DataSource.Factory
    ): DashMediaSource? {
        val dashChunkSourceFactory = DefaultDashChunkSource.Factory(mDefaultDataSourceFactory)
        return DashMediaSource.Factory(dashChunkSourceFactory, mDefaultDataSourceFactory)
            .setLoadErrorHandlingPolicy(CustomLoadPolicy()).createMediaSource(uri)
    }

    private fun getMSSMediaSource(
        uri: Uri,
        mDefaultDataSourceFactory: DataSource.Factory
    ): SsMediaSource? {
        return SsMediaSource.Factory(mDefaultDataSourceFactory)
            .setLoadErrorHandlingPolicy(CustomLoadPolicy()).createMediaSource(uri)
    }
}
