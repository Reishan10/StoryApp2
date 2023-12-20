package com.dicoding.storyapp.ui.auth.register

import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.data.CustomDataRepository

class RegisterViewModel(private val dataRepository: CustomDataRepository) : ViewModel() {
    fun registerUser(name: String, email: String, password: String) =
        dataRepository.register(name, email, password)
}
