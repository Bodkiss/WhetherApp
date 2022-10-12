package com.example.weatherapp.domain.useCase

import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapp.domain.repository.RequestRepository
import com.example.weatherapp.presentation.API_KEY

class RequestUseCase(private val requestRepository: RequestRepository) {

    fun executeRequestWeatherData(city: String) {
        requestRepository.requestWeatherData(city)
    }
}