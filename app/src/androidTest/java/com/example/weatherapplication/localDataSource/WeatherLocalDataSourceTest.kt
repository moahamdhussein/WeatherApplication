package com.example.weatherapplication.localDataSource


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.weatherapplication.model.FavouriteCountries
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class WeatherLocalDataSourceTest {
    @get:Rule
    var instanceExecutorRule = InstantTaskExecutorRule()

    lateinit var localDataSource: WeatherLocalDataSource
    lateinit var database: WeatherDatabase
    lateinit var dao: CountriesDao
    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        dao = database.getCountriesDao()
        localDataSource = WeatherLocalDataSource(dao)
    }
    @Test
    fun insertAlarmItem() = runTest {
        // given input one alarm object with type alarm
        val alarmItem = FavouriteCountries(0.0, 0.0, "alex", "Alarm", "23025", 150)

        // When we call insert dao take input and insert it in database
        localDataSource.insertCountry(alarmItem)

        // then we get first element and check validation of data and is should be true
        val loadData = localDataSource.getAllAlarm().first()[0]
        assertThat(loadData, `is`(alarmItem))
        assertThat(loadData.latitude, `is`(0.0))
        assertThat(loadData.longitude, `is`(0.0))
        assertThat(loadData.type == "Alarm", `is`(true))


    }

    @Test
    fun deleteAlarmItem() = runTest {
        // given input one alarm object with type alarm
        val alarmItem = FavouriteCountries(0.0, 0.0, "alex", "Alarm", "23025", 150)
        localDataSource.insertCountry(alarmItem)

        // When we call delete dao take input and delete it in database
        localDataSource.deleteCountry(alarmItem)

        // then we get first element and check validation of data and is should be true
        val loadData = localDataSource.getAllAlarm().first()
        assertThat(loadData.contains(alarmItem), `is`(false))

    }

    @Test
    fun getAllAlarmItem() = runTest {
        // given input 4 alarm object with type alarm and 1 with type Favourite
        val alarmItem1 = FavouriteCountries(0.0, 0.0, "alex", "Alarm", "23025", 150)
        val alarmItem2 = FavouriteCountries(1.0, 0.0, "alex", "Alarm", "23025", 150)
        val alarmItem3 = FavouriteCountries(2.0, 0.0, "alex", "Alarm", "23025", 150)
        val alarmItem4 = FavouriteCountries(3.0, 0.0, "alex", "Alarm", "23025", 150)
        val alarmItem5 = FavouriteCountries(4.0, 0.0, "alex", "Favourite", "23025", 150)
        localDataSource.insertCountry(alarmItem1)
        localDataSource.insertCountry(alarmItem2)
        localDataSource.insertCountry(alarmItem3)
        localDataSource.insertCountry(alarmItem4)
        localDataSource.insertCountry(alarmItem5)

        //when get All item that save with type Alarm
        val alarmData = localDataSource.getAllAlarm().first()
        val favouriteData = localDataSource.getStoredFavouriteCountries().first()

// then we get first element and check validation of data and is should be favourite list size = 4  and alarm size  = 1
        assertThat(alarmData.size, `is`(4))
        assertThat(favouriteData.size, `is`(1))

    }

    @Test
    fun getAllFavouriteItem() = runTest {
        // given input 1 alarm object with type alarm and 4 with type Favourite
        val alarmItem1 = FavouriteCountries(0.0, 0.0, "alex", "Favourite", "23025", 150)
        val alarmItem2 = FavouriteCountries(1.0, 0.0, "alex", "Favourite", "23025", 150)
        val alarmItem3 = FavouriteCountries(2.0, 0.0, "alex", "Favourite", "23025", 150)
        val alarmItem5 = FavouriteCountries(3.0, 0.0, "alex", "Favourite", "23025", 150)
        val alarmItem4 = FavouriteCountries(5.0, 0.0, "alex", "Alarm", "23025", 150)
        localDataSource.insertCountry(alarmItem1)
        localDataSource.insertCountry(alarmItem2)
        localDataSource.insertCountry(alarmItem3)
        localDataSource.insertCountry(alarmItem4)
        localDataSource.insertCountry(alarmItem5)

        //when get All item that save with type Alarm
        val favouriteData = localDataSource.getStoredFavouriteCountries().first()
        val alarmData = localDataSource.getAllAlarm().first()

        // then we get first element and check validation of data and is should be favourite list size = 4  and alarm size  = 1
        assertThat(favouriteData.size, `is`(4))
        assertThat(alarmData.size, `is`(1))

    }

    @After
    fun closeDatabase() = database.close()
}