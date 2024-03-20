package com.example.weatherapplication.settings

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.findFragment
import com.example.weatherapplication.Constant
import com.example.weatherapplication.R


class SettingFragment : Fragment() {

    private lateinit var languageGroup: RadioGroup
    private lateinit var locationGroup: RadioGroup
    private lateinit var temperatureGroup: RadioGroup
    private lateinit var windSpeedGroup: RadioGroup
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var radioEnglish: RadioButton
    private lateinit var radioArabic: RadioButton
    private lateinit var radioGps: RadioButton
    private lateinit var radioMap: RadioButton
    private lateinit var radioKelvin: RadioButton
    private lateinit var radioFahrenheit: RadioButton
    private lateinit var radioCelsius: RadioButton
    private lateinit var radioMeter: RadioButton
    private lateinit var radioMile: RadioButton
    private lateinit var rootView: View

    private lateinit var editor: Editor
    private var isEnglish: Boolean = true
    private var isGps: Boolean = true
    private var isMetric: Boolean = true
    private var tempUnit: String = Constant.Units.CELSIUS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rootView = view
        initialization()
        getCheckedButton()
    }

    private fun initialization() {
        languageGroup = rootView.findViewById(R.id.radio_group_language)
        locationGroup = rootView.findViewById(R.id.radio_group_location)
        temperatureGroup = rootView.findViewById(R.id.radio_group_temperature)
        windSpeedGroup = rootView.findViewById(R.id.radio_group_wind_speed)
        radioEnglish = rootView.findViewById(R.id.radio_english)
        radioArabic = rootView.findViewById(R.id.radio_arabic)
        radioGps = rootView.findViewById(R.id.radio_gps)
        radioMap = rootView.findViewById(R.id.radio_map)
        radioMeter = rootView.findViewById(R.id.radio_meter)
        radioMile = rootView.findViewById(R.id.radio_mile)
        radioCelsius = rootView.findViewById(R.id.radio_celsius)
        radioFahrenheit = rootView.findViewById(R.id.radio_fahrenheit)
        radioKelvin = rootView.findViewById(R.id.radio_kelvin)
        sharedPreferences = requireContext().getSharedPreferences("Setting", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        isEnglish = sharedPreferences.getBoolean(Constant.LANGUAGE_KEY, true)
        isGps = sharedPreferences.getBoolean(Constant.LOCATION_KEY, true)
        isMetric = sharedPreferences.getBoolean(Constant.WIND_SPEED_UNIT, true)
        tempUnit = sharedPreferences.getString(Constant.TEMPERATURE_Unit, Constant.Units.CELSIUS)
            ?: Constant.Units.CELSIUS
    }
    private fun getCheckedButton(){

        if (isEnglish) {
            radioEnglish.isChecked = true
        } else {
            radioArabic.isChecked = true
        }
        if (isMetric) {
            radioMeter.isChecked = true
        } else {
            radioMile.isChecked = true
        }
        if (isGps) {
            radioGps.isChecked = true
        } else {
            radioMap.isChecked = true
        }
        when (tempUnit) {
            Constant.Units.CELSIUS -> {
                radioCelsius.isChecked = true
            }

            Constant.Units.KELVIN -> {
                radioKelvin.isChecked = true
            }

            Constant.Units.FAHRENHEIT -> {
                radioFahrenheit.isChecked = true
            }

            else -> {
                radioCelsius.isChecked = true
            }
        }

    }
    override fun onStop() {
        super.onStop()
        if ((view?.findViewById<RadioButton>(languageGroup.checkedRadioButtonId)?.text.toString()).equals(
                "English",
                ignoreCase = true
            )
        ) {
            editor.putBoolean(Constant.LANGUAGE_KEY, true)
        } else {
            editor.putBoolean(Constant.LANGUAGE_KEY, false)
        }
        if ((view?.findViewById<RadioButton>(locationGroup.checkedRadioButtonId)?.text.toString()).equals(
                "GPS",
                ignoreCase = true
            )
        ) {
            editor.putBoolean(Constant.LOCATION_KEY, true)
        } else {
            editor.putBoolean(Constant.LOCATION_KEY, false)
        }
        if ((view?.findViewById<RadioButton>(windSpeedGroup.checkedRadioButtonId)?.text.toString()).equals(
                "Meter / Second",
                ignoreCase = true
            )
        ) {
            editor.putBoolean(Constant.WIND_SPEED_UNIT, true)
        } else {
            editor.putBoolean(Constant.WIND_SPEED_UNIT, false)
        }
        if ((view?.findViewById<RadioButton>(locationGroup.checkedRadioButtonId)?.text.toString()).equals(
                "celsius",
                ignoreCase = true
            )
        ) {
            editor.putString(Constant.TEMPERATURE_Unit, Constant.Units.CELSIUS)
        } else if ((view?.findViewById<RadioButton>(locationGroup.checkedRadioButtonId)?.text.toString()).equals(
                "kelvin",
                ignoreCase = true
            )
        ) {
            editor.putString(Constant.TEMPERATURE_Unit, Constant.Units.KELVIN)
        } else {
            editor.putString(Constant.TEMPERATURE_Unit, Constant.Units.FAHRENHEIT)
        }

        editor.commit()

    }
}

private const val TAG = "SettingFragment"