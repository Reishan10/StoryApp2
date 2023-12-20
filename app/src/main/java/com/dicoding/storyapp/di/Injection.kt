package com.dicoding.storyapp.di

import android.content.Context
import com.dicoding.storyapp.data.CustomDataRepository
import com.dicoding.storyapp.data.RemoteDataSource
import com.dicoding.storyapp.data.model.UserDataStorePreferences
import com.dicoding.storyapp.data.model.database.CustomStoryDatabase
import com.dicoding.storyapp.network.ApiConfig

object Injection {
    fun provideRepository(context: Context): CustomDataRepository {
        val apiService = ApiConfig.getApiService()
        val userDataStorePreferences = UserDataStorePreferences.getInstance(context)
        val remoteDataSource = RemoteDataSource.getInstance()
        val customStoryDatabase = CustomStoryDatabase.getDatabase(context)
        return CustomDataRepository.getInstance(
            apiService,
            userDataStorePreferences,
            remoteDataSource,
            customStoryDatabase
        )
    }
}