package com.huhn.jmpcexample.repository

import android.location.Location
import android.util.Log
import androidx.room.Room
import com.google.android.gms.location.FusedLocationProviderClient
import com.huhn.jmpcexample.application.JmpcExampleApplication
import com.huhn.jmpcexample.repository.localDataSource.AppDatabase
import com.huhn.jmpcexample.repository.localDataSource.DBWeatherDao
import com.huhn.jmpcexample.repository.localDataSource.dbModel.DBWeather
import com.huhn.jmpcexample.repository.remoteDataSource.RetrofitHelper
import com.huhn.jmpcexample.repository.remoteDataSource.WeatherApiService
import com.huhn.jmpcexample.repository.remoteDataSource.networkModel.WeatherResponse
import com.huhn.jmpcexample.viewmodel.WeatherUIState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface WeatherRepository {
    fun getWeather(
        isByLoc: Boolean,
        latitude: String?,
        longitude: String?,
        city: String?,
        usState: String?,
        country: String?,
    )
}


class WeatherRepositoryImpl : WeatherRepository {
    //local data source variables
    private var db: AppDatabase
    private var dbWeatherDao: DBWeatherDao

    //remote data source variables
    private val weatherApi: WeatherApiService

    val weatherState = MutableStateFlow(WeatherUIState())

    private val _locationState  = MutableStateFlow( LocationState())
    val locationState = _locationState.asStateFlow()

    init {
        /*
         * Create the DAOs corresponding to the db tables
         */
        db = createDb()
        dbWeatherDao = db.dbWeatherDao()

        /*
         * Use RetrofitHelper to create the instance of Retrofit
         * Then use this instance to create an instance of the API
         */
        weatherApi = RetrofitHelper.getInstance().create(WeatherApiService::class.java)
    }

    fun onDisplayWeather(isByLoc: Boolean = false) {
        val lat = weatherState.value.latitude
        val lng = weatherState.value.longitude
        getWeather(
            isByLoc = isByLoc,
            latitude = lat,
            longitude = lng,
            city = weatherState.value.city,
            usState = weatherState.value.usState,
            country = weatherState.value.country,
        )
    }

     override fun getWeather(
         isByLoc: Boolean,
         latitude: String?,
         longitude: String?,
         city: String?,
         usState: String?,
         country: String?,
     ) {
         var lat = 0.0
         try {
             lat = latitude?.toDouble() ?: 0.0
         } catch (_: Exception) { }

         var lng  = 0.0
         try {
             lng = longitude?.toDouble() ?: 0.0
         } catch (_: Exception) { }

        //The Compose UI will recompose when the view-model.weatherResponse changes
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            var weather : WeatherUIState? = null

            if (weatherState.value.city.isEmpty())
            {
                weather =  getWeatherLocal()
            }

            if ((weather == null ) ||
                ( (weatherState.value.city.isEmpty()) && ((lat != 0.0) && (lng != 0.0)) ) ||
                ((weatherState.value.city.isNotEmpty()) && (weather.city != weatherState.value.city))

                ){

                getWeatherRemote(
                    isByLoc = isByLoc,
                    latitude = lat,
                    longitude = lng,
                    city = city,
                    usState = usState,
                    country = country,
                )
            } else {
                onWeatherChanged(weather)
            }
        }
    }

    private suspend fun getWeatherLocal() : WeatherUIState? {
        val scope = CoroutineScope(Dispatchers.IO)
        var weather :DBWeather? = null
        val job = scope.launch {
            weather = dbWeatherDao.findWeatherById(weatherId = 1)
        }
        job.join()

        //convert DB Weather into WeatherUIState
        return weather?.let {
            it.convertToState()
        }
    }

    suspend fun saveWeatherLocal(dbWeather: DBWeather) {

        dbWeatherDao.insertWeather(weather = dbWeather)
//        dbWeatherDao.updateWeather(weather = dbWeather)
    }

    private fun getWeatherRemote(
        isByLoc: Boolean,
        latitude: Double?,
        longitude: Double?,
        city: String?,
        usState: String?,
        country: String?,
//        weatherRemoteCallbackHandler: Callback<WeatherResponse>
    ) {
        val scope = CoroutineScope(Dispatchers.IO)
        val weatherCall : Call<WeatherResponse>? = when {
            (isByLoc &&
                    ((latitude  != null) &&
                            (longitude != null) &&
                            ( (latitude != 0.0) && (longitude != 0.0)))
                    ) -> {
                weatherApi.fetchWeatherLatLngResponseCall(
                    latitude = latitude,
                    longitude = longitude
                )
            }

            (!isByLoc && !city.isNullOrEmpty())-> {
                var cityString = city
                if (!usState.isNullOrEmpty()) cityString = "$cityString,$usState"
                if (!country.isNullOrEmpty()) cityString = "$cityString,$country"
                weatherApi.fetchWeatherCityResponseCall(cityLocation = cityString)
            }

             else -> null
        }

        weatherCall?.let {
            scope.launch {
                //Initiate the remote call, with the passed callback to handle the remote response
                it.enqueue(weatherRemoteCallbackHandler)
            }
        }

    }

    //Create the callback object that will parse the response and
    // actually fill the weather variable
    //Define the code to execute upon request return
    private val weatherRemoteCallbackHandler = object : Callback<WeatherResponse> {
        val TAG = "weatherCallBackHandler"

        override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
            val errorMsg = "Failure Return from network call"
            Log.e(TAG, errorMsg, t)
            onErrorChanged(errorMsg)
        }

        override fun onResponse(
            call: Call<WeatherResponse>,
            response: Response<WeatherResponse>
        ) {
            Log.d(TAG, "Response Received")

            try {
                val responseCode = response.code()
                // debug information
                val responseMessage = response.message()
                val responseIsSuccessful = response.isSuccessful
                val responseHeaders = response.headers()
                val responseErrorBody = response.errorBody()
                val responseDebug =
                    "code = $responseCode, isSuccessful = $responseIsSuccessful, message = $responseMessage, headers = $responseHeaders, error = $responseErrorBody"
                Log.d(TAG, responseDebug)
                // end debug info

                when (responseCode) {
                    200 -> {
                        onErrorChanged("")
                        val weatherResponse = response.body()
                        weatherResponse?.let {
                            //insert the weather into the local DB
                            val dbWeather = it.convertToDB()
                            val outerScope = CoroutineScope(Dispatchers.IO)
                            outerScope.launch {
                                saveWeatherLocal(dbWeather = dbWeather)
                            }
                            onWeatherChanged(dbWeather.convertToState())
//                            onWholeStateChanged(dbWeather = dbWeather)
                        }
                    }
                    else -> {
                        val errorMsg = "Error response code $responseCode received on API call." /*Error = $responseErrorBody*/
                        Log.e(TAG, errorMsg)
                        onErrorChanged(errorMsg)
                    }
                }

            } catch (e: Exception) {
                val errorMsg = "Exception caught accessing response ${e.message}"
                Log.e(TAG,errorMsg)
                onErrorChanged(errorMsg)
            }
        }
    }

    private  fun onWeatherChanged(weather: WeatherUIState){
        weatherState.value = weather
    }


    fun onInitializedChanged(data : Boolean){
        weatherState.update { it.copy(isInitialized = data) }
    }
    fun onCityChanged(data: String) {
        weatherState.update { it.copy(city = data) }
    }
    fun onUsStateChanged(data: String) {
        weatherState.update { it.copy(usState = data) }
    }
    fun onCountryChanged(data: String) {
        weatherState.update { it.copy(country = data) }
    }
    fun onLatitudeChanged(data: String) {
        weatherState.update { it.copy(latitude = data) }
    }
    fun onLongitudeChanged(data: String) {
        weatherState.update { it.copy(longitude = data) }
    }
    fun onErrorChanged(data: String) {
        weatherState.update { it.copy(errorMsg = data) }
    }



    fun onLocationChanged(location: Location?) {
        onLocLatitudeChanged(data = location?.latitude.toString() )
        onLocLongitudeChanged(data = location?.longitude.toString() )
    }
    private fun onLocLatitudeChanged(data: String) {
        _locationState.update { it.copy(locLatitude = data) }
    }
    private fun onLocLongitudeChanged(data: String) {
       _locationState.update { it.copy(locLongitude = data) }
    }
    fun onGetLocation() {
        onLatitudeChanged(data = locationState.value.locLatitude)
        onLongitudeChanged(data = locationState.value.locLongitude)
    }

    fun onInitialization() {
        weatherState.update { it.copy(
            city = "Atlanta",
            usState = "Georgia",
            country = "USA",
            latitude = "34.xxx",
            longitude = "-84.9999",
            description = "Nice weather today",
            icon = "some_url",
            temp = "73",
            feelsLike = "80",
            tempMax = "99",
            tempMin = "65",
            dewTemp = "70",
            clouds = "50",
            sunrise = "1000",
            sunset = "2200"
        ) }
    }

    private fun createDb() : AppDatabase {
        val appContext = JmpcExampleApplication.appContext

        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java, "weather-database"
        ).build()
    }
}

data class LocationState(
    val location: Location? = null,
    val locLatitude : String = "0.0",
    val locLongitude: String = "0.0",
    val fusedClient : FusedLocationProviderClient? = null,
)
