package com.example.realestatemanager.ui.main

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            viewModel.hasPermissionBeenGranted(
                permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                        permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
            )
        }


    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissions()

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

    @SuppressLint("NewApi")
    private fun requestPermissions() {
        when {
            // Permissions already granted
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED ->
                viewModel.hasPermissionBeenGranted(true)

            // Permissions have been denied once - show rationale
            shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION) ||
                    shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_COARSE_LOCATION) ->
                showRequestPermissionRationale()

            // Request permission
            else ->
                requestPermissionLauncher.launch(
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
        }
    }

    private fun showRequestPermissionRationale() {
        // rationale should be shown only once
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.permission_rationale_title))
            .setMessage(getString(R.string.permission_rationale_message))
            .setPositiveButton(getString(R.string.permission_rationale_ok_btn)) { dialogInterface, _ ->
                // re-request permission
                requestPermissionLauncher.launch(
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
                dialogInterface.dismiss()
            }
            .show()
    }
}