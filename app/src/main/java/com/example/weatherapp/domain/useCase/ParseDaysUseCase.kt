package com.example.weatherapp.domain.useCase

import com.example.weatherapp.data.WeatherModel
import com.example.weatherapp.domain.repository.ParseDaysRepository
import org.json.JSONObject

class ParseDaysUseCase(private val parseDaysRepository: ParseDaysRepository) {


    fun executeDays(mainObject: JSONObject): List<WeatherModel>{
        val list:List<WeatherModel> = parseDaysRepository.parseDays(mainObject)
        return  list

    }

    fun executeDate(mainObject: JSONObject, weatherItem: WeatherModel):WeatherModel{
        val item = parseDaysRepository.parseDate(mainObject, weatherItem)
        return item
    }


}
