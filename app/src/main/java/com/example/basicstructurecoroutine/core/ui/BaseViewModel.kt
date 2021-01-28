package com.example.basicstructurecoroutine.core.ui

import androidx.lifecycle.ViewModel
import com.example.basicstructurecoroutine.core.util.mutableLiveData
import org.koin.core.KoinComponent

/**
 * Created by JeeteshSurana.
 */

open class BaseViewModel : ViewModel(), KoinComponent {
    var isProgress = mutableLiveData(false)
}