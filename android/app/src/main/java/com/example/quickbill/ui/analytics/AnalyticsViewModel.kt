package com.example.quickbill.ui.analytics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quickbill.firebaseManager.FirebaseManager

class AnalyticsViewModel : ViewModel() {

    fun getOrderData() {
        FirebaseManager.getData("testOrders")
    }

    fun getNutritionData() {
        FirebaseManager.getData("testNutrition")
    }
}