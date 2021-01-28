package com.example.basicstructurecoroutine.core.feature.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.basicstructurecoroutine.core.feature.repository.DashboardRepository
import com.example.basicstructurecoroutine.core.model.response.YouTubeListResponseItem
import com.example.basicstructurecoroutine.core.ui.BaseViewModel
import com.example.basicstructurecoroutine.core.util.mutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by JeeteshSurana.
 */

class DashboardViewModel(
    private val context: Context,
    private val repository: DashboardRepository
) : BaseViewModel() {

    var list = MutableLiveData<ArrayList<YouTubeListResponseItem>>()
    suspend fun getData() = withContext(Dispatchers.Main) {
        try {
            list.value?.clear()
            list.value = repository.getData()
        } catch (e: Exception) {
            e.message
        }
    }
}