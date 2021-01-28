package com.example.basicstructurecoroutine.core.model

/**
 * Created by Abhin.
 */
data class VideoPlayerHelperTrackSelector(
    var id: String? = "",
    var rendererIndex: Int? = -1,
    var groupIndex: Int? = -1,
    var trackIndex: Int? = -1,
    var mimeType: Int? = null,
    var title: String? = null,
    var languageCode: String? = null,
    var isChecked: Boolean = false
)
