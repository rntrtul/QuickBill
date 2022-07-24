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
import kotlin.math.max
import kotlin.math.min

data class Range(
    var start: Int,
    var end: Int
) {
    fun rangeChange(newStart: Float, rangeSize: Int, dataSize: Int) {
        start = min(max(newStart.toInt(), 0), (dataSize - rangeSize))
        end = start + rangeSize
//        Log.d("Chart", "$start - $end")
    }
}

enum class ViewRange(val barsShown: Float, val displayName: String) {
    WEEK(7f, "Week"),
    MONTH(30f, "Month"),
    YEAR(365f, "Year")
}

class AnalyticsViewModel : ViewModel() {
    val TAG = "AnalyticsViewModel"
    private var _spendingData = (0..400).map { num -> num.toFloat() }
    private var _nutritionData = (0..400).map { num -> num.toFloat() }

    //    todo: give defaults
    private var _averageCost: Int by mutableStateOf(0)
    private var _averageCalories: Int by mutableStateOf(0)
    private var _totalMeals: Int by mutableStateOf(0)

    private val spendingCurrentRange by mutableStateOf(Range(0, 0))
    private val nutritionCurrentRange by mutableStateOf(Range(0, 0))

    var spendingViewRange by mutableStateOf(ViewRange.WEEK)
    var nutritionViewRange by mutableStateOf(ViewRange.WEEK)
    var selectedTabIndex by mutableStateOf(0)

    val averageCost: String get() = centsToDisplayedAmount(_averageCost)
    val averageCalories: String get() = _averageCalories.toString()
    val totalMeals: String get() = _totalMeals.toString()

    val analyticCategories = listOf("Spending", "Nutrition")

    private fun spendingInfoCalc() {
        val viewedSpending =
            _spendingData.subList(spendingCurrentRange.start, spendingCurrentRange.end)

        _totalMeals = viewedSpending.count { item -> item != 0f }
        _averageCost = (viewedSpending.sum() / _totalMeals).toInt()
    }

    private fun nutritionInfoCalc() {
        val viewedNutrition =
            _nutritionData.subList(nutritionCurrentRange.start, nutritionCurrentRange.end)

        _averageCalories = (viewedNutrition.sum() / viewedNutrition.count()).toInt()
    }

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

    fun spendingRangeChange(start: Float, end: Float) {
        spendingCurrentRange.rangeChange(start, spendingViewRange.barsShown.toInt(), 400)
        spendingInfoCalc()
    }

    fun nutritionRangeChange(start: Float, end: Float) {
        nutritionCurrentRange.rangeChange(start, nutritionViewRange.barsShown.toInt(), 400)
        nutritionInfoCalc()
    }
}
