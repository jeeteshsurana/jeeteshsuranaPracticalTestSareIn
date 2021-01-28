package com.example.basicstructurecoroutine.core.util

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Settings.Global.getString
import android.support.v4.media.session.MediaSessionCompat
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar
import com.example.basicstructurecoroutine.core.model.VideoPlayerHelperTrackSelector
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.Player.REPEAT_MODE_OFF
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.source.MergingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.SingleSampleMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector.ParametersBuilder
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.ui.SubtitleView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util


/**
 * Created by JeeteshSurana.
 */
class VideoPlayerHelper(
    var mExoPlayer: PlayerView? = null,
    var context: Context,
    private val mExoPlayerInterface: ExoPlayerInterface? = null
) {

    private var mSimpleExoPlayer: SimpleExoPlayer? = null
    private var mDefaultDataSourceFactory: DataSource.Factory? = null
    private var assignedPosition: Long? = null
    private var defaultPlayBackSpeed: Float = 1.0f
    private var mHandler = Handler(Looper.myLooper()!!)
    private var mRunnable: Runnable? = null
    private var getTrackSelection: Boolean = false
    private var defaultTrackSelector: DefaultTrackSelector? = null
    private var defaultLoadControl: DefaultLoadControl? = null
    private var defaultRenderersFactory: DefaultRenderersFactory? = null
    private var mediaSession: MediaSessionCompat? = null
    var statusIsPlaying: Boolean? = true

    interface ExoPlayerInterface {
        fun subTitle(subTitle: String?)
        fun currentDuration(time: String?)
        fun totalDuration(totalDuration: String?)
        fun loader(show: Boolean)
        fun playerReady()
        fun seekTouched(isSeekBarTouched: Boolean)
        fun videoTracks(videoTracksArrayList: ArrayList<VideoPlayerHelperTrackSelector>)
        fun subTitleTracks(subtitleTracksArrayList: ArrayList<VideoPlayerHelperTrackSelector>)
        fun audioTracks(audioTracksArrayList: ArrayList<VideoPlayerHelperTrackSelector>)
        fun isVideoEnded(isEnded: Boolean? = null)
        fun videoSizeChanges(width: Int, height: Int)
        fun playerError(error: String?)
    }

    //play the video player
    fun videoPlay(
        videoUrl: String? = null,
        play: Boolean = true,
        getTrack: Boolean = true
    ) {

        when (videoUrl) {
            is String -> Uri.parse(videoUrl as String?)
            else -> null
        }?.let { uri ->
            getTrackSelection = getTrack
            mExoPlayer?.let {
                if (it.player == null) {
                    mExoPlayer?.player?.repeatMode = REPEAT_MODE_OFF
                    defaultTrackSelector =
                        DefaultTrackSelector(context, AdaptiveTrackSelection.Factory())
                    defaultLoadControl = getLoaderControl()
                    defaultRenderersFactory = DefaultRenderersFactory(context)
                    mSimpleExoPlayer = SimpleExoPlayer.Builder(
                        context,
                        defaultRenderersFactory!!
                    )
                        .setTrackSelector(defaultTrackSelector!!)
                        .setLoadControl(defaultLoadControl!!)
                        .setUseLazyPreparation(false)
                        .build()

                    it.player = mSimpleExoPlayer!!.apply {
                        setAudioAttributes(
                            com.google.android.exoplayer2.audio.AudioAttributes.DEFAULT,
                            true
                        )
                        addAnalyticsListener(object : AnalyticsListener {
                            override fun onPlayerStateChanged(
                                eventTime: AnalyticsListener.EventTime,
                                playWhenReady: Boolean,
                                playbackState: Int
                            ) {
                                super.onPlayerStateChanged(eventTime, playWhenReady, playbackState)
                                when (playbackState) {
                                    Player.STATE_BUFFERING -> {
                                        mExoPlayerInterface?.loader(show = true)
                                    }
                                    Player.STATE_READY -> {
                                        if (getTrackSelection) {
                                            getTrackSelection = false
                                            VideoHelperTrackSelector.getTrackSelection(
                                                context,
                                                mExoPlayerInterface,
                                                defaultTrackSelector
                                            )
                                        }
                                        mExoPlayerInterface?.loader(show = false)
                                        if (playWhenReady) start(currentPosition)
                                        mExoPlayerInterface?.playerReady()
                                    }
                                    Player.STATE_ENDED -> {
                                        mExoPlayerInterface?.isVideoEnded(true)
                                    }

                                    Player.STATE_IDLE -> {
                                    }
                                }
                            }

                            override fun onVideoSizeChanged(
                                eventTime: AnalyticsListener.EventTime,
                                width: Int,
                                height: Int,
                                unappliedRotationDegrees: Int,
                                pixelWidthHeightRatio: Float
                            ) {
                                super.onVideoSizeChanged(
                                    eventTime,
                                    width,
                                    height,
                                    unappliedRotationDegrees,
                                    pixelWidthHeightRatio
                                )
                                mExoPlayerInterface?.videoSizeChanges(width, height)
                            }

                            override fun onPlayerError(
                                eventTime: AnalyticsListener.EventTime,
                                error: ExoPlaybackException
                            ) {
                                super.onPlayerError(eventTime, error)
                                mExoPlayerInterface?.playerError(null)
                            }
                        })
                    }
                    it.requestFocus()
                    it.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    mDefaultDataSourceFactory=DefaultDataSourceFactory(context,
                        Util.getUserAgent(context, "test"), DefaultBandwidthMeter()
                    )

                }
                //set the media
                subTitle(
                    Uri.parse(dummySrtUrl),
                    mDefaultDataSourceFactory!!,
                    VideoHelperMediaSource.createMediaSource(
                                                                                                                            getMimeType(videoUrl),
                        uri,
                        mDefaultDataSourceFactory!!
                    )!!
                ).let { source ->
                    mSimpleExoPlayer?.shuffleModeEnabled = true
                    it.player?.isCurrentWindowDynamic
                    mSimpleExoPlayer?.prepare(source)
                    mSimpleExoPlayer?.playWhenReady = play
                }
            }
            setMediaSession()
            setPlayBackSpeed(defaultPlayBackSpeed)
        }
    }

    private fun getLoaderControl(): DefaultLoadControl? {
        val builder =
            DefaultLoadControl.Builder()
        /*Configure the DefaultLoadControl to use our setting for how many
           Milliseconds of media data to buffer. */
        builder.setBufferDurationsMs(
            DefaultLoadControl.DEFAULT_MAX_BUFFER_MS, DefaultLoadControl.DEFAULT_MAX_BUFFER_MS,
            /* To reduce the startup time, also change the line below */
            DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS,
            DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
        ).setBackBuffer(DefaultLoadControl.DEFAULT_MAX_BUFFER_MS, true)
        /* Build the actual DefaultLoadControl instance */
        return builder.createDefaultLoadControl()
    }

    fun setMediaSession() {
        //Use Media Session Connector from the EXT library to enable MediaSession Controls in PIP.
        mediaSession = MediaSessionCompat(context, context.packageName)
        val mediaSessionConnector = MediaSessionConnector(mediaSession!!)
        mediaSessionConnector.setPlayer(mSimpleExoPlayer)
        mediaSession?.isActive = true
    }

    //ExoPlayer Play
    fun isPlaying(): Boolean {
        return if (mExoPlayer?.player != null) {
            mExoPlayer!!.player!!.playWhenReady
        } else false
    }

    //ExoPlayer Start
    fun start(cachedPosition: Long = 0) {
        mExoPlayer?.let {
            it.player?.let { player ->
                player.seekTo(cachedPosition)
                player.playWhenReady = true
            }
        }
    }

    //ExoPlayer Seek
    fun seek(cachedPosition: Long = 0) {
        mExoPlayer?.let {
            it.player?.seekTo(cachedPosition)
        }
    }

    //ExoPlayer Pause
    fun pause() {
        mExoPlayer?.let {
            if (it.player != null && isPlaying()) {
                it.player!!.playWhenReady = false
            }
        }
    }

    fun resume() {
        if (!isPlaying()) {
            start(getCurrentPosition())
        }
    }

    //ExoPlayer Stop
    fun stop() {
        mExoPlayer?.let {
            if (isPlaying()) it.player?.playWhenReady = false

            it.player = null
            mSimpleExoPlayer?.release()
            mSimpleExoPlayer = null
        }
    }

    //ExoPlayer Current Position
    fun getCurrentPosition(): Long {
        return assignedPosition ?: mExoPlayer?.let {
            if (it.player?.duration == 0L) 0
            else it.player?.currentPosition ?: 0
        } ?: 0
    }

    //ExoPlayer total duration
    fun getDuration(): Long {
        return mExoPlayer?.let {
            return if (it.player?.duration == 0L) 0
            else it.player?.duration ?: 0
        } ?: 0
    }

    private fun getCurrentBufferedPosition(): Int {
        return mExoPlayer?.let {
            it.player?.bufferedPercentage
        } ?: 0
    }

    fun onClick() {
        if (isPlaying()) {
            pause()
        } else {
            start(getCurrentPosition())
        }
    }

    fun hideDefaultSubtitleView(view: View?) {
        val subtitleView = view?.findViewById(R.id.exo_subtitles) as SubtitleView
        subtitleView.hide()
    }

    fun setPlayBackSpeed(speed: Float) {
        val param = PlaybackParameters(speed)
        mExoPlayer?.player?.setPlaybackParameters(param)
    }

    fun forward() {
        val position = getCurrentPosition() + FORWARD_BACKWORD_DURATION
        mExoPlayer?.player?.seekTo(if (position > getDuration()) getDuration() else position)
    }

    fun replay() {
        val position = getCurrentPosition() - FORWARD_BACKWORD_DURATION
        mExoPlayer?.player?.seekTo(if (position >= 0L) position else 0L)
    }

    private fun subTitle(
        srtUri: Uri?,
        mDefaultDataSourceFactory: DataSource.Factory,
        createMediaSource: Any
    ): MergingMediaSource {
        val textFormat = Format.createTextSampleFormat(
            null,
            MimeTypes.TEXT_VTT,
            null,
            Format.NO_VALUE,
            Format.NO_VALUE,
            "en",
            null,
            Format.OFFSET_SAMPLE_RELATIVE
        )
        val textMediaSource =
            SingleSampleMediaSource.Factory(mDefaultDataSourceFactory)
                .createMediaSource(srtUri, textFormat, C.TIME_UNSET)
        mSimpleExoPlayer?.addTextOutput { cues ->
            mExoPlayerInterface?.subTitle(if (!cues.isNullOrEmpty()) cues[0].text.toString() else null)
        }
        return when (createMediaSource) {
            is HlsMediaSource -> MergingMediaSource(createMediaSource, textMediaSource)
            is DashMediaSource -> MergingMediaSource(createMediaSource, textMediaSource)
            is SsMediaSource -> MergingMediaSource(createMediaSource, textMediaSource)
            else -> {
                MergingMediaSource(createMediaSource as ProgressiveMediaSource, textMediaSource)
            }
        }
    }

    fun updateProgress(seekBar: AppCompatSeekBar) {
        setProgressListener(seekBar)
        mRunnable = Runnable {
            mHandler.postDelayed(mRunnable!!, 1000)
            val percentage = getPercentage(getCurrentPosition(), getDuration())
            seekBar.secondaryProgress = getCurrentBufferedPosition()
            seekBar.progress = percentage
            mExoPlayerInterface?.totalDuration(getVideoPlayerTime(getDuration()))
            mExoPlayerInterface?.currentDuration(getVideoPlayerTime(getCurrentPosition()))
        }
        mHandler.postDelayed(mRunnable!!, 1000)
    }

    private fun setProgressListener(seekBar: AppCompatSeekBar) {
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, userClick: Boolean) {
                if (userClick) {
                    mExoPlayer?.player?.seekTo(((getDuration() * progress) / 100))
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                mExoPlayerInterface?.seekTouched(isSeekBarTouched = true)
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                mExoPlayerInterface?.seekTouched(isSeekBarTouched = false)
            }
        })
    }

    private fun getPercentage(currentPosition: Long, maxDuration: Long): Int {
        return if (maxDuration > 0) {
            ((currentPosition * 100) / maxDuration).toInt()
        } else {
            0
        }
    }

    fun removeRunnableCallback() {
        mRunnable?.let {
            mHandler.removeCallbacks(it)
            mRunnable = null
        }
    }

    fun changeTrackSelector(
        index: Int,
        videoPlayerHelperTrackSelection: VideoPlayerHelperTrackSelector,
        isVideoQuality: Boolean = false,
        isAuto: Boolean = false
    ) {
        if (isVideoQuality && isAuto) {
            val parametersBuilder: ParametersBuilder? = defaultTrackSelector?.buildUponParameters()
            parametersBuilder?.setRendererDisabled(0, false)
            parametersBuilder?.clearSelectionOverrides(0)
            parametersBuilder?.let { defaultTrackSelector?.setParameters(it) }
        } else {
            VideoHelperTrackSelector.changeTrackSelection(
                defaultTrackSelector,
                index,
                videoPlayerHelperTrackSelection
            )
        }
    }
}
