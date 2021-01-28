package com.example.basicstructurecoroutine.core.util

import android.content.ContextWrapper
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions

/**
 * Created by JeeteshSurana.
 */

fun <T> mutableLiveData(defaultValue: T? = null): MutableLiveData<T> {
    val data = MutableLiveData<T>()
    if (defaultValue != null) {
        data.value = defaultValue
    }
    return data
}

fun View.getParentActivity(): AppCompatActivity? {
    var context = this.context
    while (context is ContextWrapper) {
        if (context is AppCompatActivity) {
            return context
        }
        context = context.baseContext
    }
    return null
}


fun <T> ImageView.setPicture(
    obj: T,
    options: RequestOptions? = null,
    listener: RequestListener<Drawable>? = null
) {
    Glide.with(this).load(obj).also {
        it.apply(
            options ?: RequestOptions.centerCropTransform().diskCacheStrategy(DiskCacheStrategy.ALL)
        )
        if (listener != null) it.listener(listener)
        it.into(this)
    }
}

@BindingAdapter("setImageUrl")
fun setImageUrl(image: AppCompatImageView, url: String?) {
    if (!url.isNullOrBlank()) {
        image.setPicture(url)
    }
}
