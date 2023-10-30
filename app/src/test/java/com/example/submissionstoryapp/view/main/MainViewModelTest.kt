package com.example.submissionstoryapp.view.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.submissionstoryapp.DataDummy
import com.example.submissionstoryapp.MainDispatcherRule
import com.example.submissionstoryapp.data.repo.StoryRepository
import com.example.submissionstoryapp.data.repo.UserRepository
import com.example.submissionstoryapp.data.response.ListStoryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var userRepository: UserRepository
    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        userRepository = mock(UserRepository::class.java)
        storyRepository = mock(StoryRepository::class.java)
    }

    @Test
    fun `when Get Quote Should Not Null and Return Data`() = runTest {
        val dummyQuote = DataDummy.generateDummyStoryResponse()
        val data: PagingData<ListStoryItem> = QuotePagingSource.snapshot(dummyQuote)
        val expectedQuote = MutableLiveData<PagingData<ListStoryItem>>()
        expectedQuote.value = data
        Mockito.`when`(storyRepository.getStoryPaging()).thenReturn(expectedQuote)

        val mainViewModel = MainViewModel(userRepository,storyRepository)
        val actualQuote: PagingData<ListStoryItem> = mainViewModel.story.value!!

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DiffCallback,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        if (actualQuote != null) {
            differ.submitData(actualQuote)
        }

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyQuote.size, differ.snapshot().size)
        Assert.assertEquals(dummyQuote[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Quote Empty Should Return No Data`() = runTest {
        val data: PagingData<ListStoryItem> = PagingData.from(emptyList())
        val expectedQuote = MutableLiveData<PagingData<ListStoryItem>>()
        expectedQuote.value = data
        Mockito.`when`(storyRepository.getStoryPaging()).thenReturn(expectedQuote)
        val mainViewModel = MainViewModel(userRepository,storyRepository)
        val actualQuote: PagingData<ListStoryItem> = mainViewModel.story.value!!
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DiffCallback,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualQuote)
        Assert.assertEquals(0, differ.snapshot().size)
    }
}

class QuotePagingSource : PagingSource<Int, LiveData<List<ListStoryItem>>>() {
    companion object {
        fun snapshot(items: List<ListStoryItem>): PagingData<ListStoryItem> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<ListStoryItem>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ListStoryItem>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}