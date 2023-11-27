# JPMC Example App

# Introduction
This project is build for an Android Developer job interview with JPMC. 

A note on naming. I'd love to tell you that the misspelling of JMPC 
is on purpose so that others won't find my public repository with a simple Google search. 
But the fact is that I'm very dyslexic and I mix up letters all the time. 
However when it came time to correct the misspelling, I realized the google search was a real 
problem, So I didn't correct it.

The project is an example of State of the Art Android Architecture circa 2023. It uses:
* Kotlin 100%
* Koin for Dependency Injection
    * For now, the only classes that are injected are the ViewModel and Repository
* Compose for building UI
    * Also uses Compose NavGraph navigation
* Retrofit for Network calls
* Coil for loading images from network URLs
* Accompanist for permissions
* FusedLocationProvider from GooglePlayServices for location management
* MVVM / MVI to
    * persist UI local cache across orientation changes
    * UI state variables governing UI compose
      * Enable state hoisting
    * business logic to calculate values for UI display (i.e. state)
    * business logic for actions in response to UI events
* Repository to separate business logic from Data Source
    * Examples of both localDataSource and remoteDataSource
    * localDataSource is ROOM DB
    * remoteDataSource is RetroFit2
* Use Flows to move UI state data back to UI compose (via the ViewModel) from Repository
* Coroutines for both serial and parallel structured concurrency
* Unit Testing
  * Retrofit unit tests with test/java/com.huhn.jmpcexample/
  * DB instrumented test with androidTest/java/com.huhn.jmpcexample/DBInstrumentedTest
    * These need to run on a real device
    * best resource for room DB testing https://medium.com/@wambuinjumbi/unit-testing-in-android-room-361bf56b69c5
  * Compose UI testing
    * reference https://developer.android.com/codelabs/jetpack-compose-testing#0

# Requirements
Build a sample app using State of the Art Architectural components

* Use API at at openweathermap.org
* Open Weather supplied both geocodes and City/State/Country interface, so I didn't use Geocoder API
* The user can request weather for a location by
  * city, city+state, city+state+country 
  * latitude / longitude
  * current location (user must grant location permissions)
* The resulting weather description with icon is displayed
  * There are also some rudimentary weather details such as temperature in degrees C
* By default, the last location viewed (if such exists) is displayed at app startup

# Architecture Discussion
* The architecture uses MVVM, flirting with MVI. As I find the word "intent" confusing because of the Android System use of the work, I call them User Events.
* There is only one Screen, but I left the "stuff" of navigation in, because almost all apps eventually have more than one screen
* The UI is truly basic. But more can be added to the screen as desired by Product
* The ViewModel is sparse to the point of non-existent. All of it's functionality migrated to the repository. But I left the ViewModel in as a placeholder. Eventually, it will have content as the app matures.

# UI Testing Script
This would be a full smoke test, and is longer than I actually built. I only implemented the City success and City fail.
* on app startup
  * Asks for location permission
  * Last location weather is displayed by default
* Error return from City API
  * Enter partial city name e.g. Bos
  * Entered value for TextFields is the value displayed for that field
  * trigger Weather by City
  * Error displays
* Success return from City API
  * Enter correct city name e.g. Boston
  * trigger Weather by City
  * Error is hidden
  * Weather is displayed
  * lat / lng is filled in
* Multiple tests for full City API
  * City / City+State / City+Country / City+State+Country
  * Entered value is the displayed value
  * trigger weather by city
  * weather displays
* Test lat / lng API
  * Enter lat / lng
  * Entered value is the displayed value
  * trigger weather by location
    * Weather is displayed
    * City is filled in


