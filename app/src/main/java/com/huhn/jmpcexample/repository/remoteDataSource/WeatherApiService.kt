package com.huhn.jmpcexample.repository.remoteDataSource

// Retrofit interface
import com.huhn.jmpcexample.BuildConfig
import com.huhn.jmpcexample.repository.remoteDataSource.networkModel.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

const val appKey = BuildConfig.weatherApiKey


//const val sampleUrl = "https://api.openweathermap.org/data/2.5/weather?q=Atlanta,USA&appid=76122ae092e6258c8853080710af3925"

interface WeatherApiService {
    //The secret key must be fetched from BuildConfig, but it is stored in the local properties file
    //The Google Gradle Secrets library moves it from local.properties to the BuildConfig
    //https://github.com/google/secrets-gradle-plugin

    @GET("weather")
    fun fetchWeatherCityResponseCall(
        @Query("q") cityLocation : String,
        @Query("appid")  apiKey: String = appKey
    ) : Call<WeatherResponse>

    @GET("weather")
    fun fetchWeatherLatLngResponseCall(
        @Query("lat")  latitude: Double,
        @Query("lon")  longitude: Double,
        @Query("appid")  apiKey: String = appKey
    ) : Call<WeatherResponse>
}
