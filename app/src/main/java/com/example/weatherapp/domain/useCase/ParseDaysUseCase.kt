package com.example.weatherapp.domain.useCase

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapp.Adapters.DialogManager
import com.example.weatherapp.data.WeatherModel
import com.example.weatherapp.presentation.API_KEY
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import org.json.JSONObject

class CheckLocationUseCase {
    private lateinit var fLocationClient: FusedLocationProviderClient

    private fun checkLocation(){
        if(isLocationEnabled()){
            getLocation()
        }else{
            DialogManager.locationSettingsDialog(requireContext()
                ,object : DialogManager.Listener{
                    override fun onClick(name:String?) {
                        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }
                })
        }
    }
    private fun isLocationEnabled():Boolean{
        val lm = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return  lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
    private fun getLocation(){
        val ct = CancellationTokenSource()
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ){
            return
        }

        fLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY,ct.token)
            .addOnCompleteListener() {
                requestWeatherData("${it.result.latitude},${it.result.longitude}")
            }
    }
    private fun requestWeatherData(city: String) {
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
                parseWeatherData(result)

            },
            { error ->
                Log.e("MyLog", "Error: $error")
            }
        )
        queue.add(request)
    }
    private fun parseWeatherData(result: String) {
        val mainObject = JSONObject(result)
        val list = parseDays(mainObject)
        parseCurrentData(mainObject,list[0])

    }
    private fun parseDays(mainObject: JSONObject): List<WeatherModel> {
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
        viewModel.liveDataList.value = list
        return  list
    }
}
