package com.example.realestatemanager.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.example.realestatemanager.R
import com.example.realestatemanager.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    companion object {
        fun navigate(context: Context, fragmentTag: String): Intent {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra(KEY_FRAGMENT_TAG, fragmentTag)
            context.startActivity(intent)
            return intent
        }

        private const val KEY_FRAGMENT_TAG = "KEY_FRAGMENT_TAG"
        private const val SLIDING_FRAGMENT_TAG = "SLIDING_FRAGMENT_TAG"
        private const val FILTER_FRAGMENT_TAG = "FILTER_FRAGMENT_TAG"
        private const val ADD_FRAGMENT_TAG = "ADD_FRAGMENT_TAG"
    }

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        setSupportActionBar(binding.mainActivityToolbar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.activity_main_container_nav_host) as NavHostFragment
        val navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.mainActivityToolbar.setupWithNavController(navController, appBarConfiguration)


        viewModel.mainViewState.observe(this) { mainViewState ->
            binding.mainActivityToolbar.menu.findItem(R.id.filterEstatesFragment)?.isVisible =
                mainViewState.isFilterAppBarButtonVisible

            binding.mainActivityToolbar.menu.findItem(R.id.estate_list)?.isVisible =
                mainViewState.isResetFilterAppBarButtonVisible

            binding.mainActivityToolbar.menu.findItem(R.id.mapsFragment)?.isVisible =
                mainViewState.isMapAppBarButtonVisible
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.activity_main_container_nav_host)
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                viewModel.onBackClicked()
                true
            }

            R.id.mapsFragment -> {
                viewModel.onMapClicked()
                return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
            }

            R.id.filterEstatesFragment -> {
                return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
            }

            R.id.estate_list -> {
                viewModel.onResetFiltersClicked()
                return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val resetFilterItem = menu?.findItem(R.id.estate_list)
        resetFilterItem?.isVisible = false
        return super.onPrepareOptionsMenu(menu)
    }

    private fun hideKeyboard(view: View?) {
        if (view != null) {
            val inputMethodManager =
                view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (currentFocus != null) {
            hideKeyboard(currentFocus)
            currentFocus!!.clearFocus()
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.activity_main_container_nav_host)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}