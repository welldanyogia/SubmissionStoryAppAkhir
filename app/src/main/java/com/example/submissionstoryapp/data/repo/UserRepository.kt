package com.example.submissionstoryapp.data.repo

import com.example.submissionstoryapp.data.api.ApiService
import com.example.submissionstoryapp.data.pref.UserModel
import com.example.submissionstoryapp.data.pref.UserPreference
import kotlinx.coroutines.flow.Flow

class UserRepository private constructor(
    private val userPreference: UserPreference
){
    suspend fun saveSession(userModel: UserModel){
        userPreference.saveSession(userModel)
    }

    fun getSession(): Flow<UserModel>{
        return userPreference.getSession()
    }

    suspend fun logout(){
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference)
            }.also { instance = it }
    }
}