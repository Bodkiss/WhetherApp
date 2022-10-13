package com.example.weatherapp.presentation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapp.presentation.Adapters.DialogManager
import com.example.weatherapp.presentation.Adapters.VpAdapter
import com.example.weatherapp.data.WeatherModel
import com.example.weatherapp.Fragments.isPermitionFranted
import com.example.weatherapp.data.repository.ParseDaysRepositoryImpl
import com.example.weatherapp.data.repository.RequsestDataRepositoryImpl
import com.example.weatherapp.databinding.FragmentMainBinding
import com.example.weatherapp.domain.useCase.ParseDaysUseCase
import com.example.weatherapp.domain.useCase.RequestUseCase
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

const val API_KEY = "6c8b10d1db354c2894e105541222608"

class MainFragment : Fragment() {
    private lateinit var fLocationClient: FusedLocationProviderClient
    private val fList = listOf(
        HoursFragment.newInstance(),
        DaysFragment.newInstance()
    )
    private val tList = listOf(
        "Hours",
        "Days"
    )
    private lateinit var pLauncher: ActivityResultLauncher<String>
    private lateinit var binding: FragmentMainBinding
    private val viewModel: MainViewModel by activityViewModels()

    /*  private val parseDaysRepository = ParseDaysRepositoryImpl()
    private val parseDaysUseCase = ParseDaysUseCase(parseDaysRepository)

   */
    /* private val requestRepository= RequsestDataRepositoryImpl()
    private val requestUseCase = RequestUseCase(requestRepository)
*/


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        checkPermition()
        init()
        requestWeatherData("Lviv")
        initLocation()
        updateCurrentCard()

        //
        //getLocation()

    }

    override fun onResume() {
        super.onResume()
        checkLocation()
    }

    private fun init() = with(binding) {
        val adapter = VpAdapter(activity as FragmentActivity, fList)
        vp2.adapter = adapter
        TabLayoutMediator(tabLayout, vp2) { tab, pos ->
            tab.text = tList[pos]
        }.attach()
        ibSync.setOnClickListener {
            tabLayout.selectTab(tabLayout.getTabAt(0))
            checkLocation()
        }
        ibSearch.setOnClickListener {
            DialogManager.searchByNameDialog(requireContext(), object : DialogManager.Listener {
                override fun onClick(name: String?) {
                    name?.let { it1 -> requestWeatherData(it1) }
                }

            })
        }

    }

    private fun initLocation() {
        fLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

    }

    private fun checkLocation() {
        if (isLocationEnabled()) {
            getLocation()
        } else {
            DialogManager.locationSettingsDialog(requireContext(), object : DialogManager.Listener {
                override fun onClick(name: String?) {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
            })
        }
    }

    private fun isLocationEnabled(): Boolean {
        val lm = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun getLocation() {
        val ct = CancellationTokenSource()
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, ct.token)
            .addOnCompleteListener() {
                requestWeatherData("${it.result.latitude},${it.result.longitude}")
            }
    }

    private fun updateCurrentCard() = with(binding) {
        viewModel.liveDataCurrent.observe(viewLifecycleOwner) {
            val maxMinTemp = "${it.maxTemp}°C/ ${it.minTemp}°C"
            tvData.text = it.time
            tvCity.text = it.city
            tvCurrentTemp.text = it.currentTemp.ifEmpty { maxMinTemp }
            tvCondition.text = it.condition
            tvMaxMin.text = if (it.currentTemp.isEmpty()) "" else maxMinTemp

            Picasso.get().load("https:" + it.imageUrl).into(imWeather)
            binding.progressBar.visibility = View.INVISIBLE
            binding.imWeather.visibility = View.VISIBLE

        }

    }


    private fun permitionListener() {
        pLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            Toast.makeText(activity, "Permition is $it", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkPermition() {
        if (!isPermitionFranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            permitionListener()
            pLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
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
                viewModel.parseWeatherData(result)

            },
            { error ->
                Log.e("MyLog", "Error: $error")
            }
        )
        queue.add(request)
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()

    }
}



   /* private fun parseWeatherData(result: String) {
        val mainObject = JSONObject(result)

        //val list = parseDays(mainObject)
        val list = parseDaysUseCase.executeDays(mainObject)
        viewModel.liveDataList.value = list

        val item = parseDaysUseCase.executeDate(mainObject,list[0])

        viewModel.liveDataCurrent.value = item
        //parseCurrentData(mainObject,list[0])
    }*/

    /*private fun parseDays(mainObject: JSONObject): List<WeatherModel> {
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
    }*/

    /*private fun parseCurrentData(mainObject: JSONObject, weatherItem: WeatherModel) {
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

        viewModel.liveDataCurrent.value = item

    }*/


