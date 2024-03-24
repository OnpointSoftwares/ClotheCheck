package com.example.clothchecker
import ClothingItem
import android.content.ClipData
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.DragEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.clothchecker.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class Clothing : AppCompatActivity() {

    private lateinit var shirtsContainer: LinearLayout
    private lateinit var trousersContainer: LinearLayout
    private lateinit var personImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clothing)

        shirtsContainer = findViewById(R.id.shirtsContainer)
        trousersContainer = findViewById(R.id.trousersContainer)
        personImageView = findViewById(R.id.personImageView)

        // Initialize Firebase Database reference
        val databaseReference = FirebaseDatabase.getInstance().reference

        // Retrieve data from Firebase Realtime Database
        databaseReference.child("SelectedClothes").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val clothingItem = snapshot.getValue(ClothingItem::class.java)
                    clothingItem?.let { createClothingImageView(it) }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })
        personImageView.setOnDragListener { view, dragEvent ->
            val clipData = ClipData.newPlainText("", "")
            val dragShadow = View.DragShadowBuilder(view)
            view.startDragAndDrop(clipData, dragShadow, view, 0)
            true
        }
        // Set OnDragListener for personImageView to handle drop event
        // Set OnDragListener for personImageView to handle drop event
        personImageView.setOnDragListener { v, event ->
            when (event.action) {
                DragEvent.ACTION_DROP -> {
                    val view = event.localState as View // Retrieve the dragged view
                    // Get the clothing item from the tag
                    val clothingItem = view.tag as ClothingItem
                    // Load the clothing image to personImageView
                    Picasso.get().load(clothingItem.imageUrl).into(personImageView)
                    true
                }

                else -> false
            }

        }
    }

        private fun createClothingImageView(clothingItem: ClothingItem) {
        val imageView = ImageView(this)
            imageView.background=null
        imageView.tag = clothingItem // Set clothingItem as tag
        Picasso.get().load(clothingItem.imageUrl).into(imageView)
        val layoutParams = LinearLayout.LayoutParams(
            600,  // Width set to 300dp
            600   // Height set to 300dp
        )
        imageView.layoutParams = layoutParams
        // Set OnLongClickListener to start drag operation
        imageView.setOnLongClickListener { view ->
            val clipData = ClipData.newPlainText("", "")
            val dragShadow = View.DragShadowBuilder(view)
            view.startDragAndDrop(clipData, dragShadow, view, 0)
            true
        }
        imageView.setOnDragListener { v, event ->
            when (event.action) {
                    DragEvent.ACTION_DROP -> {
                        val view = event.localState as View // Retrieve the dragged view
                        // Get the clothing item from the tag
                        val clothingItem = view.tag as ClothingItem
                        // Load the clothing image to personImageView
                        Picasso.get().load(clothingItem.imageUrl).into(personImageView)
                        true
                    }
                    else -> false
            }
            }
        if (clothingItem.name == "shirt") {
            shirtsContainer.addView(imageView)
        } else if (clothingItem.name == "Trousers") {
            trousersContainer.addView(imageView)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Retrieve the captured image from the intent
            val imageBitmap = data?.extras?.get("data") as Bitmap

            // Set the captured image to personImageView
            personImageView.setImageBitmap(imageBitmap)
        }
    }

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
    }
}
