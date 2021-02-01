package com.klmn.napp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.klmn.napp.R
import dagger.hilt.android.AndroidEntryPoint

/*
* Features:
*   Retrofit / Room repository
*   MVVM / MVI architecture
*
* Actions:
*   Home page- recommended stuff
*   Search by category / other tags
*   View a product
* */

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = findNavController(R.id.fragment_container_view)
    }

    override fun onNavigateUp() = navController.navigateUp() || super.onNavigateUp()
}