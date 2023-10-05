# JMPC Example App

# Introduction
This project is build for a job interview with JMPC. It is an example of State of the Art Android Architecture circa 2023. It uses:
* Kotlin 100%
* Koin for Dependency Injection
    * For now, the only classes that are injected are the ViewModel and Repository
* Compose for building UI
    * Also uses Compose NavGraph navigation
* Retrofit for Network calls
* MVVM / MVI to
    * persist UI local cache across orientation changes
    * UI state variables governing UI compose
    * business logic to calculate values for UI display (i.e. state)
    * business logic for actions in response to UI events
    * 
* Repository to separate business logic from Data Source
    * Examples of both localDataSource and remoteDataSource
    * localDataSource is ROOM DB
    * remoteDataSource is RetroFit2
* Use Flows to move lists back to UI compose (via the ViewModel) from Repository
* Coroutines for both serial and parallel structured concurrency
* Unit Testing

# Requirements
Build a sample app using State of the Art Architectural components

# Architecture Discussion
* some words around what the architecture is, what the alternatives were, and why I made the choices I did
