package com.dicoding.storyapp.ui.auth.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.dicoding.storyapp.DataDummy
import com.dicoding.storyapp.data.CustomDataRepository
import com.dicoding.storyapp.data.model.response.RegisterResponse
import com.dicoding.storyapp.getOrAwaitValue
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class RegisterViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var customDataRepository: CustomDataRepository
    private lateinit var registerViewModel: RegisterViewModel
    private val dummyResponseSuccess = DataDummy.generateDummyResponseRegisterSuccess()
    private val dummyResponseFailed = DataDummy.generateDummyResponseRegisterFailed()
    private val dummyParamName = "Reishan Tridya Rafly"
    private val dummyParamEmail = "reishantridyarafly@gmail.com"
    private val dummyParamPassword = "1234567890"

    @Before
    fun setUp() {
        registerViewModel = RegisterViewModel(customDataRepository)
    }

    @Test
    fun `when Register Success`() {
        val expectedResponse = MutableLiveData<RegisterResponse>()
        expectedResponse.value = dummyResponseSuccess
        `when`(
            customDataRepository.register(
                dummyParamName,
                dummyParamEmail,
                dummyParamPassword
            )
        ).thenReturn(expectedResponse)
        val actualResponse =
            registerViewModel.registerUser(dummyParamName, dummyParamEmail, dummyParamPassword)
                .getOrAwaitValue()
        Mockito.verify(customDataRepository)
            .register(dummyParamName, dummyParamEmail, dummyParamPassword)
        Assert.assertNotNull(actualResponse)
        Assert.assertEquals(dummyResponseSuccess, actualResponse)
    }

    @Test
    fun `when Register Failed`() {
        val expectedResponse = MutableLiveData<RegisterResponse>()
        expectedResponse.value = dummyResponseFailed
        `when`(customDataRepository.register(dummyParamName, dummyParamEmail, "")).thenReturn(
            expectedResponse
        )
        val actualResponse =
            registerViewModel.registerUser(dummyParamName, dummyParamEmail, "").getOrAwaitValue()
        Mockito.verify(customDataRepository).register(dummyParamName, dummyParamEmail, "")
        Assert.assertNotNull(actualResponse)
        Assert.assertEquals(dummyResponseFailed, actualResponse)
    }
}