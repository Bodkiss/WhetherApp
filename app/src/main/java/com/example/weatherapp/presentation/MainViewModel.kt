package com.example.weatherapp.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.data.WeatherModel
import com.example.weatherapp.domain.useCase.ParseDaysUseCase
import org.json.JSONObject

class MainViewModel:ViewModel() {

    val liveDataCurrent = MutableLiveData<WeatherModel>()
    val liveDataList = MutableLiveData<List<WeatherModel>>()

    fun parseDays(mainObject: JSONObject){

        //liveDataList.value = parseDaysUseCase.executeDays(mainObject)
    }
    fun parseDate(mainObject: JSONObject, weatherItem: WeatherModel){
       // liveDataCurrent.value = parseDaysUseCase.executeDate(mainObject,weatherItem)
    }



}