package com.example.submissionstoryapp.data.repo

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.submissionstoryapp.data.api.ApiConfig
import com.example.submissionstoryapp.data.api.ApiService
import com.example.submissionstoryapp.data.di.Injection
import com.example.submissionstoryapp.data.paging.StoryPagingSource
import com.example.submissionstoryapp.data.pref.UserPreference
import com.example.submissionstoryapp.data.response.ListStoryItem
import com.example.submissionstoryapp.data.response.Story

class StoryRepository( private val apiService: ApiService) {

    fun getStoryPaging(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService)
            }
        ).liveData
    }

    suspend fun getStories(token: String,onSuccess: (List<ListStoryItem>) -> Unit, onError: (String) -> Unit) {
        try {
            val response = ApiConfig.getApiServiceWithToken(token).getStoriesLoc()
            if (response.error) {
                onError(response.message)
            } else {
                onSuccess(response.listStory)
            }
        } catch (e: Exception) {
            onError(e.message ?: "Unknown error")
        }
    }
    suspend fun getStoryDetail( storyId: String): Story {
        val response = apiService.getStoryDetail( storyId)
        if (response.error) {
            throw Exception(response.message)
        }
        return response.story
    }
    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(apiService: ApiService,pref:UserPreference): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService).also { instance = it }
            }
    }
}