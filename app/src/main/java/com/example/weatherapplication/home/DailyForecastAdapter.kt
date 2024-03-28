package com.example.weatherapplication.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherapplication.Constant
import com.example.weatherapplication.R
import com.example.weatherapplication.model.WeatherProperty

private const val TAG = "dailyForecastAdapter"

class DailyForecastAdapter(
    var weatherProperty: MutableList<WeatherProperty>,
    val context: Context

) : RecyclerView.Adapter<DailyForecastAdapter.ViewHolder>() {
    var convertCelsiusMultiplayer = 1.0
    var convertCelsiusAddition = 0.0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val unit =  context.getSharedPreferences("Setting", Context.MODE_PRIVATE).getString(Constant.TEMPERATURE_Unit,Constant.Units.CELSIUS)

        when(unit){
            Constant.Units.FAHRENHEIT-> {
                convertCelsiusMultiplayer = 9.0 / 5.0
                convertCelsiusAddition = 32.0
            }
            Constant.Units.KELVIN -> convertCelsiusAddition = 273.15
        }

        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.daily_forecast_item, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val temperature = weatherProperty[position].main?.temp ?:0.0

        holder.tvTemperature.text = "${((temperature * convertCelsiusMultiplayer) + convertCelsiusAddition).toInt()}\u00B0"
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