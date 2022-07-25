package com.example.quickbill.ui.analytics

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quickbill.firebaseManager.NutritionInfo
import com.example.quickbill.ui.theme.QuickBillTheme

@Preview
@Composable
fun AnalyticsContent(vm: AnalyticsViewModel = viewModel()) {
    // May want to use a recycler view if we show a table
//    var selectedTabIndex by remember { mutableStateOf(0) }
    QuickBillTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            TabRow(selectedTabIndex = vm.selectedTabIndex) {
                vm.analyticCategories.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = vm.selectedTabIndex == index,
                        onClick = { vm.selectedTabIndex = index }
                    )
                }
            }
            when (vm.selectedTabIndex) {
                0 -> SpendingTab(
                    selectedViewRange = vm.spendingViewRange,
                    dataReady = vm.spendingDataReady,
                    avgCost = vm.averageCost,
                    totalMeals = vm.totalMeals,
                    spendingXData = vm.spendingXData,
                    spendingLabels = vm.spendingLabels,
                    spendingYData = vm.spendingYData,
                    onViewRangeChange = { viewRange -> vm.spendingViewRangeChange(viewRange) },
                    onTranslate = { start, end -> vm.spendingRangeChange(start, end) },
                )
                1 -> NutritionTab(
                    selectedViewRange = vm.nutritionViewRange,
                    dataReady = vm.nutritionDataReady,
                    averageCalories = vm.averageCalories,
                    calorieMax = vm.calorieMax,
                    calorieXData = vm.calorieXData,
                    calorieYData = vm.calorieYData,
                    calorieLabels = vm.calorieLabels,
                    nutritionAvg = vm.nutritionAverage,
                    onViewRangeChange = { viewRange -> vm.nutritionViewRangeChange(viewRange) },
                    onTranslate = { start, end -> vm.nutritionRangeChange(start, end) },
                )
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Preview
@Composable
fun SpendingTab(
    selectedViewRange: ViewRange = ViewRange.WEEK,
    dataReady: Boolean = true,
    avgCost: String = "$34.34",
    totalMeals: String = "43",
    spendingXData: List<Float> = listOf(0f, 1f),
    spendingLabels: List<String> = listOf("a", "a"),
    spendingYData: List<Float> = listOf(1f, 2f),
    onViewRangeChange: (ViewRange) -> Unit = { _ -> },
    onTranslate: (Float, Float) -> Unit = { _, _ -> }
) {
    AnimatedVisibility(visible = dataReady, enter = scaleIn(), exit = fadeOut()) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            ViewRangeSelector(
                selectedViewRange = selectedViewRange,
                onSelect = onViewRangeChange,
            )
            BarChart(
                xAxisData = spendingXData,
                yAxisData = spendingYData,
                xAxisBarLabels = spendingLabels,
                viewRange = selectedViewRange,
                onTranslate = onTranslate
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                InfoBox(label = "Average Cost", info = avgCost)
                InfoBox(label = "Total Meals", info = totalMeals)
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Preview
@Composable
fun NutritionTab(
    selectedViewRange: ViewRange = ViewRange.WEEK,
    dataReady: Boolean = true,
    averageCalories: String = "2300",
    calorieMax: String = "25",
    calorieXData: List<Float> = listOf(0f, 1f),
    calorieLabels: List<String> = listOf("a", "a"),
    calorieYData: List<Float> = listOf(1f, 2f),
    nutritionAvg: NutritionInfo = NutritionInfo(),
    onViewRangeChange: (ViewRange) -> Unit = { _ -> },
    onTranslate: (Float, Float) -> Unit = { _, _ -> }
) {
    // max calories in 1 meal or 1 day?
    AnimatedVisibility(visible = dataReady, enter = scaleIn()) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            ViewRangeSelector(
                selectedViewRange = selectedViewRange,
                onSelect = onViewRangeChange
            )
            BarChart(
                chartInfo = ChartInfo(
                    title = "Calories per Day",
                    yAxisName = "Calories (kCal)",
                ),
                xAxisData = calorieXData,
                yAxisData = calorieYData,
                xAxisBarLabels = calorieLabels,
                viewRange = selectedViewRange,
                onTranslate = onTranslate,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                InfoBox(label = "Average Calories", info = "$averageCalories kCal")
                InfoBox(label = "Max Calories", info = "$calorieMax kCal")

            }
            PieChart(
                data = listOf(
                    nutritionAvg.protein.toFloat(),
                    nutritionAvg.fat.toFloat(),
                    nutritionAvg.carbohydrates.toFloat()
                ),
                labels = listOf("Protein", "Fat", "Carbs")
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun InfoBox(
    label: String = "Average Cost",
    info: String = "$45"
) {
    Card {
        Box(modifier = Modifier.padding(8.dp)) {
            Column {
                Text(
                    text = info,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// compose doesn't have implementation for segmented button yet
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun ViewRangeSelector(
    ranges: List<ViewRange> = listOf(ViewRange.WEEK, ViewRange.MONTH, ViewRange.YEAR),
    selectedViewRange: ViewRange = ViewRange.WEEK,
    onSelect: (ViewRange) -> Unit = { _ -> }
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        ranges.forEach { viewType ->
            FilterChip(
                selected = viewType == selectedViewRange,
                onClick = { onSelect(viewType) },
                label = { Text(text = viewType.displayName) }
            )
        }
    }
}
