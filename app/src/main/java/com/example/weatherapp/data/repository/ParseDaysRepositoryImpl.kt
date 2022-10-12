package com.example.weatherapp.data.repository

import com.example.weatherapp.data.WeatherModel
import com.example.weatherapp.domain.repository.ParseDaysRepository
import org.json.JSONObject

class ParseDaysRepositoryImpl:ParseDaysRepository {

    override fun parseDays(mainObject: JSONObject): List<WeatherModel>{
        val list = ArrayList<WeatherModel>()
        val daysArray = mainObject.getJSONObject("forecast")
            .getJSONArray("forecastday")
        val name = mainObject.getJSONObject("location").getString("name")
        for (i in 0 until daysArray.length()) {
            val day = daysArray[i] as JSONObject
            val item = WeatherModel(
                city = name,
                time =  day.getString("date"),
                condition = day.getJSONObject("day")
                    .getJSONObject("condition").getString("text"),
                currentTemp = "",
                maxTemp = day.getJSONObject("day").getString("maxtemp_c"),
                minTemp = day.getJSONObject("day").getString("mintemp_c"),
                imageUrl = day.getJSONObject("day")
                    .getJSONObject("condition").getString("icon"),
                hours = day.getJSONArray("hour").toString()
            )
            list.add(item)
        }
        return  list
    }

    override fun parseDate(mainObject: JSONObject, weatherItem: WeatherModel): WeatherModel {
        val item = WeatherModel(
            city = mainObject.getJSONObject("location").getString("name"),
            time = mainObject.getJSONObject("current").getString("last_updated"),
            condition = mainObject.getJSONObject("current")
                .getJSONObject("condition").getString("text"),
            currentTemp = mainObject.getJSONObject("current").getString("temp_c"),
            maxTemp = weatherItem.maxTemp,
            minTemp = weatherItem.minTemp,
            imageUrl = mainObject.getJSONObject("current")
                .getJSONObject("condition").getString("icon"),
            hours = weatherItem.hours
        )

        return item
    }
}