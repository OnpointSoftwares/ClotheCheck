package com.example.clothchecker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.clothchecker.ui.main.HomeFragment

class Home : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, HomeFragment.newInstance())
                .commitNow()
        }
    }
}