package com.huhn.jmpcexample

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.huhn.jmpcexample.repository.localDataSource.AppDatabase
import com.huhn.jmpcexample.repository.localDataSource.DBWeatherDao
import com.huhn.jmpcexample.repository.localDataSource.dbModel.DBWeather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.runBlocking
import org.junit.After

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import java.util.concurrent.CountDownLatch

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

@RunWith(AndroidJUnit4::class)
@SmallTest
class WeatherDBTest {
    private lateinit var database : AppDatabase
    private lateinit var weatherDAO : DBWeatherDao
    @Before
    fun setupDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        weatherDAO = database.dbWeatherDao()
    }

    @Test
    fun insertWeather_returnsTrue() = runBlocking {
        val weather = DBWeather(
            id = 1,
            city = "Washington",
            state = "DC" ,
            country = "US",
            lat = 0.0,
            lng = 0.0,
            description = "Nice weather today",
            icon = "",
            weatherStateId = "",
            temp = 275.0,
            feelslike = 280.6,
            tempmax = 290.8,
            tempmin = 260.8,
            dewtemp = 99,
            clouds = 20,
            sunrise = 700,
            sunset = 1700,
        )
        weatherDAO.insertWeather(weather)

        val latch = CountDownLatch(1)
        val job = async(Dispatchers.IO) {
            val retrievedWeather = weatherDAO.findWeatherById(1)
            assertEquals(weather, retrievedWeather)
                latch.countDown()
            }

        latch.await()
        job.cancelAndJoin()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.huhn.jmpcexample", appContext.packageName)
    }
}