package com.example.weatherapp.domain.repository

interface RequestRepository {
    fun requestWeatherData(city: String)
}