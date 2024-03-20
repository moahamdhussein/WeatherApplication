package com.example.weatherapplication.home

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherapplication.R
import com.example.weatherapplication.model.WeatherProperty

private const val TAG = "dailyForecastAdapter"

class DailyForecastAdapter(
    var weatherProperty: MutableList<WeatherProperty>,
    val context: Context

) : RecyclerView.Adapter<DailyForecastAdapter.ViewHolder>() {
    var startTime: Int = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.daily_forecast_item, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        holder.tvTemperature.text = "${(weatherProperty[position].main?.temp)?.toInt()}\u00B0"
        val time = weatherProperty[position].dtTxt?.split(" ")?.get(1)?.substring(0,2)?.toInt() ?:0
        if (time > 12 ){
            holder.tvTime.text = "${time-12} pm"
        }else if (time ==0){
            holder.tvTime.text=" 12 am"
        }else if (time==12){
            holder.tvTime.text=" 12 pm"
        } else{
            holder.tvTime.text = "$time am"
        }
        holder.tvDate.text= weatherProperty[position].dtTxt?.split(" ")?.get(0)
        Glide.with(context)
            .load("https://openweathermap.org/img/wn/${weatherProperty[position].weather[0].icon}@2x.png")
            .into(holder.ivCloudIcon)

    }

    fun setList(newWeatherProperty: MutableList<WeatherProperty>) {

        weatherProperty = newWeatherProperty
        startTime = (weatherProperty[0].dtTxt?.split(" ")?.get(1)?.split(":")?.get(0))?.toInt() ?: 0
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
//        return weatherProperty.size * 3
        return weatherProperty.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTemperature: TextView
        var ivCloudIcon: ImageView
        var tvTime: TextView
        var tvDate:TextView

        init {
            tvTime = itemView.findViewById(R.id.tv_item_time)
            tvTemperature = itemView.findViewById(R.id.tv_item_temperature)
            ivCloudIcon = itemView.findViewById(R.id.iv_item_cloud_icon)
            tvDate = itemView.findViewById(R.id.tv_item_date)
        }
    }
}