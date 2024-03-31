package com.example.weatherapplication.ViewModelTests

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherapplication.MainCoroutineRule
import com.example.weatherapplication.alarm.AlarmViewModel
import com.example.weatherapplication.model.FavouriteCountries
import com.example.weatherapplication.repoTests.FakeRepo
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class AlarmViewModel {

    lateinit var viewModel: AlarmViewModel
    lateinit var repo: FakeRepo

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
        repo = FakeRepo()
        viewModel = AlarmViewModel(repo)
    }

    @Test
    fun GetAllAlarmTest() = runTest {
        // given add inputs to fake repo to test
        val alarmCountry = FavouriteCountries(0.0, 0.0, "test", "Alarm", "2020", 0)
        repo.insertCountry(alarmCountry)


        // when we call weatherStatus the return will be first item list the contain list from database
        val result = viewModel.weatherStatus.first()

        assertThat(result.contains(alarmCountry), `is`(true))
        assertThat(result[0].alarmId,`is`(0))
        assertThat(result[0].type == "Alarm",`is`(true))

    }
    @Test
    fun DeleteAlarmItemTest() = runTest {
        // given add inputs to fake repo to test
        val alarmCountry = FavouriteCountries(0.0, 0.0, "test", "Alarm", "2020", 0)
        repo.insertCountry(alarmCountry)

        // when we call weatherStatus the return will be first item list the contain list from database
         viewModel.deleteAlarmItem(alarmCountry)

        //then the result should be not contain the input
        val result = viewModel.weatherStatus.first()
        assertThat(result.contains(alarmCountry), `is`(false))
    }
}