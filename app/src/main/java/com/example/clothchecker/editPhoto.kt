package com.example.clothchecker

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity

class editPhoto: AppCompatActivity() {

    private lateinit var photoImageView: ImageView
    private lateinit var clothesImageView: ImageView
    private lateinit var positionSeekBar: SeekBar
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_photo)

        photoImageView = findViewById(R.id.photoImageView)
        clothesImageView = findViewById(R.id.clothesImageView)
        positionSeekBar = findViewById(R.id.positionSeekBar)
        saveButton = findViewById(R.id.saveButton)

        // Set initial position
        positionSeekBar.progress = 50

        // Set listeners
        positionSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateClothesPosition(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        saveButton.setOnClickListener {
            // Implement logic to save the edited photo
            // You may want to save the edited image to Firebase Storage or locally
        }
    }

    private fun updateClothesPosition(progress: Int) {
        // Adjust the position of the clothes based on the SeekBar progress
        val layoutParams = clothesImageView.layoutParams as RelativeLayout.LayoutParams
        layoutParams.bottomMargin = progress * 2 // You may need to adjust this based on your design
        clothesImageView.layoutParams = layoutParams
    }
}
