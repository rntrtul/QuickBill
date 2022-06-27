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
import com.google.android.material.bottomnavigation.BottomNavigationView
import sqip.Card
import sqip.CardDetails
import sqip.CardEntry
import sqip.CardEntry.setCardNonceBackgroundHandler
import sqip.CardEntryActivityResult


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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if ( requestCode == 51789 ) {
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
