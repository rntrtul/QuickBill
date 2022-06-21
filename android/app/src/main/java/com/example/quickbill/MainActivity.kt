package com.example.quickbill

import SQLiteManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.quickbill.databinding.ActivityMainBinding
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

        loadFromDBToMemory();
    }

    private fun loadFromDBToMemory() {
        println("Anudeep- entered loadFromDBToMemeory")
        val sqLiteManager = SQLiteManager.instanceOfDatabase(this)
        val order = Order(1,"title","description", )
        sqLiteManager!!.addNoteToDatabase(order)
        sqLiteManager.populateNoteListArray()
        for (element in Order.noteArrayList) {
            println("Anudeep: $element")
        }
    }
}