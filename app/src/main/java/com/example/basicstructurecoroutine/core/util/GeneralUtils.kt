package com.example.basicstructurecoroutine.core.util

import android.app.Activity
import android.content.res.Resources
import android.util.TypedValue
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.internal.ViewUtils.dpToPx
import com.google.android.material.snackbar.Snackbar


/**
 * File holding all the methods of general interest.
 * Create by JeeteshSurana
 */

//hide the keyboard
fun Activity.hideKeyboard() {
    val imm: InputMethodManager =
        getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    var view = currentFocus
    if (view == null) view = View(this)
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun FragmentActivity.addReplaceFragment(
    container: Int,
    fragment: Fragment,
    addFragment: Boolean,
    addToBackStack: Boolean
) {
    val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
    if (addFragment) {
        transaction.add(container, fragment, fragment.javaClass.simpleName)
    } else {
        transaction.replace(container, fragment, fragment.javaClass.simpleName)
    }
    if (addToBackStack) {
        transaction.addToBackStack(fragment.tag)
    }
    hideKeyboard()
    transaction.commit()
}

fun View.snackbar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).show()
}

fun View.snackbarWithAnchor(message: String, anchorView: View) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).setAnchorView(anchorView).also { snackbar ->
        snackbar.setAction("Ok") {
            snackbar.dismiss()
        }
    }.show()
}
fun View.show() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun show(vararg views: View) {
    for (view in views) {
        view.show()
    }
}

fun hide(vararg views: View) {
    for (view in views) {
        view.hide()
    }
}

fun invisible(vararg views: View) {
    for (view in views) {
        view.invisible()
    }
}

fun View.fadeOut() {
    val fadeOut: Animation = AlphaAnimation(1f, 0f)
    fadeOut.interpolator = AccelerateInterpolator()
    fadeOut.duration = FADE_OUT_IN
    fadeOut.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationRepeat(p0: Animation?) {}
        override fun onAnimationEnd(p0: Animation?) {
            invisible()
        }

        override fun onAnimationStart(p0: Animation?) {}
    })
    startAnimation(fadeOut)
}

fun View.fadeIn() {
    show()
    val fadeIn: Animation = AlphaAnimation(0f, 1f)
    fadeIn.interpolator = DecelerateInterpolator()
    fadeIn.duration = FADE_OUT_IN
    startAnimation(fadeIn)
}

fun isNotch(resources: Resources): Boolean {
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    val statusBarHeight = if (resourceId > 0) {
        resources.getDimensionPixelSize(resourceId)
    } else 0
    return statusBarHeight > dpToPx(24f, resources)
}

fun dpToPx(dp: Float, resources: Resources): Int {
    return convertToPx(TypedValue.COMPLEX_UNIT_DIP, dp, resources)
}

private fun convertToPx(unit: Int, value: Float, resources: Resources): Int {
    val px = TypedValue.applyDimension(unit, value, resources.displayMetrics)
    return px.toInt()
}
