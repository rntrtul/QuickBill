package com.example.quickbill.ui.analytics

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.quickbill.firebaseManager.FirebaseManager
import com.example.quickbill.firebaseManager.FirebaseOrderItem
import com.example.quickbill.util.centsToDisplayedAmount
import com.example.quickbill.util.daysBetween
import java.util.*
import kotlin.math.max
import kotlin.math.min

data class Range(
    var start: Int,
    var end: Int
) {
    fun rangeChange(newStart: Float, rangeSize: Int, dataSize: Int) {
        start = min(max(newStart.toInt(), 0), max((dataSize - rangeSize), 0))
        end = min(start + rangeSize, dataSize)
    }
}

enum class ViewRange(val barsShown: Float, val displayName: String) {
    WEEK(7f, "Week"),
    MONTH(30f, "Month"),
    YEAR(365f, "Year")
}

class AnalyticsViewModel : ViewModel() {
    val TAG = "AnalyticsViewModel"
    private var _spendingData = arrayListOf<FirebaseOrderItem>()
    private var _spendingXData = mutableListOf<Float>()
    private var _spendingYData = mutableListOf<Float>()
    private var _spendingLabels = mutableListOf<String>()

    private var _averageCost: Int by mutableStateOf(0)
    private var _totalMeals: Int by mutableStateOf(0)

    //    todo: give defaults
    private var _nutritionData = (0..400).map { num -> num.toFloat() }
    private var _averageCalories: Int by mutableStateOf(0)

    private val spendingCurrentRange by mutableStateOf(Range(0, 0))
    var spendingViewRange by mutableStateOf(ViewRange.WEEK)

    private val nutritionCurrentRange by mutableStateOf(Range(0, 0))
    var nutritionViewRange by mutableStateOf(ViewRange.WEEK)
    var selectedTabIndex by mutableStateOf(0)

    val spendingXData: List<Float> get() = _spendingXData
    val spendingYData: List<Float> get() = _spendingYData
    val spendingLabels: List<String> get() = _spendingLabels
    var spendingDataReady by mutableStateOf(false)

    val averageCost: String get() = centsToDisplayedAmount(_averageCost)
    val averageCalories: String get() = _averageCalories.toString()
    val totalMeals: String get() = _totalMeals.toString()

    val analyticCategories = listOf("Spending", "Nutrition")

    private fun spendingInfoCalc() {
        val viewedSpending =
            _spendingYData.subList(spendingCurrentRange.start, spendingCurrentRange.end)

        _totalMeals = viewedSpending.count { item -> item != 0f }
        _averageCost = (viewedSpending.sum() / _totalMeals).toInt()
    }

    private fun nutritionInfoCalc() {
        val viewedNutrition =
            _nutritionData.subList(nutritionCurrentRange.start, nutritionCurrentRange.end)

        _averageCalories = (viewedNutrition.sum() / viewedNutrition.count()).toInt()
    }

    private fun spendingDataToChart() {
        val minDate = _spendingData.minOf { item -> item.date }
        val maxDate = _spendingData.maxOf { item -> item.date }
        val days = daysBetween(minDate, maxDate, endIncluded = true)

        _spendingXData = (1..days).map { i -> i.toFloat() }.toMutableList()
        _spendingYData = (0 until days).map { _ -> 0f }.toMutableList()
        _spendingLabels = (1..days).map { offset -> "${offset + 1}" }.toMutableList()

        _spendingData.forEach { item ->
            _spendingYData[daysBetween(minDate, item.date)] = item.costCAD.toFloat()
        }
        spendingDataReady = true
        spendingInfoCalc()
    }

    private fun getOrderData() {
        FirebaseManager.getData("testFoodOrders", object : FirebaseManager.MyCallback {
            override fun onCallback(items: List<Map<String, Any>>) {
                for (item in items) {
//                    Log.d(TAG, "$item")
                    //fixme: jank city
                    val seconds =
                        item["date"].toString().split("seconds")[1].split(",")[0].drop(1).toLong()
                    _spendingData.add(
                        FirebaseOrderItem(
                            orderId = item["orderId"].toString(),
                            costCAD = item["costCAD"].toString().toInt(),
                            date = Date(seconds * 1000)
                        )
                    )
                }
                spendingDataToChart()
            }
        })
    }

    private fun getNutritionData(foodName: String? = null) {
        FirebaseManager.getData("testNutrition", object : FirebaseManager.MyCallback {
            override fun onCallback(items: List<Map<String, Any>>) {
                Log.d(TAG, "$items")
            }
        })
    }

    //fixme: names and code too similar
    fun spendingViewRangeChange(viewRange: ViewRange) {
        spendingViewRange = viewRange
        spendingCurrentRange.rangeChange(
            spendingCurrentRange.start.toFloat(),
            spendingViewRange.barsShown.toInt(),
            _spendingXData.size
        )
        spendingInfoCalc()
    }

    fun nutritionViewRangeChange(viewRange: ViewRange) {
        nutritionViewRange = viewRange
        nutritionCurrentRange.rangeChange(
            nutritionCurrentRange.start.toFloat(),
            nutritionViewRange.barsShown.toInt(),
            _spendingXData.size
        )
        nutritionInfoCalc()
    }

    fun spendingRangeChange(start: Float, end: Float) {
        spendingCurrentRange.rangeChange(
            start,
            spendingViewRange.barsShown.toInt(),
            _spendingXData.size
        )
        spendingInfoCalc()
    }

    fun nutritionRangeChange(start: Float, end: Float) {
        nutritionCurrentRange.rangeChange(
            start,
            nutritionViewRange.barsShown.toInt(),
            _spendingXData.size
        )
        nutritionInfoCalc()
    }

    init {
        getOrderData()
        getNutritionData()
    }
}
