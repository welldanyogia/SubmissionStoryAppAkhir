package com.example.submissionstoryapp.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.submissionstoryapp.data.repo.UserRepository
import com.example.submissionstoryapp.data.di.Injection
import com.example.submissionstoryapp.data.repo.StoryRepository
import com.example.submissionstoryapp.view.detail.DetailViewModel
import com.example.submissionstoryapp.view.login.LoginViewModel
import com.example.submissionstoryapp.view.main.MainViewModel

class ViewModelFactory(private val repository: UserRepository, private val storyRepository: StoryRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository,storyRepository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository) as T
            }
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(storyRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(Injection.provideRepository(context),Injection.provideStoryRepository(context))
                }
            }
            return INSTANCE as ViewModelFactory
        }
        @JvmStatic
        fun clearInstance(){
            INSTANCE = null
        }
    }
}