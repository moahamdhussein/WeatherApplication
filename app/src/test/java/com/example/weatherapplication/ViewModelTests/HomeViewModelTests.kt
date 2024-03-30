package com.example.weatherapplication.ViewModelTests

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherapplication.MainCoroutineRule
import com.example.weatherapplication.home.HomeViewModel
import com.example.weatherapplication.model.Root
import com.example.weatherapplication.repoTests.FakeRepo
import com.example.weatherapplication.repository.IWeatherRepository
import com.example.weatherapplication.utility.ApiState
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.observeOn
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resumeDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.notNullValue


private const val TAG = "HomeViewModelTests"


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class HomeViewModelTests {

    lateinit var viewModel: HomeViewModel
    lateinit var repo: FakeRepo

    /* @get:Rule
     val mainCoroutineRule = MainCoroutineRule()*/
//    @get:Rule
//    var instanceExecutorRule = InstantTaskExecutorRule()
    @Before
    fun setUp() {
        repo = FakeRepo()
        viewModel = HomeViewModel(repo)
//        viewModel.getWeatherStatus(lat, long, language)
    }

    @Test
    fun getWeatherStatus() = runTest {
        // Given
        val fakeRepo = FakeRepo()
        val viewModel = HomeViewModel(fakeRepo)
        val lat = 0.0
        val lon = 0.0
        val language = "en"

        // When
        viewModel.getWeatherStatus(lat, lon, language)

        // Then
        val result = viewModel.weatherStatus.first()

        result as ApiState.Success

        assertThat(result.root.city?.coord?.lat, `is`(0.0))
        // Add more assertions based on the expected behavior
    }
}
