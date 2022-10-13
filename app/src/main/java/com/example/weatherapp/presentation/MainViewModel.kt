package com.example.weatherapp.presentation

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.data.WeatherModel
import com.example.weatherapp.data.repository.ParseDaysRepositoryImpl
import com.example.weatherapp.data.repository.RequsestDataRepositoryImpl
import com.example.weatherapp.domain.useCase.ParseDaysUseCase
import com.example.weatherapp.domain.useCase.RequestUseCase
import org.json.JSONObject

class MainViewModel:ViewModel() {

    private val parseDaysRepository = ParseDaysRepositoryImpl()
    private val parseDaysUseCase = ParseDaysUseCase(parseDaysRepository)






    val liveDataCurrent = MutableLiveData<WeatherModel>()
    val liveDataList = MutableLiveData<List<WeatherModel>>()


     fun parseWeatherData(result: String) {
        val mainObject = JSONObject(result)

        //val list = parseDays(mainObject)
        val list = parseDaysUseCase.executeDays(mainObject)
        liveDataList.value = list

        val item = parseDaysUseCase.executeDate(mainObject,list[0])

        liveDataCurrent.value = item
        //parseCurrentData(mainObject,list[0])
    }



}