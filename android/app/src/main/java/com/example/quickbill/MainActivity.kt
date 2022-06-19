package com.example.quickbill

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.quickbill.databinding.ActivityMainBinding
import com.example.quickbill.util.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Temporary code for debugging the QR code scanner stuff. Will integrate properly when rest of app is ready.
        startScan( this )
        debugShowGeneratedPopupQrCode( this, "jimmy buckets" )
    }

    // Temporary code for debugging the QR code scanner stuff. Will integrate properly when rest of app is ready.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data) // Ignore the fact that it's deprecated.
        val scanResult = getScanResult( resultCode, data )
        Log.d( "MainActivity - onActivityResult()", "Scan result: $scanResult" )
        if ( scanResult != null ) debugPutScanResultInPopup( this, scanResult )
    }
}