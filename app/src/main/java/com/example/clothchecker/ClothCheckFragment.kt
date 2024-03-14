package com.example.clothchecker

import ClothingItem
import adapters.ClothAdapter
import adapters.ClothesAdapter
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import java.io.Serializable

class ClothCheckFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var clothesAdapter: ClothesAdapter

    private var selectedShirt: ClothingItem? = null
    private var selectedTrouser: ClothingItem? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_cloth_check, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        clothesAdapter = ClothesAdapter()
        recyclerView.adapter = clothesAdapter
        val next:FloatingActionButton=view.findViewById(R.id.next)
        next.setOnClickListener {
            startActivity(Intent(view.context,Clothing::class.java))
        }
        // Fetch clothes data from Firebase
        fetchClothesData()

        return view
    }

    private fun navigateToNextActivity() {
        // Start the next activity
        val intent = Intent(view?.context, Clothing::class.java)
        intent.putExtra("selectedShirt", selectedShirt as Serializable)
        intent.putExtra("selectedTrouser", selectedTrouser as Serializable)
        startActivity(intent)
    }

    private fun fetchClothesData() {
        val auth = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val databaseReference =
            FirebaseDatabase.getInstance().reference.child("Clothes").child(auth)
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
