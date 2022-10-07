package com.example.weatherapp.domain.repository

import com.example.weatherapp.data.WeatherModel
import org.json.JSONObject

interface RepositoryParse {
    fun parseDays(mainObject: JSONObject): List<WeatherModel>
    fun parseCurrentData(mainObject: JSONObject, weatherItem: WeatherModel)
}