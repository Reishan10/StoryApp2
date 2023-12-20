package com.dicoding.storyapp.ui.auth.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.dicoding.storyapp.DataDummy
import com.dicoding.storyapp.data.CustomDataRepository
import com.dicoding.storyapp.data.model.response.LoginResponse
import com.dicoding.storyapp.data.model.response.LoginResult
import com.dicoding.storyapp.getOrAwaitValue
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var customDataRepository: CustomDataRepository
    private lateinit var loginViewModel: LoginViewModel
    private val dummyResponseSuccess = DataDummy.generateDummyResponseLoginSuccess()
    private val dummyResponseErrorInvalidEmailFormat =
        DataDummy.generateDummyResponseLoginErrorInvalidEmailFormat()
    private val dummyResponseErrorUserNotFound =
        DataDummy.generateDummyResponseLoginErrorUserNotFound()
    private val dummyLoginResult = DataDummy.generateDummyLoginResult()
    private val dummyEmail = "reishantridyarafly@gmail.com"
    private val dummyPassword = "1234567890"

    @Before
    fun setUp() {
        loginViewModel = LoginViewModel(customDataRepository)
    }

    @Test
    fun `when Login Success`() {
        val expectedResponse = MutableLiveData<LoginResponse>()
        expectedResponse.value = dummyResponseSuccess
        Mockito.`when`(customDataRepository.login(dummyEmail, dummyPassword))
            .thenReturn(expectedResponse)
        val actualResponse = loginViewModel.login(dummyEmail, dummyPassword).getOrAwaitValue()
        Mockito.verify(customDataRepository).login(dummyEmail, dummyPassword)
        Assert.assertNotNull(actualResponse)
        Assert.assertEquals(expectedResponse.value, actualResponse)
    }

    @Test
    fun `when Error User Not Found`() {
        val expectedResponse = MutableLiveData<LoginResponse>()
        expectedResponse.value = dummyResponseErrorUserNotFound
        Mockito.`when`(customDataRepository.login("xx", "xx")).thenReturn(expectedResponse)
        val actualResponse = loginViewModel.login("xx", "xx").getOrAwaitValue()
        Mockito.verify(customDataRepository).login("xx", "xx")
        Assert.assertNotNull(actualResponse)
        Assert.assertEquals(actualResponse, expectedResponse.value)
    }

    @Test
    fun `when Error Invalid Email Format`() {
        val expectedResponse = MutableLiveData<LoginResponse>()
        expectedResponse.value = dummyResponseErrorInvalidEmailFormat
        Mockito.`when`(customDataRepository.login("reishantridyarafly@gmail", dummyPassword))
            .thenReturn(expectedResponse)
        val actualResponse =
            loginViewModel.login("reishantridyarafly@gmail", dummyPassword).getOrAwaitValue()
        Mockito.verify(customDataRepository).login("reishantridyarafly@gmail", dummyPassword)
        Assert.assertNotNull(actualResponse)
        Assert.assertEquals(actualResponse, expectedResponse.value)
    }

    @Test
    fun `when Get User`() {
        val expectedResponse = MutableLiveData<LoginResult>()
        expectedResponse.value = dummyLoginResult
        Mockito.`when`(customDataRepository.getUser()).thenReturn(expectedResponse)
        val actualResponse = loginViewModel.getUser().getOrAwaitValue()
        Mockito.verify(customDataRepository).getUser()
        Assert.assertNotNull(actualResponse)
        Assert.assertEquals(actualResponse, expectedResponse.value)
    }
}
