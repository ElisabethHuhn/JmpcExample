package com.huhn.jmpcexample.repository.localDataSource

import androidx.room.Database
import androidx.room.RoomDatabase
import com.huhn.jmpcexample.repository.localDataSource.dbModel.DBWeather

@Database(entities = [DBWeather::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dbWeatherDao(): DBWeatherDao

}