package com.klmn.napp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController

/*
* Features:
*   Retrofit / Room repository
*   MVVM / MVI architecture
* */

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = findNavController(R.id.fragment_container_view)
    }

    override fun onNavigateUp() = navController.navigateUp() || super.onNavigateUp()
}