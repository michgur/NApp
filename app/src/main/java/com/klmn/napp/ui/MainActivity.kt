package com.klmn.napp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.ActionBar
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.klmn.napp.R
import com.klmn.napp.databinding.ActivityMainBinding
import com.klmn.napp.util.hideKeyboard
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
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_NApp)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = findNavController(R.id.fragment_container_view)
        navController.addOnDestinationChangedListener { _, _, _ -> hideKeyboard() }
    }

    override fun onNavigateUp() = navController.navigateUp() || super.onNavigateUp()

    override fun onPrepareOptionsMenu(menu: Menu?) = false
    override fun onCreateOptionsMenu(menu: Menu?) = false
}