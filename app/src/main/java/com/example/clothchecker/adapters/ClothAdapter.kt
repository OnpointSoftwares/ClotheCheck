package adapters

import ClothingItem
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clothchecker.R

class ClothAdapter(private val listener: OnItemClickListener) : RecyclerView.Adapter<ClothAdapter.ClothesViewHolder>() {

    private val clothesList: MutableList<ClothingItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClothesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cloth, parent, false)
        return ClothesViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClothesViewHolder, position: Int) {
        val clothingItem = clothesList[position]
        holder.bind(clothingItem)
    }

    override fun getItemCount(): Int {
        return clothesList.size
    }

    fun addItem(clothingItem: ClothingItem) {
        clothesList.add(clothingItem)
        notifyDataSetChanged()
    }

    inner class ClothesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val clothNameTextView: TextView = itemView.findViewById(R.id.textViewClothName)
        private lateinit var clothingItem: ClothingItem

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(item: ClothingItem) {
            clothingItem = item
            clothNameTextView.text = item.name
        }

        override fun onClick(v: View?) {
            listener.onItemClick(clothingItem)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(clothingItem: ClothingItem)
    }

}
