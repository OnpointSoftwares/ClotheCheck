package com.example.clothchecker
import ClothingItem
import android.content.ClipData
import android.content.ClipDescription
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.DragEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.clothchecker.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Clothing : AppCompatActivity() {

    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var databaseReference: DatabaseReference
    private lateinit var dragEventListener: View.OnDragListener

    private lateinit var takePhotoButton: Button
    private lateinit var saveClothingButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clothing)

        constraintLayout = findViewById(R.id.constraintLayout)
        takePhotoButton = findViewById(R.id.takePhotoButton)
        saveClothingButton = findViewById(R.id.saveClothingButton)

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().reference.child("SelectedClothes")

        // Set up drag event listener
        dragEventListener = View.OnDragListener { v, event ->
            when (event.action) {
                DragEvent.ACTION_DROP -> {
                    // Handle the drop event
                    val view = event.localState as View
                    val owner = view.parent as ViewGroup
                    owner.removeView(view)
                    val container = v as ConstraintLayout
                    container.addView(view)

                    // Update layout parameters of dropped ImageView to reflect new position
                    val layoutParams = view.layoutParams as ConstraintLayout.LayoutParams
                    layoutParams.leftMargin = (event.x - view.width / 2).toInt()
                    layoutParams.topMargin = (event.y - view.height / 2).toInt()
                    view.layoutParams = layoutParams

                    true
                }
                else -> false
            }
        }

        // Set click listener for take photo button
        takePhotoButton.setOnClickListener {
            // Launch camera intent to capture photo
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)
        }

        // Set click listener for save clothing button
        saveClothingButton.setOnClickListener {
            captureScreen()

        }

        // Retrieve data from Firebase Realtime Database
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
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
    }

    private fun createClothingImageView(clothingItem: ClothingItem) {
        val imageView = ImageView(this)
        Picasso.get().load(clothingItem.imageUrl).into(imageView)

        val layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        imageView.layoutParams = layoutParams
        constraintLayout.addView(imageView)

        imageView.setOnLongClickListener { view ->
            val clipData = ClipData.newPlainText("", "")
            val dragShadow = View.DragShadowBuilder(view)
            view.startDragAndDrop(clipData, dragShadow, view, 0)
            true
        }

        imageView.setOnDragListener(dragEventListener)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Retrieve the captured image from the intent
            val imageBitmap = data?.extras?.get("data") as Bitmap

            // Create ImageView for the captured photo
            val imageView = ImageView(this)
            imageView.setImageBitmap(imageBitmap)

            val layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            imageView.layoutParams = layoutParams
            constraintLayout.addView(imageView)

            imageView.setOnLongClickListener { view ->
                val clipData = ClipData.newPlainText("", "")
                val dragShadow = View.DragShadowBuilder(view)
                view.startDragAndDrop(clipData, dragShadow, view, 0)
                true
            }

            imageView.setOnDragListener(dragEventListener)
        }
    }
    private fun captureScreen() {
        val rootView: View = window.decorView.findViewById(R.id.constraintLayout)
        rootView.isDrawingCacheEnabled = true
        val bitmap: Bitmap = Bitmap.createBitmap(rootView.drawingCache)
        rootView.isDrawingCacheEnabled = false

        saveBitmap(bitmap)
    }

    private fun saveBitmap(bitmap: Bitmap) {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "screenshot_$timeStamp.jpg"

        val directory = File(Environment.getExternalStorageDirectory().toString() + "/Screenshots/")
        if (!directory.exists()) {
            directory.mkdirs()
        }

        val file = File(directory, fileName)
        try {
            val fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
            val clothingClassifier = ClothingClassifier(assets)
            val prediction = clothingClassifier.classifyImage(bitmap)

            Toast.makeText(this,prediction.toString(),Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
    }
}
