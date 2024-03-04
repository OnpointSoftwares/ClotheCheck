package com.example.clothchecker.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clothchecker.R

class ClothAdapter : RecyclerView.Adapter<ClothAdapter.ClothViewHolder>() {

    // TODO: Define your data model (e.g., a list of clothes)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClothViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cloth, parent, false)
        return ClothViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClothViewHolder, position: Int) {
        // TODO: Bind data to views in the ViewHolder
    }

    override fun getItemCount(): Int {
        // TODO: Return the size of your data list
        return 0
    }

    class ClothViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // TODO: Declare and initialize views in the ViewHolder
    }
}
