package com.example.weatherapplication.localDataSource

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapplication.model.FavouriteCountries
import kotlinx.coroutines.flow.Flow


@Dao
interface FavouriteDao {

    @Query("SELECT * FROM favourite_countries Where type like'Favourite' ")
     fun getAllFavouriteCountries(): Flow<List<FavouriteCountries>>

     @Query("SELECT * FROM favourite_countries Where type like'Alarm' ")
     fun getAllAlarm(): Flow<List<FavouriteCountries>>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProduct(favouriteCounty: FavouriteCountries)

    @Delete
    suspend fun deleteProduct(favouriteCounty: FavouriteCountries)
}