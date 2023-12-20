package com.dicoding.storyapp.data

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.dicoding.storyapp.data.model.response.CreateStoryResponse
import com.dicoding.storyapp.data.model.response.ListStoryItem
import com.dicoding.storyapp.data.model.response.LoginResponse
import com.dicoding.storyapp.data.model.response.LoginResult
import com.dicoding.storyapp.data.model.response.RegisterResponse
import com.dicoding.storyapp.data.model.response.StoryResponse
import java.io.File

interface AppDataSource {
    fun getUser(): LiveData<LoginResult>
    fun login(email: String, password: String): LiveData<LoginResponse>
    fun register(name: String, email: String, password: String): LiveData<RegisterResponse>
    fun postNewStory(
        token: String,
        imageFile: File,
        desc: String,
        lon: String?,
        lat: String?
    ): LiveData<CreateStoryResponse>

    fun getAllStory(token: String): LiveData<PagingData<ListStoryItem>>
    fun getListMapsStory(token: String): LiveData<StoryResponse>
}