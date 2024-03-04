package adapters

import ClothingItem
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clothchecker.R
import com.squareup.picasso.Picasso

class ClothesAdapter : RecyclerView.Adapter<ClothesAdapter.ViewHolder>() {

    private val clothesList = mutableListOf<ClothingItem>()

    fun addItem(clothingItem: ClothingItem) {
        clothesList.add(clothingItem)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_clothes, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val clothingItem = clothesList[position]

        // Bind data to UI elements in the item layout
        holder.itemView.findViewById<TextView>(R.id.TypeDef).text = clothingItem.name

        // Load image using Picasso or your preferred image loading library
        Picasso.get().load(clothingItem.imageUrl).into(holder.itemView.findViewById<ImageView>(R.id.image))
    }

    override fun getItemCount(): Int = clothesList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
