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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class Clothing : AppCompatActivity() {

    private lateinit var shirtsContainer: LinearLayout
    private lateinit var trousersContainer: LinearLayout
    private lateinit var personImageView: ImageView
    private lateinit var databaseReference: DatabaseReference
    private lateinit var description:ArrayList<String>
    private lateinit var newList:ArrayList<String>
    private var count:Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clothing)
        description=ArrayList()
        newList= ArrayList()
        // Initialize views
        shirtsContainer = findViewById(R.id.shirtsContainer)
        trousersContainer = findViewById(R.id.trousersContainer)
        personImageView = findViewById(R.id.personImageView)
        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().reference

        // Retrieve data from Firebase Realtime Database
        databaseReference.child("SelectedClothes").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val clothingItem = snapshot.getValue(ClothingItem::class.java)
                    count+=1
                    clothingItem?.let { createClothingImageView(it)
                        sendClothingDescriptionToServer(it.description)

                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })

        // Set OnDragListener for personImageView to handle drop event
        personImageView.visibility=View.GONE
        personImageView.setOnDragListener { v, event ->
            when (event.action) {
                DragEvent.ACTION_DROP -> {
                    val view = event.localState as View // Retrieve the dragged view
                    val clothingItem = view.tag as ClothingItem // Get the clothing item from the tag
                    // Load the clothing image to personImageView
                    Picasso.get().load(clothingItem.imageUrl).into(personImageView)
                    true
                }
                else -> false
            }
        }
    }
    private fun sendClothingDescriptionToServer(description: String){
        GlobalScope.launch(Dispatchers.IO) {
            val url = URL("https://eminently-rare-pegasus.ngrok-free.app/predict")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json")

            val outputStream = connection.outputStream
            val requestBody = "{\"description\": \"$description\"}"
            outputStream.write(requestBody.toByteArray())
            outputStream.flush()

            // Read response from the server
            val responseCode = connection.responseCode
            val responseMessage = if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read and handle the response
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                reader.readText()
            } else {
                // Handle error
                "Error: ${connection.responseMessage}"
            }
            // Show response message in Toast
            launch(Dispatchers.Main) {
                val msg=responseMessage
                saveMsg(msg)
            }

        }

    }

    private fun saveMsg(msg: String) {
        description.add(msg)
        for(item in description.listIterator()) {
            val jsonObject = JSONObject(item)
            val newItem=jsonObject.getString("responses")

            newList.add(newItem)

            if(newList.size.toString()=="3") {
                if (newList.contains("indescent")) {
                    val alertDialog= AlertDialog.Builder(this@Clothing)
                    alertDialog.setIcon(android.R.drawable.ic_dialog_alert)
                    alertDialog.setTitle("Message")
                    alertDialog.setMessage("The clothes you have selected are not descent. Choose others.")
                    alertDialog.setNegativeButton(""){dialog,_->
                        dialog.dismiss()
                    }
                    alertDialog.create().show()
                    Toast.makeText(this@Clothing, "undescent", Toast.LENGTH_LONG).show()
                } else {
                    val alertDialog= AlertDialog.Builder(this@Clothing)
                    alertDialog.setIcon(android.R.drawable.ic_dialog_info)
                    alertDialog.setTitle("Message")
                    alertDialog.setMessage("The clothes you have selected are descent")
                    alertDialog.setNegativeButton(""){dialog,_->
                        dialog.dismiss()
                    }
                    alertDialog.create().show()
                    Toast.makeText(this@Clothing, "descent", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Function to create clothing item image view
    private fun createClothingImageView(clothingItem: ClothingItem) {
        val imageView = ImageView(this)
        imageView.tag = clothingItem // Set clothingItem as tag
        Picasso.get().load(clothingItem.imageUrl).into(imageView)
        val layoutParams = LinearLayout.LayoutParams(
            300,
            300
        )
        imageView.layoutParams = layoutParams
        // Set OnLongClickListener to start drag operation
        imageView.setOnLongClickListener { view ->
            sendClothingDescriptionToServer("slippers")
            val clipData = ClipData.newPlainText("", "")
            val dragShadow = View.DragShadowBuilder(view)
            view.startDragAndDrop(clipData, dragShadow, view, 0)
            true
        }
        imageView.setOnDragListener { v, event ->
            when (event.action) {
                DragEvent.ACTION_DROP -> {
                    val view = event.localState as View // Retrieve the dragged view
                    val clothingItem = view.tag as ClothingItem // Get the clothing item from the tag
                    // Load the clothing image to personImageView
                    Picasso.get().load(clothingItem.imageUrl).into(personImageView)
                    true
                }
                else -> false
            }
        }
        // Add the image view to the appropriate container based on clothing type

            shirtsContainer.addView(imageView)
    }

    // Handle the result of capturing an image
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
