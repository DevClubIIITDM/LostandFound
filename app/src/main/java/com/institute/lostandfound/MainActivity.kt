package com.institute.lostandfound

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.institute.lostandfound.databinding.ActivityMainBinding
import com.institute.lostandfound.viewmodel.AuthViewModel
import com.institute.lostandfound.config.EnvironmentConfig
import androidx.activity.viewModels

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize environment configuration
        EnvironmentConfig.init(this)
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup navigation
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setupWithNavController(navController)

        // Setup app bar
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_lost,
                R.id.navigation_found,
                R.id.navigation_post
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Observe authentication state
        authViewModel.currentUser.observe(this) { user ->
            if (user != null) {
                // User is authenticated, show bottom navigation
                bottomNav.visibility = android.view.View.VISIBLE
            } else {
                // User is not authenticated, hide bottom navigation
                bottomNav.visibility = android.view.View.GONE
            }
        }

        // Handle navigation to auth when user signs out
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_auth -> {
                    bottomNav.visibility = android.view.View.GONE
                }
                else -> {
                    if (authViewModel.currentUser.value != null) {
                        bottomNav.visibility = android.view.View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
} 