package com.dicoding.storyapp.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.dicoding.storyapp.DataDummy
import com.dicoding.storyapp.MainDispatcherRule
import com.dicoding.storyapp.StoryPagingSource
import com.dicoding.storyapp.data.model.UserDataStorePreferences
import com.dicoding.storyapp.data.model.database.CustomStoryDatabase
import com.dicoding.storyapp.data.model.response.ListStoryItem
import com.dicoding.storyapp.network.ApiService
import com.dicoding.storyapp.ui.story.StoryAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class CustomDataRepositoryTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var remoteDataSource: RemoteDataSource

    @Mock
    private lateinit var customStoryDatabase: CustomStoryDatabase

    @Mock
    private lateinit var apiService: ApiService

    @Mock
    private lateinit var userDataStorePreferences: UserDataStorePreferences

    @Mock
    private lateinit var customDataRepository: CustomDataRepository

    private val dummyResponseSignUp = DataDummy.generateDummyResponseRegister()
    private val dummyStoryResponse = DataDummy.generateDummyStoryResponse()
    private val dummyResponseSignIn = DataDummy.generateDummyResponseRegisterSuccess()
    private val dummyAddStoryResponse = DataDummy.generateDummyAddStoryResponse()
    private val dummyStory = DataDummy.generateDummyListStoryItem()

    private val dummyParamName = "Reishan Tridya Rafly"
    private val dummyParamEmail = "reishantridyarafly@gmail.com"
    private val dummyParamPassword = "1234567890"
    private val token =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLUg1Q1RmdUs0Q3ZxNUZLMUIiLCJpYXQiOjE2Njc0NDcyNjZ9.53P9_qZ5Y0ZxShzNsr14EXIaCg9Qfq1sNack8U-cT0s"


    @Before
    fun setup() {
        customDataRepository = CustomDataRepository(
            apiService,
            userDataStorePreferences,
            remoteDataSource,
            customStoryDatabase
        )
    }

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `when Sign Up response`() = runTest {
        val expectedResponse = dummyResponseSignUp
        customDataRepository.register(dummyParamName, dummyParamEmail, dummyParamPassword)
            .observeForever { actualResponse ->
                Assert.assertNotNull(actualResponse)
                Assert.assertEquals(
                    expectedResponse,
                    actualResponse
                )
            }
    }

    @Test
    fun `when Sign in response`() = runTest {
        val expectedResponse = dummyResponseSignIn
        customDataRepository.login(dummyParamEmail, dummyParamPassword)
            .observeForever { actualResponse ->
                Assert.assertNotNull(actualResponse)
                Assert.assertEquals(
                    expectedResponse,
                    actualResponse
                )
            }
    }

    @Test
    fun `when Add Story Response`() = runTest {
        val expectedResponse = dummyAddStoryResponse
        val file = File("image")
        customDataRepository.postNewStory(token, file, "description", "0.0", "0.0")
            .observeForever { actualResponse ->
                Assert.assertNotNull(actualResponse)
                Assert.assertEquals(
                    expectedResponse,
                    actualResponse
                )
            }
    }

    @Test
    fun `when Get List Map Story Response`() = runTest {
        val expectedResponse = dummyStoryResponse
        customDataRepository.getListMapsStory(token).observeForever { actualResponse ->
            Assert.assertNotNull(actualResponse)
            Assert.assertEquals(
                expectedResponse,
                actualResponse
            )
        }
    }

    @Test
    fun `when Get List All Story Response`() = runTest {
        val data: PagingData<ListStoryItem> = StoryPagingSource.snapshot(dummyStory)
        val expectedStory = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStory.value = data

        customDataRepository.getAllStory(token).observeForever {
            val differ = AsyncPagingDataDiffer(
                diffCallback = StoryAdapter.DIFF_CALLBACK,
                updateCallback = noopListUpdateCallback,
                mainDispatcher = mainDispatcherRule.testDispatcher,
                workerDispatcher = mainDispatcherRule.testDispatcher
            )
            CoroutineScope(Dispatchers.IO).launch {
                differ.submitData(it)
            }
            advanceUntilIdle()
            verify(customDataRepository).getAllStory(token)
            Assert.assertNotNull(differ.snapshot())
            Assert.assertEquals(differ.snapshot().size, dummyStory.size)
        }

    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }

}

