package com.example.quickbill.ui.analytics

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.quickbill.firebaseManager.FirebaseManager
import com.example.quickbill.util.centsToDisplayedAmount

enum class ViewRange(val barsShown: Float, val displayName: String) {
    WEEK(7f, "Week"),
    MONTH(30f, "Month"),
    YEAR(365f, "Year")
}

class AnalyticsViewModel : ViewModel() {
    val TAG = "AnalyticsViewModel"
    private var _averageCost : Int by mutableStateOf(5)
    private var _averageCalories : Int by mutableStateOf(23)

    var spendingViewRange by mutableStateOf(ViewRange.WEEK)
    var nutritionViewRange by mutableStateOf(ViewRange.WEEK)
    var selectedTabIndex by mutableStateOf(0)

    val averageCost: String get() = centsToDisplayedAmount(_averageCost)
    val averageCalories: String get() = _averageCalories.toString()

    //    fixme: change to get accurate data
    val totalMeals: String get() = "45"

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

    fun spendingViewRangeChange(viewRange: ViewRange) {
        spendingViewRange = viewRange
        // todo: update all lists of spending data?
        _averageCost = (viewRange.barsShown * 10).toInt()
    }

    fun nutritionViewRangeChange(viewRange: ViewRange) {
        nutritionViewRange = viewRange
        // todo: update all lists of nutrition data?
        _averageCalories = (viewRange.barsShown * 8).toInt()
    }
}
