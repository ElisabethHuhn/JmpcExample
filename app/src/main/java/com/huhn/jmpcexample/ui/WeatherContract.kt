package com.huhn.jmpcexample.ui

import android.location.Location


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