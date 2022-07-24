package com.example.quickbill.ui.analytics

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
                    avgCost = vm.averageCost,
                    totalMeals = vm.totalMeals,
                    onViewRangeChange = { viewRange ->
                        vm.spendingViewRangeChange(viewRange)
                    })
                1 -> NutritionTab(
                    selectedViewRange = vm.nutritionViewRange,
                    averageCalories = vm.averageCalories,
                    onViewRangeChange = { viewRange ->
                        vm.nutritionViewRangeChange(viewRange)
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun SpendingTab(
    selectedViewRange: ViewRange = ViewRange.WEEK,
    avgCost: String = "$34.34",
    totalMeals: String = "43",
    onViewRangeChange: (ViewRange) -> Unit = { _ -> },
) {
    val vm: AnalyticsViewModel = viewModel()
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        ViewRangeSelector(
            selectedViewRange = selectedViewRange,
            onSelect = onViewRangeChange
        )
        BarChart(
            viewRange = selectedViewRange,
            onTranslate = { start, end -> vm.spendingRangeChange(start, end) }
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

@Preview
@Composable
fun NutritionTab(
    selectedViewRange: ViewRange = ViewRange.WEEK,
    averageCalories: String = "2300",
    onViewRangeChange: (ViewRange) -> Unit = { _ -> },
) {
    // max calories in 1 meal or 1 day?
    val vm: AnalyticsViewModel = viewModel()
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        ViewRangeSelector(
            selectedViewRange = selectedViewRange,
            onSelect = onViewRangeChange
        )
        BarChart(
            viewRange = selectedViewRange,
            onTranslate = { start, end -> vm.nutritionRangeChange(start, end) }
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            InfoBox(label = "Average Calories", info = "$averageCalories kCal")
        }
        PieChart()
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
