package com.example.weatherapp.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.Adapters.WeatherAdapter
import com.example.weatherapp.data.WeatherModel
import com.example.weatherapp.databinding.FragmentDaysBinding


class DaysFragment : Fragment(), WeatherAdapter.Listener {

    private lateinit var adapter: WeatherAdapter
    private lateinit var binding: FragmentDaysBinding
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDaysBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        viewModel.liveDataList.observe(viewLifecycleOwner){
            adapter.submitList(it)
        }
    }

    private fun init() = with(binding){
        adapter = WeatherAdapter(this@DaysFragment)
        recWiewDays.layoutManager = LinearLayoutManager(activity)
        recWiewDays.adapter = adapter
    }

    companion object {

        fun newInstance() = DaysFragment()

    }

    override fun onClick(item: WeatherModel) {
        viewModel.liveDataCurrent.value = item
    }
}