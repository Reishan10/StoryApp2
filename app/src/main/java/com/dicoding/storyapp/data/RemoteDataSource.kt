package com.dicoding.storyapp.data

import androidx.lifecycle.MutableLiveData
import com.dicoding.storyapp.data.model.response.CreateStoryResponse
import com.dicoding.storyapp.data.model.response.LoginResponse
import com.dicoding.storyapp.data.model.response.RegisterResponse
import com.dicoding.storyapp.data.model.response.StoryResponse
import com.dicoding.storyapp.network.ApiConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class RemoteDataSource {
    val error = MutableLiveData("")
    var responsecode = ""

    fun login(callback: LoginCallback, email: String, password: String) {
        callback.onLogin(
            LoginResponse(
                null,
                true,
                ""
            )
        )

        val client = ApiConfig.getApiService().doLogin(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {

                if (response.isSuccessful) {
                    response.body()?.let { callback.onLogin(it) }
                } else {
                    when (response.code()) {
                        200 -> responsecode = "200"
                        400 -> responsecode = "400"
                        401 -> responsecode = "401"
                        else -> error.postValue("ERROR ${response.code()} : ${response.message()}")
                    }
                    callback.onLogin(
                        LoginResponse(
                            null,
                            true,
                            responsecode
                        )
                    )
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                callback.onLogin(
                    LoginResponse(
                        null,
                        true,
                        t.message.toString()
                    )
                )
            }
        })
    }

    fun register(callback: RegisterCallback, name: String, email: String, password: String) {
        val registerinfo = RegisterResponse(
            true,
            ""
        )
        callback.onRegister(
            registerinfo
        )
        val client = ApiConfig.getApiService().doRegister(name, email, password)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { callback.onRegister(it) }
                    responsecode = "201"
                    callback.onRegister(
                        RegisterResponse(
                            true,
                            responsecode
                        )
                    )
                } else {
                    responsecode = "400"
                    callback.onRegister(
                        RegisterResponse(
                            true,
                            responsecode
                        )
                    )
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                callback.onRegister(
                    RegisterResponse(
                        true,
                        t.message.toString()
                    )
                )
            }
        })
    }

    fun postNewStory(
        callback: AddNewStoryCallback,
        token: String,
        imageFile: File,
        desc: String,
        lon: String? = null,
        lat: String? = null
    ) {
        callback.onAddStory(
            createStoryResponse = CreateStoryResponse(
                true,
                ""
            )
        )

        val description = desc.toRequestBody("text/plain".toMediaType())
        val latitude = lat?.toRequestBody("text/plain".toMediaType())
        val longitude = lon?.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        val client = ApiConfig.getApiService().postNewStory(
            bearer = "Bearer $token",
            imageMultipart,
            description,
            latitude!!,
            longitude!!
        )

        client.enqueue(object : Callback<CreateStoryResponse> {
            override fun onResponse(
                call: Call<CreateStoryResponse>,
                response: Response<CreateStoryResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        callback.onAddStory(responseBody)
                    } else {
                        callback.onAddStory(
                            createStoryResponse = CreateStoryResponse(
                                true,
                                "Failed to upload file"
                            )
                        )
                    }
                } else {
                    callback.onAddStory(
                        createStoryResponse = CreateStoryResponse(
                            true,
                            "Failed to upload file"
                        )
                    )
                }
            }

            override fun onFailure(call: Call<CreateStoryResponse>, t: Throwable) {
                callback.onAddStory(
                    createStoryResponse = CreateStoryResponse(
                        true,
                        "Failed to upload file"
                    )
                )
            }
        })
    }

    fun getListMapsStory(callback: GetListMapsStoryCallback, token: String) {
        val client = ApiConfig.getApiService().getListMapsStory(bearer = "Bearer $token")
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(
                call: Call<StoryResponse>,
                response: Response<StoryResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { callback.onMapsStoryLoad(it) }
                } else {
                    val storyResponse = StoryResponse(
                        emptyList(),
                        true,
                        "Load Failed!"
                    )
                    callback.onMapsStoryLoad(storyResponse)
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                val storyResponse = StoryResponse(
                    emptyList(),
                    true,
                    t.message.toString()
                )
                callback.onMapsStoryLoad(storyResponse)
            }
        })
    }

    interface LoginCallback {
        fun onLogin(loginResponse: LoginResponse)
    }

    interface RegisterCallback {
        fun onRegister(registerResponse: RegisterResponse)
    }

    interface GetListMapsStoryCallback {
        fun onMapsStoryLoad(storyResponse: StoryResponse)
    }

    interface AddNewStoryCallback {
        fun onAddStory(createStoryResponse: CreateStoryResponse)
    }

    companion object {
        @Volatile
        private var instance: RemoteDataSource? = null

        fun getInstance(): RemoteDataSource =
            instance ?: synchronized(this) {
                instance ?: RemoteDataSource()
            }
    }
}
