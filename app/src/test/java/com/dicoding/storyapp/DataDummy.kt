package com.dicoding.storyapp

import com.dicoding.storyapp.data.model.response.CreateStoryResponse
import com.dicoding.storyapp.data.model.response.ListStoryItem
import com.dicoding.storyapp.data.model.response.LoginResponse
import com.dicoding.storyapp.data.model.response.LoginResult
import com.dicoding.storyapp.data.model.response.RegisterResponse
import com.dicoding.storyapp.data.model.response.StoryResponse

object DataDummy {

    fun generateDummyListStoryItem(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val quote = ListStoryItem(
                i.toString(),
                "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png",
                "Reishan $i",
                "ini adalah Uni testing $i",
                "2023-08-08T06:34:18.598Z",
                -10.212,
                -10.212
            )
            items.add(quote)
        }
        return items
    }

    fun generateDummyStoryResponse(): StoryResponse {
        return StoryResponse(
            generateDummyListStoryItem(),
            false,
            "Stories fetched successfully"
        )
    }

    fun generateDummyAddStoryResponse(): CreateStoryResponse {
        return CreateStoryResponse(
            false,
            "success",
        )
    }


    fun generateDummyResponseLoginSuccess(): LoginResponse {
        val loginResult = LoginResult(
            "user-H5CTfuK4Cvq5FK1B",
            "Reishan Tridya Rafly",
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLUg1Q1RmdUs0Q3ZxNUZLMUIiLCJpYXQiOjE2Njc0NDcyNjZ9.53P9_qZ5Y0ZxShzNsr14EXIaCg9Qfq1sNack8U-cT0s"
        )
        return LoginResponse(
            loginResult,
            error = false,
            message = "200"
        )
    }

    fun generateDummyResponseLoginErrorInvalidEmailFormat(): LoginResponse {
        return LoginResponse(
            null,
            true,
            "400"
        )
    }

    fun generateDummyResponseLoginErrorUserNotFound(): LoginResponse {
        return LoginResponse(
            null,
            true,
            "401"
        )
    }

    fun generateDummyLoginResult(): LoginResult {
        return LoginResult(
            "Reishan Tridya Rafly",
            "reishantridyarafly@gmail.com",
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLUg1Q1RmdUs0Q3ZxNUZLMUIiLCJpYXQiOjE2Njc0NDcyNjZ9.53P9_qZ5Y0ZxShzNsr14EXIaCg9Qfq1sNack8U-cT0s"
        )
    }

    fun generateDummyLoginResultEmpty(): LoginResult {
        return LoginResult(
            "",
            "",
            ""
        )
    }

    fun generateDummyResponseRegister(): RegisterResponse {
        return RegisterResponse(
            error = false,
            message = "success"
        )
    }

    fun generateDummyResponseRegisterSuccess(): RegisterResponse {
        return RegisterResponse(
            error = false,
            message = "200"
        )
    }

    fun generateDummyResponseRegisterFailed(): RegisterResponse {
        return RegisterResponse(
            true,
            "400"
        )
    }
}