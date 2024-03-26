package com.example.clothchecker.ui.main
import ClothingItem
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.clothchecker.ImagePagerAdapter
import com.example.clothchecker.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {

    private lateinit var viewPager: ViewPager
    private lateinit var imagePagerAdapter: ImagePagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        viewPager = view.findViewById(R.id.viewpager)
        imagePagerAdapter = ImagePagerAdapter()
        viewPager.adapter = imagePagerAdapter

        // Fetch image URLs from Firebase
        val auth = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val databaseReference = FirebaseDatabase.getInstance().reference.child("Clothes").child(auth)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val imageUrlList = mutableListOf<String>()
                for (snapshot in dataSnapshot.children) {
                    val clothingItem = snapshot.getValue(ClothingItem::class.java)
                    clothingItem?.let {
                        // Assuming imageUrl is the field containing the image URL in ClothingItem
                        imageUrlList.add(it.imageUrl)
                    }
                }
                imagePagerAdapter.setImageUrls(imageUrlList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors if needed
            }
        })

        return view
    }

    private fun fetchImageUrls() {
        val auth = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val databaseReference = FirebaseDatabase.getInstance().reference.child("Clothes").child(auth)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val imageUrlList = mutableListOf<String>()
                for (snapshot in dataSnapshot.children) {
                    val clothingItem = snapshot.getValue(ClothingItem::class.java)
                    clothingItem?.let {
                        // Assuming imageUrl is the field containing the image URL in ClothingItem
                        imageUrlList.add(it.imageUrl)
                    }
                }
                imagePagerAdapter.setImageUrls(imageUrlList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors if needed
            }
        })
    }
}
