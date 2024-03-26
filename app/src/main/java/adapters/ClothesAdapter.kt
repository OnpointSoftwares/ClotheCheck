package adapters

import ClothingItem
import android.content.DialogInterface
import android.content.Intent
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.clothchecker.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
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
        holder.itemView.setOnClickListener {
                val choice = arrayOf<CharSequence>("Select Photo", "Delete Photo", "Cancel")
                val myAlertDialog: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                myAlertDialog.setTitle("Select Image")
                myAlertDialog.setItems(choice, DialogInterface.OnClickListener { dialog, item ->
                    when {
                        // Select "Choose from Gallery" to pick image from gallery
                        choice[item] == "Select Photo" -> {
                            val ref=FirebaseDatabase.getInstance().reference.child("SelectedClothes")
                            val key=ref.push().key.toString()
                            ref.child(FirebaseAuth.getInstance().currentUser!!.uid).child(key).setValue(clothingItem).addOnCompleteListener {
                                Toast.makeText(holder.itemView.context,"Cloth selected",Toast.LENGTH_LONG).show()
                            }
                        }
                        // Select "Take Photo" to take a photo
                        choice[item] == "Delete Photo" -> {

                        }
                        // Select "Cancel" to cancel the task
                        choice[item] == "Cancel" -> {
                            dialog.dismiss()
                        }
                    }
                })
                myAlertDialog.show()
            }
        // Load image using Picasso or your preferred image loading library
        Picasso.get().load(clothingItem.imageUrl).into(holder.itemView.findViewById<ImageView>(R.id.image))
    }

    override fun getItemCount(): Int = clothesList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
