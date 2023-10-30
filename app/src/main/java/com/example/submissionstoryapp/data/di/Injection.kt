package com.example.submissionstoryapp.data.di

import android.content.Context
import com.example.submissionstoryapp.data.repo.UserRepository
import com.example.submissionstoryapp.data.api.ApiConfig
import com.example.submissionstoryapp.data.pref.UserPreference
import com.example.submissionstoryapp.data.pref.dataStore
import com.example.submissionstoryapp.data.repo.StoryRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(pref)
    }

    fun provideStoryRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking {
            pref.getSession().first()
        }
        val apiService = ApiConfig.getApiServiceWithToken(user.token)
        return StoryRepository(apiService)
    }
}