package com.example.basicstructurecoroutine.core.feature.activity

/**
 * Created by JeeteshSurana.
 */
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.basicstructurecoroutine.R
import com.example.basicstructurecoroutine.core.model.VideoPlayerHelperTrackSelector
import com.example.basicstructurecoroutine.core.ui.BaseActivity
import com.example.basicstructurecoroutine.core.util.*
import kotlinx.android.synthetic.main.activity_video_player.*
import kotlinx.android.synthetic.main.layout_exoplayer_with_controls.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class VideoPlayerActivity : BaseActivity(), VideoPlayerHelper.ExoPlayerInterface {
    var mVideoPlayerHelper: VideoPlayerHelper? = null
    private var refGroupsIds: IntArray? = null
    private val viewDelay: Long = 40

    private var mHandler = Looper.myLooper()?.let { Handler(it) }
    private var isPictureInPictureConfigChange: Boolean = false
    private var mRunnable: Runnable? = null

    private var isCaptionEnable: Boolean = true
    private var isVideoAutoSeek: Boolean = true
    private var currentTime: String? = null
    private var isResumeSection: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)
        init()
    }

    private fun init() {
        setPlayerUI()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setPlayerUI() {
        mVideoPlayerHelper =
            VideoPlayerHelper(exo_player_courses_details, this, this)
        mVideoPlayerHelper?.hideDefaultSubtitleView(window?.decorView)
        mVideoPlayerHelper?.onClick()
        refGroupsIds = group_controls.referencedIds
        mVideoPlayerHelper?.updateProgress(seek_exoplayer_course_details)
        refGroupsIds?.forEach { id ->
            findViewById<View>(id)?.fadeOut()
        }

        img_details_full_screen.setOnClickListener {
            if (checkLandscapeOrientation()) setUI(KEY_COURSE_DETAILS_PORTRAIT)
            else setUI(KEY_COURSE_DETAILS_LANDSCAPE)
        }
        img_play_reply.setOnClickListener {
            autoFadeout()
            mVideoPlayerHelper?.replay()
            setDisplayPlayButton()
        }
        img_play_foreword.setOnClickListener {
            autoFadeout()
            mVideoPlayerHelper?.forward()
            setDisplayPlayButton()
        }
        img_back.setOnClickListener {
            onBackPressed()
        }
        img_play_button.setOnClickListener {
            setChangePlayButton()
            mVideoPlayerHelper?.onClick()
            if (mVideoPlayerHelper?.isPlaying()!!) autoFadeout()
            else removeRunnableCallback()
        }
        exo_player_courses_details.setOnTouchListener { _, event ->
            if (event?.action == MotionEvent.ACTION_UP) {
                setControlVisibility(true)
            }
            true
        }
        view_transparent_background.setOnClickListener {
            setControlVisibility(false)
        }
        mVideoPlayerHelper?.videoPlay(intent.extras?.getString(KEY_VIDEO_URL, ""), true)
    }

    //change the Orientation
    private fun checkLandscapeOrientation(): Boolean {
        val orientation = resources.configuration.orientation
        return orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    //Check Rotation the Orientation
    private fun changeOrientationToLandscape(shouldLandscape: Boolean) {
        requestedOrientation = if (shouldLandscape) {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    private fun setUI(Orientation: String) {
        lifecycleScope.launch {
            when (Orientation) {
                KEY_COURSE_DETAILS_LANDSCAPE -> {
                    if (!isNotch) {
                        setThemeOnFullScreen()
                    } else {
                        Handler(Looper.getMainLooper()).postDelayed({
                            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                        }, 300)
                    }
                    img_details_full_screen.isSelected = true
                    changeOrientationToLandscape(true)
                    delay(viewDelay)
                    common_exo_player.layoutParams = CoordinatorLayout.LayoutParams(
                        CoordinatorLayout.LayoutParams.MATCH_PARENT,
                        CoordinatorLayout.LayoutParams.MATCH_PARENT
                    )
                    exo_player_courses_details.layoutParams = ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.MATCH_PARENT,
                        ConstraintLayout.LayoutParams.MATCH_PARENT
                    )
                    setDisplayPlayButton()
                }
                KEY_COURSE_DETAILS_PORTRAIT -> {
                    if (!isNotch) {
                        val flags =
                            WindowManager.LayoutParams.FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        window.clearFlags(flags)
                        window.decorView.systemUiVisibility = window.decorView.visibility
                    } else {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                    }
                    img_details_full_screen.isSelected = false
                    changeOrientationToLandscape(false)
                    delay(viewDelay)
                    setDisplayPlayButton()
                }
                KEY_COURSE_DETAILS_PIP -> {
                    removeRunnableCallback()
                    img_details_full_screen.isSelected = false
                    refGroupsIds?.forEach { id ->
                        findViewById<View>(id)?.invisible()
                    }
                    common_exo_player.layoutParams = CoordinatorLayout.LayoutParams(
                        CoordinatorLayout.LayoutParams.MATCH_PARENT,
                        CoordinatorLayout.LayoutParams.MATCH_PARENT
                    )
                    exo_player_courses_details.layoutParams = ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.MATCH_PARENT,
                        ConstraintLayout.LayoutParams.MATCH_PARENT
                    )
                }
            }
        }
    }

    private fun autoFadeout() {
        if (isPictureInPictureConfigChange) return
        removeRunnableCallback()
        if (mRunnable == null) {
            mRunnable = Runnable {
                setControlVisibility(isFadeIn = false, isRemoveCallBack = false)
            }
        }
        mHandler?.postDelayed(mRunnable!!, 3000)
    }

    private fun removeRunnableCallback() {
        mRunnable?.let {
            mHandler?.removeCallbacks(it)
            mRunnable = null
        }
    }

    private fun setControlVisibility(isFadeIn: Boolean = true, isRemoveCallBack: Boolean = true) {
        if (isRemoveCallBack) removeRunnableCallback()
        if (isFadeIn) {
            refGroupsIds?.forEach { id ->
                window?.decorView?.findViewById<View>(id)?.fadeIn()
            }
            autoFadeout()
        } else {
            if (img_play_button.drawable.constantState == ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_pause
                )?.constantState
            ) refGroupsIds?.forEach { id ->
                window?.decorView?.findViewById<View>(id)?.fadeOut()
            }
        }
    }

    private fun setChangePlayButton() {
        if (mVideoPlayerHelper?.isPlaying()!!) {
            img_play_button.setImageResource(R.drawable.ic_play)
        } else {
            img_play_button.setImageResource(R.drawable.ic_pause)
        }
    }

    private fun setDisplayPlayButton() {
        if (mVideoPlayerHelper?.isPlaying()!!) {
            img_play_button.setImageResource(R.drawable.ic_pause)
        } else {
            img_play_button.setImageResource(R.drawable.ic_play)
        }
    }

    private fun setThemeOnFullScreen() {
        val flags =
            WindowManager.LayoutParams.FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        window?.addFlags(flags)
        window?.decorView?.systemUiVisibility = flags
    }

    override fun subTitle(subTitle: String?) {
        if (isCaptionEnable) {
            if (subTitle == null) {
                txt_caption_capture?.hide()
            } else {
                if (isPictureInPictureConfigChange) return
                txt_caption_capture?.text = subTitle
                txt_caption_capture?.show()
            }
        }
    }

    override fun currentDuration(time: String?) {
        currentTime = time
        txt_video_pass_time.text = time
    }

    override fun totalDuration(totalDuration: String?) {
        txt_video_total_time.text = totalDuration
    }

    override fun loader(show: Boolean) {
        if (show) {
            progressbar_video_player?.show()
        } else {
            progressbar_video_player?.hide()
        }
    }

    override fun playerReady() {
        if (isVideoAutoSeek) {
            isVideoAutoSeek = false
        }
    }

    override fun seekTouched(isSeekBarTouched: Boolean) {
        if (isSeekBarTouched) {
            isResumeSection = true
            removeRunnableCallback()
            refGroupsIds?.forEach { id ->
                findViewById<View>(id)?.show()
            }
        } else {
            autoFadeout()
        }
    }

    override fun videoTracks(videoTracksArrayList: ArrayList<VideoPlayerHelperTrackSelector>) {
        //TODO:in future
    }

    override fun subTitleTracks(subtitleTracksArrayList: ArrayList<VideoPlayerHelperTrackSelector>) {
        //TODO:in future
    }

    override fun audioTracks(audioTracksArrayList: ArrayList<VideoPlayerHelperTrackSelector>) {
        //TODO:in future
    }

    override fun isVideoEnded(isEnded: Boolean?) {
        //TODO: for in future
    }

    override fun videoSizeChanges(width: Int, height: Int) {
        //TODO:in future
    }

    override fun playerError(error: String?) {
        //TODO: in future
    }
}