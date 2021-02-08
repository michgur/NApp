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
class MainActivity : AppCompatActivity(), TextView.OnEditorActionListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = findNavController(R.id.fragment_container_view)

        binding.searchEditText.setOnEditorActionListener(this)
        navController.addOnDestinationChangedListener { _, _, arguments ->
            binding.searchEditText.setText(arguments?.getString("query"))
        }
    }

    override fun onNavigateUp() = navController.navigateUp() || super.onNavigateUp()

    override fun onPrepareOptionsMenu(menu: Menu?) = false
    override fun onCreateOptionsMenu(menu: Menu?) = false

    override fun onEditorAction(textView: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) navController.navigate(
            R.id.searchFragment,
            bundleOf("query" to (textView?.text?.toString() ?: ""))
        )
        return true
    }
}