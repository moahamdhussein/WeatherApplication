package com.example.weatherapplication.settings

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat.recreate
import androidx.navigation.Navigation
import com.example.weatherapplication.utility.Constant
import com.example.weatherapplication.databinding.FragmentSettingBinding


class SettingFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: FragmentSettingBinding
    private lateinit var editor: Editor
    private var isEnglish: Boolean = true
    private var isGps: Boolean = true
    private var isMetric: Boolean = true
    private var tempUnit: String = Constant.Units.CELSIUS

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialization()
        getCheckedButton()
        binding.btnSave.setOnClickListener {
            saveChanges()
            recreate(requireActivity())
        }
    }

    private fun initialization() {
        sharedPreferences = requireContext().getSharedPreferences("Setting", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        isEnglish = sharedPreferences.getBoolean(Constant.LANGUAGE_KEY, true)
        isGps = sharedPreferences.getBoolean(Constant.LOCATION_KEY, true)
        isMetric = sharedPreferences.getBoolean(Constant.WIND_SPEED_UNIT, true)
        tempUnit = sharedPreferences.getString(Constant.TEMPERATURE_Unit, Constant.Units.CELSIUS)
            ?: Constant.Units.CELSIUS
        binding.btnChangeMainLocation.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(
                SettingFragmentDirections.actionSettingFragmentToMapFragment("changeMainLocation","-")
            )
        }
    }

    private fun getCheckedButton() {

        if (isEnglish) {
            binding.radioEnglish.isChecked = true
        } else {
            binding.radioArabic.isChecked = true
        }
        if (isMetric) {
            binding.radioMeter.isChecked = true
        } else {
            binding.radioMile.isChecked = true
        }
        if (isGps) {
            binding.radioGps.isChecked = true
        } else {
            binding.btnChangeMainLocation.visibility = View.VISIBLE
            binding.radioMap.isChecked = true
        }
        when (tempUnit) {
            Constant.Units.CELSIUS -> {
                binding.radioCelsius.isChecked = true
            }

            Constant.Units.KELVIN -> {
                binding.radioKelvin.isChecked = true
            }

            Constant.Units.FAHRENHEIT -> {
                binding.radioFahrenheit.isChecked = true
            }
        }

    }

    fun saveChanges() {

        if (binding.radioEnglish.isChecked) {
            editor.putBoolean(Constant.LANGUAGE_KEY, true)
        } else {
            editor.putBoolean(Constant.LANGUAGE_KEY, false)
        }

        if (binding.radioGps.isChecked) {
            editor.putBoolean(Constant.LOCATION_KEY, true)
        } else {
            editor.putBoolean(Constant.LOCATION_KEY, false)
        }

        if (binding.radioMeter.isChecked) {
            editor.putBoolean(Constant.WIND_SPEED_UNIT, true)
        } else {
            editor.putBoolean(Constant.WIND_SPEED_UNIT, false)
        }

        when (binding.radioGroupTemperature.checkedRadioButtonId) {
            binding.radioCelsius.id -> {
                editor.putString(Constant.TEMPERATURE_Unit, Constant.Units.CELSIUS)
            }

            binding.radioFahrenheit.id -> {
                editor.putString(Constant.TEMPERATURE_Unit, Constant.Units.FAHRENHEIT)
            }

            binding.radioKelvin.id -> {
                editor.putString(Constant.TEMPERATURE_Unit, Constant.Units.KELVIN)
            }
        }
        editor.commit()
    }
}
