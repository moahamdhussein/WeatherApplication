package com.example.weatherapplication.localDataSource

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.weatherapplication.model.FavouriteCountries
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class TestDao {
    lateinit var database: WeatherDatabase
    lateinit var dao: CountriesDao


    @Before
    fun setUpDataBase() {
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            WeatherDatabase::class.java
        ).build()
        dao = database.getCountriesDao()
    }


    @Test
    fun insertAlarmTest() = runTest {
        // given the input to database
        val favouriteCountries = FavouriteCountries(0.0, 0.0, "test", "Alarm", "2020", 0)

        // when we call insert that take the input and add it to database
        dao.insertCountry(favouriteCountries)


        // then return all list in database and check if list contain this input the return must be false
        val loaded = dao.getAllAlarm().first()
        assertThat(loaded.contains(favouriteCountries), `is`(true))

    }

    @Test
    fun deleteItem() = runTest {
        // given the input to database
        val favouriteCountries = FavouriteCountries(0.0, 0.0, "test", "Alarm", "2020", 0)
        dao.insertCountry(favouriteCountries)

        // when we call delete that take the input and delete it from database
        dao.deleteCountry(favouriteCountries)

        val loaded = dao.getAllAlarm().first()

        // then return all list in database and check if list contain this input the return must be false
        assertThat(loaded.contains(favouriteCountries), `is`(false))
    }


    @Test
    fun getAllAlarm() = runTest {
        // given the input to database
        val favouriteCountries = FavouriteCountries(0.0, 0.0, "test", "Alarm", "2020", 0)
        val copyObject = favouriteCountries.copy(type = "favourite")
        dao.insertCountry(favouriteCountries)

        // when we call delete that take the input should contain alarmObject
        val loaded = dao.getAllAlarm().first()

        // then return all list in database and check if list contain this input the return must be true
        // when we update object to type = favourite the return must be false
        assertThat(loaded.contains(favouriteCountries), `is`(true))
        assertThat(loaded.contains(copyObject), `is`(false))
    }

    @Test
    fun getAllFavourite() = runTest {
        // given the input to database
        val favouriteCountries = FavouriteCountries(0.0, 0.0, "test", "Favourite", "2020", 0)
        val copyObject = favouriteCountries.copy(type = "Alarm")
        dao.insertCountry(favouriteCountries)

        // when we call delete that take the input should contain alarmObject
        val loaded = dao.getAllFavouriteCountries().first()

        // then return all list in database and check if list contain this input the return must be true
        // when we update object to type = favourite the return must be false
        assertThat(loaded.contains(favouriteCountries), `is`(true))
        assertThat(loaded.contains(copyObject), `is`(false))
    }

    @After
    fun closeDatabase() = database.close()


}