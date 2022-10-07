package com.example.weatherapp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.Adapters.WeatherAdapter
import com.example.weatherapp.Adapters.WeatherModel
import com.example.weatherapp.presentation.MainViewModel
import com.example.weatherapp.databinding.FragmentHoursBinding
import org.json.JSONArray
import org.json.JSONObject


class HoursFragment : Fragment() {

    private lateinit var binding: FragmentHoursBinding
    private lateinit var adapter: WeatherAdapter
    private val viewModel: MainViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHoursBinding.inflate(
            inflater, container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecView()
        viewModel.liveDataCurrent.observe(viewLifecycleOwner) {
           adapter.submitList(getHoursList(it))
        }

    }


    private fun initRecView() = with(binding) {
        rcWiew.layoutManager = LinearLayoutManager(activity)
        adapter = WeatherAdapter(null)
        rcWiew.adapter = adapter

    }

    private fun getHoursList(weatherItem: WeatherModel): List<WeatherModel> {
        val hoursArray = JSONArray(weatherItem.hours)
        val list = ArrayList<WeatherModel>()
        for (i in 0 until hoursArray.length()) {
            val item = WeatherModel(
                city = weatherItem.city,
                time = (hoursArray[i] as JSONObject).getString("time"),
                condition = (hoursArray[i] as JSONObject).getJSONObject("condition").getString("text"),
                currentTemp = (hoursArray[i] as JSONObject).getString("temp_c"),
                maxTemp = "",
                minTemp = "",
                imageUrl = (hoursArray[i] as JSONObject).getJSONObject("condition").getString("icon"),
                hours = ""
                )
            list.add(item)
        }
        return list
    }

    companion object {
        @JvmStatic
        fun newInstance() = HoursFragment()


    }
}