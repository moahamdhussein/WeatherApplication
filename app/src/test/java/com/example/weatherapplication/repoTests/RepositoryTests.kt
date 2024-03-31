package com.example.weatherapplication.repoTests

import com.example.weatherapplication.DataSource.FakeLocalDataSource
import com.example.weatherapplication.DataSource.FakeRemoteDataSource
import com.example.weatherapplication.model.FavouriteCountries
import com.example.weatherapplication.repository.WeatherRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Test

class RepositoryTests {

    // run test individually


    lateinit var fakeLocalDataSource: FakeLocalDataSource
    lateinit var fakeRemoteDataSource: FakeRemoteDataSource
    lateinit var repo: WeatherRepository

    @Before
    fun setUp() {
        fakeLocalDataSource = FakeLocalDataSource()
        fakeRemoteDataSource = FakeRemoteDataSource()
        repo = WeatherRepository.getInstance(
            fakeRemoteDataSource,
            fakeLocalDataSource,
        )
    }

    @Test
    fun test_insertAlarmItem() = runTest {
        //given object from Country with type Alarm ti insert it
        val favouriteCountries =
            FavouriteCountries(0.0, 0.0, "alex", "Alarm", "2020", 10)

        // when we call insert that mean to insert this item in local database
        repo.insertCountry(favouriteCountries)

        // then the return value is the same of expected value with type = Alarm
        assertThat(repo.getAllAlarm().first().contains(favouriteCountries), `is`(true))
        assertThat(
            repo.getAllAlarm().first()[0].type.equals(
                favouriteCountries.type,
                ignoreCase = true
            ), `is`(true)
        )
        assertThat(repo.getAllAlarm().first()[0].alarmId, `is`(10))

    }

    @Test
    fun test_getAllAlarmItem() = runTest {
        //given object from Country with type Alarm ti insert it
        val favouriteCountries1 = FavouriteCountries(0.0, 0.0, "alex", "Alarm", "2020", 10)
        val favouriteCountries2 = FavouriteCountries(2.0, 0.0, "alex", "Alarm", "2020", 10)
        val favouriteCountries3 = FavouriteCountries(3.0, 0.0, "alex", "Alarm", "2020", 10)
        repo.insertCountry(favouriteCountries1)
        repo.insertCountry(favouriteCountries2)
        repo.insertCountry(favouriteCountries3)

        // when we call getFavouriteCountries to get All Alarm
        val res = repo.getAllAlarm().first()


        // then the return value is the same of expected value with list size  = 3
        assertThat(res.size, `is`(3))
    }

    @Test
    fun test_getAllFavouriteItem() = runTest {
        //given object from Country with type Favourite ti insert it
        val favouriteCountries1 = FavouriteCountries(0.0, 0.0, "alex", "Favourite", "2020", 10)
        val favouriteCountries2 = FavouriteCountries(2.0, 0.0, "alex", "Favourite", "2020", 10)
        val favouriteCountries3 = FavouriteCountries(3.0, 0.0, "alex", "Favourite", "2020", 10)
        repo.insertCountry(favouriteCountries1)
        repo.insertCountry(favouriteCountries2)
        repo.insertCountry(favouriteCountries3)


        // when we call getFavouriteCountries to get All favourite country
        val res = repo.getFavouriteCountries().first()

        // then the return value is the same of expected value with list size = 3
        assertThat(res.size, `is`(3))
    }

    @Test
    fun test_deleteItem() = runTest {
        //given object from Country with type Alarm ti insert it
        val favouriteCountries1 = FavouriteCountries(0.0, 0.0, "alex", "Favourite", "2020", 10)
        val favouriteCountries2 = FavouriteCountries(2.0, 0.0, "alex", "Alarm", "2020", 10)
        val favouriteCountries3 = FavouriteCountries(3.0, 0.0, "alex", "Favourite", "2020", 10)
        repo.insertCountry(favouriteCountries1)
        repo.insertCountry(favouriteCountries2)
        repo.insertCountry(favouriteCountries3)


        // when we call delete that mean to delete this item in local database and decrease size of list
        repo.deleteCountry(favouriteCountries1)


        // then the return value is the same of expected value with type = 2
        assertThat(repo.getFavouriteCountries().first().size, `is`(2))
    }

    @Test
    fun getWeatherDataTestSuccess(): Unit = runBlocking {
        //given input param for method
        val lat = 0.0
        val lon = 0.0
        val language = "en"

        // when we call getWeatherDetails the return value will be success with code 200
        val res = repo.getWeatherDetails(0.0, 0.0, "en")

        // then check the code of success response
        assertThat(res.first().code(), `is`(200))

    }


}