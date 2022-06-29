package com.example.quickbill

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.quickbill.api.API
import com.example.quickbill.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import sqip.Card
import sqip.CardDetails
import sqip.CardEntry
import sqip.CardEntry.setCardNonceBackgroundHandler
import sqip.CardEntryActivityResult
import com.example.quickbill.util.*

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

        val cardHandler = CardEntryBackgroundHandler()
        setCardNonceBackgroundHandler(cardHandler)
    }

    fun handleInvalidQrCode() {
        // Show a toast message indicating that the QR code was invalid.
        val text = "QR code is invalid. Please contact the restaurant owner."
        val duration = Toast.LENGTH_LONG
        val toast = Toast.makeText( applicationContext, text, duration )
        toast.show()

        // Invalidate the location ID and table number scanned.
        API.instance.invalidateLocationAndTableNum()

        // Go back to the scan QR code page.
        findNavController( R.id.nav_host_fragment_activity_main ).navigate(
                R.id.navigation_pay,
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data) // Ignore the fact that it's deprecated.

        if ( isScanActivityResultQRCodeScanner( requestCode ) ) {
            val scanResult = getScanResult(resultCode, data)
            Log.d( "Main Activity - onActivityResult() - QR Code Branch", "Scan result: $scanResult" )

            if ( scanResult != null ) {
                val scanTokens : List<String> = scanResult.split( '-' )
                if ( scanTokens.size != 3 ) {
                    handleInvalidQrCode()
                    return
                }

                val locationId = scanTokens[ 0 ]

                // Parse table number.
                var tableNum = 0;
                try {
                    tableNum = Integer.parseInt(scanTokens[1])
                } catch ( exception : Exception ) {
                    Log.d( "Main Activity - onActivityResult() - QR Code Branch", "Could not parse table number!" )
                    handleInvalidQrCode()
                    return
                }

                val restaurantName = scanTokens[ 2 ]

                // Set the location ID and table num (also requesting the bill from the API).
                API.instance.setLocationAndTableNum( locationId, tableNum, restaurantName )

                if ( API.instance.bill == null ) {
                    handleInvalidQrCode()
                } else {
                    // Go to the bill.
                    findNavController( R.id.nav_host_fragment_activity_main ).navigate(
                        R.id.action_navigation_pay_to_billFragment,
                    )
                }
            }
        } else if ( requestCode == 51789 ) {
            CardEntry.handleActivityResult( data, object : sqip.Callback<CardEntryActivityResult> {
                override fun onResult(result: CardEntryActivityResult) {
                    if (result.isSuccess()) {
                        Log.d( "NOPE", "---------------------------------------")
                        Log.d( "NOPE", "-------------- SUCCESS ----------------")
                        Log.d( "NOPE", "---------------------------------------")
                        val cardResult: CardDetails = result.getSuccessValue()
                        val card: Card = cardResult.card
                        val nonce = cardResult.nonce
                    } else if (result.isCanceled()) {
                        Log.d( "NOPE", "---------------------------------------")
                        Log.d( "NOPE", "------------ NOT ALLOWED --------------")
                        Log.d( "NOPE", "---------------------------------------")
                    }
                }
            })
        }
    }
}
