package com.example.weatherapplication

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.weatherapplication.utility.Constant
import com.google.android.material.navigation.NavigationView
import java.util.Locale


class MainActivity : AppCompatActivity() {
    
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var navController: NavController
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("Setting", Context.MODE_PRIVATE)
        if (sharedPreferences.getBoolean(Constant.LANGUAGE_KEY, true)) {
            setLocale("en")
        } else {
            setLocale("ar")
        }
        setContentView(R.layout.activity_main)
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.NavigationView)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.menu)
            actionBar.setDisplayShowHomeEnabled(true)
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setBackgroundDrawable(
                ColorDrawable(
                    resources.getColor(
                        R.color.background,
                        resources.newTheme()
                    )
                )
            )
        }
        navController = findNavController(this, R.id.nav_host_fragment)
        setupWithNavController(navigationView, navController)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.openDrawer(GravityCompat.START)
            } else {
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        this.resources.updateConfiguration(config, this.resources.displayMetrics)
    }
}