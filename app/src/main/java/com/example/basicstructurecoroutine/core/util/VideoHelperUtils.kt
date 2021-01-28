package com.example.basicstructurecoroutine.core.util

import android.webkit.MimeTypeMap

/**
 * Created by Jeetesh surana.
 */

fun getMimeType(videoUrl: String?): String {
    val extension: String? = MimeTypeMap.getFileExtensionFromUrl(videoUrl)
    return if (videoUrl != null && extension != null) {
        when {
            extension.trim().equals(HLS_FILE_EXTENSION, true) -> {
                HLS_MEDIA
            }
            extension.trim().equals(DASH_FILE_EXTENSION, true) -> {
                DASH_MEDIA
            }
            extension.trim().equals(MSS_FILE_EXTENSION, true) -> {
                MSS_MEDIA
            }
            else -> {
                HELPER_UNKNOWN
            }
        }
    } else {
        HELPER_UNKNOWN
    }
}
