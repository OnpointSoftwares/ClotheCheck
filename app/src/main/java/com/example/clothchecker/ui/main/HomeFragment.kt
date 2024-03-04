package com.example.clothchecker.ui.main

import ClothingItem
import adapters.ClothesAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clothchecker.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var clothesAdapter: ClothesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        clothesAdapter = ClothesAdapter()
        recyclerView.adapter = clothesAdapter

        // Fetch clothes data from Firebase
        fetchClothesData()

        return view
    }

    private fun fetchClothesData() {
        val auth = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val databaseReference = FirebaseDatabase.getInstance().reference.child("Clothes").child(auth)
        databaseReference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val clothingItem = snapshot.getValue(ClothingItem::class.java)
                clothingItem?.let { clothesAdapter.addItem(it) }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                // Handle changes if needed
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                // Handle removals if needed
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // Handle movements if needed
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors if needed
            }
        })
    }
}
