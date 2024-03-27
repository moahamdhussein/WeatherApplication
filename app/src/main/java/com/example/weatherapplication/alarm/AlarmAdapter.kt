package com.example.weatherapplication.alarm

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapplication.R
import com.example.weatherapplication.model.FavouriteCountries

private const val TAG = "AlarmAdapter"
class AlarmAdapter(private var dataSet: List<FavouriteCountries>) :
    RecyclerView.Adapter<AlarmAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.alarm_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = dataSet[position]
        currentItem.let {
            holder.tvTitle.text = it.cityName
            holder.tvCoordinator.text="${it.latitude} , ${it.longitude}"
            holder.tvTime.text=it.date
        }
        holder.btnDelete.setOnClickListener { Log.i(TAG, "onBindViewHolder:  $currentItem") }
    }

    fun setData(newData: List<FavouriteCountries>) {
        dataSet = newData
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
      return dataSet.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTitle: TextView
        var tvCoordinator: TextView
        var tvTime:TextView
        var btnDelete: Button

        init {
            tvTitle = itemView.findViewById(R.id.tv_city_coord_alarm_item)
            tvCoordinator = itemView.findViewById(R.id.tv_city_title_alarm_item)
            tvTime = itemView.findViewById(R.id.tv_alarm_time_item)
            btnDelete = itemView.findViewById(R.id.btn_delete_alarm)
        }
    }
}