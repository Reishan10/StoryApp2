package com.dicoding.storyapp.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.dicoding.storyapp.DataDummy
import com.dicoding.storyapp.MainDispatcherRule
import com.dicoding.storyapp.data.CustomDataRepository
import com.dicoding.storyapp.data.model.response.CreateStoryResponse
import com.dicoding.storyapp.data.model.response.ListStoryItem
import com.dicoding.storyapp.data.model.response.StoryResponse
import com.dicoding.storyapp.getOrAwaitValue
import com.dicoding.storyapp.ui.story.StoryAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var customDataRepository: CustomDataRepository
    private lateinit var mainViewModel: MainViewModel
    private val dummyStory = DataDummy.generateDummyListStoryItem()
    private val dummyStoryResponse = DataDummy.generateDummyStoryResponse()
    private val dummyAddStoryResponse = DataDummy.generateDummyAddStoryResponse()
    private val token =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLUg1Q1RmdUs0Q3ZxNUZLMUIiLCJpYXQiOjE2Njc0NDcyNjZ9.53P9_qZ5Y0ZxShzNsr14EXIaCg9Qfq1sNack8U-cT0s"

    @Before
    fun setUp() {
        mainViewModel = MainViewModel(customDataRepository)
    }

    @Test
    fun `when Get Story Success`() = runTest {
        val emptyData: PagingData<ListStoryItem> = PagingData.empty()
        val expectedEmptyStory = MutableLiveData<PagingData<ListStoryItem>>()
        expectedEmptyStory.value = emptyData

        Mockito.`when`(customDataRepository.getAllStory(token)).thenReturn(expectedEmptyStory)

        val actualEmptyStory: PagingData<ListStoryItem> =
            mainViewModel.getAllStory(token).getOrAwaitValue()

        Mockito.verify(customDataRepository).getAllStory(token)

        Assert.assertNotNull(actualEmptyStory)

        val differEmpty = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differEmpty.submitData(actualEmptyStory)
        advanceUntilIdle()

        Assert.assertEquals(0, differEmpty.snapshot().size)
    }

    @Test
    fun `when Get List Story Maps Success`() = runTest {
        val expectedResponse = MutableLiveData<StoryResponse>()
        expectedResponse.postValue(dummyStoryResponse)
        Mockito.`when`(customDataRepository.getListMapsStory(token)).thenReturn(expectedResponse)

        val actualResponse = mainViewModel.getListMapsStory(token).getOrAwaitValue()

        Mockito.verify(customDataRepository).getListMapsStory(token)
        Assert.assertNotNull(actualResponse)
        Assert.assertEquals(expectedResponse.value, actualResponse)
    }

    @Test
    fun `when Get List Story Maps Fails - Network Error`() = runTest {
        val expectedError = Throwable("Network error")
        Mockito.`when`(customDataRepository.getListMapsStory(token)).thenAnswer { throw expectedError }

        try {
            mainViewModel.getListMapsStory(token).getOrAwaitValue()
            Assert.fail("Exception not thrown")
        } catch (e: Throwable) {
            Assert.assertEquals(expectedError, e)
        }

        Mockito.verify(customDataRepository).getListMapsStory(token)
    }

    @Test
    fun `when Paging Data Loaded Successfully`() = runTest {
        val expectedData: PagingData<ListStoryItem> = PagingData.from(dummyStory)
        val expectedLiveData = MutableLiveData<PagingData<ListStoryItem>>()
        expectedLiveData.value = expectedData

        Mockito.`when`(customDataRepository.getAllStory(token)).thenReturn(expectedLiveData)

        val actualData: PagingData<ListStoryItem> = mainViewModel.getAllStory(token).getOrAwaitValue()

        Mockito.verify(customDataRepository).getAllStory(token)

        Assert.assertNotNull(actualData)

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualData)
        advanceUntilIdle()

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStory.size, differ.snapshot().size)
        Assert.assertEquals(dummyStory[0], differ.snapshot().get(0))
    }


    @Test
    fun `when Paging Data Loading Fails - Network Error`() = runTest {
        val expectedError = Throwable("Network error")
        Mockito.`when`(customDataRepository.getAllStory(token)).thenAnswer { throw expectedError }

        try {
            mainViewModel.getAllStory(token).getOrAwaitValue()
            Assert.fail("Exception not thrown")
        } catch (e: Throwable) {
            Assert.assertEquals(expectedError, e)
        }

        Mockito.verify(customDataRepository).getAllStory(token)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}
