package com.example.weatherapplication.ViewModelTests

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherapplication.MainCoroutineRule
import com.example.weatherapplication.favouriteList.FavouriteViewModel
import com.example.weatherapplication.model.FavouriteCountries
import com.example.weatherapplication.repoTests.FakeRepo
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavouriteViewModelTest {

    lateinit var viewModel: FavouriteViewModel
    lateinit var repo: FakeRepo

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
        repo = FakeRepo()
        viewModel = FavouriteViewModel(repo)
    }

    @Test
    fun GetAllAlarmTest() = runTest {
        // given add inputs to fake repo to test
        val favouriteCountries = FavouriteCountries(0.0, 0.0, "test", "Favourite", "2020", 0)
        repo.insertCountry(favouriteCountries)


        // when we call weatherStatus the return will be first item list the contain list from database
        val result = viewModel.weatherStatus.first()

        // then the result should bbe the same to the input and matches every element
        MatcherAssert.assertThat(result.contains(favouriteCountries), Matchers.`is`(true))
        MatcherAssert.assertThat(result[0].alarmId, Matchers.`is`(0))
        MatcherAssert.assertThat(result[0].type == "Favourite", Matchers.`is`(true))

    }
    @Test
    fun DeleteAlarmItemTest() = runTest {
        // given add inputs to fake repo to test
        val favouriteCountry = FavouriteCountries(0.0, 0.0, "test", "Favourite", "2020", 0)

        repo.insertCountry(favouriteCountry)

        // when we call weatherStatus the return will be first item list the contain list from database
        viewModel.deleteFavouriteCountry(favouriteCountry)

        //then the result should be not contain the input
        val result = viewModel.weatherStatus.first()
        MatcherAssert.assertThat(result.contains(favouriteCountry), Matchers.`is`(false))
    }

}