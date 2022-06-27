package com.example.quickbill

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.quickbill.api.API
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
                R.id.navigation_analytics, R.id.navigation_pay, R.id.navigation_settings))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data) // Ignore the fact that it's deprecated.

        if ( isScanActivityResultQRCodeScanner( requestCode ) ) {
            val scanResult = getScanResult(resultCode, data)
            Log.d( "Main Activity - onActivityResult() - QR Code Branch", "Scan result: $scanResult" )
            if ( scanResult != null ) {
                val scanTokens : List<String> = scanResult.split( '-' )
                if (scanTokens.isEmpty()) return;
                val locationId = scanTokens[ 0 ]
                var tableNum = 0;
                try {
                    tableNum = Integer.parseInt(scanTokens[1])
                } catch ( exception : Exception ) {
                    Log.d( "Main Activity - onActivityResult() - QR Code Branch", "Could not parse table number!" )
                    return;
                }
                API.instance.setLocationAndTableNum( locationId, tableNum )
                findNavController( R.id.nav_host_fragment_activity_main ).navigate(
                        R.id.action_navigation_pay_to_billFragment,
                )
            }
        }
    }
}
