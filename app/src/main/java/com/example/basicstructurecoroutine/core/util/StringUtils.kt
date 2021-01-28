package com.example.basicstructurecoroutine.core.util

import android.content.Context

/**
 * Created by JeeteshSurana.
 */

/**
 * Copies any text to system clipboard.
 * @param context the activity/application [Context].
 * @param text the string to be copied to system clipboard.
 */
fun copyToClipboard(context: Context, text: String = "") {
    val clipboard =
        context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
    val clip = android.content.ClipData.newPlainText("COPY_TEXT", text)
    clipboard.setPrimaryClip(clip)
}

