package com.huhn.jmpcexample.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import com.huhn.jmpcexample.repository.WeatherRepositoryImpl
import kotlinx.coroutines.flow.asStateFlow


class WeatherViewModelImpl(private val repo : WeatherRepositoryImpl) : ViewModel()
{
    //Define state for recomposing UI
    val weatherState = repo.weatherState.asStateFlow()

    //Define responses to User Trigger Events
    fun onWeatherUserEvent (event: WeatherUserEvent){
        when(event) {
            is WeatherUserEvent.OnInitializeWeatherEvent -> repo.onInitialization()
            is WeatherUserEvent.OnShowHideDetailsChanged -> repo.onInitializedChanged(event.data)
            is WeatherUserEvent.OnFetchLocation -> repo.onLocationChanged(event.data)
            is WeatherUserEvent.OnCityEvent -> repo.onCityChanged(event.data ?: "")
            is WeatherUserEvent.OnUsStateEvent -> repo.onUsStateChanged(event.data ?: "")
            is WeatherUserEvent.OnCountryEvent -> repo.onCountryChanged(event.data ?: "")
            is WeatherUserEvent.OnDisplayWeatherEvent -> repo.onDisplayWeather(event.isByLoc)
            is WeatherUserEvent.OnGetLocation -> repo.onGetLocation()
            is WeatherUserEvent.OnLatitudeEvent -> repo.onLatitudeChanged(event.data )
            is WeatherUserEvent.OnLongitudeEvent -> repo.onLongitudeChanged(event.data )
        }
    }

    //Define functions that update state values


    //define functions that fetch remote data

}

data class WeatherUIState(
    var isInitialized: Boolean = false,
    val city : String = "",
    val usState : String = "",
    val country : String = "",
    val latitude : String = "0.0",
    val longitude: String = "0.0",
    val description : String = "",
    val weatherStateId : String = "",
    val icon : String = "",
    val temp : String = "0.0",
    val feelsLike : String = "0.0",
    val tempMax: String = "0.0",
    val tempMin : String = "0.0",
    val dewTemp : String = "0.0",
    val clouds: String = "",
    val sunrise : String = "",
    val sunset: String = "",
    val errorMsg: String = "",
)

sealed interface WeatherUserEvent {
    object OnInitializeWeatherEvent : WeatherUserEvent
    data class OnShowHideDetailsChanged(val data: Boolean = false) : WeatherUserEvent
    data class OnFetchLocation(val data: Location?) : WeatherUserEvent
    data class OnCityEvent(val data: String?) : WeatherUserEvent
    data class OnUsStateEvent (val data: String?) : WeatherUserEvent
    data class OnCountryEvent (val data: String?) : WeatherUserEvent
    data class OnDisplayWeatherEvent(val isByLoc: Boolean = false) : WeatherUserEvent
    object OnGetLocation : WeatherUserEvent
    data class OnLatitudeEvent (val data: String): WeatherUserEvent
    data class OnLongitudeEvent (val data: String): WeatherUserEvent
}