package com.dicoding.storyapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.storyapp.data.model.UserDataStorePreferences
import com.dicoding.storyapp.data.model.database.CustomStoryDatabase
import com.dicoding.storyapp.data.model.response.CreateStoryResponse
import com.dicoding.storyapp.data.model.response.ListStoryItem
import com.dicoding.storyapp.data.model.response.LoginResponse
import com.dicoding.storyapp.data.model.response.LoginResult
import com.dicoding.storyapp.data.model.response.RegisterResponse
import com.dicoding.storyapp.data.model.response.StoryResponse
import com.dicoding.storyapp.network.ApiService
import java.io.File

class CustomDataRepository(
    private val apiService: ApiService,
    private val pref: UserDataStorePreferences,
    private val remoteDataSource: RemoteDataSource,
    private val customStoryDatabase: CustomStoryDatabase
) : AppDataSource {

    override fun getUser(): LiveData<LoginResult> {
        return pref.getUser().asLiveData()
    }

    suspend fun saveUser(userName: String, userId: String, userToken: String) {
        pref.saveUser(userName, userId, userToken)
    }

    suspend fun logout() {
        pref.logout()
    }

    override fun login(email: String, password: String): LiveData<LoginResponse> {
        val loginResponseLiveData = MutableLiveData<LoginResponse>()

        remoteDataSource.login(object : RemoteDataSource.LoginCallback {
            override fun onLogin(loginResponse: LoginResponse) {
                loginResponseLiveData.postValue(loginResponse)
            }
        }, email, password)

        return loginResponseLiveData
    }

    override fun register(
        name: String,
        email: String,
        password: String
    ): LiveData<RegisterResponse> {
        val registerResponseLiveData = MutableLiveData<RegisterResponse>()

        remoteDataSource.register(object : RemoteDataSource.RegisterCallback {
            override fun onRegister(registerResponse: RegisterResponse) {
                registerResponseLiveData.postValue(registerResponse)
            }
        }, name, email, password)

        return registerResponseLiveData
    }

    override fun getAllStory(token: String): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5,
            ),
            pagingSourceFactory = {
                CustomDataPagingSource(
                    apiServicePaging = apiService,
                    dataStoreRepository = pref
                )
            }
        ).liveData
    }

    override fun postNewStory(
        token: String,
        imageFile: File,
        desc: String,
        lon: String?,
        lat: String?
    ): LiveData<CreateStoryResponse> {
        val createStoryResponseLiveData = MutableLiveData<CreateStoryResponse>()

        remoteDataSource.postNewStory(object : RemoteDataSource.AddNewStoryCallback {
            override fun onAddStory(createStoryResponse: CreateStoryResponse) {
                createStoryResponseLiveData.postValue(createStoryResponse)
            }
        }, token, imageFile, desc, lon, lat)

        return createStoryResponseLiveData
    }

    override fun getListMapsStory(token: String): LiveData<StoryResponse> {
        val storyResponseLiveData = MutableLiveData<StoryResponse>()

        remoteDataSource.getListMapsStory(object : RemoteDataSource.GetListMapsStoryCallback {
            override fun onMapsStoryLoad(storyResponse: StoryResponse) {
                storyResponseLiveData.postValue(storyResponse)
            }
        }, token)

        return storyResponseLiveData
    }

    companion object {
        @Volatile
        private var instance: CustomDataRepository? = null

        fun getInstance(
            apiService: ApiService,
            pref: UserDataStorePreferences,
            remoteDataSource: RemoteDataSource,
            customStoryDatabase: CustomStoryDatabase
        ): CustomDataRepository =
            instance ?: synchronized(this) {
                instance ?: CustomDataRepository(
                    apiService,
                    pref,
                    remoteDataSource,
                    customStoryDatabase
                )
            }.also { instance = it }
    }
}
