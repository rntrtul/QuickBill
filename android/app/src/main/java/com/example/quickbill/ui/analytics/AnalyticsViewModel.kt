package com.example.quickbill.ui.analytics

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.quickbill.firebaseManager.FirebaseManager
import com.example.quickbill.firebaseManager.FirebaseOrderItem
import com.example.quickbill.firebaseManager.NutritionInfo
import com.example.quickbill.ui.pay.Money
import com.example.quickbill.util.centsToDisplayedAmount
import com.example.quickbill.util.daysBetween
import com.example.quickbill.util.timestampToDate
import com.google.gson.Gson
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
    private var _nutritionData = arrayListOf<NutritionInfo>()
    private var _calorieXData = mutableListOf<Float>()
    private var _calorieYData = mutableListOf<Float>()
    private var _calorieLabels = mutableListOf<String>()
    private var _nutritionAverage = NutritionInfo()
    private var _calorieMax by mutableStateOf(0)

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
    val calorieMax: String get() = _calorieMax.toString()

    val calorieXData get() = _calorieXData
    val calorieYData get() = _calorieYData
    val calorieLabels get() = _calorieLabels
    val nutritionAverage get() = _nutritionAverage
    var nutritionDataReady by mutableStateOf(false)


    val analyticCategories = listOf("Spending", "Nutrition")

    private fun spendingInfoCalc() {
        val viewedSpending =
            _spendingYData.subList(spendingCurrentRange.start, spendingCurrentRange.end)

        _totalMeals = viewedSpending.count { item -> item != 0f }
        _averageCost = (viewedSpending.sum() / _totalMeals).toInt()
    }

    private fun nutritionInfoCalc() {
        val viewedNutrition =
            _calorieYData.subList(nutritionCurrentRange.start, nutritionCurrentRange.end)

        val entries = viewedNutrition.count { item -> item != 0f }
        _averageCalories = (viewedNutrition.sum() / entries).toInt()
        _calorieMax =
            if (viewedNutrition.isNotEmpty()) viewedNutrition.maxOf { item -> item.toInt() } else 0
    }

    private fun spendingDataToChart() {
        val minDate = _spendingData.minOf { item -> item.date }
        val maxDate = _spendingData.maxOf { item -> item.date }
        val days = daysBetween(minDate, maxDate, endIncluded = true)

        _spendingLabels = (1..days).map { offset -> "$offset" }.toMutableList()
        _spendingXData =
            (1..days).map { i -> i.toFloat() }.toMutableList()
        _spendingYData = (0 until days).map { 0f }.toMutableList()

        _spendingData.forEach { item ->
            _spendingYData[daysBetween(minDate, item.date)] = item.cost.amount.toFloat()
        }
        spendingDataReady = true
        spendingInfoCalc()
    }

    private fun nutritionDataToChart() {
        val minDate = _nutritionData.minOf { item -> item.date }
        val maxDate = _nutritionData.maxOf { item -> item.date }
        val days = max(daysBetween(minDate, maxDate, endIncluded = true), 7)

        _calorieXData =
            (1..days).map { i -> i.toFloat() }.toMutableList()
        _calorieYData = (0 until days).map { 0f }.toMutableList()
        _calorieLabels = (1..days).map { offset -> "${offset + 1}" }.toMutableList()

        _nutritionData.forEach { item ->
            _calorieYData[daysBetween(minDate, item.date)] = item.calories.toFloat()
            _nutritionAverage += item
        }

        _nutritionAverage /= _nutritionData.size

        nutritionDataReady = true
        nutritionInfoCalc()
    }

    private fun getOrderData() {
        FirebaseManager.getData("testFoodOrders", object : FirebaseManager.MyCallback {
            override fun onCallback(items: List<Map<String, Any>>) {
                val gson = Gson()
                for (item in items) {
                    Log.d(TAG, "$item")
                    //fixme: jank city
                    val cost =
                        if (item["costCAD"].toString() == "null")
                            gson.fromJson(item["cost"].toString(), Money::class.java)
                        else
                            gson.fromJson(item["costCAD"].toString(), Money::class.java)
                    _spendingData.add(
                        FirebaseOrderItem(
                            orderId = item["orderId"].toString(),
                            cost = cost,
                            date = timestampToDate(item["date"].toString())
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
                val gson = Gson()
                items.forEach { item ->
//                    Log.d(TAG, "$item")
                    val split = item.toString().split(",")
                    val nameDateLess = "{" + (
                            split.subList(2, 8) + split.subList(11, split.size)
                            ).joinToString { c -> c }
                    val info = gson.fromJson(nameDateLess, NutritionInfo::class.java)
                    info.date = timestampToDate(split[0] + "," + split[1])
                    _nutritionData.add(info)
                }
                nutritionDataToChart()
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
            _calorieXData.size
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
            _calorieXData.size
        )
        nutritionInfoCalc()
    }

    init {
        getOrderData()
        getNutritionData()
    }
}
