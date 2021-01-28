package com.example.basicstructurecoroutine.core.feature.repository

import com.example.basicstructurecoroutine.connection.SafeApiRequest
import com.example.basicstructurecoroutine.connection.RetrofitInterface
import com.example.basicstructurecoroutine.core.model.response.YouTubeListResponseItem
import okhttp3.ResponseBody

/**
 * Created by Jeetesh surana.
 */

class DashboardRepository(private val api: RetrofitInterface) : SafeApiRequest() {

    suspend fun getData(): ArrayList<YouTubeListResponseItem> {
        return apiRequest { api.getYoutubeList() }
    }
}
