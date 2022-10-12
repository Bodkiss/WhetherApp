package com.example.weatherapp.data.repository

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapp.domain.repository.RequestRepository
import com.example.weatherapp.presentation.API_KEY

class RequsestDataRepositoryImpl(private val context: Context):RequestRepository {
    override fun requestWeatherData(city: String) {
        val url = "https://api.weatherapi.com/v1/forecast.json?key=" +
                API_KEY +
                "&q=" +
                city +
                "&days=" +
                "3" +
                "&aqi=no&alerts=no"
        val queue = Volley.newRequestQueue(context)
        val request = StringRequest(
            Request.Method.GET,
            url,
            { result ->
                // parseWeatherData(result)

            },
            { error ->
                Log.e("MyLog", "Error: $error")
            }
        )
        queue.add(request)
    }
}