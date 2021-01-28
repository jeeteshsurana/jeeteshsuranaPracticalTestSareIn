package com.example.basicstructurecoroutine.core.ui

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.example.basicstructurecoroutine.R
import com.example.basicstructurecoroutine.core.util.hideKeyboard
import com.example.basicstructurecoroutine.core.util.isNotch

/**
 * Created by JeeteshSurana.
 */

abstract class BaseActivity : AppCompatActivity() {
    var isNotch: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isNotch = isNotch(resources)
    }

    override fun onDestroy() {
        hideKeyboard()
        super.onDestroy()
    }
}