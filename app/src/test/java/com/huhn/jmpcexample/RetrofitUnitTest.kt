package com.huhn.jmpcexample

import com.huhn.jmpcexample.repository.remoteDataSource.RetrofitHelper
import com.huhn.jmpcexample.repository.remoteDataSource.WeatherApiService
import org.junit.Test
import retrofit2.Retrofit

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

class RetrofitClientTest {
    @Test
    fun testRetrofitInstance() {
        val baseUrl = "https://api.openweathermap.org/data/2.5/"
        //Get an instance of Retrofit
        val instance: Retrofit = RetrofitHelper.getInstance()
        //Assert that, Retrofit's base url matches to our BASE_URL
        assert(instance.baseUrl().toUrl().toString() == baseUrl)
    }

    @Test
    fun testFetchWeatherByLocation () {
        val weatherApi = RetrofitHelper.getInstance().create(WeatherApiService::class.java)
        val latitude = 33.749
        val longitude = -84.388
        val weatherLocCall =
            weatherApi.fetchWeatherLatLngResponseCall(
                latitude = latitude,
                longitude = longitude
            )
        //execute the api call and get a response
        val locResponse = weatherApi.fetchWeatherLatLngResponseCall(
            latitude,
            longitude,
            BuildConfig.weatherApiKey
        ).execute()
        //Check for error body
        val locErrorBody = locResponse.errorBody()
        assert(locErrorBody == null)
        //Check for success body
        val locResponseWrapper = locResponse.body()
        assert(locResponseWrapper != null)
        assert(locResponse.code() == 200)
    }
    @Test
    fun testFetchWeatherByCity () {
        val weatherApi = RetrofitHelper.getInstance().create(WeatherApiService::class.java)

        val cityString = "Atlanta"
        val weatherCityCall=
            weatherApi.fetchWeatherCityResponseCall(cityLocation = cityString)
        //execute the api call and get a response
        val cityResponse = weatherApi.fetchWeatherCityResponseCall(cityString, BuildConfig.weatherApiKey).execute()
        //Check for error body
        val cityErrorBody = cityResponse.errorBody()
        assert(cityErrorBody == null)
        //Check for success body
        val cityResponseWrapper = cityResponse.body()
        assert(cityResponseWrapper != null)
        assert(cityResponse.code() == 200)
    }
}