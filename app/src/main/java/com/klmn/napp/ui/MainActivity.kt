package com.klmn.napp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
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
class MainActivity : AppCompatActivity(), TextView.OnEditorActionListener {
    private lateinit var navController: NavController
    private lateinit var searchBar: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = findNavController(R.id.fragment_container_view)
        initActionBar(supportActionBar)

        searchBar.setOnEditorActionListener(this)
        navController.addOnDestinationChangedListener { _, _, arguments ->
            arguments?.getString("query")?.let(searchBar::setText)
        }
    }

    override fun onNavigateUp() = navController.navigateUp() || super.onNavigateUp()

    override fun onPrepareOptionsMenu(menu: Menu?) = false
    override fun onCreateOptionsMenu(menu: Menu?) = false

    private fun initActionBar(actionBar: ActionBar?) = actionBar?.apply {
        setDisplayShowCustomEnabled(true)
        setDisplayShowTitleEnabled(false)
        setDisplayShowHomeEnabled(false)
        setCustomView(R.layout.layout_search)
        searchBar = customView as EditText
    }

    override fun onEditorAction(textView: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) navController.navigate(
            R.id.searchFragment,
            bundleOf("query" to (textView?.text?.toString() ?: ""))
        )
        return true
    }
}