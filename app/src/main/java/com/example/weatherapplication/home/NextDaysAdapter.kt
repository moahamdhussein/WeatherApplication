package com.example.weatherapplication.home

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapplication.R
import com.example.weatherapplication.getDayName
import com.example.weatherapplication.model.WeatherProperty

private const val TAG = "NextDaysAdapter"

class NextDaysAdapter(
    var weatherProperty: MutableList<WeatherProperty>,
    val context: Context

) : RecyclerView.Adapter<NextDaysAdapter.ViewHolder>() {

    private lateinit var dailyMinMaxTemp: HashMap<String?, MutableList<Int?>>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NextDaysAdapter.ViewHolder {
        dailyMinMaxTemp = HashMap()
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.next_days_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: NextDaysAdapter.ViewHolder, position: Int) {
        if (weatherProperty.size > 0) {
            val key = dailyMinMaxTemp.keys.toList()[position]
            if (holder.adapterPosition == 0) {
                holder.tvTime.text = "Today"
            } else {
                val dayName = getDayName(key ?: " ")
                holder.tvTime.text = "${key?.takeLast(2)} $dayName"
            }
            holder.tvTemperature.text =
                "${dailyMinMaxTemp[key]?.get(0)}° / ${dailyMinMaxTemp[key]?.get(1)}°"
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
                dailyMinMaxTemp[key] = mutableListOf(min, max)
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