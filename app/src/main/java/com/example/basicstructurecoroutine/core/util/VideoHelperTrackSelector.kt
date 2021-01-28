package com.example.basicstructurecoroutine.core.util

import android.content.Context
import com.example.basicstructurecoroutine.core.model.VideoPlayerHelperTrackSelector
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.RendererCapabilities
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector.SelectionOverride
import com.google.android.exoplayer2.ui.DefaultTrackNameProvider

/**
 * Created by Jeetesh surana.
 */
@Suppress("DEPRECATED_IDENTITY_EQUALS")
object VideoHelperTrackSelector {
    private var videoTracksArrayList = ArrayList<VideoPlayerHelperTrackSelector>()
    private var subtitleTracksArrayList = ArrayList<VideoPlayerHelperTrackSelector>()
    private var audioTracksArrayList = ArrayList<VideoPlayerHelperTrackSelector>()

    fun getTrackSelection(
        context: Context,
        mExoPlayerInterface: VideoPlayerHelper.ExoPlayerInterface?,
        defaultTrackSelectors: DefaultTrackSelector?
    ) {
        videoTracksArrayList.clear()
        subtitleTracksArrayList.clear()
        audioTracksArrayList.clear()
        defaultTrackSelectors?.let { defaultTrackSelector ->
            defaultTrackSelector.currentMappedTrackInfo?.let {
                for ((index, rendererIndex) in (0 until it.rendererCount).withIndex()) {
                    val trackType: Int = it.getRendererType(rendererIndex)
                    val trackGroupArray: TrackGroupArray = it.getTrackGroups(rendererIndex)
                    for (groupIndex in 0 until trackGroupArray.length) {
                        for (trackIndex in 0 until trackGroupArray[groupIndex].length) {
                            val trackName: String =
                                DefaultTrackNameProvider(context.resources).getTrackName(
                                    trackGroupArray[groupIndex].getFormat(trackIndex)
                                )
                            val id = trackGroupArray[groupIndex].getFormat(trackIndex).id
                            val languageCode =
                                trackGroupArray[groupIndex].getFormat(trackIndex).language
                            val isTrackSupported = it.getTrackSupport(
                                rendererIndex,
                                groupIndex,
                                trackIndex
                            ) === RendererCapabilities.FORMAT_HANDLED

                            val videoPlayerHelperTrackSelector = VideoPlayerHelperTrackSelector(
                                id,
                                rendererIndex,
                                groupIndex,
                                trackIndex,
                                trackType,
                                trackName,
                                languageCode
                            )

                            if (isTrackSupported && trackType == C.TRACK_TYPE_VIDEO) {
                                videoTracksArrayList.add(videoPlayerHelperTrackSelector)
                            }

                            if (isTrackSupported && trackType == C.TRACK_TYPE_TEXT && !trackName.equals(
                                    HELPER_UNKNOWN,
                                    true
                                )
                            ) {
                                if (subtitleTracksArrayList.isNullOrEmpty()) {
                                    videoPlayerHelperTrackSelector.isChecked = true
                                }
                                subtitleTracksArrayList.add(videoPlayerHelperTrackSelector)
                            }

                            if (isTrackSupported && trackType == C.TRACK_TYPE_AUDIO) {
                                if (audioTracksArrayList.isNullOrEmpty()) {
                                    videoPlayerHelperTrackSelector.isChecked = true
                                }
                                audioTracksArrayList.add(videoPlayerHelperTrackSelector)
                            }
                        }
                    }
                    if (index == it.rendererCount - 1) {
                        mExoPlayerInterface?.videoTracks(videoTracksArrayList)
                        mExoPlayerInterface?.subTitleTracks(subtitleTracksArrayList)
                        mExoPlayerInterface?.audioTracks(audioTracksArrayList)
                    }
                }
            }
        }
    }

    fun changeTrackSelection(
        defaultTrackSelectors: DefaultTrackSelector?,
        position: Int,
        videoPlayerHelperTrackSelection: VideoPlayerHelperTrackSelector
    ) {
        defaultTrackSelectors?.let { defaultTrackSelector ->
            val parameters: DefaultTrackSelector.Parameters? = defaultTrackSelector.parameters
            val builder = parameters?.buildUpon()
            defaultTrackSelector.currentMappedTrackInfo?.let {
                videoPlayerHelperTrackSelection.rendererIndex?.let { rendererIndex ->
                    when (videoPlayerHelperTrackSelection.mimeType) {
                        C.TRACK_TYPE_VIDEO -> {
                            builder?.clearSelectionOverrides(rendererIndex)
                                ?.setRendererDisabled(rendererIndex, false)
                            val trackGroups = it.getTrackGroups(rendererIndex)
                            val override =
                                videoPlayerHelperTrackSelection.groupIndex?.let { groupIndex ->
                                    SelectionOverride(groupIndex, position)
                                }
                            if (!trackGroups.isEmpty) {
                                builder?.setSelectionOverride(rendererIndex, trackGroups, override)
                                builder?.let { it1 -> defaultTrackSelector.setParameters(it1) }
                            }
                        }
                        C.TRACK_TYPE_TEXT -> {
                            builder?.clearSelectionOverrides(rendererIndex)
                                ?.setRendererDisabled(rendererIndex, false)
                            defaultTrackSelector.parameters =
                                builder?.setPreferredTextLanguage(videoPlayerHelperTrackSelection.languageCode)
                                    ?.build()!!
                        }
                        C.TRACK_TYPE_AUDIO -> {
                            builder?.clearSelectionOverrides(rendererIndex)
                                ?.setRendererDisabled(rendererIndex, false)
                            val trackGroups = it.getTrackGroups(rendererIndex)
                            var selectionOverride: SelectionOverride? = null
                            videoPlayerHelperTrackSelection.groupIndex?.let { groupIndex ->
                                selectionOverride = SelectionOverride(groupIndex, 0)
                            }
                            if (!trackGroups.isEmpty) {
                                builder?.setSelectionOverride(
                                    rendererIndex,
                                    trackGroups,
                                    selectionOverride
                                )
                                builder?.let { it1 -> defaultTrackSelector.setParameters(it1) }
                            }
                        }
                    }
                }
            }
        }
    }
}
