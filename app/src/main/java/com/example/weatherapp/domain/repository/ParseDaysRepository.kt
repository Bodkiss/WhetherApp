package com.example.weatherapp.domain.repository

import com.example.weatherapp.data.WeatherModel
import org.json.JSONObject

interface ParseDaysRepository {

    fun parseDays(mainObject: JSONObject): List<WeatherModel>

    fun parseDate(mainObject: JSONObject, weatherItem: WeatherModel):WeatherModel
}