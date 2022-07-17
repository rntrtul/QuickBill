package com.example.quickbill.ui.analytics

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quickbill.firebaseManager.FirebaseManager

class AnalyticsViewModel : ViewModel() {
    val TAG = "AnalyticsViewModel"

    val analyticCategories = listOf("Spending", "Nutrition")

    fun getOrderData() {
        FirebaseManager.getData("testOrders", object : FirebaseManager.MyCallback {
                override fun onCallback(item: Map<String,Any>) {
                    Log.d(TAG, "${item.toString()}")
                }
        })
    }

    fun getNutritionData(foodName: String? = null) {
        FirebaseManager.getData("testNutrition", object : FirebaseManager.MyCallback {
            override fun onCallback(item: Map<String,Any>) {
                Log.d(TAG, "${item.toString()}")
            }
        })
    }
}