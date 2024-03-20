package com.example.weatherapplication.favouriteList

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapplication.R
import com.example.weatherapplication.model.FavouriteCountries

private const val TAG = "FavouriteAdapter"
class FavouriteAdapter(private var dataSet: List<FavouriteCountries>)
    : RecyclerView.Adapter<FavouriteAdapter.ViewHolder>() {

    fun setList(newData: List<FavouriteCountries>) {
        dataSet = newData
        Log.i(TAG, "setList: ")
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteAdapter.ViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.favourite_items, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavouriteAdapter.ViewHolder, position: Int) {
        val currentItem = dataSet[position]
        currentItem.let {
            holder.tvTitle.text = it.cityName
            holder.tvCoordinator.text="${it.latitude} , ${it.longitude}"
        }
        holder.btnDelete.setOnClickListener { Log.i(TAG, "onBindViewHolder: $currentItem") }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
    class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {
        var tvTitle:TextView
        var tvCoordinator:TextView
        var btnDelete:Button

        init {
            tvTitle = itemView.findViewById(R.id.tv_city_title_item)
            tvCoordinator = itemView.findViewById(R.id.tv_city_coord_item)
            btnDelete = itemView.findViewById(R.id.btn_delete)
        }
    }
}
