package com.example.clothchecker
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

class upload : AppCompatActivity() {

    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference

    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        val chooseImageButton: Button = findViewById(R.id.chooseImageButton)
        val uploadButton: Button = findViewById(R.id.uploadButton)

        chooseImageButton.setOnClickListener {
            openImageChooser()
        }

        uploadButton.setOnClickListener {
            uploadImage()
        }
    }

    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resultLauncher.launch(intent)
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                selectedImageUri = data?.data
                // Display the selected image (optional)
            }
        }

    private fun uploadImage() {
        selectedImageUri?.let { uri ->
            val imageName = "image_${UUID.randomUUID()}"
            val imageRef: StorageReference = storageReference.child("images/$imageName")

            imageRef.putFile(uri)
                .addOnSuccessListener {
                    // Image upload successful, handle accordingly
                }
                .addOnFailureListener {
                    // Image upload failed, handle accordingly (e.g., display an error message)
                }
        }
    }
}
