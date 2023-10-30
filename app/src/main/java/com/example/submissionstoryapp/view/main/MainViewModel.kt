package com.example.submissionstoryapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.submissionstoryapp.data.repo.UserRepository
import com.example.submissionstoryapp.data.pref.UserModel
import com.example.submissionstoryapp.data.repo.StoryRepository
import com.example.submissionstoryapp.data.response.ListStoryItem
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository,private val storyRepository: StoryRepository) : ViewModel() {
    val story: LiveData<PagingData<ListStoryItem>> =
        storyRepository.getStoryPaging()
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

}