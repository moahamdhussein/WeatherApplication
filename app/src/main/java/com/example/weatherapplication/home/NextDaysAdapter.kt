package com.example.weatherapplication.home

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherapplication.utility.Constant
import com.example.weatherapplication.R
import com.example.weatherapplication.utility.getDayName
import com.example.weatherapplication.model.WeatherProperty

private const val TAG = "NextDaysAdapter"

class NextDaysAdapter(
    private var weatherProperty: MutableList<WeatherProperty>,
    private val context: Context

) : RecyclerView.Adapter<NextDaysAdapter.ViewHolder>() {

    private var dailyMinMaxTemp: MutableMap<String?, MutableList<Int?>> = mutableMapOf()
    var convertCelsiusMultiplayer = 1.0
    var convertCelsiusAddition = 0.0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NextDaysAdapter.ViewHolder {

        val unit = context.getSharedPreferences("Setting", Context.MODE_PRIVATE).getString(
            Constant.TEMPERATURE_Unit,
            Constant.Units.CELSIUS
        )

        when (unit) {
            Constant.Units.FAHRENHEIT -> {
                convertCelsiusMultiplayer = 9.0 / 5.0
                convertCelsiusAddition = 32.0
            }

            Constant.Units.KELVIN -> convertCelsiusAddition = 273.15
        }
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.next_days_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: NextDaysAdapter.ViewHolder, position: Int) {
        if (weatherProperty.size > 0) {
            val key = dailyMinMaxTemp.keys.toList()[position]
            Log.i(TAG, "onBindViewHolder: $key")
            if (holder.adapterPosition == 0) {
                holder.tvTime.text = context.resources.getString(R.string.today)
            } else {
                val dayName = getDayName(key ?: " ")
                holder.tvTime.text = "${key?.takeLast(2)} $dayName"
            }

            holder.tvTemperature.text =
                "${
                    ((dailyMinMaxTemp[key]?.get(0)?.times(convertCelsiusMultiplayer))?.plus(
                        convertCelsiusAddition
                    ))?.toInt()
                }° / ${((dailyMinMaxTemp[key]?.get(1)?.times(convertCelsiusMultiplayer))?.plus(
                    convertCelsiusAddition
                ))?.toInt()}°"

            val currentWeather =
                weatherProperty.stream().filter { it.dtTxt?.split(" ")?.get(0).equals(key) }
                    .findFirst().get()
            Glide.with(holder.itemView)
                .load("https://openweathermap.org/img/wn/${currentWeather.weather[0].icon}@2x.png")
                .into(holder.ivCloudIcon)
        }
    }

    override fun getItemCount(): Int {
        return 5
    }

    private fun getMinMaxTemp() {
        weatherProperty.forEach {
            val key = it.dtTxt?.split(" ")?.get(0)
            val min = (it.main?.tempMin)?.toInt()
            val max = (it.main?.tempMax)?.toInt()
            if (dailyMinMaxTemp.containsKey(key)) {
                if ((dailyMinMaxTemp[key]?.get(0) ?: 0) > (min ?: 0)) {
                    dailyMinMaxTemp[key]?.set(0, min)
                }
                if ((dailyMinMaxTemp[key]?.get(1) ?: 0) < (max ?: 0)) {
                    dailyMinMaxTemp[key]?.set(1, max)
                }
            } else {
                Log.i(TAG, "getMinMaxTemp: $key")
                dailyMinMaxTemp[key] = mutableListOf(min, max)

                Log.i(TAG, "getMinMaxTemp: ${dailyMinMaxTemp.keys}")
            }

        }

    }

    fun setList(list: MutableList<WeatherProperty>) {
        weatherProperty = list
        getMinMaxTemp()
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTemperature: TextView
        var ivCloudIcon: ImageView
        var tvTime: TextView

        init {
            tvTime = itemView.findViewById(R.id.tv_next_date)
            tvTemperature = itemView.findViewById(R.id.tv_next_temperature)
            ivCloudIcon = itemView.findViewById(R.id.iv_next_icon)
        }
    }
}