package com.example.clothchecker

import ClothingItem
import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Xfermode
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.Segmentation
import com.google.mlkit.vision.segmentation.SegmentationMask
import com.google.mlkit.vision.segmentation.Segmenter
import com.google.mlkit.vision.segmentation.selfie.SelfieSegmenterOptions
import com.google.mlkit.vision.segmentation.selfie.SelfieSegmenterOptions.SINGLE_IMAGE_MODE
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.util.UUID

class ClothsFragment : Fragment() {

    private lateinit var viewModel: ClothsViewModel
    private lateinit var fab: FloatingActionButton
    private lateinit var imageUrl: Uri
    private lateinit var newImage: Bitmap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cloths, container, false)
        // Initialize ViewModel
        viewModel = ViewModelProvider(this).get(ClothsViewModel::class.java)
        fab = view.findViewById(R.id.fab)
        imageUrl="/test".toUri()
        val descriptionSpinner = view.findViewById<Spinner>(R.id.descriptionSpinner)
        val nameSpinner = view.findViewById<Spinner>(R.id.nameSpinner)

        val listDescriptions = arrayOf(
            "Rugged",
            "Crop top",
            "Slippers",
            "Above knees skirt",
            "Below knees skirt",
            "slippers"
        )
        val descriptionAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listDescriptions)
        descriptionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        descriptionSpinner.adapter = descriptionAdapter

        val listNames = arrayOf("Trousers", "Skirts", "Shirts", "Caps", "Slippers", "Shoes")
        val nameAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listNames)
        nameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        nameSpinner.adapter = nameAdapter

        val upload: FloatingActionButton= view.findViewById(R.id.upload)
        upload.setOnClickListener {
            if (::imageUrl.isInitialized && descriptionSpinner.selectedItemPosition != AdapterView.INVALID_POSITION) {
                val type = listDescriptions[descriptionSpinner.selectedItemPosition]
                val name = listNames[nameSpinner.selectedItemPosition]
                saveClothes(imageUrl, type, name, view)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please select an image and description",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        fab.setOnClickListener {
            selectImage()
        }
        return view
    }

    private fun saveClothes(selectedImage: Uri, type: String, name: String, view: View) {
        val auth = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = FirebaseDatabase.getInstance().reference
        val keyId = ref.push().key.toString()

        // Assuming you want to store the image URL, convert the Uri to a string
        val imageUrl = selectedImage.toString()
        val clothingItem = ClothingItem(type, imageUrl, name)
        ref.child("Clothes").child(auth).child(keyId).setValue(clothingItem)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(view.context, "Saved successfully", Toast.LENGTH_LONG).show()
                    startActivity(Intent(view.context, editPhoto::class.java))
                } else {
                    Toast.makeText(view.context, "Failed to save data", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { exception ->
                // Handle the failure here, e.g., log the exception or show an error message
                Toast.makeText(view.context, "Failed: ${exception.message}", Toast.LENGTH_LONG)
                    .show()
            }
    }

    private fun selectImage() {
        val choice = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val myAlertDialog: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        myAlertDialog.setTitle("Select Image")
        myAlertDialog.setItems(choice, DialogInterface.OnClickListener { dialog, item ->
            when {
                // Select "Choose from Gallery" to pick image from gallery
                choice[item] == "Choose from Gallery" -> {
                    val gallery =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                    startActivityForResult(gallery, 100)
                }
                // Select "Take Photo" to take a photo
                choice[item] == "Take Photo" -> {
                    openCamera()
                }
                // Select "Cancel" to cancel the task
                choice[item] == "Cancel" -> {
                    dialog.dismiss()
                }
            }
        })
        myAlertDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode === 123) {
            val photo=data!!.extras!!["data"] as Bitmap
            if (photo != null) {
                PerfoamAuto(photo)
            }
            // BitMap is data structure of image file which store the image in memory
            // Set the image in imageview for display
        }
        else if(requestCode===100) {
            val imageSelected = data?.data
            imageUrl=imageSelected!!.path!!.toUri()
            val pathColumn = arrayOf(MediaStore.Images.Media.DATA)
            if (imageSelected != null) {
                val contentResolver=view?.context!!.contentResolver
                val myCursor = contentResolver.query(
                    imageSelected,
                    pathColumn, null, null, null
                )
                // Setting the image to the ImageView
                if (myCursor != null) {
                    myCursor.moveToFirst()
                    val columnIndex = myCursor.getColumnIndex(pathColumn[0])
                    val picturePath = myCursor.getString(columnIndex)
                    val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageSelected)
                    PerfoamAuto(bitmap)
                    myCursor.close()
                }
            }
        }
    }

    fun uploadImage(fileUri: Uri, view: View) {
        // on below line checking weather our file uri is null or not.
        if (fileUri != null) {
            // on below line displaying a progress dialog when uploading an image.
            val progressDialog = ProgressDialog(view.context)
            // on below line setting title and message for our progress dialog and displaying our progress dialog.
            progressDialog.setTitle("Uploading...")
            progressDialog.setMessage("Uploading your image..")
            progressDialog.show()

            // on below line creating a storage refrence for firebase storage and creating a child in it with
            // random uuid.
            val ref: StorageReference = FirebaseStorage.getInstance().getReference()
                .child(UUID.randomUUID().toString())
            // on below line adding a file to our storage.
            ref.putFile(fileUri!!).addOnSuccessListener {
                // this method is called when file is uploaded.
                // in this case we are dismissing our progress dialog and displaying a toast message
                progressDialog.dismiss()
                it.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                    imageUrl = uri
                }
                Toast.makeText(view.context, "Image Uploaded..", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                // this method is called when there is failure in file upload.
                // in this case we are dismissing the dialog and displaying toast message
                progressDialog.dismiss()
                Toast.makeText(view.context, "Fail to Upload Image..", Toast.LENGTH_SHORT)
                    .show()
            }.addOnCompleteListener {
            }
        }
    }

    fun uploadImage(fileUri: Bitmap, view: View) {
        // on below line checking weather our file uri is null or not.
        if (fileUri != null) {
            // on below line displaying a progress dialog when uploading an image.
            val progressDialog = ProgressDialog(view.context)
            // on below line setting title and message for our progress dialog and displaying our progress dialog.
            progressDialog.setTitle("Uploading...")
            progressDialog.setMessage("Uploading your image..")
            progressDialog.show()

            // on below line creating a storage refrence for firebase storage and creating a child in it with
            // random uuid.
            val ref: StorageReference = FirebaseStorage.getInstance().getReference()
                .child(UUID.randomUUID().toString())
            // on below line adding a file to our storage.
            val baos = ByteArrayOutputStream()
            fileUri.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            ref.putBytes(data).addOnSuccessListener {
                // this method is called when file is uploaded.
                // in this case we are dismissing our progress dialog and displaying a toast message
                it.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                    imageUrl = uri
                }
                progressDialog.dismiss()
                Toast.makeText(view.context, "Image Uploaded..", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                // this method is called when there is failure in file upload.
                // in this case we are dismissing the dialog and displaying toast message
                progressDialog.dismiss()
                Toast.makeText(view.context, "Fail to Upload Image..", Toast.LENGTH_SHORT)
                    .show()
            }.addOnCompleteListener {
                if (it.isComplete) {
                    Toast.makeText(view.context, imageUrl.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun PerfoamAuto(image:Bitmap)
    {
        val bitmapFromContentUri=image
        val client: Segmenter = Segmentation.getClient(SelfieSegmenterOptions.Builder().setDetectorMode(SINGLE_IMAGE_MODE).build())
        client.process(InputImage.fromBitmap(bitmapFromContentUri, 0))
            .addOnSuccessListener(object : OnSuccessListener<SegmentationMask?> {
                override fun onSuccess(segmentationMask: SegmentationMask?) {
                    val buffer: ByteBuffer = segmentationMask!!.getBuffer()
                    val width: Int = segmentationMask.getWidth()
                    val height: Int = segmentationMask.getHeight()
                    val createBitmap = Bitmap.createBitmap(bitmapFromContentUri.width, bitmapFromContentUri.height, bitmapFromContentUri.config)
                    for (i in 0 until height) {
                        for (i2 in 0 until width) {
                            val d = buffer.float.toDouble()
                            createBitmap.setPixel(i2, i, Color.argb(((1.0 - d) * 255.0).toInt(), 0, 0, 0))
                        }
                    }
                    buffer.rewind()
                    val autoeraseimage= mergeToPinBitmap(bitmapFromContentUri, createBitmap)
                    if (autoeraseimage != null) {
                        // Now set your auto eraseimagebitmap to your imageview
                        uploadImage(autoeraseimage,view!!)
                        view!!.findViewById<ImageView>(R.id.drawView).setImageBitmap(autoeraseimage)
                    } else {
                        Toast.makeText(view!!.context,resources.getString(R.string.please_try_again),Toast.LENGTH_SHORT).show()
                    }
                }


            }).addOnFailureListener(object : OnFailureListener {
                override fun onFailure(e: Exception) {
                    Toast.makeText(view!!.context,resources.getString(R.string.please_try_again),Toast.LENGTH_SHORT).show()
                    e.message
                }

            })
    }

    fun mergeToPinBitmap(bitmap: Bitmap, bitmap2: Bitmap): Bitmap {
        val createBitmap =
            Bitmap.createBitmap(bitmap2.width, bitmap2.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(createBitmap)
        val paint = Paint(1)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, null as Paint?)
        canvas.drawBitmap(bitmap2, 0.0f, 0.0f, paint)
        paint.xfermode = null as Xfermode?
        /*bitmap2.recycle()
        bitmap.recycle()
        */return createBitmap
    }


    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, 123)
    }
}
