package com.example.basicstructurecoroutine.core.util

import java.text.SimpleDateFormat
import java.util.*

/**
 * File holding all the methods of Date interest.
 * Created by JeeteshSurana.
 */

const val mCommonTimeFormat = "hh:mm"
private val simpleDateFormat = SimpleDateFormat(mCommonTimeFormat, Locale.ENGLISH)
fun getDateString(time: Long) : String = simpleDateFormat.format(time * 1000L)
const val minFormat = "%02d:%02d"
const val hoursFormat = "%02d:%02d:%02d"

fun getVideoPlayerTime(timeMs: Long): String? {
    val mFormatBuilder = StringBuilder()
    val mFormatter = Formatter(mFormatBuilder, Locale.getDefault())
    val totalSeconds = timeMs / 1000
    val seconds = totalSeconds % 60
    val minutes = totalSeconds / 60 % 60
    val hours = totalSeconds / 3600
    mFormatBuilder.setLength(0)
    return if (hours > 0) {
        mFormatter.format(hoursFormat, hours, minutes, seconds).toString()
    } else {
        mFormatter.format(minFormat, minutes, seconds).toString()
    }
}